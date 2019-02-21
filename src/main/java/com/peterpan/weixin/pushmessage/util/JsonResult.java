package com.peterpan.weixin.pushmessage.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class JsonResult {
	private String code;
	private String message;
	private Object data;
	private int PageCount;

	public int getPageCount() {
		return PageCount;
	}

	public void setPageCount(int pageCount) {
		PageCount = pageCount;
	}

	public int getPageNum() {
		return PageNum;
	}

	public void setPageNum(int pageNum) {
		PageNum = pageNum;
	}

	private int PageNum;

	public JsonResult() {
		this.setCode(ResultCode.SUCCESS);
		this.setMessage("成功！");
	}

	public JsonResult(ResultCode code) {
		this.setCode(code);
		this.setMessage(code.msg());
	}

	public JsonResult(ResultCode code, String message) {
		this.setCode(code);
		this.setMessage(message);
	}

	public JsonResult(ResultCode code, String message, Object data) {

		this.setCode(code);
		this.setMessage(message);
		this.setData(data);

	}

	public String toString() {

		JSONObject json = new JSONObject();
		try {
			json.put("code", code);
			json.put("message", message);
			json.put("data", data);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json.toString();
	}

	public String getCode() {
		return code;
	}

	public void setCode(ResultCode code) {
		this.code = code.val();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
