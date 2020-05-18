package com.mac.scp.session;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 目前临时使用，到时候用 Redis 替换
 *
 * @author WeiHongBin
 */
public class SessionFactory {
	private static final Map<String, Long> tokenMap = Maps.newConcurrentMap();

	private SessionFactory() {
	}

	public static Map<String, Long> getSession() {
		return tokenMap;
	}
}