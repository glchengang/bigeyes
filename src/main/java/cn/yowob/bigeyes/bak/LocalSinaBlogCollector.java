package cn.yowob.bigeyes.bak;

import cn.yowob.bigeyes.Configuration;
import cn.yowob.bigeyes.collector.AbstractCollector;
import cn.yowob.bigeyes.collector.Collector;
import cn.yowob.bigeyes.collector.WebCollectConfig;
import cn.yowob.bigeyes.collector.XpathConfig;
import common.format.SimpleFormatProcesser;
import common.utils.Dom4jHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 这个程序类对于其他人应该是没什么用的。
 *
 * 此采集类主要用于处理我早年间，当时我是两年用爬虫软件做一次备份，备份完再把老文章删除掉。
 * 这些备份文章是HTML格式存放，以数字做文件名，如 78.html，其存放图片目录名则为: 78_files。
 * 然后这些文件还用Windows的CHM来组织成一体。但当我使用Mac系统后，发现用CHM真是一个大错误。
 * 最好的方式就是最简单的方式：
 * （1）应该用Markdown格式保存，而不是HTML
 * （2）应该以时间和标题做文件，而不是编号
 *
 * 所以写了这个程序来做这个转化工作。
 * 完整的工作流程如下：
 * （1）爬虫软件做的备份是整个页面，用这个类把文章抽取了出来，做一个初级的清洗工作，然后重新保存为HMTL。
 * （2）用Github上的一个叫 h2m 的 node.js 小工具，把清洗过的HTML，转换成 markdown格式
 * （3）再用 LocalSinaBlogMarkdownHandler 对 *.md 文件做一些个性化的处理
 *
 * User: chen.gang, glchengang@163.com
 * Date: 01/16/2018
 */
public class LocalSinaBlogCollector extends AbstractCollector implements Collector {
	private static final Logger log = LoggerFactory.getLogger(LocalSinaBlogCollector.class);

	@Override
	public synchronized void collect() {
		int i = 1;
		int collectMaxCount = Configuration.getInstance().getCollectMaxCount();
		File dir = new File((String) config.get("local_html_dir"));
		Collection<File> files = FileUtils.listFiles(dir, new String[]{"html"}, false);
		for (File file : files) {
			collect(file.getName());
			if (collectMaxCount > 0 && i++ >= collectMaxCount) {
				break;
			}
		}
	}

	protected void collect(String filename) {
		String outputDir = Configuration.getInstance().getOutputDir() + "blog";
		try {
			SimpleFormatProcesser processer = new SimpleFormatProcesser();
			Map<String, String> valueMap = new HashMap();
			for (WebCollectConfig collectConfig : getWebCollectConfigList()) {
				/**
				 * 将URL模板转成真实的URL
				 */
				String url = collectConfig.getUrl();
				{
					HashMap data = new HashMap();
					data.put("filename", filename);
					url = processer.process(url, data);
					log.debug(url);
				}
				/**
				 * 抓取页面
				 */
				Document doc = Dom4jHelper.getDocument(url);
				if (doc == null) {
					continue;
				}

				for (XpathConfig e : collectConfig.getXpathConfigList()) {
					handle(doc, e, valueMap);
				}
			}
			String postTime = valueMap.get("时间");
			if (postTime != null) {
				postTime = postTime.replace("　", " ");
			}
			String content = valueMap.get("内容");
			content = cleanImageHref(content);
			StringBuilder buf = new StringBuilder(10240);
			buf.append("<!DOCTYPE html>");
			buf.append("<html lang=\"zh-Hans\">");
			buf.append("<head>");
			buf.append("<meta charset=\"UTF-8\"/>");
			buf.append("</head>");
			buf.append("<body>");
			buf.append("<div id=\"title\">" + valueMap.get("标题") + "</div>");
			buf.append("<div id=\"postTime\">" + postTime + "</div>");
			buf.append("<div id=\"content\">");
			buf.append(content);
			buf.append("</div>");
			buf.append("</body>");
			buf.append("</html>");
			FileUtils.write(new File(outputDir + "/" + filename), buf, "utf-8");
		} catch (IOException e) {
			log.error("", e);
		}
	}

	/**
	 * 将图片外包围的链接清理干净
	 */
	private String cleanImageHref(String str) {
		//去掉包住图片的链接
		str = str.replaceAll("(<[aA] href=\"http://blog.sina)[^>]*(target=\"_blank\">)", "");
		str = str.replaceAll("(\"></[aA]>)", "\">");
		//去掉alt
		str = str.replaceAll("( alt=\"http://)[^\"]*\"", " ");
		str = str.replaceAll("( title=\"http://)[^\"]*\"", "");
		return str;
	}

	private void handle(Document doc, XpathConfig e, Map<String, String> valueMap) {
		XPath xpath = Dom4jHelper.getXpath(e.getXpath());
		Node node = xpath.selectSingleNode(doc);
		if (node == null) {
			log.error("没找到!----------key=" + e.getKey() + ", xpath=" + e.getXpath());
			valueMap.put(e.getKey(), "没找到");
			return;
		}
		String content;
		if (e.getHandleType() == 1) {
			content = node.getStringValue();
		} else {
			content = node.asXML();
		}
		if (StringUtils.isBlank(content)) {
			log.error("无内容,key=" + e.getKey());
		}
		log.debug("key={}, value={}", e.getKey(), content);
		valueMap.put(e.getKey(), content);
	}

}
