package com.jokerstation.member.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.github.pagehelper.PageInfo;
import com.jokerstation.common.data.ErrorCode;
import com.jokerstation.common.data.ResultModel;
import com.jokerstation.common.exception.BizException;
import com.jokerstation.member.pojo.Seller;
import com.jokerstation.member.pojo.User;
import com.jokerstation.member.service.AppService;
import com.jokerstation.member.vo.ConsumeBillVo;
import com.jokerstation.member.vo.MemberVo;
import com.jokerstation.member.vo.RechargeBillVo;
import com.jokerstation.member.vo.ShopInfoVo;

@RestController
@RequestMapping("/app")
public class AppController {
	
	@Autowired
	private AppService appService;

	private static Logger logger = LoggerFactory.getLogger(AppController.class);
	

	@PostMapping("/updateUser")
	public ResultModel updateUser(@RequestBody Map<String, String> data) {
		String nick = data.get("nick");
		String avatar = data.get("avatar");
		if (StringUtils.isBlank(nick) || StringUtils.isBlank(avatar)) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		
		String token = getToken();
		appService.updateUser(token, nick, avatar);
		return new ResultModel();
	}
	
	@PostMapping("/addShop")
	public ResultModel addShop(@RequestBody Map<String, String> data) {
		String shopName = data.get("shopName");
		String remark = data.get("remark");
		if (StringUtils.isBlank(shopName)) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		
		String token = getToken();
		appService.addShop(token, shopName, remark);
		return new ResultModel();
	}
	
	@PostMapping("/updateShop")
	public ResultModel updateShop(@RequestBody Map<String, String> data) {
		Long shopId = Long.valueOf(data.get("shopId"));
		String shopName = (String)data.get("shopName");
		String remark = (String)data.get("remark");
		if (null == shopId || StringUtils.isBlank(shopName)) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		
		checkToken(getToken(), shopId);
		appService.updateShop(shopId, shopName, remark);
		return new ResultModel();
	}
	
	@GetMapping("/getShopInfoVo")
	public ResultModel getShopInfoVo(Long shopId) {
		if (null == shopId) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		
		String token = getToken();
		checkToken(token, shopId);
		ShopInfoVo shopInfoVo = appService.getShopInfoVo(token, shopId);
		return new ResultModel(shopInfoVo);
	}
	
	@GetMapping("/listMembers")
	public ResultModel listMembers(Long shopId, String keyWord, int page, int size) {
		if (null == shopId) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		
		checkToken(getToken(), shopId);
		PageInfo<User> members = appService.listMembers(shopId, keyWord, page, size);
		return new ResultModel(members);
	}
	
	@GetMapping("/getMemberVo")
	public ResultModel getMemberVo(Long shopId, Long userId) {
		if (null == shopId || null == userId) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		
		checkToken(getToken(), shopId);
		MemberVo memberVo = appService.getMemberVo(shopId, userId);
		return new ResultModel(memberVo);
	}
	
	@PostMapping("/addMember")
	public ResultModel addMember(@RequestBody Map<String, String> data) {
		Long shopId = Long.valueOf(data.get("shopId"));
		Long userId = Long.valueOf(data.get("userId"));
		if (null == shopId || null == userId) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		
		checkToken(getToken(), shopId);
		MemberVo member = appService.addMember(shopId, userId);
		return new ResultModel(member);
	}
	
	@PostMapping("/delMember")
	public ResultModel delMember(@RequestBody Map<String, String> data) {
		Long shopId = Long.valueOf(data.get("shopId"));
		Long userId = Long.valueOf(data.get("userId"));
		if (null == shopId || null == userId) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		
		checkToken(getToken(), shopId);
		appService.delMember(shopId, userId);
		return new ResultModel();
	}
	
	@PostMapping("/recharge")
	public ResultModel recharge(@RequestBody Map<String, String> data) {
		Long shopId = Long.valueOf(data.get("shopId"));
		Long userId = Long.valueOf(data.get("userId"));
		//元
		Double rateYuan = Double.valueOf(data.get("rate"));
		String remark = data.get("remark");
		if (null == shopId || null == userId || null == rateYuan) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		
		String token = getToken();
		checkToken(token, shopId);
		Long rate = Math.round(rateYuan * 100);
		appService.recharge(token, shopId, userId, rate, remark);
		return new ResultModel();
	}
	
	@PostMapping("/consume")
	public ResultModel consume(@RequestBody Map<String, String> data) {
		Long shopId = Long.valueOf(data.get("shopId"));
		Long userId = Long.valueOf(data.get("userId"));
		//元
		Double rateYuan = Double.valueOf(data.get("rate"));
		String remark = data.get("remark");
		if (null == shopId || null == userId || null == rateYuan) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		
		String token = getToken();
		checkToken(token, shopId);
		Long rate = Math.round(rateYuan * 100); 
		appService.consume(token, shopId, userId, rate, remark);
		return new ResultModel();
	}
	
	@GetMapping("/listRechargeBill")
	public ResultModel listRechargeBill(Long shopId, Long userId, int page, int size) {
		if (null == shopId && null == userId) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		
		if (null != shopId) {
			checkToken(getToken(), shopId);
		}
		PageInfo<RechargeBillVo> bills = appService.listRechargeBill(shopId, userId, page, size);
		return new ResultModel(bills);
	}
	
	@GetMapping("/listConsumeBill")
	public ResultModel listConsumeBill(Long shopId, Long userId, int page, int size) {
		if (null == shopId && null == userId) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		
		if (null != shopId) {
			checkToken(getToken(), shopId);
		}
		PageInfo<ConsumeBillVo> bills = appService.listConsumeBill(shopId, userId, page, size);
		return new ResultModel(bills);
	}
	
	@GetMapping("/getRechargeBillVo")
	public ResultModel getRechargeBillVo(Long billId) throws Exception {
		if (null == billId) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		
		String token = getToken();
		RechargeBillVo billVo = appService.getRechargeBillVo(billId);
		boolean checkHasBill = appService.checkHasBill(token, billVo.getUserId(), billVo.getShopId());
		if (checkHasBill) {
			return new ResultModel(billVo);
		}
		return new ResultModel();
	}
	
	@GetMapping("/getConsumeBillVo")
	public ResultModel getConsumeBillVo(Long billId) throws Exception {
		if (null == billId) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		
		String token = getToken();
		ConsumeBillVo billVo = appService.getConsumeBillVo(billId);
		boolean checkHasBill = appService.checkHasBill(token, billVo.getUserId(), billVo.getShopId());
		if (checkHasBill) {
			return new ResultModel(billVo);
		}
		return new ResultModel();
	}
	
	@GetMapping("/listClerks")
	public ResultModel listClerks(Long shopId, String keyWord, int page, int size) {
		if (null == shopId) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		
		checkToken(getToken(), shopId);
		PageInfo<User> clerks = appService.listClerks(shopId, keyWord, page, size);
		return new ResultModel(clerks);
	}
	
	@PostMapping("/addClerk")
	public ResultModel addClerk(@RequestBody Map<String, String> data) {
		Long shopId = Long.valueOf(data.get("shopId"));
		Long userId = Long.valueOf(data.get("userId"));
		if (null == shopId && null == userId) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		
		Seller seller = checkToken(getToken(), shopId);
		byte sellerType = seller.getType();
		if (Seller.TYPE_OWNER != sellerType) {
			return new ResultModel();
		}
		
		appService.addClerk(userId, shopId);
		return new ResultModel();
	}
	
	@PostMapping("/delClerk")
	public ResultModel delClerk(@RequestBody Map<String, String> data) {
		Long shopId = Long.valueOf(data.get("shopId"));
		Long userId = Long.valueOf(data.get("userId"));
		if (null == shopId && null == userId) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		
		Seller seller = checkToken(getToken(), shopId);
		byte sellerType = seller.getType();
		if (Seller.TYPE_OWNER != sellerType) {
			return new ResultModel();
		}
		
		appService.delClerk(userId, shopId);
		return new ResultModel();
	}
	
	
	/*
	 * common
	 */
	
	private String getToken() {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		return request.getHeader("token");
	}
	
	private Seller checkToken(String token, Long shopId) {
		if (null == shopId) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		return appService.checkSeller(token, shopId);
	}
}
