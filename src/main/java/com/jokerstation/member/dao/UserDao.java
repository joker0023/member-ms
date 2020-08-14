package com.jokerstation.member.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jokerstation.member.mapper.UserMapper;
import com.jokerstation.member.pojo.User;

@Service
public class UserDao {

	@Autowired
	private UserMapper userMapper;

	public User selectByOpenId(String openId) {
		User record = new User();
		record.setOpenId(openId);
		return userMapper.selectOne(record);
	}
}
