package cn.yowob.bigeyes;

import cn.yowob.bigeyes.collector.BlogjavaCollector;
import cn.yowob.bigeyes.collector.SinaBlogCollector;
import cn.yowob.bigeyes.collector.SinaStockInfoCollector;
import cn.yowob.bigeyes.collector.WeatherCollector;

public class Starter {

	public static void main(String[] args) throws Exception {
		Configuration.getInstance();

		if (args.length == 0) {
			System.err.println("Invalid args!!!");
			return;
		}
		for (String arg : args) {
			String collname = arg.toLowerCase();
			if (collname.equals("weather")) {
				System.out.println("Starting WeatherCollector ...");
				System.out.println("启动天气采集程序 ...");
				new WeatherCollector().collect();
			} else if (collname.equals("sina_stock_info")) {
				System.out.println("Starting SinaStockInfoCollector ...");
				System.out.println("启动新浪的上市公司信息采集程序 ...");
				new SinaStockInfoCollector().collect();
			} else if (collname.equals("sinablog")) {
				System.out.println("Starting SinaBlogCollector ...");
				System.out.println("启动新浪博客的备份程序 ...");
				new SinaBlogCollector().collect();
			} else if (collname.equals("blogjava")) {
				System.out.println("Starting BlogjavaCollector ...");
				System.out.println("启动BlogJava博客的备份程序 ...");
				new BlogjavaCollector().collect();
			} else {
				System.out.println("ERROR args: " + arg);
			}
		}
	}

}