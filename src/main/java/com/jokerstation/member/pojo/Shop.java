package com.jokerstation.member.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

import lombok.Data;

@Data
public class Shop implements Serializable {

	private static final long serialVersionUID = 7774074345745513733L;

	@Id
	private Long id;
	
	private String name;
	
	private String remark;
	
	private Integer maxClerk;
	
	private Date created;
}
