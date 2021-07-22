package cn.easyutil.easyapi.util;


import cn.easyutil.easyapi.entity.auth.User;
import cn.easyutil.easyapi.interview.entity.SessionUser;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 请求临时对象缓存池
 */
public class RequestPool{

    private static final String session_user = "session_user";

    private static HttpSession getHttpSession(){
        return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest().getSession();
    }

    public static void setSessionUser(SessionUser user){
        getHttpSession().setAttribute(session_user,user);
    }

    private static SessionUser getSessionUser(){
        Object sessionUser = getHttpSession().getAttribute(session_user);
        if(sessionUser == null){
            return null;
        }
        return (SessionUser) sessionUser;
    }

    public static User getUser(){
        SessionUser user = getSessionUser();
        if(user == null){
            return null;
        }
        return user.getUser();
    }

    public static List<Long> getProjects(){
        SessionUser user = getSessionUser();
        if(user == null){
            return Collections.EMPTY_LIST;
        }
        return user.getProjects().stream().map(s -> s.getProjectId()).collect(Collectors.toList());
    }

    public static List<Integer> getUserAuths(){
        SessionUser user = getSessionUser();
        if(user == null){
            return Collections.EMPTY_LIST;
        }
        return user.getAuths().stream().map(s -> s.getAuthCode()).collect(Collectors.toList());
    }

    public static boolean isLogin(){
        SessionUser user = getSessionUser();
        if(user == null){
            return false;
        }
        return true;
    }

    public static boolean isAdmin(){
        SessionUser user = getSessionUser();
        if(user == null){
            return false;
        }
        return user.getUser().getSuperAdmin() == 1;
    }
}
