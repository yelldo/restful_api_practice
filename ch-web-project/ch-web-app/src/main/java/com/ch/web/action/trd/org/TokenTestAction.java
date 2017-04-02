package com.ch.web.action.trd.org;

import com.alibaba.fastjson.JSONObject;
import com.ch.frame.session.SessionManager;
import com.ch.web.base.Message;
import com.ch.web.base.ParentAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ludynice on 2017/1/17.
 */
@Controller
@RequestMapping("/tokenTest")
//@RestController
public class TokenTestAction extends ParentAction{

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Message add() {
        JSONObject params = new JSONObject();
        params.put("username","123456");
        params.put("password","123");
        params.put("timestamp","123456789");
        String code = Message.SUCCESS_CODE;
        String msg = Message.SUCCESS;
        String token = "";
        try{
            token = SessionManager.get().createToken();
        }catch (Exception e){
            code=Message.FAIL_CODE;
            msg=Message.FAIL;
        }
        return Message.responseMsg(code,msg,token);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Message get(String token) {
        String code = Message.SUCCESS_CODE;
        String msg = Message.SUCCESS;
        Boolean flag = false;
        try{
           flag = SessionManager.get().exists("token",token);
        }catch (Exception e){
            code=Message.FAIL_CODE;
            msg=Message.FAIL;
        }
        return Message.responseMsg(code,msg,flag.toString());
    }

    @RequestMapping(value="helloworld")
    @ResponseBody
    public Message test() {
        return Message.success("访问成功","helloword_token_test");
    }
}
