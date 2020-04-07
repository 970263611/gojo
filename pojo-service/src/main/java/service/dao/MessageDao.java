package service.dao;

import model.User;
import model.UserToUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MessageDao {

    @Insert("insert into user_to_user values (#{model.id},#{model.fromUserId},#{model.toUserId},#{model.toUserName},#{model.createTime},#{model.msg})")
    void saveMsg(@Param("model") UserToUser model);

    @Select("select * from user where username = #{username}")
    User check(@Param("username") String username);

    @Insert("insert into user value (#{user.id},#{user.username},#{user.password},#{user.createTime})")
    int saveUser(@Param("user")User user);
}
