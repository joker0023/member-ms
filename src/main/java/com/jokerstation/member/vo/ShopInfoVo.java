package com.jokerstation.member.vo;

import com.jokerstation.member.pojo.Shop;

import lombok.Data;

@Data
public class ShopInfoVo {

	private Shop shop;
	
	private Integer memberNum;
	
	private Integer clerkNum;
	
	private String totalRecharge;
	
	private String totalConsume;
	
	private Byte sellerType;
}
