package cn.yowob.bigeyes;

import org.junit.Test;

/**
 * User: chen.gang, glchengang@163.com
 * Date: 2018-01-15
 *
 */
public class ConfigurationTest {

	@Test
	public void getInstance() throws Exception {
		Configuration conf = Configuration.getInstance();
		System.out.println(conf.getOutputDir());
		System.out.println(conf.getCollectMaxCount());
	}
}
