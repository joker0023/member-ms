package com.jokerstation.member.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jokerstation.common.exception.BizException;
import com.jokerstation.common.util.JsonUtils;
import com.jokerstation.common.util.WebUtil;
import com.jokerstation.member.mapper.UserMapper;
import com.jokerstation.member.pojo.User;
import com.jokerstation.member.vo.TokenVo;

@Service
public class AppService {

	@Value("${app.id}")
	private String AppID;
	
	@Value("${app.secret}")
	private String AppSecret;
	
	private UserMapper userMapper;
	
	public static Map<String, TokenVo> tokenMap = new HashMap<String, TokenVo>();
	
	private final static long TOKEN_EXPIRED = 12 * 3600 * 1000;
	
	public static TokenVo getTokenVo(String token) {
		if (null == token) {
			return null;
		}
		
		TokenVo tokenVo = tokenMap.get(token);
		if (null == tokenVo) {
			return null;
		}
		return tokenVo;
	}
	
	public static String getOpenId(String token) {
		TokenVo tokenVo = getTokenVo(token);
		if (null != tokenVo) {
			tokenVo.getOpenId();
		}
		return null;
	}
	
	private static void setSessionTokenVo(TokenVo tokenVo) {
		tokenMap.put(tokenVo.getToken(), tokenVo);
	}
	
	public static void cleanToken() {
		List<String> expiredKey = new ArrayList<String>();
		for (String key : tokenMap.keySet()) {
			TokenVo tokenVo = tokenMap.get(key);
			Long now = System.currentTimeMillis();
			Long timeMillis = tokenVo.getTimeMillis();
			if (now - timeMillis > TOKEN_EXPIRED) {
				expiredKey.add(key);
			}
		}
		
		for (String key : expiredKey) {
			tokenMap.remove(key);
		}
	}
	
	@SuppressWarnings("unchecked")
	public TokenVo getAppUserByCode(String code) throws Exception {
		String url = "https://api.weixin.qq.com/sns/jscode2session?appid={{appid}}&secret={{secret}}&js_code={{js_code}}&grant_type=authorization_code";
		url = url.replace("{{appid}}", AppID)
				.replace("{{secret}}", AppSecret)
				.replace("{{js_code}}", code);
		String result = WebUtil.doGet(url, null);
		Map<String, String> resultMap = JsonUtils.toBean(result, Map.class);
		String openId = (String)resultMap.get("openid");
		String sessionKey = (String)resultMap.get("session_key");
		if (StringUtils.isBlank(openId)) {
			throw new BizException("获取openid出错");
		}
		
		long timeMillis = System.currentTimeMillis();
		String token = DigestUtils.md5Hex(openId + "_" + sessionKey + "_" + timeMillis);
		initUser(openId);
		
		TokenVo vo = new TokenVo();
		vo.setSessionKey(sessionKey);
		vo.setToken(token);
		vo.setTimeMillis(timeMillis);
		vo.setOpenId(openId);
		setSessionTokenVo(vo);
		return vo;
	}
	
	private User initUser(String openId) {
		User user = selectUserByOpenId(openId);
		if (null != user) {
			return user;
		}
		
		user = new User();
		user.setCreated(new Date());
		user.setOpenId(openId);
		user.setUserStatus(User.STATUS_OK);
		userMapper.insert(user);
		
		return selectUserByOpenId(openId);
	}
	
	public User selectUserByOpenId(String openId) {
		User record = new User();
		record.setOpenId(openId);
		return userMapper.selectOne(record);
	}
	
}
