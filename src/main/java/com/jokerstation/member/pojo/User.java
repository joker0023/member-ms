package com.jokerstation.member.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

import lombok.Data;

@Data
public class User implements Serializable {

	private static final long serialVersionUID = 7697720807350257075L;
	
	public final static byte STATUS_OK = 1;

	@Id
	private Long id;
	
	private String openId;
	
	private String nick;
	
	private String avatar;
	
	private String remark;
	
	private String phone;
	
	private Byte userStatus;
	
	private Integer maxShop;
	
	private Date created;
}
