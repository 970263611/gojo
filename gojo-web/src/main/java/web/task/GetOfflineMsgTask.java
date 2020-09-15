package web.task;

import api.MessageService;
import model.Chats;
import web.handler.SocketHandler;

import java.util.List;

/**
 * @Author dingwq
 * @Date 2020/9/15 9:45
 * @Description
 */
public class GetOfflineMsgTask implements Runnable {

    private String userId;
    private Object bean;

    public GetOfflineMsgTask(Object bean, String userId) {
        this.userId = userId;
        this.bean = bean;
    }

    @Override
    public void run() {
        List<Chats> chatsList = ((MessageService) bean).getOfflineMsg(userId);
        for (Chats model : chatsList) {
            SocketHandler.pool.execute(new Thread(new SendMsgTask(bean, model)));
        }
    }
}
