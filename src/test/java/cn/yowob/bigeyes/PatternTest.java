package cn.yowob.bigeyes;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTest {

	@Test
	public void test1() throws Exception {
		//			articalContent=articalContent.replaceAll("</?[^/?(br)|(p)|(img)|(BR)|(P)|(IMG)][^><]*>","");//保留br标签和p标签
		String line = "${brandName}${styleName} ${goodsTypeName}";
		String reg = "\\$\\{[a-zA-Z]+\\}";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			String matchWord = matcher.group(0);
			String property = matchWord.substring(2, matchWord.length() - 1);
			System.out.println(matchWord + "-->" + property);
			line = line.replace(matchWord, "aaa");
		}
		System.out.println(line);
	}

	@Test
	public void test2() throws Exception {
		String str = "<ul><li>decora<div><p>ting</li></ul>";

		String regEx_html = "<[^>]+>";
		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(str);

		str = m_html.replaceAll("");
		System.out.println(str);
	}

	@Test
	public void test3() {
		String content = "<a href=\"URL\">";
		String pattern = "href=\"([^\"]*)\"";
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(content);
		if (m.find()) {
			System.out.println(m.group(1));
		}
	}

	@Test
	public void test4() {
		String str = "<a href=\"http://blog.sina.com.cn/main/html/showpic.html#url=http://s8.album.sina.com.cn/pic/54bde01c02000wnj\" target=\"_blank\"><img alt=\"http://s8.album.sina.com.cn/pic/54bde01c02000wnj\" border=\"1\" src=\"92_files/54bde01c02000wnj[1].jpg\" title=\"http://s8.album.sina.com.cn/pic/54bde01c02000wnj\"></a>";
		str += "<a href=\"http://blog.sina.com.cn/main/html/showpic.html#url=http://s8.album.sina.com.cn/pic/54bde01c02000wnj\" target=\"_blank\"><img2 alt=\"http://s8.album.sina.com.cn/pic/54bde01c02000wnj\" border=\"1\" src=\"92_files/54bde01c02000wnj[1].jpg\" title=\"http://s8.album.sina.com.cn/pic/54bde01c02000wnj\"></a>";
		str += "<A href=\"http://blog.sina.com.cn/main/html/showpic.html#url=http://s8.album.sina.com.cn/pic/54bde01c02000wnj\" target=\"_blank\"><img3 alt=\"http://s8.album.sina.com.cn/pic/54bde01c02000wnj\" border=\"1\" src=\"92_files/54bde01c02000wnj[1].jpg\" title=\"http://s8.album.sina.com.cn/pic/54bde01c02000wnj\"></A>";
		str = str.replaceAll("(<[aA] href=\"http://blog.sina)[^>]*(target=\"_blank\">)", "");
		str = str.replaceAll("(\"></[aA]>)", "\">");
		str = str.replaceAll("(<[(IMG)|(img)] alt=\")[^\"]*\"", "<IMG ");
		str = str.replaceAll("(title=\"http://)[^\"]*\"", "");

		System.out.println("--");
		System.out.println(str);
	}
}