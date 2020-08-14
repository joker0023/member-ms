package com.jokerstation.member.mapper;

import com.jokerstation.member.pojo.Shop;

import tk.mybatis.mapper.common.Mapper;

public interface ShopMapper extends Mapper<Shop> {

	public int insertShop(Shop shop);
}
