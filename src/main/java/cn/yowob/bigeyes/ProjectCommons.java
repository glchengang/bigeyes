package cn.yowob.bigeyes;

import cn.yowob.bigeyes.collector.WebCollectConfig;
import cn.yowob.bigeyes.collector.XpathConfig;
import common.utils.CloseHelper;
import common.utils.CommonException;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 * User: chen.gang, glchengang@163.com
 * Date: 2018-01-15
 */
public class ProjectCommons {
	private static final Logger log = LoggerFactory.getLogger(ProjectCommons.class);

	public static void replace(List list, int i, Object o) {
		list.remove(i);
		list.add(i, o);
	}

	/**
	 * remark 给很多字符中了斜杠，这里把它们复原
	 */
	public static String revertRemarkChange(String line) {
		if (line == null)
			return null;
//		line = StringUtils.replace(line, "\\*", "*");
//		line = StringUtils.replace(line, "\\_", "_");
//		line = StringUtils.replace(line, "\\-", "-");
//		line = StringUtils.replace(line, "\\{", "{");
//		line = StringUtils.replace(line, "\\}", "}");
//		line = StringUtils.replace(line, "\\[", "[");
//		line = StringUtils.replace(line, "\\]", "]");
//		line = StringUtils.replace(line, "\\+", "+");
//		line = StringUtils.replace(line, "\\\\", "\\");
		String reg = "\\\\[^a-zA-Z0-9]";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			String matchWord = matcher.group(0);
			String newWord = matchWord.substring(1);
			//System.out.println(matchWord + "--->" + newWord);
			line = line.replace(matchWord, newWord);
		}
		return line;
	}

	public static StringBuilder toHtml(String content, String comments, Map<String, String> metas) {
		StringBuilder buf = new StringBuilder(10240);
		buf.append("<!DOCTYPE html>\n");
		buf.append("<html lang=\"zh-Hans\">\n");
		buf.append("<head>\n");
		buf.append("<meta charset=\"UTF-8\"/>\n");
		buf.append("</head>\n");
		buf.append("<body>\n");
		buf.append("<div id=\"my_post_meta_data\">\n");
		for (String key : metas.keySet()) {
			buf.append("<span class=\"" + key + "\">" + metas.get(key) + "</span>, \n");
		}
		buf.append("</div>\n");
		buf.append("<div id=\"my_post_body\">\n");
		buf.append("<div id=\"my_post_content\">\n").append(content).append("</div>\n");
		if (comments != null) {
			buf.append("<div id=\"my_post_comments\">\n").append(comments).append("</div>\n");
		}
		buf.append("</div>\n");
		buf.append("</body>");
		buf.append("</html>");
		return buf;
	}

	public static StringBuilder toHtml(String bodyHtml, Map<String, String> metas) {
		StringBuilder buf = new StringBuilder(10240);
		buf.append("<!DOCTYPE html>\n");
		buf.append("<html lang=\"zh-Hans\">\n");
		buf.append("<head>\n");
		buf.append("<meta charset=\"UTF-8\"/>\n");
		buf.append("</head>\n");
		buf.append("<body>\n");
		buf.append("<div id=\"my_post_meta_data\">\n");
		for (String key : metas.keySet()) {
			buf.append("<span class=\"" + key + "\">" + metas.get(key) + "</span>, \n");
		}
		buf.append("</div>\n");
		buf.append("<div id=\"my_post_body\">\n");
		buf.append(bodyHtml);
		buf.append("</div>\n");
		buf.append("</body>\n");
		buf.append("</html>\n");
		return buf;
	}

	public static String getArticleDir(String title, String postTime) {
		postTime = postTime.substring(0, postTime.indexOf(" "));
		postTime = postTime.replaceAll("-", "");
		return postTime + "_" + title;
	}

	public static Document getDocument(String url) {
		try {
			if (url.startsWith("http://") || url.startsWith("https://")) {
				return Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; rv:30.0) Gecko/20100101 Firefox/30.0").get();
			} else { //绝对路径
				return Jsoup.parse(new File(url), "utf-8");
			}
		} catch (Exception e) {
			log.error("read failure: " + url, e);
			throw new RuntimeException("read failure: " + url);
		}
	}

	public static Document getDocumentByHtml(String html) {
		try {
			return Jsoup.parseBodyFragment(html);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 下载图片
	 * @param srcFileURL "http://www.blogjava.net/images/blogjava_net/chengang/jbpm/20060823_image001.jpg"å
	 * @param saveDir 文件保存目录： "/Users/chen/workspace/oldblog/out/"
	 */
	public static void download(String srcFileURL, String saveDir) {
		DataInputStream in = null;
		FileOutputStream out = null;
		try {
			String filename = FilenameHelper.getFilenameFromUrl(srcFileURL);
			File saveFile = new File(saveDir + "/" + filename);
			if (saveFile.exists()) {
				log.debug("file exists. skip download: {}", saveFile.getAbsolutePath());
				return;
			}
			URL url = new URL(srcFileURL);
			in = new DataInputStream(url.openStream());

			FileUtils.forceMkdirParent(saveFile);
			out = new FileOutputStream(saveFile);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
			out.write(output.toByteArray());
		} catch (Exception e) {
			log.error(srcFileURL, e);
		} finally {
			CloseHelper.close(in);
			CloseHelper.close(out);
		}
	}

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
