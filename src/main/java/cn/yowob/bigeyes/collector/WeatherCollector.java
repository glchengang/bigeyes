package cn.yowob.bigeyes.collector;

import cn.yowob.bigeyes.Configuration;
import cn.yowob.bigeyes.ProjectCommons;
import cn.yowob.bigeyes.output.CsvOutput;
import cn.yowob.bigeyes.output.Output;
import common.format.SimpleFormatProcesser;
import common.utils.CommonException;
import common.utils.DateHelper;
import common.utils.Dom4jHelper;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 天气信息采集
 * @author: 陈刚 2016/3/28
 */
public class WeatherCollector extends AbstractCollector implements Collector {
	private static final Logger log = LoggerFactory.getLogger(WeatherCollector.class);
	private static final String weather_city_id_file = "weather_city_id.xml";

	/**
	 * 采集
	 */
	@Override
	public synchronized void collect() {
		Map<String, String> weatherCodeList = getWeatherCodeList();
		collect(weatherCodeList);
	}

//	protected String getConfigFile() {
//		return "weather.yml";
//	}

	/**
	 * 取得所有城市的code
	 */
	protected Map<String, String> getWeatherCodeList() {
		Document document;
		try {
			SAXReader reader = new SAXReader();
			document = reader.read(ProjectCommons.getFileInputStream(weather_city_id_file));
		} catch (DocumentException e) {
			log.error("", e);
			throw new CommonException("read fail: " + weather_city_id_file);
		}
		assert document != null;

		HashMap<String, String> cityCodeNameMap = new HashMap();
		List<Element> nodeList = document.selectNodes("//county");
		for (Element element : nodeList) {
			String weatherCode = element.attribute("weatherCode").getValue();
			String name = element.attribute("name").getValue();
			log.debug("{},{}", weatherCode, name);
			cityCodeNameMap.put(weatherCode, name);
		}
		return cityCodeNameMap;
	}

	protected void collect(Map<String, String> codeMap) {
		ArrayList<Map> valueMapList = new ArrayList();
		int i = 1;
		int collectMaxCount = Configuration.getInstance().getCollectMaxCount();
		for (String code : codeMap.keySet()) {
			try {
				Map<String, String> valueMap = new LinkedHashMap();
				valueMap.put("cityCode", code);
				valueMap.put("cityName", codeMap.get(code));
				collect(code, valueMap);
				valueMapList.add(valueMap);
			} catch (Exception e) {
				log.error("", e);
			}
			if (collectMaxCount > 0 && i++ >= collectMaxCount) {
				break;
			}
		}
//		String outputDir = Configuration.getInstance().getCollectorDir() + "weather";
//		String dayStr = DateHelper.formatDateYMD(new Date());
//		Output output = new JsonOutput(outputDir, dayStr + ".json");
//		output.put(valueMapList);

		String outputDir = Configuration.getInstance().getOutputDir() + "weather";
		String dayStr = DateHelper.formatDateYMD(new Date());
		Output output = new CsvOutput(outputDir, dayStr + ".csv");
		output.put(valueMapList);

	}

	/**
	 * 采集单个股票
	 */
	protected void collect(String code, Map<String, String> valueMap) {
		SimpleFormatProcesser processer = new SimpleFormatProcesser();
		List<WebCollectConfig> webCollectConfigList = getWebCollectConfigList();
		for (WebCollectConfig webCollectConfig : webCollectConfigList) {
			/**
			 * url
			 */
			String url = webCollectConfig.getUrl();
			HashMap data = new HashMap();
			data.put("code", code);
			url = processer.process(url, data);
			log.debug(url);
			/**
			 *
			 */
			Document doc = Dom4jHelper.getDocument(url);
			if (doc == null) {
				continue;
			}
			for (XpathConfig e : webCollectConfig.getXpathConfigList()) {
				if (e.getHandleType() == 0) {
					handle0(doc, e, valueMap);
				}
			}
		}
	}

	private void handle0(Document doc, XpathConfig e, Map<String, String> valueMap) {
		XPath xpath = Dom4jHelper.getXpath(e.getXpath());
		Element node = (Element) xpath.selectSingleNode(doc);
//		log.debug(node.getNodeTypeName());
		if (node == null) {
			//log.error("没找到!----------key={}, xpath={}", e.getKey(), e.getValue());
			log.error("没找到!----------key=" + e.getKey() + ", xpath=" + e.getXpath());
			valueMap.put(e.getKey(), "没找到");
		} else {
			String content = node.attribute("value").getStringValue();
			content = StringUtils.trim(content);
			if (StringUtils.isBlank(content)) {
				log.error("无内容,key=" + e.getKey());
			}
			//log.debug("key={}, xpath={}, getUniquePath={}", e.getKey(), e.getValue(), node.getUniquePath());
			log.debug("key={}, value={}", e.getKey(), content);
			//"气温" : "04月17日20时 周日  晴转多云  2/16°C"
			String[] strs = StringUtils.split(content, " ");
			if (strs != null && strs.length >= 4) {
				//TODO 自定义脚本对值进来分割
				valueMap.put("时间", strs[0]);
				valueMap.put("晴雨", strs[2]);
				valueMap.put("温度", strs[3]);
			} else {
				valueMap.put(e.getKey(), content);
			}
		}
	}

}
