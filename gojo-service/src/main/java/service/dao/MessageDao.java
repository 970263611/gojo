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

    @Select("SELECT a.id userId,a.username,true,a.createTime FROM `user` a LEFT JOIN friend b on b.friendId = a.id WHERE b.userId = #{0}")
    List<Friend> getUserFriends(String userId);

    @Select("select * from group where userId = #{0}")
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
    void deleteOfflineMsg(@Param("ids")List<String> ids);

    @Delete("delete from chats where id = #{id}")
    int deleteOfflineMsg1(@Param("id")String id);
}
