package com.jokerstation.member.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jokerstation.member.pojo.ConsumeBill;
import com.jokerstation.member.vo.ConsumeBillVo;

import tk.mybatis.mapper.common.Mapper;

public interface ConsumeBillMapper extends Mapper<ConsumeBill> {

	public Long sumByShop(@Param("shopId") Long shopId);
	
	public List<ConsumeBillVo> selectConsumeBillVos(@Param("shopId") Long shopId, @Param("userId") Long userId);
}
