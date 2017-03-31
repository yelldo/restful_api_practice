package com.ch.web.action.trd.org;

import com.ch.web.base.Message;
import com.ch.web.base.ParentAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ludynice on 2017/1/17.
 */
@Controller
@RequestMapping("/sessionTest")
//@RestController
public class SessionTestAction extends ParentAction{

    @RequestMapping(value="add")
    @ResponseBody
    public Message add() {
        return Message.responseMsg(Message.CFAIL, Message.MFAIL, Message.NORESP);
    }

    @RequestMapping(value="get")
    @ResponseBody
    public Message get() {
        //SessionManager.getProject().put();
        return Message.responseMsg(Message.CFAIL, Message.MFAIL, Message.NORESP);
    }

    @RequestMapping(value="update")
    @ResponseBody
    public Message update() {
        //SessionManager.getProject().put();
        return Message.responseMsg(Message.CFAIL, Message.MFAIL, Message.NORESP);
    }

    @RequestMapping(value="delete")
    @ResponseBody
    public Message delete() {
        //SessionManager.getProject().put();
        return Message.responseMsg(Message.CFAIL, Message.MFAIL, Message.NORESP);
    }

    @RequestMapping(value="helloworld")
    @ResponseBody
    public Message test() {
        return Message.success("访问成功","helloword_session_test");
    }
}
