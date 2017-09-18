package foundation.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 正则表达式 工具类
 *
 * @author wujian
 *
 */
public class RegularHelper {
	public static boolean checkSpecialChar(String str) throws PatternSyntaxException {
		// 清除掉所有特殊字符
		String regEx =  ".*[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\\\]+.*";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.matches();
	}


	// 判断密码是否符合规则 至少6位，字母与数字的组合
	public static boolean isValidPassword(String password) {
//		Pattern p = Pattern
//				.compile("(?!^\\d+$)(?!^[a-zA-Z]+$)(?!^[_#@]+$).{6,18}");
		Pattern p = Pattern
				.compile("^[0-9a-zA-Z]{6,16}$");
		Matcher m = p.matcher(password);
		return m.matches();
	}

	// 判断姓名是不是符合规则
	public static boolean isChinese(String name) {
		Pattern p = Pattern
				.compile("[\u4E00-\u9FA5]{2,5}(?:·[\u4E00-\u9FA5]{2,5})*");
		Matcher m = p.matcher(name);
		return m.matches();
	}
	// 判断姓名是不是符合规则
	public static boolean isOfficer(String name) {
		Pattern p = Pattern
				.compile("[\u4e00-\u9fa5](字第){1}(\\d{4,8})(号?)$/");
		Matcher m = p.matcher(name);
		return m.matches();
	}

	/**
	 * 校验银行卡卡号
	 *
	 * @param cardId
	 * @return
	 */
	public static boolean checkBankCard(String cardId) {
		char bit = getBankCardCheckCode(cardId
				.substring(0, cardId.length() - 1));
		return cardId.charAt(cardId.length() - 1) == bit;
	}

	/**
	 * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
	 *
	 * @param nonCheckCodeCardId
	 * @return
	 */
	public static char getBankCardCheckCode(String nonCheckCodeCardId) {
		if (nonCheckCodeCardId == null
				|| nonCheckCodeCardId.trim().length() == 0
				|| !nonCheckCodeCardId.matches("\\d+")) {
			throw new IllegalArgumentException("Bank card code must be number!");
		}
		char[] chs = nonCheckCodeCardId.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}

	/**
	 * 验证邮箱地址是否正确
	 *
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}

		return flag;
	}

	/**
	 * 验证手机号码
	 *
	 * @param mobiles
	 * @return [0-9]{5,9}
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^0{0,1}(13[0-9]|15[0-9]|18[0-9]|17[0-9]|14[0-9])[0-9]{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 验证身份证号码是否正确
	 * @param sfzhm
	 * @return
	 */
	public static boolean isSfzhm(String sfzhm) {
		Pattern patternSfzhm1 = Pattern
				.compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{4}$");
		Pattern patternSfzhm2 = Pattern
				.compile("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$");
		Matcher matcherSfzhm1 = patternSfzhm1.matcher(sfzhm);
		Matcher matcherSfzhm2 = patternSfzhm2.matcher(sfzhm);
		return matcherSfzhm1.find() && !matcherSfzhm2.find();
	}

	public static boolean isValidNum(String price) {
//		Pattern p = Pattern
//				.compile("^[+-]?[0-9]{1,9}+(.[0-9]{1,3})?$");

		Pattern p = Pattern.compile("^(\\d{0,7}+(?:\\.\\d{2})?)$");
		Matcher m = p.matcher(price);
		return m.matches();
	}

}
