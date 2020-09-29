package web.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import util.GojoUtil;
import util.HttpClientUtil;
import web.request.WebHttpRequest;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * @Author dingwq
 * @Date 2020/9/22 15:00
 * @Description
 */
@Component
@WebFilter(filterName = "authorFilter", urlPatterns = "/*")
public class AuthorFilter implements Filter {

    @Value("${handlerAddress}")
    private String handlerAddress;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String sessionIdEncode = request.getHeader("Authorization");
        if (GojoUtil.isNotEmpty(sessionIdEncode)) {
            String user = (String) HttpClientUtil.httpClient("http://" + handlerAddress + "/gojo/handler/getLoginUser", new HashMap<String, String>() {{
                put("sessionIdEncode", sessionIdEncode);
            }}, String.class);
            if (GojoUtil.isNotEmpty(user)) {
                WebHttpRequest webRequest = new WebHttpRequest(request);
                webRequest.setParameter("loginUser", user);
                webRequest.setParameter("sessionIdEncode", sessionIdEncode);
                //注意这里传的是自定义的request
                filterChain.doFilter(webRequest, response);
            } else {
                return;
            }
        } else {
            return;
        }
    }
}
