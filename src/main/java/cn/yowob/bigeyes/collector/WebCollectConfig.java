package cn.yowob.bigeyes.collector;

import java.util.List;

/**
 * Web收集器的配置，一类页面一个
 * @author: 陈刚 2016/4/1
 */
public class WebCollectConfig {
	private String name;  //任意名称
	private String url;  // web页面的http地址
	private List<XpathConfig> xpathConfigList; //取数据的xpath配置,一个xpath取一个数据

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<XpathConfig> getXpathConfigList() {
		return xpathConfigList;
	}

	public void setXpathConfigList(List<XpathConfig> xpathConfigList) {
		this.xpathConfigList = xpathConfigList;
	}
}
