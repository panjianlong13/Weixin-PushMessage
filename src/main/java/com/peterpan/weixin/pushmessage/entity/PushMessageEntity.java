package com.peterpan.weixin.pushmessage.entity;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushMessageEntity {
	private String[] openidlist;
	private String template_id;
	private String url;
	private JSONObject data;
	
}