package com.jokerstation.member.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

import lombok.Data;

@Data
public class Seller implements Serializable {

	private static final long serialVersionUID = 5105824186960252293L;

	public static final byte TYPE_OWNER = 1;
	public static final byte TYPE_CLERK = 2;
	
	@Id
	private Long userId;
	
	@Id
	private Long shopId;
	
	private Byte type;
	
	private Date created;
}
