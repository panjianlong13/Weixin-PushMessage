package com.peterpan.weixin.pushmessage.entity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessToken {
	// 获取到的凭证
	private String access_token;

	// 凭证有效时间，单位 秒
	private Integer expires_in;
}
