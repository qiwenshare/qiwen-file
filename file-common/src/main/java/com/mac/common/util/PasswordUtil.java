package com.mac.common.util;

import cn.hutool.core.util.RandomUtil;

public class PasswordUtil {
	public static String getSaltValue() {
		return RandomUtil.randomNumbers(16);
	}
}
