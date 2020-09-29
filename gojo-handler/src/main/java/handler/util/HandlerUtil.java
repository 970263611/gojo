package handler.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionKey;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author dingwq
 * @Date 2020/9/22 9:30
 * @Description
 */
@Slf4j
public class HandlerUtil {

    private volatile static int webSocketKey = 0;
    private volatile static int webKey = 0;
    private volatile static ConcurrentHashMap map = new ConcurrentHashMap();

    public synchronized static int getKey(String type) {
        if ("webSocket".equals(type)) {
            return webSocketKey++;
        }
        if ("web".equals(type)) {
            return webKey++;
        }
        return 0;
    }

    public static Session getSessionByKey(SessionKey sessionKey) {
        return SecurityUtils.getSecurityManager().getSession(sessionKey);
    }

    public static String getWebSocketAddrByUserId(String userId) {
        log.info("获取用户id -> " + userId + "。地址 -> " + map.get(userId));
        return (String) map.get(userId);
    }

    public static void setWebSocketAddrByUserId(String userId, String addr) {
        map.put(userId, addr);
        log.info("添加用户id -> " + userId + "。地址 -> " + addr);
    }

    public static void removeWebSocketAddrByUserId(String userId) {
        map.remove(userId);
        log.info("移除用户id -> " + userId);
    }
}
