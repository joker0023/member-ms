package com.jokerstation.member.vo;

import lombok.Data;

@Data
public class MemberVo {

	private Long userId;
	
	private Long shopId;
	
	private Long balance;
	
	private String balanceYuan;
	
	private String nick;
	
	private String avatar;
	
	private String remark;
	
	private String phone;
}
