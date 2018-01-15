package cn.yowob.bigeyes;

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
public class YumlTest {

	@Test
	public void weather() throws Exception {
		FileInputStream fis = ProjectCommons.getFileInputStream("weather2.yml");
		Map<String, Object> conf = new Yaml().load(fis);
		CloseHelper.close(fis);

		List<Map> pages = (List<Map>) conf.get("pages");
		for (Map page : pages) {
			String name = (String) page.get("name");
			System.out.println("name=" + name);
			String url = (String) page.get("url");
			System.out.println("url=" + url);
//

			List xpaths = (List) page.get("xpaths");
			for (Object xpath : xpaths) {
				//System.out.println(xpath.getClass());
				List aaaa = (List) xpath;
				for (Object o : aaaa) {
					System.out.println("    " + o);
				}
			}
		}

	}

}
