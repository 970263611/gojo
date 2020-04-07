package web.task;

import api.MessageService;
import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import model.UserToUser;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import web.handler.SocketHandler;
import web.util.SpringBeanUtil;

public class SendMsgTask implements Runnable {

    public SendMsgTask(UserToUser model) {
        this.model = model;
    }

    private UserToUser model;

    @SneakyThrows
    @Override
    public void run() {
        ConcurrentHashMap<Object, WebSocketSession> map = SocketHandler.getSessions();
        if (map.containsKey(model.getToUserId())) {
            WebSocketSession session = map.get(model.getToUserId());
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(JSON.toJSONString(model)));
            } else {
                MessageService service = (MessageService) SpringBeanUtil.getBean(MessageService.class);
                service.saveMsg(model);
            }
        }
    }
}
