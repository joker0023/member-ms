package com.jokerstation.member.vo;

import lombok.Data;

@Data
public class TokenVo {

	private String sessionKey;
	
	private String token;
	
	private Long timeMillis;
	
	private String openId;
}
