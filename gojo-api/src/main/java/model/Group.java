package model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group implements Serializable {

    private String groupId;
    private String groupName;
    private String users;
    private String createUser;
    private String createTime;
    private String notice;
    private String remark;
    private boolean author = false;
    private String headImg;

    public void setCreateTime(Date createTime) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createTime);
        this.createTime = date;
    }
}
