package com.ch.service.trd.org;

import com.ch.model.trd.org.account.domain.User;
import com.ch.service.base.ParentServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


/**
 * Created by ludynice on 2017/1/16.
 */
@Service
public class UserServiceImpl extends ParentServiceImpl implements UserService {

    //public UserServiceImpl() {//测试 - 在spring容器初始化的时候，会被加载！！！
    //    System.out.println("hello");
    //}

    @Override
    public String foo() {
        System.out.println("UserService Method foo()");
        return "UserService Method foo()";
    }

    @Override
    @Transactional
    public User regist(String mobile, String password) {
        User user = new User();
        user.setCode(UUID.randomUUID().toString());
        user.setMobile(mobile);
        user.setPassword(DigestUtils.md5Hex(mobile+""+password));
        em.merge(user);
        return user;
    }

    @Override
    public String login(String mobile,String password) {

        return "";
    }

}
