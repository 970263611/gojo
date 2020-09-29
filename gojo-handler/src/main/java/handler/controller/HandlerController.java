package handler.controller;

import api.MessageService;
import com.dahuaboke.model.ResultModel;
import handler.util.HandlerUtil;
import model.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.proxy.UndeclaredThrowableException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.GojoUtil;
import util.HuaDecode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * @Author dingwq
 * @Date 2020/9/22 9:24
 * @Description
 */
@RestController
@RequestMapping("/gojo/handler")
public class HandlerController {

    @Autowired
    private MessageService messageService;
    @Value("${websocket.servers}")
    private String websocket_servers;
    @Value("${web.servers}")
    private String web_servers;

    @RequestMapping("/checkUser")
    public ResultModel checkUser(@RequestBody Map map) {
        String username = (String) map.get("username");
        if (messageService.check(username) != null) {
            return ResultModel.fail("用户名重复");
        }
        return ResultModel.success();
    }

    @RequestMapping("/login")
    public ResultModel login(@RequestBody User user) throws HuaDecode.HuaDecodeException {
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            SecurityUtils.getSubject().getSession().stop();
            return ResultModel.fail("用户名或密码错误");
        } catch (UndeclaredThrowableException e) {
            SecurityUtils.getSubject().getSession().stop();
            return ResultModel.fail("服务器发生错误");
        }
        String sessionIdDecode = HuaDecode.decode(subject.getSession().getId().toString());
        return ResultModel.success((Object) sessionIdDecode);
    }

    @RequestMapping("/registry")
    public ResultModel registry(@RequestBody User user) {
        try {
            if (GojoUtil.isNotEmpty(user.getUsername()) && GojoUtil.isNotEmpty(user.getPassword())) {
                if (messageService.check(user.getUsername()) == null) {
                    user.setId(GojoUtil.getUUID());
                    user.setCreateTime(new Date());
                    messageService.saveUser(user);
                    SecurityUtils.getSubject().getSession().setAttribute("user", user);
                    SecurityUtils.getSubject().getSession().setTimeout(-1);
                } else {
                    return ResultModel.fail("用户名重复");
                }
            } else {
                return ResultModel.fail("创建用户资料不合法");
            }
        } catch (Exception e) {
            return ResultModel.fail("存储用户信息失败");
        }
        return ResultModel.success();
    }

    @RequestMapping("/getLoginUser")
    public User getLoginUser(HttpServletRequest request, HttpServletResponse response, @RequestBody Map map) throws HuaDecode.HuaDecodeException {
        String sessionId = HuaDecode.encode((String) map.get("sessionIdEncode"));
        WebSessionKey webSessionKey = new WebSessionKey(sessionId, request, response);
        try {
            Session session = HandlerUtil.getSessionByKey(webSessionKey);
            return (User) session.getAttribute("user");
        } catch (InvalidSessionException e) {
            return null;
        }
    }

    @RequestMapping("/getKey")
    public int getKey(String type) {
        return HandlerUtil.getKey(type);
    }

    @RequestMapping("/getWebSocketAddr")
    public String getWebSocketAddr() {
        String[] servers = websocket_servers.split(",");
        int key = getKey("webSocket");
        return servers[key % servers.length];
    }

    @RequestMapping("/getWebSocketAddrByUserId")
    public String getWebSocketAddrByUserId(@RequestBody Map map) {
        return HandlerUtil.getWebSocketAddrByUserId((String) map.get("userId"));
    }

    @RequestMapping("/setWebSocketAddrByUserId")
    public void setWebSocketAddrByUserId(@RequestBody Map map) {
        HandlerUtil.setWebSocketAddrByUserId((String) map.get("userId"), (String) map.get("addr"));
    }

    @RequestMapping("/removeWebSocketAddrByUserId")
    public void removeWebSocketAddrByUserId(HttpServletRequest request, HttpServletResponse response, @RequestBody Map map) throws HuaDecode.HuaDecodeException {
        HandlerUtil.removeWebSocketAddrByUserId((String) map.get("userId"));
        removeSession(request, response, map);
    }

    @RequestMapping("/getWebAddr")
    public String getWebAddr() {
        String[] servers = web_servers.split(",");
        int key = getKey("web");
        return servers[key % servers.length];
    }

    @RequestMapping("/removeSession")
    public void removeSession(HttpServletRequest request, HttpServletResponse response, @RequestBody Map map) throws HuaDecode.HuaDecodeException {
        String sessionId = HuaDecode.encode((String) map.get("sessionIdEncode"));
        WebSessionKey webSessionKey = new WebSessionKey(sessionId, request, response);
        try {
            Session session = HandlerUtil.getSessionByKey(webSessionKey);
            session.stop();
        } catch (InvalidSessionException e) {

        }
    }

    @RequestMapping("/checkToken")
    public boolean checkToken(HttpServletRequest request, HttpServletResponse response, @RequestBody Map map) throws HuaDecode.HuaDecodeException {
        String sessionId = HuaDecode.encode((String) map.get("token"));
        WebSessionKey webSessionKey = new WebSessionKey(sessionId, request, response);
        try {
            Session session = HandlerUtil.getSessionByKey(webSessionKey);
            if (session != null) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
