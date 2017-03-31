package com.ch.model.trd.org.account.domain;

import com.ch.model.BaseEntity;
import com.ch.model.annotation.CacheItem;
import com.ch.model.annotation.ModelCache;
import com.ch.model.annotation.ModelField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Date;

/**
 * 会员基本信息
 * Created by ludynice on 2017/1/16.
 */
@Data
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "ac_user")
@DynamicInsert
@DynamicUpdate
@ModelCache(items={
        //from OrgUser a where a.userAccount = ?
        @CacheItem("bymobile.{mobile}"),
        //@CacheItem("bymemberid.{id}"),
})
public class User extends BaseEntity{

    private static final long serialVersionUID = 1L;

    //会员代码
    @Column(unique=true,nullable=false)
    //@ModelField(label = "会员代码")
    private String code;

    //手机号
    @Column(unique=true,nullable=false)
    //@ModelField(label = "手机号")
    private String mobile;

    //密码
    @Column(nullable=false)
    //@ModelField(label = "密码")
    private String password;

    //demo字段
    private String grade;

    /*//微信ID
    @Column(unique=true)
    private String wechatId;

    //QQID
    @Column(unique=true)
    private String qqId;

    //新浪ID
    @Column(unique=true)
    private String xinlangId;

    //状态 1启用2停用
    @Column(nullable=false)
    private Integer status;

    //所属邀请码
    private String invitationCodeId;

    //认证状态 1.未认证,2.邀请码认证,3.实名认证审核中 ，4.实名认证
    @Column(nullable=false)
    private Integer authenticationStatus;

    //合作伙伴状态(1.未申请，2.审核中，3.已开通)
    private Integer partnerStatus;

    //合作伙伴开通日期
    private Date partnerStartTime;

    //认证开始时间
    private Date startTime;

    //认证有效期(截止时间)
    private Date endTime;

    //认证是否有有效期
    @Column(nullable=false)
    private Boolean hasValidTime;

    //营业执照(图片)
    @Column(nullable=false)
    private String licenseNoId;

    //营业执照号
    @Column(nullable=false)
    private String licenseNo;

    //所在地(运营审核时填写,只能选择一个最小的地区)
    @Column(nullable=false)
    private String location;

    //联系人
    @Column(nullable=false)
    private String contactPerson;

    //联系人电话
    @Column(nullable=false)
    private String contactMobile;*/
}
