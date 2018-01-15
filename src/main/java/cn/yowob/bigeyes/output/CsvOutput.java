package cn.yowob.bigeyes.output;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * 数据输出到本机目录文件
 * @author: 陈刚 2016/3/28
 */
public class CsvOutput implements Output {
	private static final Logger log = LoggerFactory.getLogger(CsvOutput.class);
	private String path;
	private String filename;

	public CsvOutput(String path, String filename) {
		this.path = path;
		this.filename = filename;
	}

	@Override
	public void put(Object data) {
		ArrayList<Map> dataList = (ArrayList<Map>) data;
		File file = new File(path + "/" + filename);
		ArrayList<String> lines = new ArrayList(dataList.size());
		String title = "城市代码,城市名,时间,温度";
		lines.add(title);
		for (Map m : dataList) {
			String s = m.get("cityCode") + "," + m.get("cityName") + "," + m.get("时间") + "," + m.get("温度");
			lines.add(s);
		}
		try {
			FileUtils.writeLines(file, lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
