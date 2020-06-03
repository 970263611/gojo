package service.impl;

import api.MessageService;
import com.dahuaboke.rpc.annotation.RpcService;
import model.Chats;
import model.Friend;
import model.Group;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import service.dao.MessageDao;

import java.util.List;

@RpcService
@Transactional(rollbackFor=Exception.class)
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
        if(dao.saveUser(user) != 1){
            throw new Exception();
        }
    }

    public List<Friend> getUserFriends(String userId) {
        return dao.getUserFriends(userId);
    }

    public List<Group> getUserGroups(String userId) {
        return dao.getUserGroups(userId);
    }
}