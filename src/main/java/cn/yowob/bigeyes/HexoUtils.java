package cn.yowob.bigeyes;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Hexo blog manager utils
 * 这是一个hexo博客系统的工具类，用处理hexo使用的markdown源文件
 * User: chen.gang, glchengang@163.com
 * Date: 01/20/2018
 */
public class HexoUtils {
	private static final Logger log = LoggerFactory.getLogger(HexoUtils.class);
	private static final String METADATA_SUB_LINE_PREFIX = "  - ";
	private static final int MAX_META_LINES = 20; //元数据在最前行，给20行空间足够了。此数为防止死循环
	private static final String NEW_LINE = "\n";  //Windows:\r\n, Linux/Unix:\n

//	/**
//	 *
//	 * 将图片链接, 由 “Hexo格式” 转化成 “Markdown标准的格式”
//	 * Hexo 格式： {% asset_img 1.jpg %}
//	 * Markdown标准的格式:   ![](20041001_10.1 北京－天津/1.jpg)
//	 */
//	public static void toMarkdownImage(File file) {
//		try {
//			//TODO
//			List<String> lines = FileUtils.readLines(file, "utf-8");
////getSingleMetaValue(lines,)
//			for (int i = 0; i < lines.size(); i++) {
//				String line = lines.get(i);
//				int index = line.indexOf("{% asset_img ");
//
//			}
//			FileUtils.writeLines(file, lines, NEW_LINE);
//		} catch (Exception e) {
//			log.error("", e);
//		}
//	}
//
//	/**
//	 * 将图片链接, 由 “Markdown标准的格式” 转化成 “Hexo格式”
//	 * Hexo 格式： {% asset_img 1.jpg %}
//	 * Markdown标准的格式:   ![](20041001_10.1 北京－天津/1.jpg)
//	 */
//	public static void toHexoImage(File file) {
//		try {
//			//TODO ...
//		} catch (Exception e) {
//			log.error("", e);
//		}
//	}

	/**
	 * 有些标题使用了特殊字符，不方便用来做文件名和目录名
	 */
	public static void makeRightTitle(File file) {
		try {
			List<String> lines = FileUtils.readLines(file, "utf-8");
			String metaKey = "title";
			SingleMetaDTO metaDTO = getSingleMetaValue(lines, metaKey);
			if (metaDTO.metaKeyIndex == -1) {
				log.debug("NOT found key: {}", metaKey);
				return;
			}
			String title = metaDTO.value;
			title = StringUtils.replace(title, "[", "【"); //改为全角
			title = StringUtils.replace(title, "]", "】"); //改为全角
			title = StringUtils.replace(title, "［", "【"); //改为全角
			title = StringUtils.replace(title, "］", "】"); //改为全角
			title = StringUtils.replace(title, "(", "（"); //改为全角
			title = StringUtils.replace(title, ")", "）"); //改为全角
			title = StringUtils.replace(title, "'", "＇"); //改为全角单引号
			title = StringUtils.replace(title, ":", "："); //改为全角冒号
			title = StringUtils.replace(title, "　", " "); //全角空格改为半角
			title = StringUtils.replace(title, "  ", " "); //两个空格改为一个
			if (!title.equals(metaDTO.value)) {
				log.info("{}-->{}", metaDTO.value, title);
				lines.remove(metaDTO.metaKeyIndex);
				lines.add(metaDTO.metaKeyIndex, metaKey + ": " + title);
				FileUtils.writeLines(file, lines, NEW_LINE);
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}

	/**
	 * 用时间+标题来改文件名
	 */
	public static void renameByDateAndTitle(File file) {
		try {
			List<String> lines = FileUtils.readLines(file, "utf-8");
			SingleMetaDTO metaDTO = getSingleMetaValue(lines, "title");
			String title = metaDTO.value;
			metaDTO = getSingleMetaValue(lines, "date");
			String date = metaDTO.value;
			String mainName = ProjectCommons.getArticleDir(title, date);
			File newFile = new File(file.getParent() + "/" + mainName + ".md");
			if (!file.getAbsolutePath().equals(newFile.getAbsolutePath())) {
//				log.info("old: {}", file.getAbsolutePath());
//				log.info("new: {}", newFile.getAbsolutePath());
				FileUtils.moveFile(file, newFile);
				log.info("{}-->{}", file.getName(), newFile.getName());
				//move dir
				String dir = file.getAbsolutePath();
				dir = dir.substring(0, dir.lastIndexOf(".md"));
				File oldDir = new File(dir);
				File newDir = new File(file.getParent() + "/" + mainName);
				log.info("old dir: {}", oldDir.getAbsolutePath());
				log.info("new dir: {}", newDir.getAbsolutePath());
				if (oldDir.exists() && !oldDir.getAbsolutePath().equals(newDir.getAbsolutePath())) {
					FileUtils.moveDirectory(oldDir, newDir);
				}
			}
		} catch (Exception e) {
			log.error(file.getAbsolutePath(), e);
		}
	}

	/**
	 * 删除 tags/categories 的某个值
	 */
	public static void removeMetaValue(File file, String metaKey, String value) throws IOException {
		log.info("--------------------------------------------------- " + file.getName());
		List<String> lines = FileUtils.readLines(file, "utf-8");
		MultiMetaDTO metaDTO = getMultiMetaValue(lines, metaKey);
		List<String> values = metaDTO.values;
		if (values.isEmpty() || !values.remove(value)) {
			log.info("NOT found value: {}", value);
			return;
		}
		removeMetaData(lines, metaDTO.removeIndexList);
		if (!values.isEmpty()) {
			String newLine = joinMetaValues(metaKey, values);
			lines.add(metaDTO.metaKeyIndex, newLine);
			log.debug(newLine);
		}
		printMeta(lines);
		FileUtils.writeLines(file, lines, NEW_LINE);
	}

	/**
	 * 将用减号“-”表示的多行 tags/categories 值，改为单行[]方式
	 * 经如 tags: [java, eclipse]
	 */
	public static void joinMetaValues(File file, String metaKey) throws IOException {
		log.info("--------------------------------------------------- " + file.getName());
		List<String> lines = FileUtils.readLines(file, "utf-8");
		MultiMetaDTO metaDTO = getMultiMetaValue(lines, metaKey);
		if (metaDTO.metaKeyIndex == -1) {
			log.debug("NOT found key: {}", metaKey);
			return;
		}
		List<String> values = metaDTO.values;
		if (values == null || values.isEmpty()) {
			log.debug("NOT found value: {}", metaKey);
			return;
		}
		removeMetaData(lines, metaDTO.removeIndexList);
		String newLine = joinMetaValues(metaKey, values);
		lines.add(metaDTO.metaKeyIndex, newLine);
		FileUtils.writeLines(file, lines, NEW_LINE);
	}

	/**
	 * 增加 tags/categories 的值
	 * @param isFirstPosition true=在所有值里，位列第一, false=在所有值里，位列最后
	 * @param acceptRepeat true=允许重复值
	 * @throws IOException
	 */
	public static void addMetaValue(File file, String metaKey, String value, boolean isFirstPosition, boolean acceptRepeat) throws IOException {
		log.info("--------------------------------------------------- " + file.getName());
		List<String> lines = FileUtils.readLines(file, "utf-8");
		MultiMetaDTO metaDTO = getMultiMetaValue(lines, metaKey);
		if (metaDTO.metaKeyIndex == -1) {
			log.info("NOT found key: {}", metaKey);
		}
		List<String> values = metaDTO.values;
		if (values == null || values.isEmpty()) {
			values = new ArrayList<>();
			values.add(value);
		} else {
			if (!acceptRepeat && values.contains(value)) {
				log.info("The value exists, pass. value={}, values={}", value, values);
				return;
			}
			if (isFirstPosition) {
				values.add(0, value);
			} else {
				values.add(value);
			}
		}
		removeMetaData(lines, metaDTO.removeIndexList);
		String newLine = joinMetaValues(metaKey, values);
		if (metaDTO.metaKeyIndex == -1) {
			lines.add(metaDTO.metaLastIndex, newLine);
		} else {
			lines.add(metaDTO.metaKeyIndex, newLine);
		}
		log.debug(newLine);
		printMeta(lines);
		FileUtils.writeLines(file, lines, NEW_LINE);
	}

	/**
	 * 修改 tags/categories 的值
	 * @param file
	 * @param metaKey 元数据key，比如 tags 或 categories
	 * @param oldValue 旧值
	 * @param newValue 新值
	 */
	public static void replaceMetaValue(File file, String metaKey, String oldValue, String newValue) throws IOException {
		log.info("--------------------------------------------------- " + file.getName());
		List<String> lines = FileUtils.readLines(file, "utf-8");
		MultiMetaDTO metaDTO = getMultiMetaValue(lines, metaKey);
		if (metaDTO.metaKeyIndex == -1) {
			log.info("NOT found key: {}", metaKey);
			return;
		}
		List<String> values = metaDTO.values;
		if (!values.contains(oldValue)) {
			log.info("NOT found value: {}", oldValue);
			return;
		}
		log.info("元数据全部值:" + values);
		for (int i = 0; i < values.size(); i++) {
			String v = values.get(i);
			if (v.equals(oldValue)) {
				values.remove(i);
				values.add(i, newValue);
			}
		}
		removeMetaData(lines, metaDTO.removeIndexList);
		String newLine = joinMetaValues(metaKey, values);
		lines.add(metaDTO.metaKeyIndex, newLine);
		log.debug(newLine);
		printMeta(lines);

		FileUtils.writeLines(file, lines, NEW_LINE);
		log.info("write file");
	}

	/**
	 * 用新值重置 tags/categories 的值
	 * @param file
	 * @param metaKey
	 * @param newValue 新值
	 */
	public static void setMetaValue(File file, String metaKey, String newValue) throws IOException {
		log.info("--------------------------------------------------- " + file.getName());
		List<String> lines = FileUtils.readLines(file, "utf-8");
		MultiMetaDTO metaDTO = getMultiMetaValue(lines, metaKey);
		removeMetaData(lines, metaDTO.removeIndexList);
		if (!StringUtils.isEmpty(newValue)) {
			String newLine = metaKey + ": " + newValue;
			if (metaDTO.metaKeyIndex == -1) {
				lines.add(metaDTO.metaLastIndex, newLine);
			} else {
				lines.add(metaDTO.metaKeyIndex, newLine);
			}
		}
		FileUtils.writeLines(file, lines, NEW_LINE);
	}

	/**
	 * 删除行
	 */
	private static void removeMetaData(List<String> lines, List<Integer> removeIndexList) {
		if (lines == null || removeIndexList == null || lines.isEmpty() || removeIndexList.isEmpty())
			return;
		//倒序删
		for (int i = removeIndexList.size() - 1; i >= 0; i--) {
			int index = removeIndexList.get(i);
			lines.remove(index);
		}
	}

	/**
	 * 打印出元数据
	 */
	private static void printMeta(List<String> lines) {
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			log.debug(line);
			if (i > 1 && line.equals("---")) {
				break;
			}
		}
	}

	/**
	 * 将多个元数据合成一行
	 */
	protected static String joinMetaValues(String metaKey, List<String> values) {
		if (values.size() == 1) {
			return metaKey + ": " + values.get(0);
		}
		//多个元数据用  tags: [java, eclipse]
		String newLine = metaKey + ": [" + values.get(0);
		for (int i = 1; i < values.size(); i++) {
			newLine = newLine + ", " + values.get(i);
		}
		return newLine + "]";
	}

	/**
	 * 内部类，用于封装tags，categories 这类会有多个值的元数据
	 */
	private static class MultiMetaDTO {
		int metaKeyIndex = -1;
		int metaLastIndex = -1;
		List<Integer> removeIndexList = new ArrayList<>();
		List<String> values = new ArrayList<>();
	}

	/**
	 * 内部类，用于封装title, date 这类只一个值的元数据
	 */
	private static class SingleMetaDTO {
		int metaKeyIndex = -1;
		String value = null;
	}

	/**
	 * 取得多值的元数据
	 * @param lines
	 * @param metaKey 元数据KEY
	 */
	private static MultiMetaDTO getMultiMetaValue(List<String> lines, String metaKey) throws IOException {
		MultiMetaDTO dto = new MultiMetaDTO();
		for (int i = 0; i < MAX_META_LINES; i++) {
			String line = lines.get(i);
			//发现要找的元数据
			if (line.startsWith(metaKey + ":")) {
				dto.metaKeyIndex = i;
				dto.removeIndexList.add(i);
				String str = line.substring(metaKey.length() + 1).trim();
				log.info(metaKey + "元数据的同行值：" + str);
				if (!StringUtils.isEmpty(str)) {
					if (str.startsWith("[")) {
						str = str.substring(1, str.length() - 1);
						for (String s : str.split(",")) {
							dto.values.add(clearQuoting(s));
						}
					} else {
						dto.values.add(clearQuoting(str));
					}
				}
			}
			//第二个分隔符时，元数据结束
			if (i > 3 && line.equals("---")) {
				log.info("元数据区结束");
				dto.metaLastIndex = i;
				break;
			}
			//log.info("metaKeyIndex={}, i={} , line={}", metaKeyIndex, i, line);
			//到了新的元数据
			if (dto.metaKeyIndex != -1 && i > dto.metaKeyIndex && line.indexOf(":") != -1) {
				log.info("new meta: " + line);
				break;
			} else {
				//多行式的值
				if (dto.metaKeyIndex != -1 && line.startsWith(METADATA_SUB_LINE_PREFIX)) {
					String v = line.substring(METADATA_SUB_LINE_PREFIX.length()).trim();
					log.info("sub value={}", v);
					if (v.startsWith("'") || v.startsWith("\"")) {
						v = v.substring(1, v.length() - 2);
					}
					dto.values.add(v);
					dto.removeIndexList.add(i);
				}
			}
		}
		if (dto.metaLastIndex == -1) {
			for (int i = 0; i < 15; i++) {
				String line = lines.get(i);
				//第二个分隔符时，元数据结束
				if (i > 3 && line.equals("---")) {
					dto.metaLastIndex = i;
					break;
				}
			}
		}
		return dto;
	}

	/**
	 * 取得单值元数据
	 * @param lines
	 * @param metaKey 元数据KEY
	 */
	private static SingleMetaDTO getSingleMetaValue(List<String> lines, String metaKey) throws IOException {
		SingleMetaDTO dto = new SingleMetaDTO();
		for (int i = 0; i < MAX_META_LINES; i++) {
			String line = lines.get(i);
			//发现要找的元数据
			if (line.startsWith(metaKey + ":")) {
				dto.metaKeyIndex = i;
				String str = line.substring(metaKey.length() + 1).trim();
				str = clearQuoting(str);
				dto.value = str;
			}
			//第二个分隔符时，元数据结束
			if (i > 3 && line.equals("---")) {
				//log.info("元数据区结束");
				break;
			}
		}
		return dto;
	}

	/**
	 * 去掉值前后的引号
	 */
	protected static String clearQuoting(String v) {
		v = v.trim();
		if (v.startsWith("'") || v.startsWith("\"")) {
			v = v.substring(1, v.length() - 1);
		}
		return v;
	}

	/**
	 * 将 categories分类，设入到 tags标签
	 * categories: Eclipse
	 * tags: Eclipse
	 *
	 * @param dir=需要处理的目录
	 */
	public static void categoriesTotags(String dir) throws IOException {
		Collection<File> files = FileUtils.listFiles(new File(dir), new String[]{"md"}, true);
		for (File file : files) {
			categoriesTotags(file);
		}
		log.info("文件数={}", files.size());
	}

	/**
	 * 将 categories分类，设入到 tags标签
	 * categories: Eclipse
	 * tags: Eclipse
	 * @param file=需要处理的文件
	 */
	public static void categoriesTotags(File file) throws IOException {
		List<String> lines = FileUtils.readLines(file, "utf-8");
		MultiMetaDTO categoriesDTO = getMultiMetaValue(lines, "categories");
		if (categoriesDTO.metaKeyIndex == -1) {
			return;
		}
		MultiMetaDTO tagsDTO = getMultiMetaValue(lines, "tags");
		List<String> newTags = mergeCategoriesToTags(categoriesDTO.values, tagsDTO.values);
		if (newTags.isEmpty() || newTags.toString().equals(tagsDTO.values.toString())) { // no change
			return;
		}
		/**
		 * replace by new tags
		 */
		String line = "tags: " + newTags.toString();
		removeMetaData(lines, tagsDTO.removeIndexList);
		if (tagsDTO.metaKeyIndex == -1) {
			lines.add(tagsDTO.metaLastIndex, line);
		} else {
			lines.add(tagsDTO.metaKeyIndex, line);
		}
		FileUtils.writeLines(file, lines, NEW_LINE);
	}

	/**
	 * 将分类合并进标签
	 * @param categories 分类
	 * @param tags 标签
	 * @return
	 */
	private static List<String> mergeCategoriesToTags(List<String> categories, List<String> tags) {
		List<String> result = new ArrayList<>();
		if (tags != null && !tags.isEmpty()) {
			result.addAll(tags);
		}
		if (categories != null) {
			for (String category : categories) {
				if (!result.contains(category)) {
					result.add(category);
				}
			}
		}
		return result;
	}

}
