package web.util;

import exception.impl.NoLoginException;
import model.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

public class WebUtil {

    public static User getLoginUser() throws NoLoginException {
        Session session = SecurityUtils.getSubject().getSession();
        if (session != null && session.getAttribute("user") != null) {
            return  (User) session.getAttribute("user");
        }
        throw new NoLoginException("用户未登录");
    }
}
