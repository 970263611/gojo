package web.task;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import model.Chats;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import web.handler.SocketHandler;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class BatchSendMsgTask implements Runnable {

    public BatchSendMsgTask(List<Chats> models, String handlerAddress, String sessionIdEncode) {
        this.models = models;
        this.handlerAddress = handlerAddress;
        this.sessionIdEncode = sessionIdEncode;
    }

    private List<Chats> models;
    private String handlerAddress;
    private String sessionIdEncode;

    @SneakyThrows
    @Override
    public void run() {
        ConcurrentHashMap<Object, WebSocketSession> map = SocketHandler.getSessions();
        for (Chats model : models) {
            if (map.containsKey(model.getToUserId())) {
                WebSocketSession session = map.get(model.getToUserId());
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(JSON.toJSONString(model)));
                } else {
                    SocketHandler.pool.execute(new Thread(new SaveMsgTask(model, handlerAddress, sessionIdEncode)));
                }
            } else {
                SocketHandler.pool.execute(new Thread(new SaveMsgTask(model, handlerAddress, sessionIdEncode)));
            }
        }
    }
}
