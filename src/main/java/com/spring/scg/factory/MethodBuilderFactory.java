package com.spring.scg.factory;

import java.util.List;

import javax.lang.model.element.Modifier;
import org.springframework.util.ObjectUtils;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

public class MethodBuilderFactory
{
//------------------------------------------------------------------------------BUILD WITH LIST RETURN TYPE----------------------------------
	
	public static <T> MethodSpec build(List<AnnotationSpec> annotationSpecs, List<Modifier> modifiers, TypeName rType, 
										String methodName, List<ParameterSpec> parameterSpecs)
	{
		MethodSpec methodSpec = null;
		
		TypeName returnType = !ObjectUtils.isEmpty(rType) ? rType :  ClassName.get(void.class);
		
		if(!ObjectUtils.isEmpty(parameterSpecs) && (!ObjectUtils.isEmpty(annotationSpecs)))
		{
			methodSpec = MethodSpec.methodBuilder(methodName)
									.addParameters(parameterSpecs)
									.addModifiers(modifiers)
									.addAnnotations(annotationSpecs)
									.returns(returnType)
									.build();
		}
		else if(!ObjectUtils.isEmpty(parameterSpecs))
		{
			methodSpec = MethodSpec.methodBuilder(methodName)
									.addParameters(parameterSpecs)
									.addModifiers(modifiers)
									.returns(returnType)
									.build();
		}
		else if(!ObjectUtils.isEmpty(annotationSpecs))
		{
			methodSpec = MethodSpec.methodBuilder(methodName)
									.addAnnotations(annotationSpecs)
									.addModifiers(modifiers)
									.returns(returnType)									
									.build();
		}
		else
		{
			methodSpec = MethodSpec.methodBuilder(methodName)
									.addModifiers(modifiers)							
									.returns(returnType)
									.build();
		}		
		return methodSpec;
	}
	
//------------------------------------------------------------------------------BUILD WITH PREMITIVE CLASS RETURN TYPE----------------------------------
	
	public static <T> MethodSpec build(List<AnnotationSpec> annotationSpecs, List<Modifier> modifiers, Class<T> returnType,
										String methodName, List<ParameterSpec> parameterSpecs)
	{
		MethodSpec methodSpec = null;
				
		if(!ObjectUtils.isEmpty(parameterSpecs) && (!ObjectUtils.isEmpty(annotationSpecs)))
		{
			methodSpec = MethodSpec.methodBuilder(methodName)
									.addParameters(parameterSpecs)
									.addModifiers(modifiers)
									.addAnnotations(annotationSpecs)
									.returns(returnType)
									.build();
		}
		else if(!ObjectUtils.isEmpty(parameterSpecs))
		{
			methodSpec = MethodSpec.methodBuilder(methodName)
									.addParameters(parameterSpecs)
									.addModifiers(modifiers)
									.returns(returnType)
									.build();
		}
		else if(!ObjectUtils.isEmpty(annotationSpecs))
		{
			methodSpec = MethodSpec.methodBuilder(methodName)
									.addAnnotations(annotationSpecs)
									.addModifiers(modifiers)
									.returns(returnType)									
									.build();
		}
		else
		{
			methodSpec = MethodSpec.methodBuilder(methodName)
									.addModifiers(modifiers)							
									.returns(returnType)
									.build();
		}		
		return methodSpec;
	}
}
