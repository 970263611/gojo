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

    List<User> getUserFriends(String userId);

    List<Group> getUserGroups(String userId);

    List<Chats> getOfflineMsg(String userId);

    List<User> getGroupUsers(String groupId);

    void createGroup(Group group);

    void updateGroupUsers(String groupId, List<String> userIds);

    String getNotFriendAndGroup(String id);

    void addFriend(String userId, String id);

    void addGroup(String userId, String id);

    void updateUser(User user);

    User getMy(String id);
}
