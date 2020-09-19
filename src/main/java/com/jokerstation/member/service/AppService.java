package com.jokerstation.member.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jokerstation.common.data.ErrorCode;
import com.jokerstation.common.exception.BizException;
import com.jokerstation.common.util.JsonUtils;
import com.jokerstation.common.util.NumberUtil;
import com.jokerstation.common.util.WebUtil;
import com.jokerstation.member.dao.MemberDao;
import com.jokerstation.member.dao.SellerDao;
import com.jokerstation.member.dao.UserDao;
import com.jokerstation.member.mapper.ConsumeBillMapper;
import com.jokerstation.member.mapper.MemberMapper;
import com.jokerstation.member.mapper.RechargeBillMapper;
import com.jokerstation.member.mapper.SellerMapper;
import com.jokerstation.member.mapper.ShopMapper;
import com.jokerstation.member.mapper.UserMapper;
import com.jokerstation.member.pojo.ConsumeBill;
import com.jokerstation.member.pojo.Member;
import com.jokerstation.member.pojo.RechargeBill;
import com.jokerstation.member.pojo.Seller;
import com.jokerstation.member.pojo.Shop;
import com.jokerstation.member.pojo.User;
import com.jokerstation.member.vo.ConsumeBillVo;
import com.jokerstation.member.vo.MemberVo;
import com.jokerstation.member.vo.RechargeBillVo;
import com.jokerstation.member.vo.ShopInfoVo;
import com.jokerstation.member.vo.TokenVo;

import tk.mybatis.mapper.entity.Example;

@Service
public class AppService {

	@Value("${app.id}")
	private String AppID;
	
	@Value("${app.secret}")
	private String AppSecret;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private ShopMapper shopMapper;
	
	@Autowired
	private MemberMapper memberMapper;
	
	@Autowired
	private SellerMapper sellerMapper;
	
	@Autowired
	private RechargeBillMapper rechargeBillMapper;
	
	@Autowired
	private ConsumeBillMapper consumeBillMapper;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private SellerDao sellerDao;
	
	@Autowired
	private MemberDao memberDao;
	
	private static Map<String, TokenVo> tokenMap = new HashMap<String, TokenVo>();
	
	private final static long TOKEN_EXPIRED = 12 * 3600 * 1000;
	
	public static Map<String, TokenVo> getTokenMap() {
		return tokenMap;
	}
	
	public static void cleanToken() {
		List<String> expiredKey = new ArrayList<String>();
		for (String key : tokenMap.keySet()) {
			TokenVo tokenVo = tokenMap.get(key);
			Long now = System.currentTimeMillis();
			Long timeMillis = tokenVo.getTimeMillis();
			if (now - timeMillis > TOKEN_EXPIRED) {
				expiredKey.add(key);
			}
		}
		
		for (String key : expiredKey) {
			tokenMap.remove(key);
		}
	}
	
	@SuppressWarnings("unchecked")
	public TokenVo initTokenVoByCode(String code) throws Exception {
		String url = "https://api.weixin.qq.com/sns/jscode2session?appid={{appid}}&secret={{secret}}&js_code={{js_code}}&grant_type=authorization_code";
		url = url.replace("{{appid}}", AppID)
				.replace("{{secret}}", AppSecret)
				.replace("{{js_code}}", code);
		String result = WebUtil.doGet(url, null);
		Map<String, String> resultMap = JsonUtils.toBean(result, Map.class);
		String openId = (String)resultMap.get("openid");
		String sessionKey = (String)resultMap.get("session_key");
		if (StringUtils.isBlank(openId)) {
			throw new BizException("获取openid出错");
		}
		
		long timeMillis = System.currentTimeMillis();
		String token = DigestUtils.md5Hex(openId + "_" + sessionKey + "_" + timeMillis);
		User user = initUser(openId);
		
		TokenVo vo = new TokenVo();
		vo.setSessionKey(sessionKey);
		vo.setToken(token);
		vo.setTimeMillis(timeMillis);
//		vo.setOpenId(openId);
		vo.setUserId(user.getId());
		tokenMap.put(token, vo);
		return vo;
	}
	
	private User initUser(String openId) {
		User user = selectUserByOpenId(openId);
		if (null != user) {
			return user;
		}
		
		user = new User();
		user.setCreated(new Date());
		user.setOpenId(openId);
		user.setUserStatus(User.STATUS_OK);
		user.setMaxShop(1);
		userMapper.insert(user);
		
		return selectUserByOpenId(openId);
	}
	
	public static TokenVo getTokenVo(String token) {
		if (null == token) {
			return null;
		}
		TokenVo tokenVo = tokenMap.get(token);
		if (null == tokenVo) {
			return null;
		}
		return tokenVo;
	}
	
	private User selectUserByOpenId(String openId) {
		return userDao.selectByOpenId(openId);
	}
	
	public User getUser(String token) {
		Long userId = getTokenVo(token).getUserId();
		return userMapper.selectByPrimaryKey(userId);
	}
	
	public void updateUser(String token, String nick, String avatar) {
		Long userId = getTokenVo(token).getUserId();
		if (null == userId) {
			return;
		}
		User record = new User();
		record.setId(userId);
		record.setNick(nick);
		record.setAvatar(avatar);
//		record.setPhone(phone);
		userMapper.updateByPrimaryKeySelective(record);
	}
	
	@Transactional
	public void addShop(String token, String shopName, String remark) {
		Long userId = getTokenVo(token).getUserId();
		User user = userMapper.selectByPrimaryKey(userId);
		
		int ownerShopCount = sellerDao.countOwnerByUserId(userId);
		if (ownerShopCount >= user.getMaxShop()) {
			throw new BizException("已超过最大可建立店铺数");
		}
		
		Shop shop = new Shop();
		shop.setCreated(new Date());
		shop.setMaxClerk(2);
		shop.setName(shopName);
		shop.setRemark(remark);
		shopMapper.insertShop(shop);
		
		Seller seller = new Seller();
		seller.setCreated(new Date());
		seller.setShopId(shop.getId());
		seller.setType(Seller.TYPE_OWNER);
		seller.setUserId(userId);
		sellerMapper.insert(seller);
	}
	
	public void updateShop(Long shopId, String shopName, String remark) {
		Shop shop = new Shop();
		shop.setId(shopId);
		shop.setName(shopName);
		shop.setRemark(remark);
		shopMapper.updateByPrimaryKeySelective(shop);
	}
	
	public ShopInfoVo getShopInfoVo(String token, Long shopId) {
		Long userId = getTokenVo(token).getUserId();
		Seller seller = sellerDao.selectOne(shopId, userId);
		Shop shop = shopMapper.selectByPrimaryKey(shopId);
		
		int memberNum = memberDao.countByShopId(shopId);
		int clerkNum = sellerDao.countClerkByShopId(shopId);
		long totalRecharge = rechargeBillMapper.sumByShop(shopId);
		long totalConsume = consumeBillMapper.sumByShop(shopId);
		String totalRechargeStr = NumberUtil.toYuan(totalRecharge);
		String totalConsumeStr = NumberUtil.toYuan(totalConsume);
		
		ShopInfoVo vo = new ShopInfoVo();
		vo.setShop(shop);
		vo.setMemberNum(memberNum);
		vo.setClerkNum(clerkNum);
		vo.setTotalRecharge(totalRechargeStr);
		vo.setTotalConsume(totalConsumeStr);
		vo.setSellerType(seller.getType());
		
		return vo;
	}
	
	
	public PageInfo<User> listMembers(Long shopId, String keyWord, int page, int size) {
		PageHelper.startPage(page, size);
		List<User> users = userMapper.listMembers(shopId, keyWord);
		return new PageInfo<>(users);
	}
	
	public MemberVo getMemberVo(Long shopId, Long userId) {
		Member member = memberDao.selectOne(shopId, userId);
		if (null == member) {
			return null;
		}
		
		User user = userMapper.selectByPrimaryKey(userId);
		MemberVo vo = new MemberVo();
		vo.setUserId(userId);
		vo.setShopId(shopId);
		vo.setBalance(member.getBalance());
		vo.setNick(user.getNick());
		vo.setAvatar(user.getAvatar());
		vo.setRemark(user.getRemark());
		vo.setPhone(user.getPhone());
		vo.setBalanceYuan(NumberUtil.toYuan(member.getBalance()));
		return vo;
	}
	
	public MemberVo addMember(Long shopId, Long userId) {
		Member existsMember = memberDao.selectOne(shopId, userId);
		if (null == existsMember) {
			Member member = new Member();
			member.setBalance(0L);
			member.setCreated(new Date());
			member.setShopId(shopId);
			member.setUserId(userId);
			memberMapper.insert(member);
		}
		
		return getMemberVo(shopId, userId);
	}
	
	public void delMember(Long shopId, Long userId) {
		memberDao.deleteOne(shopId, userId);
	}
	
	/**
	 * recharge
	 * @param token
	 * @param shopId
	 * @param userId
	 * @param rate 单位：分
	 * @param remark
	 */
	@Transactional
	public void recharge(String token, Long shopId, Long userId, Long rate, String remark) {
		Member member = memberDao.selectOne(shopId, userId);
		if (null == member) {
			throw new BizException("会员不存在");
		}
		
		Long operateUserId = getTokenVo(token).getUserId();
		RechargeBill rechargeBill = new RechargeBill();
		rechargeBill.setCreated(new Date());
		rechargeBill.setRate(rate);
		rechargeBill.setRemark(remark);
		rechargeBill.setShopId(shopId);
		rechargeBill.setUserId(userId);
		rechargeBill.setOperateUserId(operateUserId);
		rechargeBillMapper.insert(rechargeBill);
		
		memberMapper.recharge(userId, shopId, rate);
	}
	
	/**
	 * consume 目前并发会有问题，假如存在并发，记得加个锁
	 * @param token
	 * @param shopId
	 * @param userId
	 * @param rate 单位：分
	 * @param remark
	 */
	@Transactional
	public void consume(String token, Long shopId, Long userId, Long rate, String remark) {
		Member member = memberDao.selectOne(shopId, userId);
		if (null == member) {
			throw new BizException("会员不存在");
		}
		if (member.getBalance() < rate) {
			throw new BizException("余额不足");
		}
		
		Long operateUserId = getTokenVo(token).getUserId();
		ConsumeBill consumeBill = new ConsumeBill();
		consumeBill.setCreated(new Date());
		consumeBill.setRate(rate);
		consumeBill.setRemark(remark);
		consumeBill.setShopId(shopId);
		consumeBill.setUserId(userId);
		consumeBill.setOperateUserId(operateUserId);
		consumeBillMapper.insert(consumeBill);
		
		memberMapper.consume(userId, shopId, rate);
	}
	
	public PageInfo<RechargeBillVo> listRechargeBill(Long shopId, Long userId, int page, int size) {
		PageHelper.startPage(page, size);
		List<RechargeBillVo> billVos = rechargeBillMapper.selectRechargeBillVos(shopId, userId);
		return new PageInfo<RechargeBillVo>(billVos);
	}
	
	public PageInfo<ConsumeBillVo> listConsumeBill(Long shopId, Long userId, int page, int size) {
		PageHelper.startPage(page, size);
		List<ConsumeBillVo> billVos = consumeBillMapper.selectConsumeBillVos(shopId, userId);
		return new PageInfo<ConsumeBillVo>(billVos);
	}
	
	public RechargeBillVo getRechargeBillVo(Long billId) throws Exception {
		RechargeBill bill = rechargeBillMapper.selectByPrimaryKey(billId);
		RechargeBillVo vo = new RechargeBillVo();
		BeanUtils.copyProperties(vo, bill);
		return vo;
	}
	
	public ConsumeBillVo getConsumeBillVo(Long billId) throws Exception {
		ConsumeBill bill = consumeBillMapper.selectByPrimaryKey(billId);
		ConsumeBillVo vo = new ConsumeBillVo();
		BeanUtils.copyProperties(vo, bill);
		return vo;
	}
	
	public PageInfo<User> listClerks(Long shopId, String keyWord, int page, int size) {
		PageHelper.startPage(page, size);
		List<User> users = userMapper.listClerks(shopId, keyWord);
		return new PageInfo<>(users);
	}
	
	public void addClerk(Long userId, Long shopId) {
		Shop shop = shopMapper.selectByPrimaryKey(shopId);
		
		int clerkCount = sellerDao.countClerkByShopId(shopId);
		if (clerkCount >= shop.getMaxClerk()) {
			throw new BizException("超过最大可添加店员数");
		}
		
		Seller existsSeller = sellerDao.selectOne(shopId, userId);
		if (null == existsSeller) {
			Seller seller = new Seller();
			seller.setCreated(new Date());
			seller.setShopId(shopId);
			seller.setType(Seller.TYPE_CLERK);
			seller.setUserId(userId);
			sellerMapper.insert(seller);
		}
	}
	
	public void delClerk(Long userId, Long shopId) {
		sellerDao.deleteOne(shopId, userId);
	}
	
	public Seller checkSeller(String token, Long shopId) {
		Long userId = getTokenVo(token).getUserId();
		if (null == userId || null == shopId) {
			throw new BizException(ErrorCode.PARAM_ILLEGAL);
		}
		Seller record = new Seller();
		record.setShopId(shopId);
		record.setUserId(userId);
		Seller seller = sellerMapper.selectOne(record);
		if (null == seller) {
			throw new BizException(ErrorCode.REQUEST_ILLEGA);
		}
		
		return seller;
	}
	
	public boolean checkHasBill(String token, Long billUserId, Long billShopId) {
		Long userId = getTokenVo(token).getUserId();
		if (userId.equals(billUserId)) {
			return true;
		}
		
		Set<Long> shopIds = sellerDao.listShopsByUserId(userId);
		if (shopIds.contains(billShopId)) {
			return true;
		}
		
		return false;
	}
	
	
	public List<Shop> getOwnerShop(String token) {
		Long userId = getTokenVo(token).getUserId();
		Seller record = new Seller();
		record.setUserId(userId);
		List<Seller> sellers = sellerMapper.select(record);
		List<Long> shopIds = sellers.stream().map(Seller::getShopId).collect(Collectors.toList());
		if (shopIds.size() == 0) {
			return null;
		}
		
		Example example = new Example(Shop.class);
		example.createCriteria().andIn("id", shopIds);
		return shopMapper.selectByExample(example);
	}
	
	
	
	
}
