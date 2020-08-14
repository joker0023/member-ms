package com.jokerstation.member.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jokerstation.member.pojo.RechargeBill;
import com.jokerstation.member.vo.RechargeBillVo;

import tk.mybatis.mapper.common.Mapper;

public interface RechargeBillMapper extends Mapper<RechargeBill> {

	public long sumByShop(@Param("shopId") Long shopId);
	
	public List<RechargeBillVo> selectRechargeBillVos(@Param("shopId") Long shopId, @Param("userId") Long userId);
}
