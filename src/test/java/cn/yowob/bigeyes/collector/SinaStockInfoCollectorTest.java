package cn.yowob.bigeyes.collector;

import org.junit.Test;

/**
 * User: chen.gang, glchengang@163.com
 * Date: 2018-01-15
 *
 */
public class SinaStockInfoCollectorTest {
	@Test
	public void testCollect() throws Exception {
		new SinaStockInfoCollector().collect();
	}
}
