package com.jokerstation.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jokerstation.member.service.AppService;
import com.jokerstation.member.vo.TokenVo;

@RestController
public class CommTest {

	@GetMapping("/hello")
	public String hello() {
		return "success";
	}
	
	@GetMapping("/info")
	public Object info() {
		Map<String, Object> map = new HashMap<>();
		map.put("token-size", AppService.getTokenMap().size());
		map.put("token-keys", AppService.getTokenMap().keySet());
		return map;
	}
	
	@GetMapping("/login")
	public String login() {
		setToken("123", 2L);
		setToken("456", 3L);
		
		return "success";
	}
	
	private void setToken(String token, Long userId) {
		TokenVo vo = new TokenVo();
		vo.setToken(token);
		vo.setTimeMillis(System.currentTimeMillis());
		vo.setUserId(userId);
		AppService.getTokenMap().put(token, vo);
	}
	
}
