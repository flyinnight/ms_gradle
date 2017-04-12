/*********************************************************************/
/*  文件名  StringUtils.java    　                                      */
/*  程序名  字符串工具类                     						                         */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.util;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

public class StringUtils {

	/**
	 * 随机实例
	 */
	private static final Random DEFULT_RANDOM = new Random();

	/**
	 * 判断字符串是否为空
	 * 
	 * @param value
	 *            字符串
	 * @return true:为空,false:不为空
	 */
	public static boolean isEmpty(Object value) {
		if (value == null || value.toString().trim().length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 去除制表符、空格、回车、换行
	 * 
	 * @param str
	 *            原字符串
	 * @return 去除制表符后的字符串
	 */
	public static String filterBlankTag(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 按Byte位数截取字符串
	 * 
	 * @param str
	 *            字符串
	 * @param byteLen
	 *            byte位数
	 * @param paddingSuffix
	 *            超长补位符，一般为省略号
	 * @return 截取后的字符串
	 */
	public static String subStrB(String str, int byteLen, String paddingSuffix) {
		if (str == null) {
			return str;
		}
		int suffixLen = paddingSuffix.getBytes().length;

		StringBuffer sbuffer = new StringBuffer();
		char[] chr = str.trim().toCharArray();
		int len = 0;
		for (int i = 0; i < chr.length; i++) {
			if (chr[i] >= 0xa1) {
				len += 2;
			} else {
				len++;
			}
		}

		if (len <= byteLen) {
			return str;
		}

		len = 0;
		for (int i = 0; i < chr.length; i++) {

			if (chr[i] >= 0xa1) {
				len += 2;
				if (len + suffixLen > byteLen) {
					break;
				} else {
					sbuffer.append(chr[i]);
				}
			} else {
				len++;
				if (len + suffixLen > byteLen) {
					break;
				} else {
					sbuffer.append(chr[i]);
				}
			}
		}
		sbuffer.append(paddingSuffix);
		return sbuffer.toString();
	}

	/**
	 * 电话号码加掩码*****
	 * 
	 * @param pn
	 * @return
	 */
	public static String getMaskPhoneNumber(String phoneNumber) {
		if (phoneNumber == null || phoneNumber.length() < 5) {
			return phoneNumber;
		}
		StringBuffer s = new StringBuffer(phoneNumber.substring(0, 3));
		for (int i = 0; i < phoneNumber.length() - 4; i++) {
			s.append("*");
		}
		s.append(phoneNumber.charAt(phoneNumber.length() - 1));
		return s.toString();
	}

	/**
	 * 生成随机验证码
	 * 
	 * @param len
	 *            验证码位数
	 * @return 随机验证码
	 */
	public static String makeRandom(int len) {

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < len; i++) {
			buffer.append(DEFULT_RANDOM.nextInt(10));
		}
		return buffer.toString();
	}

	public static String intToString(int[] ints) {
		String strLabel = "";
		for (int i = 0; i < ints.length; i++) {
			strLabel += String.valueOf(ints[i]);
		}
		return strLabel;
	}

	/**
	 * 判断是否为手机号
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNum(String mobiles) {
		// Pattern p = Pattern
		// .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		// Pattern p = Pattern
		// .compile("^((13[0-9])|(15[^4,//D])|(18[0,5-9]))//d{8}$");
		// Matcher m = p.matcher(mobiles);
		// System.out.println(m.matches() + "---");
		// return m.matches();
		String telRegex = "[1][34578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}

}
