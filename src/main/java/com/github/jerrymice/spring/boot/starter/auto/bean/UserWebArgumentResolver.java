package com.github.jerrymice.spring.boot.starter.auto.bean;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author tumingjian
 *         SpringMVC参数解析拦截器
 *         返回当前用户信息
 */
public class UserWebArgumentResolver implements WebArgumentResolver {
    /**
     * 当前用户在session中的key值
     */
    private String userSessionKey;
    /**
     * 是否缓存用户对象的class类型,如果存在多种不同类型的用户登录同一系统,那么建议设置为false
     */
    private boolean enableCacheUserClass;

    public UserWebArgumentResolver(String userSessionKey) {
        this(userSessionKey, false);
    }

    public UserWebArgumentResolver(String userSessionKey, boolean enableCacheUserClass) {
        this.userSessionKey = userSessionKey;
        this.enableCacheUserClass = enableCacheUserClass;
    }

    /**
     * 缓存当前用户类
     */
    private static Class<?> cacheCurrentUserClass;

    @Override
    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
        if (cacheCurrentUserClass != null || !enableCacheUserClass) {
            if (methodParameter.getParameterType() != null && cacheCurrentUserClass.isAssignableFrom(methodParameter.getParameterType())) {
                return getCurrentUser(webRequest);
            }
        } else {
            Object currentUser = getCurrentUser(webRequest);
            if (currentUser != null) {
                if (enableCacheUserClass) {
                    cacheCurrentUserClass = currentUser.getClass();
                }
                if (methodParameter.getParameterType() != null && cacheCurrentUserClass.isAssignableFrom(methodParameter.getParameterType())) {
                    return currentUser;
                }
            }
        }
        return UNRESOLVED;
    }

    /**
     * 获取当前用户.
     * @param webRequest
     * @return
     */
    private Object getCurrentUser(NativeWebRequest webRequest) {
        HttpSession session = webRequest.getNativeRequest(HttpServletRequest.class).getSession();
        return session == null ? null : session.getAttribute(userSessionKey);
    }

}
