package com.jokerstation.member.dao;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jokerstation.member.mapper.SellerMapper;
import com.jokerstation.member.pojo.Seller;

@Service
public class SellerDao {

	@Autowired
	private SellerMapper sellerMapper;
	
	public int countClerkByShopId(Long shopId) {
		Seller sellerRecord = new Seller();
		sellerRecord.setShopId(shopId);
		sellerRecord.setType(Seller.TYPE_CLERK);
		return sellerMapper.selectCount(sellerRecord);
	}
	
	public int countOwnerByUserId(Long userId) {
		Seller sellerRecord = new Seller();
		sellerRecord.setUserId(userId);
		sellerRecord.setType(Seller.TYPE_OWNER);
		return sellerMapper.selectCount(sellerRecord);
	}
	
	public Seller selectOne(Long shopId, Long userId) {
		Seller record = new Seller();
		record.setShopId(shopId);
		record.setUserId(userId);
		return sellerMapper.selectOne(record);
	}
	
	public int deleteOne(Long shopId, Long userId) {
		Seller seller = new Seller();
		seller.setShopId(shopId);
		seller.setUserId(userId);
		return sellerMapper.delete(seller);
	}
	
	public List<Seller> selectByUserId(Long userId) {
		Seller sellerRecord = new Seller();
		sellerRecord.setUserId(userId);
		return sellerMapper.select(sellerRecord);
	}
	
	public Set<Long> listShopsByUserId(Long userId) {
		List<Seller> selects = selectByUserId(userId);
		Set<Long> shopIds = selects.stream().map(Seller::getShopId).collect(Collectors.toSet());
		return shopIds;
	}
	
}
