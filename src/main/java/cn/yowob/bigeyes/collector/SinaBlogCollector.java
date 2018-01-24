package cn.yowob.bigeyes.collector;

import cn.yowob.bigeyes.Configuration;
import cn.yowob.bigeyes.FilenameHelper;
import cn.yowob.bigeyes.ProjectCommons;
import com.overzealous.remark.Remark;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 抓取新浪博客 blog.sina.com.cn 上的文章
 * 不包括评论抓取, 因为我自己的新浪博客基本无评论, 懒得去写代码了
 *
 * User: chen.gang, glchengang@163.com
 * Date: 01/18/2018
 */
public class SinaBlogCollector extends AbstractCollector implements Collector {
	private static final Logger log = LoggerFactory.getLogger(SinaBlogCollector.class);
	private final String outputDir; //输出文件目录

	private String imagesDirName = null;
	private String articlePath = null; // .md 文章所在目录
	private String imagesPath = null; // 文章图片所在目录
	private String title = null;
	private String postTime = "2000-01-01 00:00:00";
	private static int articleCounter = 1; //处理文章数的计数器

	public SinaBlogCollector() {
		super();
		this.outputDir = Configuration.getInstance().getOutputDir() + "sinablog/";
	}

	/**
	 * 采集所有文章
	 * 新浪用户ID为一串数字, 比如博文目录页的地址为: http://blog.sina.com.cn/s/articlelist_1182391231_0_1.html
	 * 则其userId=1182391231
	 */
	@Override
	public void collect() {
		String blogUserId = config.get("blog_user_id").toString();
		if (StringUtils.isEmpty(blogUserId)) {
			System.err.println("--------------------------------------------------------------------");
			System.err.println("出错了,备份失败! 请先到 SinaBlogCollector.yml 文件配置好 blog_user_id ");
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
		log.info((articleCounter++) + ", " + url);
		try {
			Document document = ProjectCommons.getDocument(url);
			Element articlebody = document.selectFirst("#articlebody");
			title = articlebody.selectFirst(".articalTitle .titName").text();
			postTime = articlebody.selectFirst(".articalTitle .time").text();
			postTime = postTime.substring(1, postTime.length() - 1);
			imagesDirName = ProjectCommons.getArticleDir(title, postTime);

			Element e;
			e = articlebody.selectFirst(".articalTag .blog_class a");
			String categories = (e != null ? e.text().trim() : null);
			e = articlebody.selectFirst(".articalTag .blog_tag a");
			String tags = (e != null ? e.text().trim() : null);

			String categorieDir; //分类目录
			if (categories != null) {
				categorieDir = categories;
			} else {
				categorieDir = "其它";
			}
			articlePath = outputDir + categorieDir + "/";
			imagesPath = articlePath + imagesDirName + "/";
			/**
			 * http://remark.overzealous.com/manual/index.html
			 */
			ArrayList<String> lines = new ArrayList<>();
			{
				lines.add("---");
				lines.add("title: " + title);
				lines.add("date: " + postTime);
				lines.add("ourl: '" + url + "'");
				if (categories != null) {
					categories = categories.replaceAll(" ", ", ");
					lines.add("categories: [" + categories + "]");
				}
				if (tags != null) {
					tags = tags.replaceAll(" ", ", ");
					lines.add("tags: [" + tags + "]");
				}
				lines.add("---");
			}

			Element articalElement = articlebody.selectFirst(".articalContent");
			toLocalImage(articalElement, imagesPath);
			toHexoImage(articalElement);

			Remark remark = new Remark();
			String html = articalElement.html();
			String articalContent = remark.convertFragment(html);

			lines.add("\r\n");
			lines.add("原文链接: " + url);
			lines.add("\r\n");
			lines.add(articalContent);

			File file = new File(articlePath + imagesDirName + ".md");
			FileUtils.writeLines(file, lines, null);

			makeRightForMarkdown(file);

			log.info("success!");
		} catch (IOException e) {
			log.error("", e);
		}
	}

	/**
	 * 对 markdown 文件做再处理, 比如合并分行
	 * @param file
	 */
	private void makeRightForMarkdown(File file) {
		try {
			List<String> oldLines = FileUtils.readLines(file, "utf-8");
			List<String> newLines = new ArrayList<>(oldLines.size());
			for (int i = 0; i < oldLines.size(); i++) {
				String line = oldLines.get(i);
				String cleanLine = line.trim();
				if (cleanLine.startsWith("{% asset_img ") || cleanLine.startsWith("\\{% asset\\_img ")) {
					newLines.add("\n");
					newLines.add(cleanLine);
					newLines.add("\n");
				} else if (cleanLine.equals("")) {
					//
				} else {
					newLines.add(StringUtils.stripEnd(line, null));
				}
			}
			FileUtils.writeLines(file, newLines, "\r\n");
		} catch (Exception e) {
			log.error("", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * 处理图片： 1.转成本地链接， 2.下载
	 */
	private void toLocalImage(Element document, String saveDir) {
		for (Element element : document.select("img")) {
			String alt = element.attr("alt");

			String src = element.attr("real_src");
			if (StringUtils.isEmpty(src)) {
				src = element.attr("src");
			}
			log.debug("image link: {}", src);
			ProjectCommons.download(src, saveDir);
			String filename = FilenameHelper.getFilenameFromUrl(src);
			element.clearAttributes();
			element.attr("src", filename);
			if (alt == null || alt.indexOf(".") != -1 || alt.equals(src) || alt.equals(title)) {
				//element.attr("alt", "");
			} else {
				log.debug("{}--->{}", filename, alt);
				element.attr("alt", alt);
			}
		}
	}

	/**
	 * 将图片链接转化成 hexo 的格式
	 */
	private void toHexoImage(Element document) {
		for (Element element : document.select("img")) {
			String alt = element.attr("alt");
			String src = element.attr("src");
			if (StringUtils.isEmpty(alt)) {
				element.after("{% asset_img   " + src + " %}");
			} else {
				System.out.println("alt=" + alt);
				element.after("{% asset_img   " + src + " " + alt + " %}");
			}
			element.remove();
		}
	}

	/**
	 * 取得该用户所有文章链接
	 * @param userId 用户ID
	 */
	protected List<String> getAllArticleLinkList(String userId) {
		ArrayList<String> all = new ArrayList<>(200);
		//文章目录页的URL模板
		String template = "http://blog.sina.com.cn/s/articlelist_" + userId + "_0_${index}.html";
		int firstPageCount = -1;
		int i = 1;
		while (true) {
			String url = template.replace("${index}", String.valueOf(i++));
			List list = getOnePageArticleLinkList(url);
			if (firstPageCount == -1) {
				firstPageCount = list.size();
			}
			all.addAll(list);
			if (firstPageCount == 0 || firstPageCount != list.size() || i > 100) {
				break;
			}
		}
		log.debug("page count=" + (i - 1));
		return all;
	}

	/**
	 * 文章目录某一页所有的文章URL
	 * @param url 文章目录页URL地址
	 */
	protected List<String> getOnePageArticleLinkList(String url) {
		ArrayList<String> list = new ArrayList<>(60);
		Document document = ProjectCommons.getDocument(url);
		Elements elements = document.select(".atc_title");
		for (Element element : elements) {
			Elements titleElement = element.select("a");
			String link = titleElement.attr("href").trim();
			log.debug(titleElement.text() + ": " + link);
			list.add(link);
		}
		return list;
	}

}
