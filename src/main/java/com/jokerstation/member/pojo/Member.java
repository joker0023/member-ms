package com.jokerstation.member.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

import lombok.Data;

@Data
public class Member implements Serializable {

	private static final long serialVersionUID = 1201608250630243337L;

	@Id
	private Long userId;
	
	@Id
	private Long shopId;
	
	//余额，单位：分
	private Long balance;
	
	private Date created;
}
