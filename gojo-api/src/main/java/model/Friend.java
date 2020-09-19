package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friend implements Serializable {

    private String userId;
    private String friendId;
    private boolean isFriend = false;
    private String createTime;

    public void setCreateTime(Date createTime) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createTime);
        this.createTime = date;
    }

    public Friend(String userId, boolean isFriend) {
        this.userId = userId;
        this.isFriend = isFriend;
    }
}
