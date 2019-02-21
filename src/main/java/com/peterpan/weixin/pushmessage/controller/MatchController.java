package com.peterpan.weixin.pushmessage.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.peterpan.weixin.pushmessage.entity.AccessToken;
import com.peterpan.weixin.pushmessage.entity.PushMessageEntity;
import com.peterpan.weixin.pushmessage.entity.ResultTemplateDate;
import com.peterpan.weixin.pushmessage.util.JsonResult;
import com.peterpan.weixin.pushmessage.util.ResultCode;
import com.peterpan.weixin.pushmessage.util.WebChatUtil;


@RestController
public class MatchController {

	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping(value = "/push/message", method = { RequestMethod.POST })
	public JsonResult pushMessage(@RequestBody PushMessageEntity pushMessageEntity)
			throws ClientProtocolException, IOException {
		System.out.println("Into pushMessage method");
		// Get AccessToken
		String accessToken = getAccessToken();
		if (accessToken.equals("")) {
			return new JsonResult(ResultCode.FAIL, "无法通过APPID和APPSecret获取AccessToken");
		}
		System.out.println("AccessToken = " + accessToken);
		// WeiXin Push Message URL
		String pushMessageUrl = WebChatUtil.SEND_TEMPLAYE_MESSAGE_URL + getAccessToken();
		// Get Mobile Array List
		String[] openidlist = pushMessageEntity.getOpenidlist();
		
		ArrayList<String> sendSuccessUsers = new ArrayList<String>();
		ArrayList<String> sendFailUsers = new ArrayList<String>();
		

		JSONObject data = pushMessageEntity.getData();
		String template_id = pushMessageEntity.getTemplate_id();
		if (null == template_id || template_id.equals("")) {
			return new JsonResult(ResultCode.DATANOTFOUND, "无法从PushMessageEntity获取template_id");
		}
		String url = pushMessageEntity.getUrl();
		if (null == url || url.equals("")) {
			return new JsonResult(ResultCode.DATANOTFOUND, "无法从PushMessageEntity获取url");
		}
		String params = generatePushTemplate(data, template_id,url);
		if (params.equals("")) {
			return new JsonResult(ResultCode.EXCEPTION, "generatePushTemplate()失败");
		}

		HttpPost httpPost = new HttpPost(pushMessageUrl);
		CloseableHttpClient httpClient = HttpClients.createDefault();

		// 装配post请求参数
		for (String openid : openidlist) {
			StringBuffer buffer = new StringBuffer();
			// 按照官方api的要求提供params
			buffer.append("{");
			buffer.append(String.format("\"touser\":\"%s\"", openid)).append(",");
			buffer.append(params);
			String urlParams = new String(buffer.toString().getBytes("UTF-8"));
			
			
			StringEntity myEntity = new StringEntity(urlParams, ContentType.APPLICATION_JSON);
			System.out.println("Push Message urlParams = " + urlParams);
			
			httpPost.setEntity(myEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			String resultStr = "发送失败";
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 发送成功
				String resutlEntity = EntityUtils.toString(httpResponse.getEntity());
				ResultTemplateDate resultTemplateDate = JSONObject.parseObject(resutlEntity, ResultTemplateDate.class);
				if (resultTemplateDate.getErrcode().equals("40037")) {
					resultStr = "template_id不正确";
					sendFailUsers.add(openid);	
				}
				if (resultTemplateDate.getErrcode().equals("41028")) {
					resultStr = "form_id不正确，或者过期";
					sendFailUsers.add(openid);	
				}
				if (resultTemplateDate.getErrcode().equals("41029")) {
					resultStr = "form_id已被使用";
					sendFailUsers.add(openid);	
				}
				if (resultTemplateDate.getErrcode().equals("41030")) {
					resultStr = "page不正确";
					sendFailUsers.add(openid);	
				}
				if (resultTemplateDate.getErrcode().equals("45009")) {
					resultStr = "接口调用超过限额（目前默认每个帐号日调用限额为100万）";
					sendFailUsers.add(openid);	
				}
				resultStr = "ok";
				sendSuccessUsers.add(openid);
//				return resultStr;
			} else {
				// 发送失败
				sendFailUsers.add(openid);			}
		}

		JsonResult jsonResult = new JsonResult();
		if (sendFailUsers.isEmpty()) {
			jsonResult.setCode(ResultCode.SUCCESS);
			jsonResult.setMessage("全部USER通知发送成功");
		}else {
			jsonResult.setCode(ResultCode.FAIL);
			jsonResult.setMessage("USER微信通知发送失败");
		}
		HashMap<String, Object> sendMessageResult = new HashMap<String, Object>();
		sendMessageResult.put("sendSuccessUsers", sendSuccessUsers);
		sendMessageResult.put("sendFailUsers", sendFailUsers);
		jsonResult.setData(sendMessageResult);
		return jsonResult;
	}

	private String generatePushTemplate(JSONObject data, String template_id,String url) {

		JSONObject firstObj = data.getJSONObject("first");
		String firstContent = firstObj.getString("value");
		String firstColor = firstObj.getString("color");

		JSONObject keyword_1_Obj = data.getJSONObject("keyword1");
		String keyword_1_Content = keyword_1_Obj.getString("value");
		String keyword_1_Color = keyword_1_Obj.getString("color");
		
		JSONObject keyword_2_Obj = data.getJSONObject("keyword2");
		String keyword_2_Content = keyword_2_Obj.getString("value");
		String keyword_2_Color = keyword_2_Obj.getString("color");


//		JSONObject remarkObj = data.getJSONObject("remark");
//		String remarkContent = remarkObj.getString("value");
//		String remarkColor = remarkObj.getString("color");

		StringBuffer buffer = new StringBuffer();
		buffer.append(String.format("\"template_id\":\"%s\"", template_id)).append(",");
		buffer.append(String.format("\"url\":\"%s\"", url)).append(",");
		buffer.append("\"data\":{");
		buffer.append(String.format("\"%s\": {\"value\":\"%s\",\"color\":\"%s\"},", "first", firstContent, firstColor));
		buffer.append(String.format("\"%s\": {\"value\":\"%s\",\"color\":\"%s\"},", "keyword1", keyword_1_Content,
				keyword_1_Color));
		buffer.append(String.format("\"%s\": {\"value\":\"%s\",\"color\":\"%s\"}", "keyword2", keyword_2_Content, keyword_2_Color));

		buffer.append("}");
		buffer.append("}");
		String params = "";
		try {
			params = new String(buffer.toString().getBytes("UTF-8"));
			System.out.println("utf-8 编码：" + params);
			return params;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	private String getAccessToken() {

		CloseableHttpClient httpCilent = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(WebChatUtil.ACCESS_TOKEN_URL);
		try {
			HttpResponse httpResponse = httpCilent.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String entity = EntityUtils.toString(httpResponse.getEntity());
				AccessToken accessToken = JSONObject.parseObject(entity, AccessToken.class);
				return accessToken.getAccess_token();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 释放资源
				httpCilent.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
}
