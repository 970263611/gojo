package web.controller;

import api.MessageService;
import com.alibaba.fastjson.JSON;
import com.dahuaboke.model.ResultModel;
import lombok.extern.slf4j.Slf4j;
import model.Chats;
import model.Group;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.GojoUtil;
import util.HttpClientUtil;

import java.io.File;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/gojo/web")
public class WebController {

    @Autowired
    private MessageService messageService;
    @Value("${handlerAddress}")
    private String handlerAddress;

    @Value("${img.local.dir}")
    private String imgLocalDir;

    @RequestMapping("/getUserFriends")
    public ResultModel getUserFriends(String loginUser) {
        try {
            User user = JSON.parseObject(loginUser, User.class);
            List<User> users = messageService.getUserFriends(user.getId());
            return ResultModel.success(users);
        } catch (Exception e) {
            return ResultModel.fail("000000");
        }
    }

    @RequestMapping("/getUserGroups")
    public ResultModel getUserGroups(String loginUser) {
        try {
            User user = JSON.parseObject(loginUser, User.class);
            List<Group> list = messageService.getUserGroups(user.getId());
            list.forEach(l -> {
                if (l.getCreateUser().equals(user.getId())) {
                    l.setAuthor(true);
                }
            });
            return ResultModel.success(list);
        } catch (Exception e) {
            return ResultModel.fail("000000");
        }
    }

    @RequestMapping("/getGroupUsers")
    public ResultModel getGroupUsers(@RequestBody Map map) {
        List<User> users = messageService.getGroupUsers((String) map.get("groupId"));
        return ResultModel.success(users);
    }

    @RequestMapping("/createGroup")
    public ResultModel createGroup(@RequestBody Map map, String loginUser) {
        User user = JSON.parseObject(loginUser, User.class);
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
    public void getOfflineMsg(String loginUser, String sessionIdEncode) {
        User user = JSON.parseObject(loginUser, User.class);
        String addr = (String) HttpClientUtil.httpClient("http://" + handlerAddress + "/gojo/handler/getWebSocketAddr", null, String.class);
        List<Chats> offlineMsg = messageService.getOfflineMsg(user.getId());
        Map map = new HashMap() {{
            put("chatsList", offlineMsg);
            put("sessionIdEncode", sessionIdEncode);
        }};
        if (!offlineMsg.isEmpty()) {
            HttpClientUtil.httpClient("http://" + addr + "/gojo/websocket/sendOfflineMsg", map, String.class);
        }
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
    public ResultModel getNotFriendAndGroup(String loginUser) {
        User user = JSON.parseObject(loginUser, User.class);
        Map resultMap = JSON.parseObject(messageService.getNotFriendAndGroup(user.getId()));
        return ResultModel.success(resultMap);
    }

    @RequestMapping("/addFriend")
    public ResultModel addFriend(@RequestBody Map map, String loginUser) {
        User user = JSON.parseObject(loginUser, User.class);
        messageService.addFriend(user.getId(), (String) map.get("id"));
        return ResultModel.success();
    }

    @RequestMapping("/addGroup")
    public ResultModel addGroup(@RequestBody Map map, String loginUser) {
        User user = JSON.parseObject(loginUser, User.class);
        messageService.addGroup(user.getId(), (String) map.get("id"));
        return ResultModel.success();
    }

    @RequestMapping("/getMy")
    public ResultModel getMy(String loginUser) {
        User user = JSON.parseObject(loginUser, User.class);
        User loginUserNow = messageService.getMy(user.getId());
        return ResultModel.success(loginUserNow);
    }

    @RequestMapping("/updateUser")
    public ResultModel updateUser(@RequestBody Map map, String loginUser) {
        User userSession = JSON.parseObject(loginUser, User.class);
        User user = new User();
        user.setUsername((String) map.get("username"));
        user.setPassword((String) map.get("password"));
        user.setHeadImg((String) map.get("headImg"));
        user.setId(userSession.getId());
        messageService.updateUser(user);
        return ResultModel.success();
    }

    @RequestMapping("/saveMsg")
    public void saveMsg(@RequestBody Chats model) {
        messageService.saveMsg(model);
    }
}
