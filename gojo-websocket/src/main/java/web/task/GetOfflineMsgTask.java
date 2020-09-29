package web.task;

import model.Chats;
import web.handler.SocketHandler;

import java.util.List;

/**
 * @Author dingwq
 * @Date 2020/9/15 9:45
 * @Description
 */
public class GetOfflineMsgTask implements Runnable {

    private List<Chats> chatsList;
    private String handlerAddress;
    private String sessionIdEncode;

    public GetOfflineMsgTask(List<Chats> chatsList, String handlerAddress, String sessionIdEncode) {
        this.chatsList = chatsList;
        this.handlerAddress = handlerAddress;
        this.sessionIdEncode = sessionIdEncode;
    }

    @Override
    public void run() {
        for (Chats model : chatsList) {
            SocketHandler.pool.execute(new Thread(new SendMsgTask(model, handlerAddress, sessionIdEncode)));
        }
    }
}
