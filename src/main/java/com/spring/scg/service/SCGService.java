package com.spring.scg.service;

import java.util.List;

import com.spring.scg.model.Column;

public interface SCGService 
{
	public void generateModel(String tableName, List<Column> columns);

	public void generateController(String tableName, Column column);
	
	public void generateService(String tableName, Column column);

	public void generateRepository(String tableName, Column column);

	public void generateServiceImpl(String tableName, Column column);
}
