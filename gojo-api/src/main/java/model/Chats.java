package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

//用户对用户实体类

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chats implements Serializable {

    //id
    private String id;
    //发送用户id
    private String fromUserId;
    //发送用户名字
    private String fromUserName;
    //接收用户id
    private String toUserId;
    //接收用户名字
    private String toUserName;
    //创建时间
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private String createTime;
    //消息体
    private String msg;
    //消息记录是否本人发送
    private boolean flag;
    //消息类型  个人or群组
    private String type;

    public void setCreateTime(Date createTime) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createTime);
        this.createTime = date;
    }
}
