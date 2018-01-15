package cn.yowob.bigeyes.output;

import common.utils.JSONUtils;
import common.utils.StringHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;

/**
 * 数据输出到大数据平台
 * @author: 陈刚 2016/3/28
 */
public class FlumeOutput implements Output {
	private static final Logger log = LoggerFactory.getLogger(FlumeOutput.class);

	@Override
	public void put(Object data) {
		try {
			String url = "http://localhost:44444";
			HttpPost request = new HttpPost(url);
			HttpClient client = new DefaultHttpClient();

			HashMap requestMap = new HashMap(2);
			HashMap headers = new HashMap();
			headers.put("h1", "v1");
			headers.put("h2", "v2");
			requestMap.put("headers", headers);
			requestMap.put("body", JSONUtils.json2String(data));
			String json = JSONUtils.json2String(new Object[]{requestMap});
			log.info(json);
			StringEntity entity = new StringEntity(json, "application/json", "UTF-8");
			request.setEntity(entity);
			//
			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			log.info("statusCode={}", statusCode);
			InputStream is = response.getEntity().getContent();
			String content = StringHelper.toString(is);
			log.info(content);
		} catch (Exception e) {
			log.error("", e);
		}
	}
}
