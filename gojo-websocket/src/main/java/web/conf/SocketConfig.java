package web.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import web.handler.SocketHandler;
import web.interceptor.SocketInterceptor;

@Configuration
@EnableWebSocket
public class SocketConfig implements WebSocketConfigurer {

    @Value("${handlerAddress}")
    private String handlerAddress;
    @Value("${server.port}")
    private String port;
    @Value("${websocket.ip}")
    private String websocketIp;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/gojo/websocket").setAllowedOrigins("*").addInterceptors(new SocketInterceptor(handlerAddress));//.setAllowedOrigins(allowsOrigins); // 此处与客户端的 URL 相对应
        registry.addHandler(webSocketHandler(), "/gojo/websocket").setAllowedOrigins("*").addInterceptors(new SocketInterceptor(handlerAddress)).withSockJS();//addInterceptors将拦截器添加进来
    }

    @Bean
    public SocketHandler webSocketHandler() {
        return new SocketHandler(handlerAddress, websocketIp + ":" + port);
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
//        container.setMaxSessionIdleTimeout((long) -1);
        return container;
    }

}