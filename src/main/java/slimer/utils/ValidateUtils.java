package slimer.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;


public class ValidateUtils {
	/**
	 * 验证手机号码
	 * @param number
	 * @return
	 */
	public static boolean isPhoneNumber(String number) {
//		String rgx = "^((17[0-9])|(13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";
		String rgx = "^1\\d{10}$";
		return isCorrect(rgx, number);
	}

	/**
	 * 验证身份证号码
	 * @param number
	 * @return
	 */
	public static boolean idCardNumber(String number) {
		String rgx = "^\\d{15}|^\\d{17}([0-9]|X|x)$";
		return isCorrect(rgx, number);
	}
	
	public static boolean isMail(String mail){
		if(Strings.isNullOrEmpty(mail)){
			return false;
		}
		return mail.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+");
	}

	// 正则验证
	public static boolean isCorrect(String rgx, String res) {
		Pattern p = Pattern.compile(rgx);
		Matcher m = p.matcher(res);
		return m.matches();
	}
}
