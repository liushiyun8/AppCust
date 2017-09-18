package com.eke.cust.model;

import java.io.Serializable;

/**
 * Created by wujian on 2016/8/4.
 */

public class User implements Serializable {

    private  String password;
    private  String email;
    private  String name;
    private  String sex;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
