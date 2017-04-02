package com.ch.web.action.trd.org;

import com.alibaba.fastjson.JSONObject;
import com.ch.frame.session.SessionManager;
import com.ch.model.trd.org.account.domain.User;
import com.ch.service.general.intel.QueryService;
import com.ch.service.trd.org.UserService;
import com.ch.web.base.Message;
import com.ch.web.base.ParentAction;
import com.ch.web.proxy.ServiceProxyFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ludynice on 2017/1/17.
 */
@Controller
@RequestMapping("/user")
//@RestController
public class UserAction extends ParentAction {

    @RequestMapping(value = "helloworld")
    @ResponseBody
    public Message test() {
        return Message.success("访问成功", "helloword1");
    }

    @RequestMapping(value = "testUserService")
    @ResponseBody
    public Message testUserService() {
        UserService userService = this.getProxy(UserService.class);

        return Message.success("访问成功", userService.foo());
    }

    //未登录，通知前端跳转到登录页
    @RequestMapping("validFailed")
    @ResponseBody
    public Message validFailed() {
        return Message.responseMsg(Message.DOLOGIN_CODE, Message.FAIL, Message.NORESP);
    }

    /**
     * 会员注册
     * 注册成功，前端控制跳转到登录页
     *
     * @param mobile
     * @param password
     * @return URL :  60.205.230.76:9099/chapp/user
     * 0代表成功
     * 100,客户端错误
     * 200,服务端错误
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Message regist(String mobile, String password) {
        if (StringUtils.isBlank(mobile)) {
            return Message.responseMsg(Message.FAIL_CODE, "手机号不能为空", "");
        }
        if (StringUtils.isBlank(password)) {
            return Message.responseMsg(Message.FAIL_CODE, "密码不能为空", "");
        }
        QueryService queryService = ServiceProxyFactory.get(QueryService.class);
        if (null != queryService.findFirst("from User a where a.mobile = ?", mobile)) {
            return Message.responseMsg(Message.FAIL_CODE, "手机号已被注册", "");
        }
        UserService userService = ServiceProxyFactory.get(UserService.class);
        try {
            userService.regist(mobile, password);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Message.responseMsg(Message.FAIL_CODE, Message.ERRO_RESP, "");
        }
        return Message.responseMsg(Message.SUCCESS_CODE,Message.SUCCESS,"");

       /* //注册成功，生成 access_token，返回给客户端
        if (null != user) {
            String token = "";
            JSONObject params = new JSONObject();
            params.put("username", mobile);
            params.put("password", user.getPassword());
            params.put("timestamp", timestamp);
            try {
                token = SessionManager.get().createToken(params);
            } catch (Exception e) {
                log.error(e.getMessage());
                return Message.responseMsg(Message.FAIL_CODE, Message.ERRO_RESP, "");
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("access_token", token);
            jsonObject.put("expires_in", "25200");//一周失效
            return Message.responseMsg(Message.SUCCESS_CODE, Message.SUCCESS, jsonObject);
        } else {
            return Message.responseMsg(Message.FAIL_CODE, Message.ERRO_RESP, "");
        }*/
    }

    /**
     * 会员登录
     * 登录成功，前端控制跳转到首页
     */
    @RequestMapping(value = "token", method = RequestMethod.POST)
    @ResponseBody
    public Message login(String mobile,String password) {
        if (StringUtils.isBlank(mobile)) {
            return Message.responseMsg(Message.FAIL_CODE, "手机号不能为空", "");
        }
        if (StringUtils.isBlank(password)) {
            return Message.responseMsg(Message.FAIL_CODE, "密码不能为空", "");
        }
        UserService userService = ServiceProxyFactory.get(UserService.class);
        User user;
        try{
           user = userService.login(mobile,password);
        }catch (Exception e){
            return Message.responseMsg(Message.FAIL_CODE, e.getMessage(), "");
        }

        JSONObject resp = SessionManager.get().generateToken((JSONObject)JSONObject.toJSON(user));

        return Message.responseMsg(Message.SUCCESS_CODE, Message.SUCCESS, resp);
    }

    /**
     * 退出登录
     *
     * @return URL :  60.205.230.76:9099/chapp/member/token
     */
    //@PathVariable
    @RequestMapping(value = "token", method = RequestMethod.DELETE)
    @ResponseBody
    public Message logout(String token) {
        SessionManager.get().deleteToken(token);
        return Message.responseMsg(Message.SUCCESS_CODE,Message.SUCCESS,"");
    }
}
