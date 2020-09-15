package web.task;

import api.MessageService;
import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import model.Chats;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import web.handler.SocketHandler;

import java.util.concurrent.ConcurrentHashMap;

public class SendMsgTask implements Runnable {

    public SendMsgTask(Object bean, Chats model) {
        this.model = model;
        this.bean = bean;
    }

    private Chats model;
    private Object bean;

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
                    ((MessageService) bean).saveMsg(model);
                }
            }
        } else {
            ((MessageService) bean).saveMsg(model);
        }
    }
}
