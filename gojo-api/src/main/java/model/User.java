package model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private String id;
    private String username;
    private String password;
    private String headImg;
    @JSONField(deserialize = false)
    private String createTime;

    public void setCreateTime(Date createTime) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createTime);
        this.createTime = date;
    }

}
