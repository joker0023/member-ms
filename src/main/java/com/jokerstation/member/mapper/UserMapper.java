package com.jokerstation.member.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jokerstation.member.pojo.User;

import tk.mybatis.mapper.common.Mapper;

public interface UserMapper extends Mapper<User> {

	public List<User> listMembers(@Param("shopId") Long shopId, @Param("keyWord") String keyWord);
	
	public List<User> listClerks(@Param("shopId") Long shopId, @Param("keyWord") String keyWord);
}
