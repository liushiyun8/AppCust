package com.eke.cust.pay.alipay.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by lenovo on 2016/7/12.
 */
public class AlipayServer {
    //商户PID  替换自己的商户id
    private static final String PARTNER = "2088421675730621";
    //商户收款账号  替换自己的收款账号
    private static final String SELLER = "wjcbeijing@qq.com";
    //商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAM7qUSVVYnNN9DI+\n" +
            "wVj7N1AlQ9bWKIVdh8Vd07SuuNynoTydxNltNAXLiw8UvaoifOhpGunKbNDGR+yJ\n" +
            "8NELEZMvLNKABtO+14OdmfeT0gDkfqiDQ3SZODIwpizhBn1iJMw8XYksK2F7B73M\n" +
            "FkAkoyVwAx/4XY4HwLD4w0Jk7hphAgMBAAECgYAe18y6vOyM9XzGFJarld7b4oIn\n" +
            "PwsRPizj3aWWFElYEuC08WuLYXzUtE+JMvVGrBdkaW5qlRL9V+EWwEUWVAkNW+hk\n" +
            "sO7kBXud2eO/QpeD5eZLlzx7bFbMIykbXX8Nd2cxU6s+yBuHyi2dGcT2ORSyevD4\n" +
            "vs3ExLpTtPdLYmExNQJBAOcC8V119PUwA7I02/e9/KNr+vK76j+cNd+P0Tg+cSt8\n" +
            "hVynIEhuPCzzooxwP4m2lXKVNoGeAzfejw+Irqt8t/sCQQDlTB03HzFTIxNI/Blf\n" +
            "k3C7wuVk6/6TTsWuAU0m5ZUFCIlBcYfAELDokx3OhLIRD1J+2H9P2JRdLhciCr48\n" +
            "7RxTAkAcS1z6wIbXPSFGw3ipuGhoL8KV1nRThDOJBVlv1R4RYHgJKCMpG+9c5h5j\n" +
            "qx/tIyLc3O/HIm5semL1WR1jvavfAkBaHuZEqe/a+ygzQVI/rFVclqdOWSq/fvBX\n" +
            "CY/nP8ioJ8i6fgKazHSblGbcqm7KIbmMvU6cdUUqsljJOmu2yoKdAkBn+wePGzqi\n" +
            "mZCzgu77dd3SSezfG4MaCTcM/MpCsluD/hxekIAqaE4Z1F391zeI1hva+vx5Klfm\n" +
            "PTnUG5LzOF4Q";

    /**
     * 获取订单信息
     *
     * @param orderId   订单号
     * @param subject   商品名称
     * @param body      商品描述
     * @param money     金额
     * @param notifyUrl 支付回调地址
     * @return
     */
    public static String getPayInfo(String orderId, String subject, String body, String money, String notifyUrl) {
        // 订单
        String orderInfo = getOrderInfo(orderId, subject,
                body, money, notifyUrl);

        return signOrderInfo(orderInfo);
    }

    private static String signOrderInfo(String orderInfo) {

        // 对订单做RSA 签名
        String sign = sign(orderInfo);

        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        return payInfo;
    }

    /**
     * create the order info. 创建订单信息
     */
    private static String getOrderInfo(String orderId, String subject, String body, String price, String notifyUrl) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号

        orderInfo += "&out_trade_no=" + "\"" + orderId + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + notifyUrl
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    private static String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private static String getSignType() {
        return "sign_type=\"RSA\"";
    }
}
