package com.jokerstation.common.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jokerstation.common.data.ResultModel;

public class HttpUtil {

	public static void setResponse(HttpServletResponse response, int status, String msg) throws Exception {
		response.setStatus(status);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		ResultModel model = new ResultModel(status + "", msg);
		response.getWriter().print(JsonUtils.toJson(model));
	}
	
	public static boolean isAjaxRequest(HttpServletRequest request) {
		String requestedWith = request.getHeader("x-requested-with");
		if (requestedWith != null && requestedWith.equalsIgnoreCase("XMLHttpRequest")) {
			return true;
		} else {
			return false;
		}
	}
}
