package com.eke.cust.bean;

import java.io.Serializable;

/**
 * Created by wujian on 2017/4/16.
 * <p>
 * 启动页
 */

public class LaunchModel implements Serializable {

    private   String linkid;
    private   String linkpra;
    private   String linkurl;
    private   String remark;
    private   String coverurl;

    public String getLinkid() {
        return linkid;
    }

    public void setLinkid(String linkid) {
        this.linkid = linkid;
    }

    public String getLinkpra() {
        return linkpra;
    }

    public void setLinkpra(String linkpra) {
        this.linkpra = linkpra;
    }

    public String getLinkurl() {
        return linkurl;
    }

    public void setLinkurl(String linkurl) {
        this.linkurl = linkurl;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCoverurl() {
        return coverurl;
    }

    public void setCoverurl(String coverurl) {
        this.coverurl = coverurl;
    }
}
