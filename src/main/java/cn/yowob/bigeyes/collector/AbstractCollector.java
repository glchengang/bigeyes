package cn.yowob.bigeyes.collector;

import cn.yowob.bigeyes.ProjectCommons;
import common.utils.CloseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

/**
 * 天气信息采集
 * @author: 陈刚 2016/3/28
 */
public abstract class AbstractCollector implements Collector {
	private static final Logger log = LoggerFactory.getLogger(AbstractCollector.class);
	protected Map<String, Object> config;

	public AbstractCollector() {
		FileInputStream fis = ProjectCommons.getFileInputStream(getConfigFile());
		config = new Yaml().load(fis);
		CloseHelper.close(fis);
	}

	/**
	 * 单项目采集的配置文件名和类名相同
	 */
	protected String getConfigFile() {
		return this.getClass().getSimpleName() + ".yml";
	}

	/**
	 * 构建抓取模板
	 */
	protected List<WebCollectConfig> getWebCollectConfigList() {
		List<Map> pages = (List<Map>) config.get("pages");
		return ProjectCommons.getWebCollectConfigList(pages);
	}

}
