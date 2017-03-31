package com.ch.web.interceptor;


import com.ch.frame.redis.RedisHelper;
import com.ch.frame.session.SessionManager;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登陆拦截器
 *
 * @Author ludynice
 * @date 2017/1/19 14:02
 * @Version V1.0
 */public class LoginInterceptor implements HandlerInterceptor {

    /**
     * Handler执行完成之后调用这个方法
     */
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception exc)
            throws Exception {

    }

    /**
     * Handler执行之后，ModelAndView返回之前调用这个方法
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
    }

    /**
     * Handler执行之前调用这个方法
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        /*String token = request.getParameter("access_token");
        if(SessionManager.getProject().validateToken(token)){
            //if(null != SessionManager.getProject().getSessionData(token)){
            if(SessionManager.getProject().existToken(token)){
                return true;
            }
        }*/
        //登录状态验证失败，直接返回
        //request.getRequestDispatcher("/member/validFailed").forward(request, response);

        return true;
        //return false;
    }
}