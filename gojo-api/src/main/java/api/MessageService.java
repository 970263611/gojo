package api;

import model.Chats;
import model.Friend;
import model.Group;
import model.User;

import java.util.List;

public interface MessageService {

    void saveMsg(Chats model);

    User check(String username);

    void saveUser(User user) throws Exception;

    List<Friend> getUserFriends(String userId);

    List<Group> getUserGroups(String userId);

    List<Chats> getOfflineMsg(String userId);
}
