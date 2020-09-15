package web.controller;

import api.MessageService;
import com.dahuaboke.model.ResultModel;
import exception.impl.NoLoginException;
import lombok.extern.slf4j.Slf4j;
import model.Friend;
import model.Group;
import model.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.UndeclaredThrowableException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.GojoUtil;
import web.task.GetOfflineMsgTask;
import web.util.WebUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/gojo")
public class WebController {

    @Autowired
    private MessageService messageService;

    @RequestMapping("/login")
    public ResultModel login(@RequestBody User user) {
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
        return ResultModel.success();
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

    @RequestMapping("/checkUser")
    public ResultModel checkUser(@RequestBody Map map) {
        String username = (String) map.get("username");
        if (messageService.check(username) != null) {
            return ResultModel.fail("用户名重复");
        }
        return ResultModel.success();
    }

    @RequestMapping("/getUserFriends")
    public ResultModel getUserFriends() {
        try {
            User user = WebUtil.getLoginUser();
            List<Friend> users = messageService.getUserFriends(user.getId());
            return ResultModel.success(users);
        } catch (NoLoginException e) {
            e.printStackTrace();
        }
        return ResultModel.fail();
    }

    @RequestMapping("/getUserGroups")
    public ResultModel getUserGroups() {
        List<Group> list = new ArrayList<Group>();
        for (int a = 0; a < 9; a++) {
            List list1 = new ArrayList();
            Friend friend = new Friend(a + "g", "dahua" + a, true, "2020-01-0" + a);
            list1.add(friend);
            Group group = new Group(a + "", "dahuagroup" + a, list1, "2020-01-0" + a);
            list.add(group);
        }
        return ResultModel.success(list);
    }

    @RequestMapping("/getOfflineMsg")
    public void getOfflineMsg() {
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        new Thread(new GetOfflineMsgTask(messageService, user.getId())).start();
    }
}
