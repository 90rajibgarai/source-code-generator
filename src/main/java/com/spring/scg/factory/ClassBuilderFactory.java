package com.spring.scg.factory;

import java.util.List;

import javax.lang.model.element.Modifier;

import org.springframework.util.ObjectUtils;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

public class ClassBuilderFactory 
{	
	public static TypeSpec build(String className, List<FieldSpec> fields, List<MethodSpec> methods, List<AnnotationSpec> annotations)
	{
		Builder typeSpecBuilder = null;
				
		if(!ObjectUtils.isEmpty(fields) && !ObjectUtils.isEmpty(methods) && !ObjectUtils.isEmpty(annotations))
		{
			typeSpecBuilder = TypeSpec.classBuilder(className)
										.addModifiers(Modifier.PUBLIC)
										.addFields(fields)
										.addMethods(methods)
										.addAnnotations(annotations);
		}
		else if(!ObjectUtils.isEmpty(fields) && !ObjectUtils.isEmpty(methods))
		{
			typeSpecBuilder = TypeSpec.classBuilder(className)
										.addModifiers(Modifier.PUBLIC)
										.addFields(fields)
										.addMethods(methods);
		}
		else if(!ObjectUtils.isEmpty(methods) && !ObjectUtils.isEmpty(annotations))
		{
			typeSpecBuilder = TypeSpec.classBuilder(className)
										.addModifiers(Modifier.PUBLIC)
										.addMethods(methods)
										.addAnnotations(annotations);
		}
		else if(!ObjectUtils.isEmpty(fields) && !ObjectUtils.isEmpty(annotations))
		{
			typeSpecBuilder = TypeSpec.classBuilder(className)
										.addModifiers(Modifier.PUBLIC)
										.addFields(fields)
										.addAnnotations(annotations);
		}
		else
		{
			typeSpecBuilder = TypeSpec.classBuilder(className)
										.addModifiers(Modifier.PUBLIC);
		}
		
		typeSpecBuilder.addJavadoc("@Author: ItzMee and \n@Author: Rajib Garai \n@BuiltWith: JavaPoet\n");
		return typeSpecBuilder.build();
	}
	
//--------------------------------------------------------------------------------------CLASS GENERATION WITH INTERFACE IMPLEMENTATION-------------------------
	
	public static TypeSpec build(String className, List<FieldSpec> fields, List<MethodSpec> methods, List<AnnotationSpec> annotations, ClassName implClassName)
	{
		Builder typeSpecBuilder = null;
				
		if(!ObjectUtils.isEmpty(fields) && !ObjectUtils.isEmpty(methods) && !ObjectUtils.isEmpty(annotations))
		{
			typeSpecBuilder = TypeSpec.classBuilder(className)
										.addModifiers(Modifier.PUBLIC)
										.addFields(fields)
										.addMethods(methods)
										.addAnnotations(annotations)										
										.addSuperinterface(implClassName);
		}
		else if(!ObjectUtils.isEmpty(fields) && !ObjectUtils.isEmpty(methods))
		{
			typeSpecBuilder = TypeSpec.classBuilder(className)
										.addModifiers(Modifier.PUBLIC)
										.addFields(fields)
										.addMethods(methods)
										.addSuperinterface(implClassName);
		}
		else if(!ObjectUtils.isEmpty(methods) && !ObjectUtils.isEmpty(annotations))
		{
			typeSpecBuilder = TypeSpec.classBuilder(className)
										.addModifiers(Modifier.PUBLIC)
										.addMethods(methods)
										.addAnnotations(annotations)
										.addSuperinterface(implClassName);
		}
		else if(!ObjectUtils.isEmpty(fields) && !ObjectUtils.isEmpty(annotations))
		{
			typeSpecBuilder = TypeSpec.classBuilder(className)
										.addModifiers(Modifier.PUBLIC)
										.addFields(fields)
										.addAnnotations(annotations)
										.addSuperinterface(implClassName);
		}
		else
		{
			typeSpecBuilder = TypeSpec.classBuilder(className)
										.addModifiers(Modifier.PUBLIC)
										.addSuperinterface(implClassName);
		}
		
		typeSpecBuilder.addJavadoc("@Author: ItzMee and \n@Author: Rajib Garai \n@BuiltWith: JavaPoet\n");
		return typeSpecBuilder.build();
	}
}
