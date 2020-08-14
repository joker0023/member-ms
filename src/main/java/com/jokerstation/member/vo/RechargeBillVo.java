package com.jokerstation.member.vo;

import com.jokerstation.common.util.NumberUtil;
import com.jokerstation.member.pojo.RechargeBill;

public class RechargeBillVo extends RechargeBill {

	private static final long serialVersionUID = -5493007232632658008L;

	private String rateYuan;
	
	private String operateUserNick;
	
	public String getRateYuan() {
		rateYuan = NumberUtil.toYuan(this.getRate());
		return rateYuan;
	}
	
	public String getOperateUserNick() {
		return operateUserNick;
	}

	public void setOperateUserNick(String operateUserNick) {
		this.operateUserNick = operateUserNick;
	}
}
