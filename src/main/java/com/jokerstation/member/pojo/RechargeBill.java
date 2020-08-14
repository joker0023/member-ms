package com.jokerstation.member.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

import lombok.Data;

@Data
public class RechargeBill implements Serializable {

	private static final long serialVersionUID = 4197503675265157977L;

	@Id
	private Long id;
	
	private Long userId;
	
	private Long shopId;
	
	//余额，单位：分
	private Long rate;
	
	private String remark;
	
	private Long operateUserId;
	
	private Date created;
}
