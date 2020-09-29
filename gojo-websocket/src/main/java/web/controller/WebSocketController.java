package web.controller;

import com.alibaba.fastjson.JSON;
import model.Chats;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.handler.SocketHandler;
import web.task.BatchSendMsgTask;
import web.task.SendMsgTask;

import java.util.List;
import java.util.Map;

/**
 * @Author dingwq
 * @Date 2020/9/22 10:34
 * @Description
 */
@RestController
@RequestMapping("/gojo/websocket")
public class WebSocketController {

    @Value("${handlerAddress}")
    private String handlerAddress;

    @RequestMapping("/sendOfflineMsg")
    public void sendOfflineMsg(@RequestBody Map map) {
        List<Chats> chatsList = JSON.parseArray(JSON.toJSONString(map.get("chatsList")), Chats.class);
        String sessionIdEncode = (String) map.get("sessionIdEncode");
        SocketHandler.pool.execute(new Thread(new BatchSendMsgTask(chatsList, handlerAddress, sessionIdEncode)));
    }

    @RequestMapping("/sendMsg")
    public void sendMsg(@RequestBody Map map) {
        Chats model = JSON.parseObject(JSON.toJSONString(map.get("model")),Chats.class);
        String sessionIdEncode = (String) map.get("sessionIdEncode");
        SocketHandler.pool.execute(new Thread(new SendMsgTask(model, handlerAddress, sessionIdEncode)));
    }
}
