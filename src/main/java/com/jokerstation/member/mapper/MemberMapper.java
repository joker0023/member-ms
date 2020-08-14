package com.jokerstation.member.mapper;

import org.apache.ibatis.annotations.Param;

import com.jokerstation.member.pojo.Member;

import tk.mybatis.mapper.common.Mapper;

public interface MemberMapper extends Mapper<Member> {

	public void recharge(@Param("userId") Long userId, @Param("shopId") Long shopId, @Param("rate") Long rate);
	
	public void consume(@Param("userId") Long userId, @Param("shopId") Long shopId, @Param("rate") Long rate);
	
}
