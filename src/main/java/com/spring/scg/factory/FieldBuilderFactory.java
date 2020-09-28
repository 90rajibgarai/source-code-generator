package com.spring.scg.factory;

import java.util.List;

import javax.lang.model.element.Modifier;

import org.springframework.util.ObjectUtils;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;

public class FieldBuilderFactory
{	
	public static <T> FieldSpec build(String fieldName, Class<T> type, T defaultValue, List<AnnotationSpec> annotationSpec)
	{
		FieldSpec fieldSpec = null;
		
		if(!ObjectUtils.isEmpty(annotationSpec))
		{
			fieldSpec = FieldSpec.builder(type, fieldName).addModifiers(Modifier.PRIVATE).addAnnotations(annotationSpec).build();
		}
		else
		{
			fieldSpec = FieldSpec.builder(type, fieldName).addModifiers(Modifier.PRIVATE).build();
		}
		return fieldSpec;
	}

//----------------------------------------------------------------------------------------------------------[TRY TO IMPROVE]----------------------
	
	public static FieldSpec build(String fieldName, ClassName className, Object defaultValue, List<AnnotationSpec> annotationSpec) 
	{		
		FieldSpec fieldSpec = null;
		
		if(!ObjectUtils.isEmpty(annotationSpec))
		{
			fieldSpec = FieldSpec.builder(className, fieldName).addModifiers(Modifier.PRIVATE).addAnnotations(annotationSpec).build();
		}
		else
		{
			fieldSpec = FieldSpec.builder(className, fieldName).addModifiers(Modifier.PRIVATE).build();
		}
		return fieldSpec;
	}	
}
