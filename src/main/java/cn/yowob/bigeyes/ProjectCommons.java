package cn.yowob.bigeyes;

import cn.yowob.bigeyes.collector.WebCollectConfig;
import cn.yowob.bigeyes.collector.XpathConfig;
import common.utils.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 工具类
 * User: chen.gang, glchengang@163.com
 * Date: 2018-01-15
 */
public class ProjectCommons {
	private static final Logger log = LoggerFactory.getLogger(ProjectCommons.class);

	public static FileInputStream getFileInputStream(String filename) {
		URL url = ProjectCommons.class.getClassLoader().getResource(filename);
		if (url == null) {
			throw new CommonException("NOT FOUND in classpath: " + filename);
		}
		try {
			return new FileInputStream(url.getFile());
		} catch (FileNotFoundException e) {
			log.error("", e);
			throw new CommonException(filename);
		}

//		filename = System.getenv(HOME_KEY) + "/conf/" + filename;
//		log.info("Read by absolute path = {}", filename);
//		return new FileInputStream(filename);
//
//		//file:/Users/chen/workspace/bigeyes/target/bigeyes-1.0.jar!/cn/yowob/bigeyes/output/
//		log.info(this.getClass().getResource("").getPath());
	}

	/**
	 * 构建抓取模板
	 */
	public static List<WebCollectConfig> getWebCollectConfigList(List<Map> pages) {
		List<WebCollectConfig> resultList = new ArrayList();
		for (Map page : pages) {
			WebCollectConfig conf = new WebCollectConfig();
			conf.setName((String) page.get("name"));
			conf.setUrl((String) page.get("url"));
			//
			conf.setXpathConfigList(new ArrayList());
			List xpaths = (List) page.get("xpaths");
			for (Object xpathObj : xpaths) {
				List xpath = (List) xpathObj;
				XpathConfig xpathConfig = new XpathConfig();
				xpathConfig.setHandleType((int) xpath.get(0));
				xpathConfig.setKey((String) xpath.get(1));
				xpathConfig.setXpath((String) xpath.get(2));
				conf.getXpathConfigList().add(xpathConfig);
				resultList.add(conf);
			}
			print(conf);
		}
		return resultList;
	}

	public static void print(WebCollectConfig config) {
		log.debug("{}, {}", config.getName(), config.getUrl());
		for (XpathConfig xpathConfig : config.getXpathConfigList()) {
			log.debug("    {},{},{}", xpathConfig.getHandleType(), xpathConfig.getKey(), xpathConfig.getXpath());
		}
	}
}
