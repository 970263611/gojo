package web.controller;

import api.MessageService;
import exception.impl.NoLoginException;
import lombok.extern.slf4j.Slf4j;
import model.Friend;
import model.Group;
import model.ResultModel;
import model.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.GojoUtil;
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
        ResultModel result = new ResultModel();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        Subject subject = SecurityUtils.getSubject();
        boolean flag = false;
        try {
            subject.login(token);
            subject.getSession().setAttribute("user", messageService.check(user.getUsername()));
            flag = true;
        } catch (Exception e) {
            result.setMsg("用户名或密码错误");
        }
        result.setFlag(flag);
        return result;
    }

    @RequestMapping("/registry")
    public ResultModel registry(@RequestBody User user) {
        ResultModel result = new ResultModel();
        boolean flag = false;
        try {
            if (GojoUtil.isNotEmpty(user.getUsername()) && GojoUtil.isNotEmpty(user.getPassword())) {
                if (messageService.check(user.getUsername()) == null) {
                    user.setId(GojoUtil.getUUID());
                    user.setCreateTime(new Date());
                    messageService.saveUser(user);
                    SecurityUtils.getSubject().getSession().setAttribute("user", user);
                    SecurityUtils.getSubject().getSession().setTimeout(-1);
                    flag = true;
                } else {
                    result.setMsg("用户名重复");
                }
            } else {
                result.setMsg("创建用户资料不合法");
            }
        } catch (Exception e) {
            result.setMsg("存储用户信息失败");
        }
        result.setFlag(flag);
        return result;
    }

    @RequestMapping("/checkUser")
    public ResultModel checkUser(@RequestBody Map map) {
        String username = (String) map.get("username");
        ResultModel result = new ResultModel();
        boolean flag = true;
        if (messageService.check(username) != null) {
            result.setMsg("用户名重复");
            flag = false;
        }
        result.setFlag(flag);
        return result;
    }

    @RequestMapping("getUserFriends")
    public ResultModel getUserFriends() {
        try {
            User user = WebUtil.getLoginUser();
            List<Friend> users = messageService.getUserFriends(user.getId());
            return new ResultModel(true, users, null);
        } catch (NoLoginException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("getUserGroups")
    public ResultModel getUserGroups(@RequestBody Map map) {
        List<Group> list = new ArrayList<Group>();
        for (int a = 0; a < 10; a++) {
            List list1 = new ArrayList();
            Friend friend = new Friend(a + "g", "dahua" + a, true, "2020-01-0" + a);
            list1.add(friend);
            Group group = new Group(a + "", "dahuagroup" + a, list1, "2020-01-0" + a);
            list.add(group);
        }
        return new ResultModel(true, list, null);
    }
}
