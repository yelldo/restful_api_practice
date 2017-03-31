package com.ch.web.base;

import com.alibaba.fastjson.JSONObject;

/**
 * @author ludynice
 */
public class Message {
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    private String code;
    private String msg;
    private Object data;

    public final static String CSUCCESS = "0";
    public final static String CFAIL = "1";
    public final static String MSUCCESS = "success";
    public final static String MFAIL = "false";
    public final static String NORESP = "";


    public static Message responseMsg(String code,String msg,Object data){
        Message message = new Message();
        message.setCode(code);
        message.setMsg(msg);
        message.setData(data);
        return message;
    }
    public static Message buidMessage() {
        Message message = new Message();
        message.setCode("");
        message.setData(null);
        message.setMsg("");
        return message;
    }

    public static Message success(String msg, Object data) {
        Message message = new Message();
        message.setCode("100");
        message.setData(data);
        message.setMsg(msg);
        return message;
    }

    public static Message error(String msg) {
        Message message = new Message();
        message.setCode("400");
        message.setData(null);
        message.setMsg(msg);
        return message;
    }
}
