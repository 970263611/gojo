package web.handler;

import api.MessageService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import model.Chats;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import util.Const;
import util.GojoUtil;
import web.task.SendMsgTask;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class SocketHandler extends TextWebSocketHandler {

    private static ExecutorService pool = Executors.newFixedThreadPool(5);
    private volatile static ConcurrentHashMap<Object, WebSocketSession> map = new ConcurrentHashMap<Object, WebSocketSession>();
    @Autowired
    private MessageService messageService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String fromUserId = (String) session.getAttributes().get(Const.LOGIN_USER_ID_CONST);
            String fromUserName = (String) session.getAttributes().get(Const.LOGIN_USER_NAME_CONST);
            Chats model = JSONObject.parseObject(message.getPayload(), Chats.class);
            model.setId(GojoUtil.getUUID());
            model.setCreateTime(new Date());
            model.setFromUserId(fromUserId);
            model.setFromUserName(fromUserName);
            if ("user".equals(model.getType())) {
                pool.execute(new Thread(new SendMsgTask(messageService, model)));
            } else if ("group".equals(model.getType())) {

            }
        } catch (Exception e) {
            log.error("发送消息转换失败" + message + "。时间：" + new Date() + "。用户：" + session.getAttributes().get(Const.LOGIN_USER_ID_CONST));
        }
    }

    //连接建立后处理
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        addOnlineCount(session);
    }

    //抛出异常时处理
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {

    }

    //连接关闭后处理
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        subOnlineCount(session);
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
        System.out.println("用户加入，当前在线用户：" + getOnlineCount() + "人");
    }

    private void subOnlineCount(WebSocketSession session) {
        map.remove(session.getAttributes().get(Const.LOGIN_USER_ID_CONST));
        System.out.println("用户退出，当前在线用户：" + getOnlineCount() + "人");
    }

    public static ConcurrentHashMap<Object, WebSocketSession> getSessions() {
        return map;
    }

}