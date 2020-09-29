package web.task;

import model.Chats;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import util.HttpClientUtil;

/**
 * @Author dingwq
 * @Date 2020/9/22 10:43
 * @Description
 */
public class SaveMsgTask implements Runnable {

    private Chats model;
    private String handlerAddress;
    private String sessionIdEncode;

    public SaveMsgTask(Chats model, String handlerAddress, String sessionIdEncode) {
        this.model = model;
        this.handlerAddress = handlerAddress;
        this.sessionIdEncode = sessionIdEncode;
    }

    @Override
    public void run() {
        String addr = (String) HttpClientUtil.httpClient("http://" + handlerAddress + "/gojo/handler/getWebAddr", null, String.class);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", sessionIdEncode);
        HttpEntity<Chats> requestEntity = new HttpEntity<>(model, requestHeaders);
        HttpClientUtil.httpClientExchange("http://" + addr + "/gojo/web/saveMsg", HttpMethod.POST, requestEntity, String.class);
    }
}
