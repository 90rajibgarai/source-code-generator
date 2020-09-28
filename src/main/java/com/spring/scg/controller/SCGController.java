package com.spring.scg.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.scg.model.Column;
import com.spring.scg.service.SCGService;

@RestController
@RequestMapping(value = "/scg/generate")
public class SCGController
{
	@Autowired
	private SCGService scgService;
	
//-------------------------------------------------------------------MODEL CLASS GENERATION------------------------------------	
	
	@PostMapping("/model")
	public ResponseEntity<Object> generateModel(@RequestParam("tableName") String tableName, @RequestBody List<Column> columns)
	{
		scgService.generateModel(tableName, columns);	
		return ResponseEntity.ok().build();
	}
	
//-------------------------------------------------------------------CONTROLLER CLASS GENERATION-------------------------------
	
	@PostMapping("/controller")
	public ResponseEntity<Object> generateController(@RequestParam("tableName") String tableName, @RequestBody Column column)
	{
		scgService.generateController(tableName, column);
		return ResponseEntity.ok().build();
	}
	
//-------------------------------------------------------------------SERVICE INTERFACE GENERATION-------------------------------

	@PostMapping("/service")
	public ResponseEntity<Object> generateService(@RequestParam("tableName") String tableName, @RequestBody Column column)
	{
		scgService.generateService(tableName, column);
		return ResponseEntity.ok().build();
	}
	
//-------------------------------------------------------------------SERVICE CLASS IMPLEMENTATION---------------------------

	@PostMapping("/service-impl")
	public ResponseEntity<Object> generateServiceImpl(@RequestParam("tableName") String tableName, @RequestBody Column column)
	{
		scgService.generateServiceImpl(tableName, column);
		return ResponseEntity.ok().build();
	}
	
//-------------------------------------------------------------------REPOSITORY INTERFACE GENERATION---------------------------

	@PostMapping("/repository")
	public ResponseEntity<Object> generateRepository(@RequestParam("tableName") String tableName, @RequestBody Column column)
	{
		scgService.generateRepository(tableName, column);
		return ResponseEntity.ok().build();
	}
	
//-------------------------------------------------------------------END-------------------------------------------------------
	
}
