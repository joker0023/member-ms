package com.jokerstation.member.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jokerstation.common.data.ResultModel;
import com.jokerstation.member.service.AppService;
import com.jokerstation.member.vo.TokenVo;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AppService appService;

	
	@PostMapping("/appLogin")
	public ResultModel appLogin(@RequestBody Map<String, String> data) throws Exception {
		String code = data.get("code");
		TokenVo tokenVo = appService.initTokenVoByCode(code);
		return new ResultModel(tokenVo.getToken());
	}
}
