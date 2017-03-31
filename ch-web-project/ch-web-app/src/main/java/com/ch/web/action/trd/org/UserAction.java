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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ludynice on 2017/1/17.
 */
@Controller
@RequestMapping("/user")
//@RestController
public class UserAction extends ParentAction {

    @RequestMapping(value="helloworld")
    @ResponseBody
    public Message test() {
        return Message.success("访问成功","helloword");
    }

    @RequestMapping(value="testUserService")
    @ResponseBody
    public Message testUserService() {
        UserService userService = this.getProxy(UserService.class);

        return Message.success("访问成功",userService.foo());
    }

    //登录状态检查
    @RequestMapping("validFailed")
    @ResponseBody
    public Message validFailed() {
        return Message.responseMsg(Message.CFAIL, Message.MFAIL, Message.NORESP);
    }

    /**
     * 会员注册
     *
     * @param mobile
     * @param password
     * @return URL :  60.205.230.76:9099/chapp/member
     * 0代表成功
     * 100,客户端错误
     * 200,服务端错误
     */
    @RequestMapping(value = "token", method = RequestMethod.POST)
    @ResponseBody
    public Message regist(String mobile, String password, Long timestamp) {
        String msg = "success";
        String code = "0";

        if (StringUtils.isBlank(mobile)) {
            return Message.responseMsg("100", "手机号不能为空", "");
        }
        if (StringUtils.isBlank(password)) {
            return Message.responseMsg("100", "密码不能为空", "");
        }
        QueryService queryService = ServiceProxyFactory.get(QueryService.class);
        if (null != queryService.findFirst("from User a where a.mobile = ?", mobile)) {
            return Message.responseMsg("100", "手机号已被注册", "");
        }
        UserService userService = ServiceProxyFactory.get(UserService.class);
        User member = null;
        try {
            member = userService.regist(mobile, password);
        } catch (Exception e) {
            log.error(e.getMessage());
            msg = "服务端错误，请联系管理员";
            return Message.responseMsg("200", msg, "");
        }

        JSONObject params = new JSONObject();
        params.put("username", mobile);
        params.put("password", member.getPassword());

        //注册成功，生成 access_token，返回给客户端
        if (null != member) {
            String token = "";
            try {
                token = SessionManager.get().createToken(params);
            } catch (Exception e) {
                log.error(e.getMessage());
                System.out.println(e.getMessage());
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("access_token", token);
            jsonObject.put("expires_in", "25200");//一周失效
            return Message.responseMsg(code, msg, jsonObject);
        }
        return Message.responseMsg(code, msg, "");
    }

    /**
     * 会员登录
     *
     * @param mobile
     * @param password
     * @return URL :  60.205.230.76:9099/chapp/member/token
     */
    @RequestMapping(value = "token", method = RequestMethod.PATCH)
    @ResponseBody
    public Message login(String mobile, String password, String token) {
        String msg = "success";
        String code = "0";

        if (StringUtils.isBlank(mobile)) {
            msg = "手机号不能为空";
            code = "100";
        }
        if (StringUtils.isBlank(password)) {
            msg = "密码不能为空";
            code = "100";
        }
        UserService userService = ServiceProxyFactory.get(UserService.class);
        return Message.responseMsg(code, msg, "");
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
        String code = "0";
        String msg = "success";
        return Message.responseMsg(code, msg, null);
    }
}