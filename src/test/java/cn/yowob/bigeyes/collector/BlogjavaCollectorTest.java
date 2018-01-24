package cn.yowob.bigeyes.collector;

import org.junit.Test;

/**
 *
 * User: chen.gang, glchengang@163.com
 * Date: 01/19/2018
 */
public class BlogjavaCollectorTest {
	public static final String TEST_RESOURCES_DIR = "/Users/chen/workspace/bigeyes/src/test/resources/"; //测试文件
	private BlogjavaCollector spider = new BlogjavaCollector();

	@Test
	public void collect() throws Exception {
		spider.collect();
//		spider.collect("chengang");
	}

	@Test
	public void collectOne() throws Exception {
//		String url = TEST_RESOURCES_DIR + "blogjava_test_01.html";
		String url = TEST_RESOURCES_DIR + "blogjava_test_02_img.html";
//		String url = TEST_RESOURCES_DIR + "blogjava_test_03_comments.html";
//		String url = "http://www.blogjava.net/chengang/archive/2006/08/23/65346.html";
		spider.collectOne(url);
	}

	@Test
	public void convertToMarkdown() throws Exception {
		String url = "/Users/chen/workspace/oldblog/in/a.html";
		spider.convertToMarkdown(url);
	}

	@Test
	public void cleanHtml() throws Exception {
		//String url = "http://www.blogjava.net/chengang/archive/2006/08/23/65346.html";
		String url = "/Users/chen/workspace/oldblog/in/java1.html";
		spider.cleanHtml(url);
	}

	@Test
	public void getAllArticleLinkList() throws Exception {
		spider.getAllArticleLinkList("chengang");
	}

	@Test
	public void getOnePageArticleLinkList() throws Exception {
		spider.getOnePageArticleLinkList("http://www.blogjava.net/chengang/default.html?page=1");
	}

}
