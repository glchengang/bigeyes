package cn.yowob.bigeyes.collector;

import org.junit.Test;

import java.util.List;

/**
 *
 * User: chen.gang, glchengang@163.com
 * Date: 01/18/2018
 */
public class SinaBlogCollectorTest {
	private SinaBlogCollector spider = new SinaBlogCollector();

	@Test
	public void testGetArticleListOne() throws Exception {
		String url = "http://blog.sina.com.cn/s/articlelist_1421729820_0_5.html";
		List<String> articleList = spider.getAllArticleLinkList(url);
		for (String o : articleList) {
			System.out.println(o);
		}
		System.out.println(articleList.size());
	}

	@Test
	public void collect() throws Exception {
		spider.collect();
//		spider.collect("1182391231");
	}

	@Test
	public void collectOne() throws Exception {
//		String url= "file:///Users/chen/workspace/oldblog/scr/${filename}";
		//String url ="http://blog.sina.com.cn/s/blog_56c35a550102xvbx.html?tj=1";
//		String url = "/Volumes/M320/Downloads/b.html";
//		String url = "http://blog.sina.com.cn/s/blog_54bde01c0100ljzm.html";
//		String url = "http://blog.sina.com.cn/s/blog_61ff32de0102y04e.html?tj=1";
//		String url = "http://blog.sina.com.cn/s/blog_61ff32de0102y04e.html";
		String url = "http://blog.sina.com.cn/s/blog_6b50cc810102xcu8.html?tj=1";
		spider.collectOne(url);
	}

}
