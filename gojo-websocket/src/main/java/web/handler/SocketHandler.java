package web.handler;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import model.Chats;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import util.Const;
import util.GojoUtil;
import util.HttpClientUtil;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class SocketHandler extends TextWebSocketHandler {

    public static ExecutorService pool = Executors.newFixedThreadPool(17);
    private volatile static ConcurrentHashMap<Object, WebSocketSession> map = new ConcurrentHashMap<Object, WebSocketSession>();
    private String handlerAddress;
    private String localAddr;

    public SocketHandler(String handlerAddress, String localAddr) {
        this.handlerAddress = handlerAddress;
        this.localAddr = localAddr;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        Chats model = null;
        try {
            model = JSONObject.parseObject(message.getPayload(), Chats.class);
        } catch (Exception e) {
            log.error("发送消息转换失败" + message + "。时间：" + new Date() + "。用户：" + session.getAttributes().get(Const.LOGIN_USER_ID_CONST));
        }
        String sessionIdEncode = (String) session.getAttributes().get(Const.LOGIN_USER_SESSION_CONST);
        model.setId(GojoUtil.getUUID());
        model.setCreateTime(new Date());
        if ("user".equals(model.getType())) {
            String fromUserId = (String) session.getAttributes().get(Const.LOGIN_USER_ID_CONST);
            String fromUserName = (String) session.getAttributes().get(Const.LOGIN_USER_NAME_CONST);
            model.setFromUserId(fromUserId);
            model.setFromUserName(fromUserName);
            Map map = new HashMap() {{
                put("userId", session.getAttributes().get(Const.LOGIN_USER_ID_CONST));
            }};
            String WebSocketIp = (String) HttpClientUtil.httpClient("http://" + handlerAddress + "/gojo/handler/getWebSocketAddrByUserId", map, String.class);
            Chats finalModel = model;
            Map map1 = new HashMap() {{
                put("model", finalModel);
                put("sessionIdEncode", sessionIdEncode);
            }};
            HttpClientUtil.httpClient("http://" + WebSocketIp + "/gojo/websocket/sendMsg", map1, String.class);
        } else if ("group".equals(model.getType())) {

        } else if ("token".equals(model.getType())) {
            Map map = new HashMap() {{
                put("token", sessionIdEncode);
            }};
            Boolean flag = null;
            try {
                flag = (Boolean) HttpClientUtil.httpClient("http://" + handlerAddress + "/gojo/handler/checkToken", map, Boolean.class);
            } catch (Exception e) {
                session.close();
            }
            if (!flag) {
                session.close();
            }
        }
    }

    //连接建立后处理
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        addOnlineCount(session);
        Map map = new HashMap() {{
            put("userId", session.getAttributes().get(Const.LOGIN_USER_ID_CONST));
            put("addr", localAddr);
        }};
        HttpClientUtil.httpClient("http://" + handlerAddress + "/gojo/handler/setWebSocketAddrByUserId", map, String.class);
    }

    //抛出异常时处理
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        Map map = new HashMap() {{
            put("userId", session.getAttributes().get(Const.LOGIN_USER_ID_CONST));
            put("sessionIdEncode", session.getAttributes().get(Const.LOGIN_USER_SESSION_CONST));
        }};
        HttpClientUtil.httpClient("http://" + handlerAddress + "/gojo/handler/removeWebSocketAddrByUserId", map, String.class);
        log.error("连接异常");
    }

    //连接关闭后处理
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        subOnlineCount(session);
        Map map = new HashMap() {{
            put("userId", session.getAttributes().get(Const.LOGIN_USER_ID_CONST));
            put("sessionIdEncode", session.getAttributes().get(Const.LOGIN_USER_SESSION_CONST));
        }};
        HttpClientUtil.httpClient("http://" + handlerAddress + "/gojo/handler/removeWebSocketAddrByUserId", map, String.class);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public static int getOnlineCount() {
        return map.size();
    }

    private void addOnlineCount(WebSocketSession session) {
        map.put(session.getAttributes().get(Const.LOGIN_USER_ID_CONST), session);
        log.info("用户加入，当前在线用户：" + getOnlineCount() + "人");
    }

    private void subOnlineCount(WebSocketSession session) {
        map.remove(session.getAttributes().get(Const.LOGIN_USER_ID_CONST));
        log.info("用户退出，当前在线用户：" + getOnlineCount() + "人");
    }

    public static ConcurrentHashMap<Object, WebSocketSession> getSessions() {
        return map;
    }

}