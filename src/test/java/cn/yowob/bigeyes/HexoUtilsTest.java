package cn.yowob.bigeyes;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

/**
 *
 * User: chen.gang, glchengang@163.com
 * Date: 01/20/2018
 */
public class HexoUtilsTest {
	String dir = "/Volumes/M320/Downloads/tmp/";

	@Test
	public void makeRightTitle() throws Exception {
		Collection<File> files = FileUtils.listFiles(new File(dir), new String[]{"md"}, true);
		for (File file : files) {
			HexoUtils.makeRightTitle(file);
		}
	}

	@Test
	public void setMetaValue() throws Exception {
		Collection<File> files = FileUtils.listFiles(new File(dir), new String[]{"md"}, true);
		for (File file : files) {
			HexoUtils.setMetaValue(file, "tags", "户外");
			HexoUtils.setMetaValue(file, "categories", "户外");
		}
	}

	@Test
	public void renameByTitleAndDate() throws Exception {
		Collection<File> files = FileUtils.listFiles(new File(dir), new String[]{"md"}, true);
		for (File file : files) {
			HexoUtils.renameByDateAndTitle(file);
		}
	}

	@Test
	public void joinMetaValues() throws Exception {
		Collection<File> files = FileUtils.listFiles(new File(dir), new String[]{"md"}, true);
		for (File file : files) {
			HexoUtils.joinMetaValues(file, "tags");
			HexoUtils.joinMetaValues(file, "categories");
		}
	}

	@Test
	public void removeMetaValue() throws Exception {
		Collection<File> files = FileUtils.listFiles(new File(dir), new String[]{"md"}, true);
		for (File file : files) {
			HexoUtils.removeMetaValue(file, "tags", "路，一个人走");
		}
	}

	@Test
	public void replaceMetaValue() throws Exception {
		Collection<File> files = FileUtils.listFiles(new File(dir), new String[]{"md"}, true);
		for (File file : files) {
//			MarkdownUtils.replaceMetaValue(file, "categories", "其它技术", "[技术, 笔记]");
			HexoUtils.replaceMetaValue(file, "categories", "others", "Others");
//			MarkdownUtils.replaceMetaValue(file, "tags", "股票", "经济");
//			MarkdownUtils.replaceMetaValue(file, "tags", "财经", "经济");
		}
	}

	@Test
	public void addMetaValue() throws Exception {
		Collection<File> files = FileUtils.listFiles(new File(dir), new String[]{"md"}, false);
		for (File file : files) {
			HexoUtils.addMetaValue(file, "categories", "旅行", true, true);
		}
	}

	@Test
	public void testCategoriesTotags() throws Exception {
		HexoUtils.categoriesTotags(dir);
	}

}
