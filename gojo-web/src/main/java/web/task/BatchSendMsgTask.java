package web.task;

import api.MessageService;
import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import model.Chats;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import web.handler.SocketHandler;

import java.util.List;

public class BatchSendMsgTask implements Runnable {

    public BatchSendMsgTask(Object bean, List<Chats> models) {
        this.models = models;
        this.bean = bean;
    }

    private List<Chats> models;
    private Object bean;

    @SneakyThrows
    @Override
    public void run() {
        ConcurrentHashMap<Object, WebSocketSession> map = SocketHandler.getSessions();
        for(Chats model : models){
            if (map.containsKey(model.getToUserId())) {
                WebSocketSession session = map.get(model.getToUserId());
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(JSON.toJSONString(model)));
                } else {
                    ((MessageService) bean).saveMsg(model);
                }
            } else {
                ((MessageService) bean).saveMsg(model);
            }
        }
    }
}
