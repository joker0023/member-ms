package com.jokerstation.member.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.jokerstation.common.data.ErrorCode;
import com.jokerstation.common.util.HttpUtil;
import com.jokerstation.member.service.AppService;
import com.jokerstation.member.vo.TokenVo;

public class AppInterceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object paramObject,
			Exception paramException) throws Exception {
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object paramObject,
			ModelAndView paramModelAndView) throws Exception {
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object paramObject) throws Exception {
		String token = request.getHeader("token");
		if (null == token) {
			HttpUtil.setResponse(response, 403, ErrorCode.REQUEST_ILLEGA.getMsg());
			return false;
		}
		TokenVo tokenVo = AppService.getTokenVo(token);
		if (null == tokenVo) {
			HttpUtil.setResponse(response, 403, ErrorCode.EXPIRED.getMsg());
			return false;
		} else {
			tokenVo.setTimeMillis(System.currentTimeMillis());
		}
		
		return true;
	}
}
