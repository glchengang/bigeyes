package cn.yowob.bigeyes;

import common.utils.CloseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;

public class Configuration {
	private static final Logger log = LoggerFactory.getLogger(Configuration.class);
	private static final String CONFIG_FILE = "config.yml";
	private static Configuration conf = null;
	private String outputDir; //抓取数据保存目录
	private int collectMaxCount; //采集的最大数, 开发测试时设值小些.  0 or -1 = 不限制.

	private Configuration() {
	}

	synchronized public static Configuration getInstance() {
		if (conf == null) {
			conf = new Configuration().load();
		}
		return conf;
	}

	private Configuration load() {
		FileInputStream fis = ProjectCommons.getFileInputStream(CONFIG_FILE);
		Configuration conf = new Yaml().loadAs(fis, Configuration.class);
		CloseHelper.close(fis);
		log.info("------------------- {} ------------------- ", CONFIG_FILE);
		log.info("outputDir=" + conf.outputDir);
		log.info("collectMaxCount=" + conf.collectMaxCount);
		return conf;
	}

	public int getCollectMaxCount() {
		return collectMaxCount;
	}

	public void setCollectMaxCount(int collectMaxCount) {
		this.collectMaxCount = collectMaxCount;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

}
