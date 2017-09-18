package com.eke.cust.bean;

import java.io.Serializable;

/**
 * Created by wujian on 2017/3/31.
 * 对话框菜单
 */

public class DialogMenu implements Serializable {
    private  String  name;
    private   int menuResource;

    public DialogMenu(String name, int menuResource) {
        this.name = name;
        this.menuResource = menuResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMenuResource() {
        return menuResource;
    }

    public void setMenuResource(int menuResource) {
        this.menuResource = menuResource;
    }
}
