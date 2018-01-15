package cn.yowob.bigeyes.output;

import common.utils.FileHelper;
import common.utils.JSONUtils;

/**
 * 数据输出到本机目录文件
 * @author: 陈刚 2016/3/28
 */
public class JsonOutput implements Output {
	private String path;
	private String filename;

	public JsonOutput(String path, String filename) {
		this.path = path;
		this.filename = filename;
	}

	@Override
	public void put(Object data) {
		String content = JSONUtils.json2PrettyString(data);
		FileHelper.saveFile(content, path, filename);
	}

}
