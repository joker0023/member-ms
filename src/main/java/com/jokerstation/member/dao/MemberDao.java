package com.jokerstation.member.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jokerstation.member.mapper.MemberMapper;
import com.jokerstation.member.pojo.Member;

@Service
public class MemberDao {

	@Autowired
	private MemberMapper memberMapper;
	
	public int countByShopId(Long shopId) {
		Member memberRecord = new Member();
		memberRecord.setShopId(shopId);
		return memberMapper.selectCount(memberRecord);
	}
	
	public Member selectOne(Long shopId, Long userId) {
		Member record = new Member();
		record.setShopId(shopId);
		record.setUserId(userId);
		return memberMapper.selectOne(record);
	}
	
	public int deleteOne(Long shopId, Long userId) {
		Member member = new Member();
		member.setShopId(shopId);
		member.setUserId(userId);
		return memberMapper.delete(member);
	}
}
