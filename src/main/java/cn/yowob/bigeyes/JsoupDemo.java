package cn.yowob.bigeyes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class JsoupDemo {
	public static void main(String[] args) throws IOException {
		String url = "http://blog.sina.com.cn/s/articlelist_1421729820_0_1.html";
		Document document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; rv:30.0) Gecko/20100101 Firefox/30.0").get();
		Elements elements = document.select(".atc_title");
		for (Element element : elements) {
			Elements titleElement = element.select("a");
			String title = titleElement.text();
			String link = titleElement.attr("href").trim();
			System.out.println(title + ": " + link);
//
//			Elements dataElement = element.select(".date");
//			Elements autherElement = dataElement.select("a");
//			String auther = autherElement.text();
//			autherElement.remove();
//			String date = dataElement.text();
//			String detail = element.select(".detail").text();
//			System.out.println("链接：        " + link);
//			System.out.println("标题：        " + title);
//			System.out.println("作者：        " + auther);
//			System.out.println("发布时间： " + date);
//			System.out.println("详细信息： " + detail);
//			System.out.println();
//			System.out.println();
		}
		System.out.println(elements.size());
	}
}