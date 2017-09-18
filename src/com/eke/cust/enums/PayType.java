package com.eke.cust.enums;

/**
 * Created by wujian on 2016/8/3.
 */

public enum PayType {


    /**
     * 微信支付
     */
    WEIXINGPAY(),
    /**
     * 支付宝
     */
    ALIPAY();
    private int code;

    PayType(int code) {
        this.code = code;
        EnumsType._basecode = this.code + 3000;
    }

    PayType() {
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
