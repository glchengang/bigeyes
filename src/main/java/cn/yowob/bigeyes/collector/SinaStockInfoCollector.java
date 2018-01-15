package cn.yowob.bigeyes.collector;

import cn.yowob.bigeyes.Configuration;
import cn.yowob.bigeyes.ProjectCommons;
import cn.yowob.bigeyes.output.JsonOutput;
import cn.yowob.bigeyes.output.Output;
import common.format.SimpleFormatProcesser;
import common.utils.CommonException;
import common.utils.Dom4jHelper;
import common.utils.FileHelper;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 新浪网股票信息查寻
 * @author: 陈刚 2016/3/28
 */
public class SinaStockInfoCollector extends AbstractCollector implements Collector {
	private static final Logger log = LoggerFactory.getLogger(SinaStockInfoCollector.class);
	private static final List<String> stockCodeList = new ArrayList();

	protected List<String> getStockCodeList() {
		if (stockCodeList.isEmpty()) {
			List<String> lineList = new ArrayList(10000);
			try {
				lineList.addAll(FileHelper.readLine(ProjectCommons.getFileInputStream("stock_sh.txt")));
				lineList.addAll(FileHelper.readLine(ProjectCommons.getFileInputStream("stock_sz.txt")));
			} catch (Exception e) {
				log.error("", e);
				throw new CommonException("文件没找到: stock_sz.txt, stock_sh.txt");
			}
			for (String line : lineList) {
				String[] args = StringUtils.split(line, ",");
				if (args != null && args.length > 0) {
					String code = args[0].trim();
					stockCodeList.add(code);
				}
			}
		}
		return stockCodeList;
	}
//
//	/**
//	 * TODO 以后改为从数据库读取各个项的xpath配置
//	 * @return
//	 */
//	private List<WebCollectConfig> getWebCollectConfigList1() {
//		List<WebCollectConfig> wcList = new ArrayList();
//		{
//			WebCollectConfig wc = new WebCollectConfig();
//			wc.setName("公司简介");
//			wc.setUrl("http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_CorpInfo/stockid/${code}.phtml");
////			wc.setUrl("file:///Users/chen/Documents/workspace/BigEyes/collector/sysadm/src/test/java/siam/sysadm/coll/公司简介_${code}.html");
//
//			List<XpathConfig> xpathList = new ArrayList();
//			wc.setXpathConfigList(xpathList);
//			xpathList.add(new XpathConfig("股票名称及代码", "//*[@id='stockName']", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("公司名称", "//TD[text()='公司名称：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("公司英文名称", "//TD[text()='公司英文名称：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("上市市场", "//TD[text()='上市市场：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("上市日期", "//TD[text()='上市日期：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("发行价格", "//TD[text()='发行价格：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("主承销商", "//TD[text()='主承销商：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("成立日期", "//TD[text()='成立日期：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("注册资本", "//TD[text()='注册资本：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("机构类型", "//TD[text()='机构类型：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("组织形式", "//TD[text()='组织形式：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("董事会秘书", "//TD[text()='董事会秘书：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("公司电话", "//TD[text()='公司电话：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("董秘电话", "//TD[text()='董秘电话：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("公司传真", "//TD[text()='公司传真：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("董秘传真", "//TD[text()='董秘传真：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("公司电子邮箱", "//TD[text()='公司电子邮箱：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("董秘电子邮箱", "//TD[text()='董秘电子邮箱：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("公司网址", "//TD[text()='公司网址：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("邮政编码", "//TD[text()='邮政编码：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("信息披露网址", "//TD[text()='信息披露网址：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("证券简称更名历史", "//TD[text()='证券简称更名历史：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("注册地址", "//TD[text()='注册地址：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("办公地址", "//TD[text()='办公地址：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("公司简介", "//TD[text()='公司简介：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			xpathList.add(new XpathConfig("经营范围", "//TD[text()='经营范围：']/following-sibling::TD", CdHandleType.TABLE_KEY_VALUE));
//			wcList.add(wc);
//		}
//		{
//			WebCollectConfig wc = new WebCollectConfig();
//			wc.setName("公司高管");
//			wc.setUrl("http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_CorpManager/stockid/${code}.phtml");
////			wc.setUrl("file:///Users/chen/Documents/workspace/BigEyes/collector/sysadm/src/test/java/siam/sysadm/coll/公司高管_${code}.html");
//
//			List<XpathConfig> xpathList = new ArrayList<XpathConfig>();
//			wc.setXpathConfigList(xpathList);
//			xpathList.add(new XpathConfig("历届高管成员", "//TH[text()='历届高管成员']/../../../TBODY/TR", CdHandleType.TABLE_STANDARD));
//			xpathList.add(new XpathConfig("历届董事会成员", "//TH[text()='历届董事会成员']/../../../TBODY/TR", CdHandleType.TABLE_STANDARD));
//			xpathList.add(new XpathConfig("历届监事会成员", "//TH[text()='历届监事会成员']/../../../TBODY/TR", CdHandleType.TABLE_STANDARD));
//			wcList.add(wc);
//		}
//		{
//			WebCollectConfig wc = new WebCollectConfig();
//			wc.setName("公司章程");
//			wc.setUrl("http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_CorpRule/stockid/${code}.phtml");
////			wc.setUrl("file:///Users/chen/Documents/workspace/BigEyes/collector/sysadm/src/test/java/siam/sysadm/coll/公司章程_${code}.html");
//			List<XpathConfig> xpathList = new ArrayList<XpathConfig>();
//			wc.setXpathConfigList(xpathList);
//			xpathList.add(new XpathConfig("公司章程", "//TABLE[@id]/THEAD", CdHandleType.TABLE_MULTI));
//			wcList.add(wc);
//		}
//		{
//			WebCollectConfig wc = new WebCollectConfig();
//			wc.setName("相关证券");
//			wc.setUrl("http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_CorpXiangGuan/stockid/${code}.phtml");
////			wc.setUrl("file:///Users/chen/Documents/workspace/BigEyes/collector/sysadm/src/test/java/siam/sysadm/coll/相关证券_${code}.html");
//			List<XpathConfig> xpathList = new ArrayList<XpathConfig>();
//			wc.setXpathConfigList(xpathList);
//			xpathList.add(new XpathConfig("相关证券", "//TH[text()='相关证券']/../../../TBODY/TR", CdHandleType.TABLE_STANDARD));
//			xpathList.add(new XpathConfig("所属指数", "//TH[text()='所属指数']/../../../TBODY/TR", CdHandleType.TABLE_STANDARD));
//			xpathList.add(new XpathConfig("所属系", "//TH[text()='所属系']/../../../TBODY/TR", CdHandleType.TABLE_STANDARD));
//			wcList.add(wc);
//		}
//		{
//			WebCollectConfig wc = new WebCollectConfig();
//			wc.setName("所属行业");
//			wc.setUrl("http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_CorpOtherInfo/stockid/${code}/menu_num/2.phtml");
////			wc.setUrl("file:///Users/chen/Documents/workspace/BigEyes/collector/sysadm/src/test/java/siam/sysadm/coll/所属行业_${code}.html");
//			List<XpathConfig> xpathList = new ArrayList<XpathConfig>();
//			wc.setXpathConfigList(xpathList);
//			xpathList.add(new XpathConfig("所属行业板块", "//TD[text()='所属行业板块']/../../TR", CdHandleType.TABLE2));
//			xpathList.add(new XpathConfig("所属概念板块", "//TD[text()='所属概念板块']/../../TR", CdHandleType.TABLE2));
//			wcList.add(wc);
//		}
//		return wcList;
//	}

	/**
	 * 采集
	 */
	@Override
	public synchronized void collect() {
		int i = 1;
		int collectMaxCount = Configuration.getInstance().getCollectMaxCount();
		for (String code : getStockCodeList()) {
			try {
				collect(code);
			} catch (Exception e) {
				log.error("", e);
			}
			if (collectMaxCount > 0 && i++ >= collectMaxCount) {
				break;
			}
		}
	}

	/**
	 * 采集单个股票
	 * @param stockCode
	 */
	protected void collect(String stockCode) {
		if (!isValid(stockCode)) {
			return;
		}
		SimpleFormatProcesser processer = new SimpleFormatProcesser();
		Map<String, Object> valueMap = new LinkedHashMap();
		for (WebCollectConfig collectConfig : getWebCollectConfigList()) {
			/**
			 * 将URL模板转成真实的URL
			 */
			String url = collectConfig.getUrl();
			{
				HashMap data = new HashMap();
				data.put("code", stockCode);
				url = processer.process(url, data);
				log.debug(url);
			}
			/**
			 * 抓取页面
			 */
			Document doc = Dom4jHelper.getDocument(url);
			if (doc == null) {
				continue;
			}

			for (XpathConfig e : collectConfig.getXpathConfigList()) {
				if (e.getHandleType() == CdHandleType.TABLE_KEY_VALUE) {
					handle1(doc, e, valueMap);
				} else if (e.getHandleType() == CdHandleType.TABLE_STANDARD) {
					handleTable(doc, e, valueMap);
				} else if (e.getHandleType() == CdHandleType.TABLE2) {
					handleTable2(doc, e, valueMap);
				} else if (e.getHandleType() == CdHandleType.TABLE_MULTI) {
					handleTable4(doc, e, valueMap);
				}
			}
			//log.debug("--------------------------------------------------------------------------------");
			//log.debug("所有内容----{}", valueMap);
		}
		String outputDir = Configuration.getInstance().getOutputDir() + "stock";
		Output output = new JsonOutput(outputDir, stockCode + ".json");
		output.put(valueMap);
	}

	/**
	 * TODO 验证是否有效（ 有些股票代码不是公司,比如 399001	深证成指）
	 */
	private boolean isValid(String code) {
		return true;
	}

	private void handle1(Document doc, XpathConfig e, Map<String, Object> valueMap) {
		XPath xpath = Dom4jHelper.getXpath(e.getXpath());
		Node node = xpath.selectSingleNode(doc);
		if (node == null) {
			//log.error("没找到!----------key={}, xpath={}", e.getKey(), e.getValue());
			log.error("没找到!----------key=" + e.getKey() + ", xpath=" + e.getXpath());
			valueMap.put(e.getKey(), "没找到");
		} else {
			String content = node.getStringValue();
			content = StringUtils.trim(content);
			if (StringUtils.isBlank(content)) {
				log.error("无内容,key=" + e.getKey());
			}
			//log.debug("key={}, xpath={}, getUniquePath={}", e.getKey(), e.getValue(), node.getUniquePath());
			log.debug("key={}, value={}", e.getKey(), content);
			valueMap.put(e.getKey(), content);
		}
	}

	private void handleTable(Document doc, XpathConfig e, Map<String, Object> valueMap) {
		log.debug("---------------------------{}-----------------------------------", e.getKey());
		XPath xpath = Dom4jHelper.getXpath(e.getXpath());
		List<Node> nodeList = xpath.selectNodes(doc);
		if (nodeList == null || nodeList.isEmpty()) {
			//log.error("没找到!----------key={}, xpath={}", e.getKey(), e.getValue());
			log.error("没找到!----------key=" + e.getKey() + ", xpath=" + e.getXpath());
			valueMap.put(e.getKey(), null);
			return;
		}

		ArrayList<ArrayList<String>> itemList = new ArrayList<ArrayList<String>>();
		valueMap.put(e.getKey(), itemList);
		for (Node trNode : nodeList) {
			log.debug("TR node -------------- " + trNode.getUniquePath());
			List<Node> nodes = trNode.selectNodes("TD");
			ArrayList<String> valueList = new ArrayList<String>();
			itemList.add(valueList);
			for (Node node : nodes) {
				String content = node.getStringValue();
				content = StringUtils.trim(content);
				content = content.trim();
				log.debug(content);
				valueList.add(content);
			}
		}

		print(valueMap);
	}

	/**
	 * 没有thead,而且表头在tbody的第二行
	 */
	private void handleTable2(Document doc, XpathConfig e, Map<String, Object> valueMap) {
		log.debug("---------------------------{}-----------------------------------", e.getKey());
		XPath xpath = Dom4jHelper.getXpath(e.getXpath());
		List<Node> nodeList = xpath.selectNodes(doc);
		if (nodeList == null || nodeList.isEmpty()) {
			//log.error("没找到!----------key={}, xpath={}", e.getKey(), e.getValue());
			log.error("没找到!----------key=" + e.getKey() + ", xpath=" + e.getXpath());
			valueMap.put(e.getKey(), null);
			return;
		}

		ArrayList<ArrayList<String>> itemList = new ArrayList<ArrayList<String>>();
		valueMap.put(e.getKey(), itemList);
		if (!nodeList.isEmpty()) {
			nodeList.remove(0); //删除第二行
		}
		for (Node trNode : nodeList) {
			log.debug("TR node -------------- " + trNode.getUniquePath());
			List<Node> nodes = trNode.selectNodes("TD");
			if (nodes.isEmpty()) {
				log.debug("未找到TD,改为找TH");
				nodes = trNode.selectNodes("TH");
			}
			ArrayList<String> valueList = new ArrayList<String>();
			itemList.add(valueList);
			for (Node node : nodes) {
				String content = node.getStringValue();
				content = StringUtils.trim(content);
				content = content.trim();
				log.debug(content);
				valueList.add(content);
			}
		}
		print(valueMap);
	}

	/**
	 * 没有thead,而且表头在tbody的第二行
	 */
	private void handleTable4(Document doc, XpathConfig e, Map<String, Object> valueMap) {
		log.debug("---------------------------{}-----------------------------------{}", e.getKey(), e.getXpath());
		XPath xpath = Dom4jHelper.getXpath(e.getXpath());
		List<Element> tableNodeList = xpath.selectNodes(doc);
		if (tableNodeList == null || tableNodeList.isEmpty()) {
			//log.error("没找到!----------key={}, xpath={}", e.getKey(), e.getValue());
			log.error("没找到!----------key=" + e.getKey() + ", xpath=" + e.getXpath());
			valueMap.put(e.getKey(), null);
			return;
		}

		ArrayList<ArrayList<String>> itemList = new ArrayList<ArrayList<String>>();
		valueMap.put(e.getKey(), itemList);
		if (!tableNodeList.isEmpty()) {
			tableNodeList.remove(0); //删除第二行
		}
		for (Element tableNode : tableNodeList) {
			log.debug(tableNode.getNodeTypeName() + ", table node -------------- " + tableNode.getUniquePath());
			ArrayList<String> valueList = new ArrayList<String>();
			itemList.add(valueList);
			{
				Node node = tableNode.selectSingleNode("TR/TH");
				if (node == null) {
				} else {
					String content = node.getStringValue();
					content = StringUtils.trim(content);
					content = content.trim();
					log.debug(content);
					valueList.add(content);
				}
			}
			{
				List<Node> nodes = tableNode.selectNodes("../TBODY/TR/TD");
				for (Node node : nodes) {
					String content = node.getStringValue();
					content = StringUtils.trim(content);
					content = content.trim();
					if (content.length() > 200) {
						log.debug(content.substring(0, 50));
					} else {
						log.debug(content);
					}
					valueList.add(content);
				}
			}
		}
	}

	private void print(Map<String, Object> valueMap) {
//		System.out.println("--------------------------------------------------------------------------------------------");
//		for (String key : valueMap.keySet()) {
//			System.out.println("----------------------" + key);
//			Object itemListObj = valueMap.get(key);
//			if (itemListObj instanceof List) {
//				List<List> itemList = (List<List>) itemListObj;
//				for (List<String> item : itemList) {
//					for (Object v : item) {
//						System.out.print(v + ", ");
//					}
//					System.out.println("  ");
//				}
//			}
//		}
	}
}
