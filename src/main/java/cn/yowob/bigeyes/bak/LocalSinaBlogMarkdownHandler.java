package cn.yowob.bigeyes.bak;

import common.utils.DateHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 以自己的个性化需求，处理用 h2m 转化后的 *.md 新浪博客文件
 * User: chen.gang, glchengang@163.com
 * Date: 01/17/2018
 */
public class LocalSinaBlogMarkdownHandler {

	private static final String MD_SOURCE_DIR = "/Users/chen/myblog/source/_posts/test/"; //markdown的源文件目录

	public void handle() {
		File dir = new File(MD_SOURCE_DIR);
		Collection<File> files = FileUtils.listFiles(dir, new String[]{"md"}, false);
		for (File file : files) {
			//System.out.println(file.getName());
			handle3(file.getName());
		}
	}

	public void handle3(String filename) {
		try {
			int strIndex = filename.indexOf(".");
			String imageFilesDir = MD_SOURCE_DIR + filename.substring(0, strIndex) + "/";
			//System.out.println(imageFilesDir);
			if (!new File(imageFilesDir).exists()) {
				return;
			}
			File mdfile = new File(MD_SOURCE_DIR + filename);

			boolean isFoundImage = false;
			List<String> lines = FileUtils.readLines(mdfile, "utf-8");
			int fileIndex = 0;
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
//				{% asset_img 54bde01c020001nn[1].jpg %}
				if (line.contains("{% asset_img ")) {
					String assetImgLine = line.replaceFirst("asset_img", "");
					if (assetImgLine.indexOf("asset_img") != -1) {
						System.err.println("一行不只一个图片设置, " + filename + ", " + line);
						continue;
					}
					String imageFilename = StringUtils.substringBetween(line, "{% asset_img ", " %}");
					imageFilename = imageFilename.trim();
					//System.out.println(imageFilename);
					strIndex = imageFilename.indexOf(".");
					String mainName = imageFilename.substring(0, strIndex);
					String extName = imageFilename.substring(strIndex);
//					System.out.println(mainName);
//					System.out.println(extName);
					if (NumberUtils.isDigits(mainName)) {
						System.err.println("已经含有数字图片：" + imageFilesDir);
						return;
					}
					isFoundImage = true;
					fileIndex++;
					String newImageFilename = fileIndex + extName;
					File oldImagefile = new File(imageFilesDir + imageFilename);
					if (oldImagefile.exists()) {
						File destFile = new File(imageFilesDir + newImageFilename);
						if (destFile.exists()) {
							System.err.println("exist image：" + destFile.getName());
							return;
						}
						FileUtils.moveFile(oldImagefile, destFile);
					} else {
						FileUtils.write(new File(imageFilesDir + fileIndex + "___" + mainName + ".NOF_FOUND"), "", "utf-8");
					}
					String newLine = "{% asset_img " + newImageFilename + " %}";
					lines.remove(i);
					lines.add(i, newLine);
					//System.out.println(newLine);
				}
			}
			if (isFoundImage) {
				System.out.println(imageFilesDir);
				FileUtils.writeLines(mdfile, lines, "\n");
			}
//
//			String dayStr = DateHelper.formatDate(DateHelper.parseDate(created), "yyyy-MM-dd");
//			FileUtils.writeLines(new File(MD_SOURCE_DIR + dayStr + ".md"), lines, "\n");
//			//删除原文件
//			FileUtils.deleteQuietly(new File(MD_SOURCE_DIR + filename));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handle2(String filename) {
		try {
			List<String> lines = FileUtils.readLines(new File(MD_SOURCE_DIR + filename), "utf-8");
			String title = lines.get(0);
			lines.remove(0);
			title = title.replace("　", " ");
			title = title.trim();
//			System.out.println(title);
			String created = StringUtils.substringBetween(title, "(", ")");
			title = title.replaceAll("\\(.*\\)", "");
//			System.out.println(title+"("+created);

			int i = 0;
			lines.add(i++, "---");
			{
				lines.add(i++, "title: '" + title + "'");
				lines.add(i++, "categories:");
				lines.add(i++, "  - sina blog");
				lines.add(i++, "  - others");
				lines.add(i++, "date: " + created);
			}
			lines.add(i++, "---");

			String dayStr = DateHelper.formatDate(DateHelper.parseDate(created), "yyyy-MM-dd");
			FileUtils.writeLines(new File(MD_SOURCE_DIR + dayStr + ".md"), lines, "\n");
//			//删除原文件
//			FileUtils.deleteQuietly(new File(MD_SOURCE_DIR + filename));
		} catch (Exception e) {
			try {
				FileUtils.write(new File(MD_SOURCE_DIR + filename + ".error"), "", "utf-8");
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
	}

	public void handle(String filename) {
		try {
			if (filename.contains("-")) {
				return;
			}
			List<String> lines = FileUtils.readLines(new File(MD_SOURCE_DIR + filename), "utf-8");
			String title = lines.get(0);
			String updated = lines.get(2);
			if (StringUtils.isBlank(title)) {
				throw new RuntimeException("标题为空");
			}
			if (StringUtils.isBlank(updated)) {
				throw new RuntimeException("时间为空");
			}
			lines.remove(0);
			lines.remove(0);
			lines.remove(0);

			int i = 0;
			lines.add(i++, "---");
			String created;
			if (title.contains("一个人走")) {
				title = cleanTitle(title);
				created = DateHelper.formatDateTime(buildDateFromTitle(title));
				lines.add(i++, "title: '" + title + "'");
				lines.add(i++, "tags:");
				lines.add(i++, "  - 旅行");
				lines.add(i++, "  - 单车旅行");
				lines.add(i++, "categories:");
				lines.add(i++, "  - \"路，一个人走\"");
				lines.add(i++, "date: " + (created == null ? updated : created));
				lines.add(i++, "updated: " + updated);
			} else if (title.contains("我的长征")) {
				created = updated;
				lines.add(i++, "title: '" + title + "'");
				lines.add(i++, "categories:");
				lines.add(i++, "  - sina blog");
				lines.add(i++, "  - 我的长征");
				lines.add(i++, "date: " + updated);
			} else {
				created = updated;
				lines.add(i++, "title: '" + title + "'");
				lines.add(i++, "categories:");
				lines.add(i++, "  - sina blog");
				lines.add(i++, "  - others");
				lines.add(i++, "date: " + updated);
				lines.add(i++, "updated: " + updated);
			}
			lines.add(i++, "---");

			//写入新文件
			String dayStr;
			if (created != null) {
				dayStr = DateHelper.formatDate(DateHelper.parseDate(created), "yyyy-MM-dd");
			} else {
				dayStr = DateHelper.formatDate(DateHelper.parseDate(updated), "yyyy-MM-dd_HHmm");
			}
			//重名则加一个数字后缀
			String mainFileName = MD_SOURCE_DIR + dayStr;
			File mdfile = new File(mainFileName + ".md");
			i = 0;
			while (mdfile.exists()) {
				mainFileName = mainFileName + "###" + (i++) + "##" + filename;
				mdfile = new File(mainFileName + ".md");
				if (!mdfile.exists()) {
					break;
				}
			}
			FileUtils.writeLines(mdfile, lines, "\n");

			//图片目录改名
			File imagesDir = new File(MD_SOURCE_DIR + filename.replace(".md", "") + "_files");
			if (imagesDir.exists()) {
				FileUtils.moveDirectory(imagesDir, new File(mainFileName));
			}
			//删除原文件
			FileUtils.deleteQuietly(new File(MD_SOURCE_DIR + filename));
		} catch (Exception e) {
			try {
				FileUtils.write(new File(MD_SOURCE_DIR + filename + ".error"), "", "utf-8");
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
	}

	protected String cleanTitle(String title) {
		title = title.replace("[路，一个人走]", "");
		title = title.replace("路，一个人走", "");
		title = title.replace("[", "");
		title = title.replace("]", "");
		title = title.replace("(", "（");
		title = title.replace(")", "）");
		title = title.trim();
		title = title.replace("　", " ");
		System.out.println(title);
		return title;
	}

	/**
	 * 原始的发表时间已经没有，尝试根据标题还原它
	 * @param title  "[路，一个人走]11.2,3,4　瑞安市－福鼎市－福安市－连江县";
	 */
	protected Date buildDateFromTitle(String title) {
		try {
			System.out.println("---");
			String date = "";
			for (int i = 0; i < title.length(); i++) {
				String c = title.charAt(i) + "";
				if (NumberUtils.isNumber(c)) {
					System.out.println(c);
					date += c;
				} else {
					date += "-";
				}
			}
			System.out.println(date);
			String[] split = date.split("-");
			date = "2004-" + split[0] + "-" + split[split.length - 1] + " 23:00:00";
			System.out.println(date);
			return new SimpleDateFormat("yyyy-M-d HH:mm:ss").parse(date);  //yyyy-MM-dd HH:mm:ss
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
