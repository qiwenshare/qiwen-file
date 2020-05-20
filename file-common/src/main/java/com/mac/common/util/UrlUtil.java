package com.mac.common.util;

import java.util.List;

/**
 * URL工具类
 *
 * @author WeiHongBin
 */
public class UrlUtil {

	/**
	 * URL 匹配器
	 *
	 * @param uri  待验证URL
	 * @param urls 白名单URL
	 * @return 待验证URL匹配白名单返回 true 否则返回 false
	 */
	public static boolean match(String uri, List<String> urls) {
		boolean match = false;
		if (uri != null) {
			for (String regex : urls) {
				if (uri.matches(regex)) {
					match = true;
					break;
				}
			}
		}
		return match;
	}
}
