package web.interceptor;

import model.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import util.Const;
import exception.impl.NoLoginException;
import web.util.WebUtil;

import java.util.Map;

public class SocketInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        //这里有个坑，如果你前端调用的websocket地址使用的localhost，那么页面的请求地址也要是localhost，否则无法获取到session
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            //HttpSession httpSession = servletRequest.getServletRequest().getSession(false);
            if (servletRequest.getServletRequest().getSession(false) != null) {
                User user = WebUtil.getLoginUser();
                attributes.put(Const.LOGIN_USER_ID_CONST, user.getId());
                attributes.put(Const.LOGIN_USER_NAME_CONST, user.getUsername());
            }else{
                throw new NoLoginException("用户未登录");
            }
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }

}