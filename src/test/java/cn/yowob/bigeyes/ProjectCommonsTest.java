package cn.yowob.bigeyes;

import cn.yowob.bigeyes.collector.WebCollectConfig;
import common.utils.CloseHelper;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

/**
 * User: chen.gang, glchengang@163.com
 * Date: 2018-01-15
 *
 */
public class ProjectCommonsTest {
	@Test
	public void testGetWebCollectConfigList() throws Exception {
		FileInputStream fis = ProjectCommons.getFileInputStream("weather2.yml");
		Map<String, Object> conf = new Yaml().load(fis);
		CloseHelper.close(fis);

		List<Map> pages = (List<Map>) conf.get("pages");
		List<WebCollectConfig> list = ProjectCommons.getWebCollectConfigList(pages);
		for (WebCollectConfig config : list) {
			ProjectCommons.print(config);
		}
	}

}
