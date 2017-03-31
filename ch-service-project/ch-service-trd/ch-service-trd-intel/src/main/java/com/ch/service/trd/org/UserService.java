package com.ch.service.trd.org;

import com.ch.model.trd.org.account.domain.User;

/**
 * Created by ludynice on 2017/1/16.
 */
public interface UserService {

    public String foo();

    /**
     * 会员注册
     * @param mobile
     * @param password
     * @return
     */
    public User regist(String mobile, String password);

    /**
     * 会员登录
     * @param mobile
     * @param password
     * @return
     */
    public String login(String mobile,String password);



}
