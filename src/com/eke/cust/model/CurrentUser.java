package com.eke.cust.model;

import java.io.Serializable;

/**
 * Created by wujian on 2016/8/1.
 * 用户相关信息
 */

public class CurrentUser implements Serializable {
    public String token;
    public String custid;
    public String curfocusestateid;
    public String trade;
    public String regdate;
    public String custtel;
    public String custname;
    public String nickname;
    public String email;
    public String sign;
    public String ekeicon;
    public String pwd;
}
