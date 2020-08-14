package com.jokerstation.member.vo;

import com.jokerstation.common.util.NumberUtil;
import com.jokerstation.member.pojo.ConsumeBill;

public class ConsumeBillVo extends ConsumeBill {

	private static final long serialVersionUID = -3264706648242440350L;

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
