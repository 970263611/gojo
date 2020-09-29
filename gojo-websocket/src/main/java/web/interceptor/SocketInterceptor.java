package web.interceptor;

import com.alibaba.fastjson.JSON;
import model.User;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import util.Const;
import util.GojoUtil;
import util.HttpClientUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class SocketInterceptor extends HttpSessionHandshakeInterceptor {

    private String handlerAddress;
    private String sessionIdEncode;

    public SocketInterceptor(String handlerAddress) {
        this.handlerAddress = handlerAddress;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        //这里有个坑，如果你前端调用的websocket地址使用的localhost，那么页面的请求地址也要是localhost，否则无法获取到session
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            //HttpSession httpSession = servletRequest.getServletRequest().getSession(false);
//            if (servletRequest.getServletRequest().getSession(false) != null) {
//                User user = WebUtil.getLoginUser();
//                String sessionId = servletRequest.getServletRequest().getSession(false).getId();
//                attributes.put(Const.LOGIN_USER_ID_CONST, user.getId());
//                attributes.put(Const.LOGIN_USER_NAME_CONST, user.getUsername());
//            }else{
////                throw new NoLoginException("用户未登录");
//            }
            /**
             * 升级协议之后必须要返回给前端，否则链接不成功，这是个大坑
             */
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            sessionIdEncode = httpRequest.getHeader("sec-websocket-protocol");
            if (GojoUtil.isNotEmpty(sessionIdEncode)) {
                try {
                    String userStr = (String) HttpClientUtil.httpClient("http://" + handlerAddress + "/gojo/handler/getLoginUser", new HashMap<String, String>() {{
                        put("sessionIdEncode", sessionIdEncode);
                    }}, String.class);
                    User user = JSON.parseObject(userStr, User.class);
                    attributes.put(Const.LOGIN_USER_ID_CONST, user.getId());
                    attributes.put(Const.LOGIN_USER_NAME_CONST, user.getUsername());
                    attributes.put(Const.LOGIN_USER_SESSION_CONST, sessionIdEncode);
                } catch (Exception e) {
                    return false;
                }
            }
            /**
             * 升级协议之后必须要返回给前端，否则链接不成功，这是个大坑
             */
            if (response instanceof ServletServerHttpResponse) {
                ServletServerHttpResponse servletResponse = (ServletServerHttpResponse) response;
                HttpServletResponse httpResponse = servletResponse.getServletResponse();
                httpResponse.setHeader("sec-websocket-protocol", sessionIdEncode);
//            String sessionIdEncode = httpResponse.getHeader("sec-websocket-protocol");
//            System.out.println(sessionIdEncode);
            }
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
//        /**
//         * 升级协议之后必须要返回给前端，否则链接不成功，这是个大坑
//         */
//        if (response instanceof ServletServerHttpResponse) {
//            ServletServerHttpResponse servletResponse = (ServletServerHttpResponse) response;
//            HttpServletResponse httpResponse = servletResponse.getServletResponse();
//            httpResponse.setHeader("sec-websocket-protocol", sessionIdEncode);
////            String sessionIdEncode = httpResponse.getHeader("sec-websocket-protocol");
////            System.out.println(sessionIdEncode);
//        }
        super.afterHandshake(request, response, wsHandler, ex);
    }

}