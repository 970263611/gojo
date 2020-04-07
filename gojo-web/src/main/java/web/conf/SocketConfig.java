package web.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import web.handler.SocketHandler;
import web.interceptor.SocketInterceptor;

@Configuration
@EnableWebSocket
public class SocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/gojo/websocket").setAllowedOrigins("*").addInterceptors(new SocketInterceptor());//.setAllowedOrigins(allowsOrigins); // 此处与客户端的 URL 相对应
        registry.addHandler(webSocketHandler(), "/gojo/websocket").setAllowedOrigins("*").addInterceptors(new SocketInterceptor()).withSockJS();//addInterceptors将拦截器添加进来
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new SocketHandler();
    }

}