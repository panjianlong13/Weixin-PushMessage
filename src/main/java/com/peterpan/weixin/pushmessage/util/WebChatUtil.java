package com.peterpan.weixin.pushmessage.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
//import org.json.JSONObject;

public class WebChatUtil {
	public static final String APPID = "*** Your APP ID ***";
	public static final String APPSECRET = "*** Your APP SECRET ***";
	// 微信模板接口
	public static final  String SEND_TEMPLAYE_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";
	// 获取微信ACCESS_TOKEN接口
	public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + APPID
			+ "&secret=" + APPSECRET;

	private static final String TOKEN = "weixin";

	public static JSONObject doGetJson(String url) throws ClientProtocolException, IOException {
		JSONObject jsonObject = null;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = client.execute(httpGet);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			// 把返回的结果转换为JSON对象
			String result = EntityUtils.toString(entity, "UTF-8");
			jsonObject = JSON.parseObject(result);
		}

		return jsonObject;
	}

}
