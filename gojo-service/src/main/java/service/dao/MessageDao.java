package service.dao;

import model.Chats;
import model.Friend;
import model.Group;
import model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageDao {

    @Insert("insert into chats values (#{model.id},#{model.fromUserId},#{model.fromUserName},#{model.toUserId},#{model.toUserName},#{model.createTime},#{model.msg},#{model.flag},#{model.type})")
    void saveMsg(@Param("model") Chats model);

    @Select("select * from user where username = #{0}")
    User check(String username);

    @Insert("insert into user value (#{user.id},#{user.username},#{user.password},#{user.createTime})")
    int saveUser(@Param("user") User user);

    @Select("select u.id,u.username,u.headImg from `user` u right JOIN friend f on f.friendId = u.id WHERE f.userId = #{0}")
    List<User> getUserFriends(String userId);

    @Select("select * from `group` where JSON_CONTAINS(users, #{0}) > 0")
    List<Group> getUserGroups(String userId);

    @Select("select * from chats where toUserId = #{0} order by createTime asc")
    List<Chats> getOfflineMsg(String userId);

    @Delete("<script>" +
            "delete from chats where id in " +
            "<foreach open='(' close=')' seperator=',' item='item' collection='ids'>" +
            "#{item}" +
            "</foreach>" +
            "</script>"
    )
    void deleteOfflineMsg(@Param("ids") List<String> ids);

    @Delete("delete from chats where id = #{id}")
    int deleteOfflineMsg1(@Param("id") String id);

    //    @Select("select u.id userId,u.username username,g.friend from `user` u right join " +
//            "(select users->'$[0].userId' userId,users->'$[0].friend' friend from `group` " +
//            "where groupId = #{0}) g on u.id = g.userId")
    @Select("select id id,username username from `user` where JSON_CONTAINS((select users from `group` where groupId = #{0}), CONCAT('[\"',id,'\"]') ) > 0")
    List<User> getGroupUsers(String groupId);

    @Insert("insert into `group` values (#{group.groupId},#{group.groupName},#{group.users},#{group.createUser},#{group.createTime},#{group.notice},#{group.remark},#{group.headImg})")
    void createGroup(@Param("group") Group group);

    @Update("update `group` set users = #{userIds} where groupId = #{groupId}")
    void updateGroupUsers(@Param("groupId") String groupId, @Param("userIds") String userIds);

    @Select("SELECT id,username from `user` where id not in (SELECT friendId FROM friend where userId = #{0} and isFriend = 1) and id != #{0}")
    List<User> getNotFriend(String id);

    @Select("select groupId,groupName from `group` where JSON_CONTAINS(users, #{0}) = 0")
    List<Group> getNotGroup(String id);

    @Update("update `group` set users = (select JSON_MERGE(users,CONCAT('[\"',#{userId},'\"]'))) where groupId = #{groupId}")
    void addGroup(@Param("userId") String userId, @Param("groupId") String groupId);

    @Insert("insert into friend values (#{friend.userId},#{friend.friendId},#{friend.isFriend},#{friend.createTime})")
    void addFriend(@Param("friend") Friend friend);

    @Update("update `user` set username = #{user.username},password = #{user.password},headImg = #{user.headImg} where id = #{user.id}")
    void updateUser(@Param("user")User user);

    @Select("select id,username,headImg from `user` where id = #{0}")
    User getMy(String id);
}
