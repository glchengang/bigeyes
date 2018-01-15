package cn.yowob.bigeyes.collector;

import cn.yowob.bigeyes.collector.WeatherCollector;
import org.junit.Test;

/**
 * User: chen.gang, glchengang@163.com
 * Date: 2018-01-15
 *
 */
public class WeatherCollectorTest {
	@Test
	public void testCollect() throws Exception {
		new WeatherCollector().collect();
	}
}
