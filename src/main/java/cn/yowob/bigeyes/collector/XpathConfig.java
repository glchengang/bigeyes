package cn.yowob.bigeyes.collector;

/**
 * @author: 陈刚 2016/4/1
 */
public class XpathConfig {
	private String key;
	private String xpath;
	private int handleType = 1;

	public XpathConfig() {
	}

	public XpathConfig(String key, String xpath, int handleType) {
		this.key = key;
		this.xpath = xpath;
		this.handleType = handleType;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public int getHandleType() {
		return handleType;
	}

	public void setHandleType(int handleType) {
		this.handleType = handleType;
	}
}
