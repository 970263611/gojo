package web.controller;

import api.MessageService;
import com.alibaba.fastjson.JSON;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.proxy.UndeclaredThrowableException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.GojoUtil;
import web.task.GetOfflineMsgTask;
import web.util.WebUtil;

import java.io.File;
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

    @Value("${img.local.dir}")
    private String imgLocalDir;

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
            List<User> users = messageService.getUserFriends(user.getId());
            return ResultModel.success(users);
        } catch (NoLoginException e) {
            e.printStackTrace();
        }
        return ResultModel.fail();
    }

    @RequestMapping("/getUserGroups")
    public ResultModel getUserGroups() {
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        List<Group> list = messageService.getUserGroups(user.getId());
        list.forEach(l -> {
            if (l.getCreateUser().equals(user.getId())) {
                l.setAuthor(true);
            }
        });
        return ResultModel.success(list);
    }

    @RequestMapping("/getGroupUsers")
    public ResultModel getGroupUsers(@RequestBody Map map) {
        List<Friend> users = messageService.getGroupUsers((String) map.get("groupId"));
        return ResultModel.success(users);
    }

    @RequestMapping("/createGroup")
    public ResultModel createGroup(@RequestBody Map map) {
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        Group group = new Group();
        group.setCreateUser(user.getId());
        group.setCreateTime(new Date());
        group.setGroupId(GojoUtil.getUUID());
        group.setGroupName((String) map.get("groupName"));
        group.setNotice((String) map.get("notice"));
        group.setRemark((String) map.get("remark"));
        group.setHeadImg((String) map.get("headImg"));
        group.setUsers(JSON.toJSONString(new ArrayList<String>() {{
            add(user.getId());
        }}));
        messageService.createGroup(group);
        return ResultModel.success();
    }

    @RequestMapping("/getOfflineMsg")
    public void getOfflineMsg() {
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        new Thread(new GetOfflineMsgTask(messageService, user.getId())).start();
    }

    @RequestMapping("/updateGroupUsers")
    public void updateGroupUsers(@RequestBody Map map) {
        String groupId = (String) map.get("groupId");
        List<String> userIds = (List<String>) map.get("userIds");
        messageService.updateGroupUsers(groupId, userIds);
    }

    @RequestMapping("/imgUploadUrl")
    public ResultModel imgUploadUrl(MultipartFile file) {
        if (!imgLocalDir.endsWith("/")) {
            imgLocalDir += "/";
        }
        try {
            File localFile = new File(imgLocalDir + GojoUtil.getUUID() + "." + file.getOriginalFilename().split("\\.")[1]);
            file.transferTo(localFile);
            return ResultModel.success(localFile.getCanonicalPath());
        } catch (Exception e) {
            return ResultModel.fail();
        }
    }

    @RequestMapping("/getNotFriendAndGroup")
    public ResultModel getNotFriendAndGroup() {
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        Map map = JSON.parseObject(messageService.getNotFriendAndGroup(user.getId()));
        return ResultModel.success(map);
    }

    @RequestMapping("/addFriend")
    public ResultModel addFriend(@RequestBody Map map) {
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        messageService.addFriend(user.getId(), (String) map.get("id"));
        return ResultModel.success();
    }

    @RequestMapping("/addGroup")
    public ResultModel addGroup(@RequestBody Map map) {
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        messageService.addGroup(user.getId(),(String) map.get("id"));
        return ResultModel.success();
    }
}
