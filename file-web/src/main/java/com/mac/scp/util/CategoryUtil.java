package com.mac.scp.util;

import com.google.common.collect.Sets;
import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class CategoryUtil {
	/**
	 * 过去文件类型
	 *
	 * @return 0 未知类型; 1 图片; 2 视频; 3 音乐
	 */
	public int getFileCategory(String fileName) {

		int i = fileName.indexOf('.');
		if (i == -1 || i == fileName.length() - 1) {
			return 0;
		}
		String suffix = fileName.substring(i + 1);
		// html 支持的图像格式 https://developer.mozilla.org/zh-CN/docs/Web/HTML/Element/img
		Set<String> imageSuffixSet = Sets.newHashSet("apng", "bmp", "gif", "ico", "cur", "jpg", "jpeg", "jfif", "pjpeg", "pjp", "png", "svg", "tif", "tiff", "webp");
		if (imageSuffixSet.contains(suffix)) {
			return 1;
		}
		// html 支持的视频格式 https://www.runoob.com/tags/tag-video.html
		Set<String> videoSuffixSet = Sets.newHashSet("mp4", "webm", "ogg");
		if (videoSuffixSet.contains(suffix)) {
			return 2;
		}
		// html 支持的音频格式 https://www.runoob.com/tags/tag-audio.html
		Set<String> audioSuffixSet = Sets.newHashSet("mp3", "wav");
		if (audioSuffixSet.contains(suffix)) {
			return 3;
		}
		return 0;
	}

}
