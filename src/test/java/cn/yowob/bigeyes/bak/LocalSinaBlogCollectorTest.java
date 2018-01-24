package cn.yowob.bigeyes.bak;

import org.junit.Test;

/**
 *
 * User: chen.gang, glchengang@163.com
 * Date: 01/16/2018
 */
public class LocalSinaBlogCollectorTest {
	@Test
	public void testCollectOne() throws Exception {
		new LocalSinaBlogCollector().collect("177.html");
		new LocalSinaBlogCollector().collect("186.html");
		new LocalSinaBlogCollector().collect("178.html");
	}

	@Test
	public void testCollectALL() throws Exception {
		new LocalSinaBlogCollector().collect();
	}

}
