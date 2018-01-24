## 项目介绍

这是一个网页小爬虫，以命令行方式运行，现支持如下信息采集:

* 抓取天气

* 新浪网的上市公司信息

* 新浪博客备份 (以markdown格式保存)

* BlogJava博客备份 (以markdown格式保存) 

  ​

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

4. 修改 bin/start.sh 如下一行 ，在Starter之后加入你想要抓取的网页类型

   ```
   java cn.yowob.bigeyes.Starter sinablog
   ```

5. 运行

   ```
   bin/start.sh
   ```

   本小工具没有自带定时执行功能，可以借用 linux 的定时器来调用执行。



## 代码说明

* Starter   这是Main入口类
* Configuration   这是配置类，它读取配置文件 config.yml。   
* SinaBlogCollector.yml 是抓取新浪博客的配置文件, 和 SinaBlogCollector.java 类同名
* BlogjavaCollector.yml 是抓取BlogJava博客的配置文件, 和 BlogjavaCollector.java 类同名
* SinaStockInfoCollector.yml  是抓取新浪上市公司信息的配置文件， 和 SinaStockInfoCollector.java 类同名
* WeatherCollector.yml 是抓取天气信息的配置文件， 和 WeatherCollector.java 类同名
* stock_sh.txt  上证股票代号
* stock_sz.txt  深证股票代号
* weather_city_id.xml    城市及其代码，抓取天气时需要用到。








