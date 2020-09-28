package com.spring.scg.factory;

import java.io.File;

import com.spring.scg.constant.KeyConstant;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileBuilderFactory
{
	public static void build(String packageName, TypeSpec typeSpec)
	{	
		JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
		
		File file = new File(KeyConstant.GENERATED_PROJECT_LOCATION);
		try 
		{
			javaFile.writeTo(file);
		} 
		catch (Exception e) 
		{
			log.error("Error in File Generation : {}, {}",e.getMessage(), e.getCause());
		}
	}
}
