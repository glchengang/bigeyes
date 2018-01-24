package cn.yowob.bigeyes.collector;

import cn.yowob.bigeyes.Configuration;
import cn.yowob.bigeyes.FilenameHelper;
import cn.yowob.bigeyes.ProjectCommons;
import com.overzealous.remark.Options;
import com.overzealous.remark.Remark;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 抓取 www.blogjava.net 上的技术类文章(包括评论回复)
 * 我的技术博客: http://www.blogjava.net/chengang
 *
 * User: chen.gang, glchengang@163.com
 * Date: 01/18/2018
 */
public class BlogjavaCollector extends AbstractCollector implements Collector {
	private static final Logger log = LoggerFactory.getLogger(BlogjavaCollector.class);
	private final String outputDir; //输出文件目录

	private static final String META_KEY_TITLE = "title";
	private static final String META_KEY_POST_TIME = "date";
	private static final String META_KEY_CATEGORIES = "categories";
	private static final String META_KEY_READS = "阅读数";
	private static final String META_KEY_COMMENTS = "评论数";
	//代码块标志
	private static final String BLOCK_MARK = "－－－－－－－－－－－－ThisIsABlock－－－－－－－－－－－－";
	private static final String NEW_LINE = "\n";  //Windows:\r\n, Linux/Unix:\n

	private String imagesDirName = null;
	private String imagesPath = null; // 文章图片所在目录

	private String title = null;
	private String postTime = "2000-01-01 00:00:00"; //如果找不到发表时间，就用这个默认时间
	private HashMap<String, String> metaDataMap = new HashMap<>(); //元数据MAP

	public BlogjavaCollector() {
		super();
		this.outputDir = Configuration.getInstance().getOutputDir() + "blogjava/";
	}

	@Override
	public void collect() {
		String blogUserId = (String) config.get("blog_user_id");
		if (StringUtils.isEmpty(blogUserId)) {
			System.err.println("--------------------------------------------------------------------");
			System.err.println("出错了,备份失败! 请先到 BlogjavaCollector.yml 文件配置好 blog_user_id ");
			System.err.println("--------------------------------------------------------------------");
			return;
		}
		List<String> linkList = getAllArticleLinkList(blogUserId);
		for (String url : linkList) {
			collectOne(url);
		}
		System.out.println("采集完毕. 输出目录: " + this.outputDir);
	}

	protected void collectOne(String url) {
		try {
			url = saveOriginalHtml(url);   //（1）先保存原始页面
			url = cleanHtml(url);          //（2）清理原始页面，为转markdown做好准备
			url = convertToMarkdown(url);  //（3）用remark工具将HTML转换成markdown格式
			handleMarkdown(url);           //（4）对最终的markdown文件再修正
		} catch (Exception e) {
			log.error("", e);
		}
	}

	/**
	 * 保存原始页面
	 */
	protected String saveOriginalHtml(String url) {
		try {
			log.info(url);
			Document document = ProjectCommons.getDocument(url);
			Element body = document.selectFirst("#main");
			body.select("script, style, .commentform, #divRefreshComments").remove();
			/**
			 *
			 */
			metaDataMap.clear();
			title = body.selectFirst("#viewpost1_TitleUrl").text();
			//posted on 2006-08-23 19:26 陈刚 阅读(44773) 评论(36) 编辑 收藏 所属分类: jBPM
			String postfootStr = body.selectFirst(".postfoot").text();
			String[] split = postfootStr.split(" ");
			for (int i = 0; i < split.length; i++) {
				String s = split[i].trim();
				if (s.equals("posted")) {
					postTime = split[i + 2] + " " + split[i + 3] + ":00";
				}
				if (s.startsWith("阅读")) {
					metaDataMap.put(META_KEY_READS, StringUtils.substringBetween(s, "(", ")"));
				}
				if (s.startsWith("评论")) {
					metaDataMap.put(META_KEY_COMMENTS, StringUtils.substringBetween(s, "(", ")"));
				}
				if (s.contains("分类")) {
					metaDataMap.put(META_KEY_CATEGORIES, split[i + 1]);
				}
			}
			metaDataMap.put(META_KEY_TITLE, title);
			metaDataMap.put(META_KEY_POST_TIME, postTime);
			log.info("" + metaDataMap);
			/**
			 *
			 */
			this.imagesDirName = ProjectCommons.getArticleDir(title, postTime);
			this.imagesPath = this.outputDir + this.imagesDirName + "/";
			toLocalImage(document, imagesPath);
			StringBuilder html = ProjectCommons.toHtml(body.html(), metaDataMap);

			String returnURL = this.outputDir + "original/" + imagesDirName + ".html";
			FileUtils.write(new File(returnURL), html, "utf-8");
			return returnURL;
		} catch (Exception e1) {
			log.error("", e1);
			throw new RuntimeException(e1.getMessage());
		}
	}

	/**
	 * 清理原始页面，为转markdown做好准备
	 */
	protected String cleanHtml(String url) {
		try {
			Document document = ProjectCommons.getDocument(url);

			toHexoImage(document);
			handleCodeBlock(document);
			String content = handleContent(document);
			String comments = handleComments(document);

			StringBuilder html = ProjectCommons.toHtml(content, comments, metaDataMap);
			String returnURL = this.outputDir + "tmp/" + imagesDirName + ".clean.html";
			FileUtils.write(new File(returnURL), html, "utf-8");
			return returnURL;
		} catch (Exception e1) {
			log.error("", e1);
			throw new RuntimeException(e1.getMessage());
		}
	}

	/**
	 * 清理html大量的标签属性，避免Remark转换出错
	 */
	private String handleContent(Document document) {
		Element element = document.selectFirst(".postbody");
		for (Element e : element.getAllElements()) {
			e.clearAttributes();
		}
		return element.html();
	}

	/**
	 * 处理评论回复
	 */
	private String handleComments(Document document) {
		Element element = document.selectFirst("#comments");
		if (element == null) {
			return null;
		}
		element.prepend(BLOCK_MARK);
		element.append(BLOCK_MARK);
		element.select("h2, h3").remove();
		for (Element postEle : element.select("div.post")) {
			Elements nameHref = postEle.select(".postfoot a");
			String href = nameHref.attr("href");
			nameHref.unwrap();
			if (!StringUtils.isBlank(href)) {
				nameHref.append(" | " + href);
			}
			//每个回复之间加一行分割线
			postEle.prepend("<br/>－－－－－－－－－－－－－－－－－－－－－－－－<br/>");
		}
		for (Element e : element.getAllElements()) {
			e.clearAttributes();
		}
		return element.html();
	}

	/**
	 * 用remark工具将HTML转换成markdown格式
	 */
	protected String convertToMarkdown(String url) {
		try {
			ArrayList<String> lines = new ArrayList<>();
			lines.add("---");
			lines.add(META_KEY_TITLE + ": '" + title + "'");
			lines.add(META_KEY_POST_TIME + ": " + postTime);
			Set<String> keys = new HashSet<>(this.metaDataMap.keySet());
			keys.remove(META_KEY_TITLE);
			keys.remove(META_KEY_POST_TIME);
			for (String key : keys) {
				lines.add(key + ": " + this.metaDataMap.get(key));
			}
			lines.add("---");

			Document document = ProjectCommons.getDocument(url);
			this.toHexoImage(document);

			/**
			 * http://remark.overzealous.com/manual/index.html
			 */
			Options opts = Options.markdown();
			opts.tables = Options.Tables.CONVERT_TO_CODE_BLOCK;
			Remark remark = new Remark(opts);
			String content = document.selectFirst("#my_post_content").html();
			content = remark.convertFragment(content);
			lines.add(content);
			/**
			 * comments
			 */
			Element commentsEle = document.selectFirst("#my_post_comments");
			if (commentsEle != null) {
				lines.add("-------------");
				lines.add("## 评论");
				String comments = remark.convert(commentsEle.html());
				lines.add(comments);
			}

			String returnURL = this.outputDir + "tmp/" + imagesDirName + ".md";
			FileUtils.writeLines(new File(returnURL), lines, NEW_LINE);
			return returnURL;
		} catch (Exception e1) {
			log.error("", e1);
			throw new RuntimeException(e1.getMessage());
		}
	}

	/**
	 * 对最终的markdown文件再修正
	 */
	protected String handleMarkdown(String url) {
		try {
			List<String> oldLines = FileUtils.readLines(new File(url), "utf-8");
			List<String> newLines = new ArrayList<>(oldLines.size());
			boolean isBlock = false;
			for (int i = 0; i < oldLines.size(); i++) {
				String line = oldLines.get(i);
				String cleanLine = line.trim();
//				boolean isTitle = (!isBlock && (cleanLine.startsWith("#") || cleanLine.startsWith("---------")));
//				if (isTitle) {
//					newLines.add(NEW_LINE);
//				}
				cleanLine = ProjectCommons.revertRemarkChange(cleanLine);
				if (cleanLine.equals(BLOCK_MARK)) {
					newLines.add("```");
					isBlock = !isBlock;
				} else if (cleanLine.startsWith("l ")) {
					newLines.add(cleanLine.replaceFirst("l ", "- "));
				} else if (cleanLine.startsWith("| ")) {
					newLines.add(cleanLine);
				} else if (isBlock) { //处理代码块里很多的粗体字
					if (cleanLine.equals("") || cleanLine.equals(" ")) {
						// 压缩空行
					} else {
						while (cleanLine.indexOf("  ") != -1) {
							cleanLine = StringUtils.replace(cleanLine, "  ", " ");//压缩行内的空格
						}
						newLines.add(cleanLine);
					}
				} else {
					newLines.add(line);
				}
//				if (isTitle) {
//					newLines.add(NEW_LINE);
//				}
			}
			String returnURL = this.outputDir + imagesDirName + ".md";
			FileUtils.writeLines(new File(returnURL), newLines, NEW_LINE);
			return returnURL;
		} catch (Exception e1) {
			log.error("", e1);
			throw new RuntimeException(e1.getMessage());
		}
	}

	/**
	 * 处理图片： 1.转成本地链接， 2.下载
	 */
	private void toLocalImage(Document document, String saveDir) {
		for (Element element : document.select("img")) {
			String alt = element.attr("alt");
			String src = element.attr("src");
			log.debug(src);
			if (src.endsWith("/xml.gif")  //
					|| src.endsWith("/dot.gif")//
					|| src.endsWith(".html?webview=1")//
					) {
				element.remove();
				continue;
			}

			if (!src.startsWith("http")) {
				src = "http://www.blogjava.net" + src;
			}

			ProjectCommons.download(src, saveDir);

			String filename = FilenameHelper.getFilenameFromUrl(src);
			element.clearAttributes();
			element.attr("src", filename);
			if (alt == null || alt.indexOf(".") != -1 || alt.equals(src)) {
				element.attr("alt", "");
			} else {
				log.debug("{}--->{}", filename, alt);
				element.attr("alt", alt);
			}
		}
	}

	/**
	 * 将图片链接转化成 hexo 的格式
	 */
	private void toHexoImage(Document document) {
		for (Element element : document.select("img")) {
			String alt = element.attr("alt");
			String src = element.attr("src");
			if (StringUtils.isEmpty(alt)) {
				element.after("{% asset_img   " + src + " %}");
			} else {
				element.after("{% asset_img   " + src + " " + alt + " %}");
			}
			element.remove();
		}
	}

	/**
	 * 处理代码块
	 */
	private void handleCodeBlock(Document document) {
		for (Element blockElement : document.select("div[style*='background:'], div[style*='background-color:']")) {
			for (Element e : blockElement.select("b,font,span")) {
				e.unwrap();
			}
			blockElement.prepend("<br/>" + BLOCK_MARK + "<br/>");
			blockElement.append("<br/>" + BLOCK_MARK + "<br/>");
		}
	}

	/**
	 * 取得所有文章的URL
	 */
	protected List<String> getAllArticleLinkList(String userId) {
		try {
			File file = new File(outputDir + "link_list.txt"); //文章链接列表的缓存
			if (file.exists()) {
				return FileUtils.readLines(file, "utf-8");
			}
			ArrayList<String> all = new ArrayList<>(200);
			int firstPageCount = -1; //第一页的文章数
			int i = 1;
			while (true) {
				String url = "http://www.blogjava.net/" + userId + "/default.html?page=" + (i++);
				List list = getOnePageArticleLinkList(url);
				if (firstPageCount == -1) {
					firstPageCount = list.size();
				}
				all.addAll(list);
				if (firstPageCount == 0 || firstPageCount != list.size() || i > 100) {
					break;
				}
			}
			FileUtils.writeLines(file, all, NEW_LINE);
			return all;
		} catch (Exception e) {
			log.error("", e);
			throw new RuntimeException("");
		}
	}

	/**
	 * 取得文章目录中某页目录的文章链接数
	 */
	protected List<String> getOnePageArticleLinkList(String url) {
		ArrayList<String> list = new ArrayList<>(60);
		Document document = ProjectCommons.getDocument(url);
		Elements elements = document.select("#main > div.post > h2 > a");
		for (Element titleElement : elements) {
			String title = titleElement.text();
			String link = titleElement.attr("href").trim();
			log.debug(title + ": " + link);
			list.add(link);
		}
		log.debug("本页文章数：" + elements.size());
		return list;
	}

}
