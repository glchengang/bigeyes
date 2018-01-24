package cn.yowob.bigeyes;

import cn.yowob.bigeyes.collector.WebCollectConfig;
import common.utils.CloseHelper;
import junit.framework.TestCase;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: chen.gang, glchengang@163.com
 * Date: 2018-01-15
 *
 */
public class ProjectCommonsTest {
	@Test
	public void addList() {
		ArrayList<String> list = new ArrayList<>();
		list.add("0");
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		list.add("5");

		list.add(5,"a");
		list.add(5,"b");
		list.add(0,"a");
		System.out.println(list);
	}

	@Test
	public void replace() {
		ArrayList<String> list = new ArrayList<>();
		list.add("0");
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		list.add("5");

		ProjectCommons.replace(list, 0, "a");
		TestCase.assertEquals("[a, 1, 2, 3, 4, 5]", list.toString());
		ProjectCommons.replace(list, 1, "b");
		TestCase.assertEquals("[a, b, 2, 3, 4, 5]", list.toString());
		ProjectCommons.replace(list, 2, "c");
		TestCase.assertEquals("[a, b, c, 3, 4, 5]", list.toString());
		ProjectCommons.replace(list, 3, "d");
		TestCase.assertEquals("[a, b, c, d, 4, 5]", list.toString());
		ProjectCommons.replace(list, 4, "e");
		TestCase.assertEquals("[a, b, c, d, e, 5]", list.toString());
		ProjectCommons.replace(list, 5, "f");
		TestCase.assertEquals("[a, b, c, d, e, f]", list.toString());
	}

	@Test
	public void revertRemarkChange() throws Exception {
		TestCase.assertEquals("a*ba*b", ProjectCommons.revertRemarkChange("a\\*ba\\*b"));
		TestCase.assertEquals("a*ba*b+c+d", ProjectCommons.revertRemarkChange("a\\*ba\\*b\\+c\\+d"));
		TestCase.assertEquals("a\\b\\c[]_d+e", ProjectCommons.revertRemarkChange("a\\\\b\\c\\[\\]\\_d\\+e"));
	}

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
