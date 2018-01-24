package cn.yowob.bigeyes.bak;

import common.utils.DateHelper;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Date;

/**
 *
 * User: chen.gang, glchengang@163.com
 * Date: 01/17/2018
 */
public class SinaBlogMarkdownHandlerTest {
	LocalSinaBlogMarkdownHandler hm = new LocalSinaBlogMarkdownHandler();

	@Test
	public void testHandleOne() throws Exception {
		hm.handle("94.md");
	}

	@Test
	public void testHandleAll() throws Exception {
		hm.handle();
	}

	@Test
	public void buildDateFromTitle() throws Exception {
		TestCase.assertEquals("2004-11-23 23:00:00", getDateStr("[路，一个人走]11.22,23,23　瑞安市－福鼎市－福安市－连江县"));
		TestCase.assertEquals("2004-01-03 23:00:00", getDateStr("[路，一个人走]1.2,3,3　瑞安市－福鼎市－福安市－连江县"));
		TestCase.assertEquals("2004-11-22 23:00:00", getDateStr("[路，一个人走]11.22　瑞安市－福鼎市－福安市－连江县"));
	}

	private String getDateStr(String title) {
		title = hm.cleanTitle(title);
		Date date = hm.buildDateFromTitle(title);
		String s = DateHelper.formatDateTime(date);
		System.out.println(s);
		return s;
	}

}
