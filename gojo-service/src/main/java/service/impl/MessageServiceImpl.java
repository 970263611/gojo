package service.impl;

import api.MessageService;
import com.alibaba.fastjson.JSON;
import com.dahuaboke.rpc.annotation.RpcService;
import model.Chats;
import model.Friend;
import model.Group;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import service.dao.MessageDao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RpcService
@Transactional(rollbackFor = Exception.class)
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageDao dao;

    public void saveMsg(Chats model) {
        dao.saveMsg(model);
    }

    public User check(String username) {
        return dao.check(username);
    }

    public void saveUser(User user) throws Exception {
        if (dao.saveUser(user) != 1) {
            throw new Exception();
        }
    }

    public List<User> getUserFriends(String userId) {
        return dao.getUserFriends(userId);
    }

    public List<Group> getUserGroups(String userId) {
        userId = "[\"" + userId + "\"]";
        return dao.getUserGroups(userId);
    }

    public List<Chats> getOfflineMsg(String userId) {
        List<Chats> offlineMsg = dao.getOfflineMsg(userId);
//        List<String> ids = new ArrayList<>();
//        offlineMsg.forEach(o -> ids.add(o.getId()));
//        if(!ids.isEmpty()){
//            dao.deleteOfflineMsg(ids);
//        }
        offlineMsg.forEach(o -> dao.deleteOfflineMsg1(o.getId()));
        return offlineMsg;
    }

    @Override
    public List<User> getGroupUsers(String groupId) {
        return dao.getGroupUsers(groupId);
    }

    @Override
    public void createGroup(Group group) {
        dao.createGroup(group);
    }

    @Override
    public void updateGroupUsers(String groupId, List<String> userIds) {
        dao.updateGroupUsers(groupId, JSON.toJSONString(userIds));
    }

    @Override
    public String getNotFriendAndGroup(String id) {
        Map map = new HashMap() {{
            put("users", dao.getNotFriend(id));
            put("groups", dao.getNotGroup("[\"" + id + "\"]"));
        }};
        return JSON.toJSONString(map);
    }

    @Override
    public void addFriend(String userId, String friendId) {
        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setFriendId(friendId);
        friend.setFriend(true);
        friend.setCreateTime(new Date());
        dao.addFriend(friend);
    }

    @Override
    public void addGroup(String userId, String groupId) {
        dao.addGroup(userId, groupId);
    }

    @Override
    public void updateUser(User user) {
        dao.updateUser(user);
    }

    @Override
    public User getMy(String id) {
        return dao.getMy(id);
    }


}
