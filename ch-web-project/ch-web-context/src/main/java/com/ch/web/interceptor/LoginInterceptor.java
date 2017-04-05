package com.ch.web.interceptor;


import com.ch.frame.redis.RedisHelper;
import com.ch.frame.session.SessionManager;
import org.hibernate.Session;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录状态验证       拦截器
 *
 * @Author ludynice
 * @date 2017/1/19 14:02
 * @Version V1.0
 */
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * Handler执行完成之后调用这个方法
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exc) throws Exception {

    }

    /**
     * Handler执行之后，ModelAndView返回之前调用这个方法
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    /**
     * Handler执行之前调用这个方法
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String context = request.getContextPath();
        String uri = request.getRequestURI();
        String requestMethod = request.getMethod();
        if (context.equals("/chapp")) {
            // 登录，注册，注销的请求直接放行
            if (uri.indexOf("user/token") > 0 || uri.indexOf("regist") > 0 || uri.indexOf("logout") > 0 || uri.indexOf("helloworld") > 0 || uri.indexOf("user/validFailed") > 0) {
                return true;
            }
            //注册
            if (uri.indexOf("user") > 0 && requestMethod.equals("POST")) {
                return true;
            }
            //request.getRequestDispatcher("/member/validFailed").forward(request, response);
            String token = request.getParameter("token");
            //如果token已存在与缓存中，说明已经是登录状态
            if (SessionManager.get().exists(SessionManager.get().TOKENCACHEGROUP, token)) {
                return true;
            }
            request.getRequestDispatcher("/user/validFailed").forward(request, response);
        }
        return false;
    }
}