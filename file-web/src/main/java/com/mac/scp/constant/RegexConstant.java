package com.mac.scp.constant;

/**
 * 正则表达式 常量
 *
 * @author WeiHongBin
 */
public interface RegexConstant {

	/**
	 * 邮箱正则
	 */
	String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

	/**
	 * 邮箱或手机号码正则
	 */
	String EMAIL_OR_PHONE_REGEX = "^([a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+)|(1[3456789]\\d{9})$";

	/**
	 * 手机号码正则
	 */
	String PHONE_REGEX = "^1[3456789]\\d{9}$";
	/**
	 * 文件名 正则
	 * 被保留的设备名不能被用来作为文件 名：CON, PRN, AUX, NUL, COM1, COM2, COM3, COM4, COM5, COM6, COM7, COM8, COM9, LPT1, LPT2, LPT3, LPT4, LPT5, LPT6, LPT7, LPT8, and LPT9
	 * 不能用//:*?"<>|作为文件名称,文件名称为1-255位
	 */
	String FILE_NAME_REGEX = "(?!((^(con)$)|^(con)//..*|(^(prn)$)|^(prn)//..*|(^(aux)$)|^(aux)//..*|(^(nul)$)|^(nul)//..*|(^(com)[1-9]$)|^(com)[1-9]//..*|(^(lpt)[1-9]$)|^(lpt)[1-9]//..*)|^//s+|.*//s$)(^[^\\\\|\\/|:|\\*|\\?|\"|<|>|\\|]{1,255}$)";
	/**
	 * 父路径 正则
	 */
	String PARENT_PATH_REGEX = "^(/.+?/)|(/)$";
	/**
	 * Md5 正则
	 */
	String MD5_REGEX = "^[0-9a-z]{32}$";
	/**
	 * 密码正则
	 */
	String PASSWORD_REGEX = "^[^\\s\\u4e00-\\u9fa5]{6,20}$";
}

