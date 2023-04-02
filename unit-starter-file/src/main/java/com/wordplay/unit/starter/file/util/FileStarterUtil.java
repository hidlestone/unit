package com.wordplay.unit.starter.file.util;

import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.InputStream;

/**
 * @author zhuangpf
 */
public class FileStarterUtil {

	/**
	 * 获取文件后缀名
	 *
	 * @param name 文件名
	 * @return 后缀名
	 */
	public static String getFileExt(String name) {
		name = name.replace('\\', '/');
		name = name.substring(name.lastIndexOf("/") + 1);
		int index = name.lastIndexOf(".");
		String ext = null;
		if (index >= 0) {
			ext = StringUtils.trimToNull(name.substring(index + 1));
		}
		return ext;
	}

	/**
	 * 判断文件是否是图片类型
	 */
	public static boolean isImage(InputStream imageFile) {
		try {
			Image img = ImageIO.read(imageFile);
			if (img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 创建文件路径
	 */
	public static String getFilePath(String fKey, String path) {
		if (!new File(path).exists()) {
			new File(path).mkdirs();
		}

		fKey = StringUtils.trimToNull(fKey);
		if (fKey == null) {
			return null;
		} else {
			return path + "/" + fKey;
		}
	}

}
