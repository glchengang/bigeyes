## 项目介绍

这是一个网页抓取小工具，以命令行方式运行，现在可以抓取天气和股票信息。技术上就是使用 Dom4j 和 xpath 来定位和抓取页面中的相关信息。



## 使用指南

本项目使用 JDK7 + Maven3  编译打包，我在本机环境测试通过：JDK_1.7.0_55 + Maven_3.5.2 + OS X EI Capitan 10.11.6

1. 编译打包：

   ```
   mvn package
   ```

   target 目录里找到 bigeyes-bin.zip，其它文件可以删除。


2. 解压 bigeyes-bin.zip，得到 一个 bigeyes 目录

3. 配置

   打开 conf/config.yml ，按你的服务器情况修改其中两项配置

   ```
   #抓取数据的保存目录
   outputDir: /Users/chen/Downloads/tmp/

   #采集的最大数量。试运行时建议设为2，0 是不限制
   collectMaxCount: 0
   ```

4. 运行

   ```
   bigeyes/bin/start.sh
   ```

   本小工具没有自带定时执行功能，可以借用 linux 的定时器来调用执行。



## 代码说明

* Starter   这是Main入口类
* Configuration   这是配置类，它读取配置文件 config.yml。   
* SinaStockInfoCollector.yml  是抓取新浪股票信息的配置文件， 和 SinaStockInfoCollector.java 类同名
* WeatherCollector.yml 是抓取天气信息的配置文件， 和 WeatherCollector.java 类同名
* stock_sh.txt  上证股票代号
* stock_sz.txt  深证股票代号
* weather_city_id.xml    城市及其代码，抓取天气时需要用到。


