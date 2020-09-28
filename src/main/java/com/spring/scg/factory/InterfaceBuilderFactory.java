package com.spring.scg.factory;

import java.util.List;

import javax.lang.model.element.Modifier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.ObjectUtils;

import com.spring.scg.constant.KeyConstant;
import com.spring.scg.model.Column;
import com.spring.scg.service.SCGServiceImpl;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.TypeSpec.Builder;

public class InterfaceBuilderFactory
{	
	//----------------------------------------------------------------BUILD SERVICE-------------------------------------------------
	
	public static TypeSpec buildService(String interfaceName, List<FieldSpec> fields, List<MethodSpec> methods, 
											List<AnnotationSpec> annotations, Column column)
	{
		Builder typeSpecBuilder = null;			
		
		if(!ObjectUtils.isEmpty(fields) && !ObjectUtils.isEmpty(methods) && !ObjectUtils.isEmpty(annotations))
		{
			typeSpecBuilder = TypeSpec.interfaceBuilder((interfaceName).trim())
										.addModifiers(Modifier.PUBLIC)
										.addFields(fields)
										.addMethods(methods)
										.addAnnotations(annotations);
		}
		else if(!ObjectUtils.isEmpty(fields) && !ObjectUtils.isEmpty(methods))
		{
			typeSpecBuilder = TypeSpec.interfaceBuilder((interfaceName).trim())
										.addModifiers(Modifier.PUBLIC)
										.addFields(fields)
										.addMethods(methods);
		}
		else if(!ObjectUtils.isEmpty(methods) && !ObjectUtils.isEmpty(annotations))
		{
			typeSpecBuilder = TypeSpec.interfaceBuilder((interfaceName).trim())
										.addModifiers(Modifier.PUBLIC)
										.addMethods(methods)
										.addAnnotations(annotations);
		}
		else if(!ObjectUtils.isEmpty(fields) && !ObjectUtils.isEmpty(annotations))
		{
			typeSpecBuilder = TypeSpec.interfaceBuilder((interfaceName).trim())
										.addModifiers(Modifier.PUBLIC)
										.addFields(fields)
										.addAnnotations(annotations);
		}
		else if(!ObjectUtils.isEmpty(methods))
		{
			typeSpecBuilder = TypeSpec.interfaceBuilder((interfaceName).trim())
										.addModifiers(Modifier.PUBLIC)
										.addMethods(methods);
		}
		else
		{
			typeSpecBuilder = TypeSpec.interfaceBuilder((interfaceName).trim())
										.addModifiers(Modifier.PUBLIC);
		}		
		typeSpecBuilder.addJavadoc("@Author: ItzMee and \n@Author: Rajib Garai \n@BuiltWith: JavaPoet\n");
		return typeSpecBuilder.build();
	}

//----------------------------------------------------------------BUILD REPOSITORY-------------------------------------------------

	public static TypeSpec buildRepository(String interfaceName, List<FieldSpec> fields, List<MethodSpec> methods,
												List<AnnotationSpec> annotations, Column column)
	{
		Builder typeSpecBuilder = null;	
		
		ClassName modelClass = ClassName.get((KeyConstant.MSG_PKG_COM_DOT + interfaceName.toLowerCase() 
												+ KeyConstant.MSG_PKG_DOT_MODEL).trim(), interfaceName);
	
		ClassName primaryKey = ClassName.get(SCGServiceImpl.getFieldType(column.getDataType()));
				
		TypeVariableName jpaRepository = TypeVariableName.get(JpaRepository.class.getTypeName().concat("<" + modelClass + ", "
																		+ primaryKey + ">"));
		
		if(!ObjectUtils.isEmpty(methods) && !ObjectUtils.isEmpty(annotations))
		{
			typeSpecBuilder = TypeSpec.interfaceBuilder((interfaceName+KeyConstant.MSG_NAME_REPOSITORY).trim())
										.addModifiers(Modifier.PUBLIC)
										.addMethods(methods)
										.addAnnotations(annotations)
										.addSuperinterface(jpaRepository);
		}
		else
		{
			typeSpecBuilder = TypeSpec.interfaceBuilder((interfaceName+KeyConstant.MSG_NAME_REPOSITORY).trim())
										.addModifiers(Modifier.PUBLIC)
										.addAnnotations(annotations)
										.addSuperinterface(jpaRepository);
		}
		typeSpecBuilder.addJavadoc("@Author: ItzMee and \n@Author: Rajib Garai \n@BuiltWith: JavaPoet\n");
		return typeSpecBuilder.build();
	}
}
