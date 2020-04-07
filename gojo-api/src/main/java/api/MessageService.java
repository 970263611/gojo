package api;

import model.User;
import model.UserToUser;

public interface MessageService {

    void saveMsg(UserToUser model);

    User check(String username);

    void saveUser(User user) throws Exception;
}
