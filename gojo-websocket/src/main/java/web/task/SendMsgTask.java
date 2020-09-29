package web.task;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import model.Chats;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import web.handler.SocketHandler;

import java.util.concurrent.ConcurrentHashMap;

public class SendMsgTask implements Runnable {

    public SendMsgTask(Chats model, String handlerAddress, String sessionIdEncode) {
        this.model = model;
        this.handlerAddress = handlerAddress;
        this.sessionIdEncode = sessionIdEncode;
    }

    private Chats model;
    private String handlerAddress;
    private String sessionIdEncode;

    @SneakyThrows
    @Override
    public void run() {
        ConcurrentHashMap<Object, WebSocketSession> map = SocketHandler.getSessions();
        if (map.containsKey(model.getToUserId())) {
            WebSocketSession session = map.get(model.getToUserId());
            synchronized (session) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(JSON.toJSONString(model)));
                } else {
                    SocketHandler.pool.execute(new Thread(new SaveMsgTask(model, handlerAddress, sessionIdEncode)));
                }
            }
        } else {
            SocketHandler.pool.execute(new Thread(new SaveMsgTask(model, handlerAddress, sessionIdEncode)));
        }
    }
}
