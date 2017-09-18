package foundation.enums;

import java.util.ArrayList;
import java.util.List;

class EnumsRestApiCode {
	public static int _basecode = 0;
	public static List<RestApiCode> _enumArray = new ArrayList<RestApiCode>();
}

public enum RestApiCode {

	RestApi_OK(0), // 成功
	RestApi_Error(500), // 失败
	//错误码为RestApi_InvalidToken时，跳转到登录界面
	//用户信息失效
	RestApi_InvalidToken(401),
	// 国家码错误
	RestApi_CountryCode(4031),
	RestApi_DoesNotExist(404),
	// 直播结束
	RestApi_UnLiveCode(5000),
	// 用户不存在
	RestApi_AccountNotFound(5),
	// 验证码错误
	RestApi_InvalidSMS(6),
	RestApi_NotModify(16), // 没有变化
	RestApi_UnCompatible(3), // 版本不兼容，需要更新
	//余额不足
	RestApi_Wallet_NotEnough(1001),

	RestApi_Unknown(0x15000), // 未知错误

	RestApi_Internal_HTTP_Failed(0x8000), // HTTP
	RestApi_Internal_UnsupportedEncodingException(), // UnsupportedEncodingException
	RestApi_Internal_TimeoutException(), // TimeoutException
	RestApi_Internal_JSONException(), // JSONException
	RestApi_Internal_JSONInvalid(), // JSONInvalid
	RestApi_Internal_ClientProtocolException(), // ClientProtocolException
	RestApi_Internal_IOException(), // IOException
	RestApi_Internal_UnknownHostException(); // UnknownHostException


	private int code;

	private RestApiCode(int code) {
		this(code, true);
	}

	private RestApiCode() {
		this.code = EnumsRestApiCode._basecode;
		EnumsRestApiCode._basecode++;
		EnumsRestApiCode._enumArray.add(this);
	}

	private RestApiCode(int code, boolean base) {
		this.code = code;
		if (base) {
			EnumsRestApiCode._basecode = this.code + 1;
			EnumsRestApiCode._enumArray.add(this);
		}
	}

	public String getCode() {
		return String.valueOf(code);
	}

	public static RestApiCode createCode(int code) {
		for (RestApiCode c : EnumsRestApiCode._enumArray) {
			if (c.code == code)
				return c;
		}
		RestApiCode c = RestApi_Unknown;
		c.code = code;
		return c;
	}
}