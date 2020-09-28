package com.spring.scg.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class Column implements Serializable
{
	private static final long serialVersionUID = -3099525840580580263L;

	private String name;
	
	private String dataType;
	
	private boolean isPrimaryKey;	
}
