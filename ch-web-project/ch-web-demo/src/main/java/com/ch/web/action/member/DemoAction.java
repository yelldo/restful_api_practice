package com.ch.web.action.member;

import com.alibaba.fastjson.JSONObject;
import com.ch.service.trd.org.UserService;
import com.ch.web.base.Message;
import com.ch.web.base.ParentAction;
import com.ch.web.proxy.ServiceProxyFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ludynice on 2017/1/17.
 */
@Controller
@RequestMapping("/member")
//@RestController
public class DemoAction extends ParentAction{

    @RequestMapping("list")
    @ResponseBody
    public Message list(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(".......");
        UserService userService = ServiceProxyFactory.get(UserService.class);
        userService.foo();
        return Message.success("操作成功", "");
    }

    @RequestMapping("test")
    @ResponseBody
    public Message test(HttpServletRequest request, HttpServletResponse response) {
        /*String code = request.getParameter("code");
        String mobile = request.getParameter("mobile");
        String password = request.getParameter("password");
        String grade = request.getParameter("grade");
        User m = new User();
        if (StringUtils.isNotBlank(code)) {
            m.setCode(code);
        }
        if (StringUtils.isNotBlank(mobile)) {
            m.setMobile(mobile);
        }
        if (StringUtils.isNotBlank(password)) {
            m.setPassword(password);
        }
        if (StringUtils.isNotBlank(grade)) {
            m.setGrade(grade);
        }*/
        JSONObject json = new JSONObject();
        json.put("data","helloworld");
        return Message.success("访问成功",json);
    }
}
