package cn.yowob.bigeyes;

import cn.yowob.bigeyes.collector.SinaStockInfoCollector;
import cn.yowob.bigeyes.collector.WeatherCollector;

public class Starter {

	public static void main(String[] args) throws Exception {
		Configuration.getInstance();

		if (args.length == 0) {
			System.err.println("Invalid args");
			return;
		}
		for (String arg : args) {
			String collname = arg.toLowerCase();
			if (collname.equals("weather")) {
				System.out.println("Starting WeatherCollector ...");
				new WeatherCollector().collect();
			} else if (collname.equals("sina_stock_info")) {
				System.out.println("Starting SinaStockInfoCollector ...");
				new SinaStockInfoCollector().collect();
			} else {
				System.out.println("ERROR args: " + arg);
			}
		}
	}

}