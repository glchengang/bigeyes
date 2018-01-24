package cn.yowob.bigeyes;

/**
 *
 * User: chen.gang, glchengang@163.com
 * Date: 01/19/2018
 */
public class FilenameHelper {

	public static String getFilenameFromUrl(String url) {
		int i = url.lastIndexOf("/");
		String filename = url.substring(i + 1);
		return filename;
	}

	public static String getMainFromFilename(String filename) {
		int i = filename.indexOf(".");
		String mainName = filename.substring(0, i);
		return mainName;
	}

	public static String getExtFromFilename(String filename) {
		int i = filename.indexOf(".");
		String extName = filename.substring(i);
		return extName;
	}

}
