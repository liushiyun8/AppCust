package com.eke.cust.enums;


public enum StartType {

    /**
     * 修改昵称
     */
    SetNikeName(),
    /**
     * 修改邮箱
     */
    SetEmail(),
    /**
     * 修改签名
     */
    SetAboutMe(),
    /**
     * 合同
     */
    Contract(),
    /**
     * 收藏
     */
    Collect();

    private int code;

    StartType(int code) {
        this.code = code;
        EnumsType._basecode = this.code + 2000;
    }

    StartType() {
        this.code = EnumsType._basecode;
        EnumsType._basecode++;
    }

    public String getCode() {
        return String.valueOf(code);
    }

    public int getTypeCode() {
        return code;
    }
}
