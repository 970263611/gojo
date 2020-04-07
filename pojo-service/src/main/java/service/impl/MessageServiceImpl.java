package service.impl;

import api.MessageService;
import com.dahuaboke.rpc.annotation.RpcService;
import model.User;
import model.UserToUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import service.dao.MessageDao;

@RpcService
@Transactional(rollbackFor=Exception.class)
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageDao dao;

    public void saveMsg(UserToUser model) {
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
}
