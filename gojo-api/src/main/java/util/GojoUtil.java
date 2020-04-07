package util;

import java.util.UUID;

public class GojoUtil {

    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    public static boolean isNotEmpty(String str){
        if(str == null || "".equals(str)){
            return false;
        }
        return true;
    }
}
