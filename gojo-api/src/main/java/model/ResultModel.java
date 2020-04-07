package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultModel {

    private boolean flag;
    private Object obj;
    private String msg;

    public ResultModel(boolean flag) {
        this.flag = flag;
    }
}
