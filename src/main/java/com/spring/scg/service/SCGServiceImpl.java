package com.spring.scg.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.scg.constant.KeyConstant;
import com.spring.scg.factory.ClassBuilderFactory;
import com.spring.scg.factory.FieldBuilderFactory;
import com.spring.scg.factory.FileBuilderFactory;
import com.spring.scg.factory.InterfaceBuilderFactory;
import com.spring.scg.factory.MethodBuilderFactory;
import com.spring.scg.model.Column;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SCGServiceImpl implements SCGService 
{	
//--------------------------------------------------------------------------------GENERATE MODEL CLASS-----------------------------------------
	@Override
	public void generateModel(String tableName, List<Column> columns) 
	{
		List<AnnotationSpec> classAnnotations = new ArrayList<>();
		List<FieldSpec> fields = new ArrayList<>();
		
		try
		{
			for (Column column : columns) 
			{
				if(column.isPrimaryKey())	
				{
					List<AnnotationSpec> fieldAnnotations = new ArrayList<>();
					
					fieldAnnotations.add(AnnotationSpec.builder(GeneratedValue.class)
							.addMember(KeyConstant.MSG_STRATEGY, KeyConstant.MSG_PERSISTENCE_IDENTITY).build());
					
					fieldAnnotations.add(AnnotationSpec.builder(Id.class).build());
					fields.add(FieldBuilderFactory.build(column.getName(), getFieldType(column.getDataType()), null, fieldAnnotations));
				}
				else	
					fields.add(FieldBuilderFactory.build(column.getName(), getFieldType(column.getDataType()), null, null));			
			}	
	
			classAnnotations.add(AnnotationSpec.builder(Entity.class).build());
			classAnnotations.add(AnnotationSpec.builder(Table.class).addMember(KeyConstant.MSG_STATEMENT_NAME, "\"" + tableName + "\"").build());
			
			classAnnotations.add(AnnotationSpec.builder(Data.class).build());
			classAnnotations.add(AnnotationSpec.builder(AllArgsConstructor.class).build());
			classAnnotations.add(AnnotationSpec.builder(NoArgsConstructor.class).build());
			classAnnotations.add(AnnotationSpec.builder(ToString.class).build());
			
			TypeSpec classTypeSpec = ClassBuilderFactory.build(tableName, fields, null, classAnnotations);
			
			FileBuilderFactory.build((KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_MODEL).trim(),
										classTypeSpec);
		}
		catch(Exception e) {
			log.error("Error : {}, {}", e.getMessage(), e.getCause());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getFieldType(String dataType)
	{
		Class<T> type = null;	
		switch(dataType.toUpperCase())
		{
			case KeyConstant.DATA_TYPE_VARCHAR:
			case KeyConstant.DATA_TYPE_VARCHAR_2:
				type = (Class<T>) String.class;
				break;
				
			case KeyConstant.DATA_TYPE_INTEGER:
			case KeyConstant.DATA_TYPE_NUMBER:
				type = (Class<T>) Integer.class;
				break;
				
			case KeyConstant.DATA_TYPE_DATE:
				type = (Class<T>) Date.class;
				break;
				
			case KeyConstant.DATA_TYPE_BOOLEAN:
				type = (Class<T>) Boolean.class;
				break;
		}
		return type;
	}
	
//--------------------------------------------------------------------------------GENERATE REPOSITORY CLASS------------------------------------

	@Override
	public void generateRepository(String tableName, Column column)
	{	
		List<AnnotationSpec> interfaceAnnotations = new ArrayList<>();	
		
		try
		{
			interfaceAnnotations.add(AnnotationSpec.builder(Repository.class).build());
			
			TypeSpec interfaceTypeSpec = InterfaceBuilderFactory.buildRepository(tableName, null, null, interfaceAnnotations, column);
			
			FileBuilderFactory.build((KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_REPOSITORY).trim(), 
										interfaceTypeSpec);
		}
		catch(Exception e) {
			log.error("Error : {}, {}", e.getMessage(), e.getCause());
		}
	}
	
//--------------------------------------------------------------------------------GENERATE SERVICE CLASS------------------------------------

	@Override
	public void generateService(String tableName, Column column)
	{	
		List<MethodSpec> methods = new ArrayList<>();		
		ClassName modelClass = ClassName.get((KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_MODEL).trim(), tableName);
		
		List<Modifier> modifiers = new ArrayList<>();
		try
		{
			modifiers.add(Modifier.PUBLIC);
			modifiers.add(Modifier.ABSTRACT);
			//--------------------------------------------------SAVE METHOD--------------------------------------------------------------------
			
			List<ParameterSpec> saveMethodParameters = new ArrayList<>();
			saveMethodParameters.add(ParameterSpec.builder(modelClass, tableName.toLowerCase()).build());	
			methods.add(MethodBuilderFactory.build(null, modifiers, modelClass, (KeyConstant.MSG_METHOD_SAVE+tableName).trim(), saveMethodParameters));
			
			//---------------------------------------------------GET METHOD----------------------------------------------------------------------		
			
			ClassName list = ClassName.get(KeyConstant.MSG_PKG_JAVA_UTIL, KeyConstant.MSG_JAVA_LIST);
			TypeName modelClasses = ParameterizedTypeName.get(list, modelClass);
			
			methods.add(MethodBuilderFactory.build(null, modifiers, modelClasses, (KeyConstant.MSG_METHOD_GET+tableName+"s").trim(), null)); //Class.forName(modelName)
			
			//----------------------------------------------------GET BY ID---------------------------------------------------------------------
			
			List<ParameterSpec> getMethodParameters = new ArrayList<>();
			
			getMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase()).build());	
			
			methods.add(MethodBuilderFactory.build(null, modifiers, modelClass, (KeyConstant.MSG_METHOD_GET+tableName).trim(), getMethodParameters));
			
			//----------------------------------------------------UPDATE BY ID-------------------------------------------------------------------
			
			List<ParameterSpec> updateMethodParameters = new ArrayList<>();
			
			updateMethodParameters.add(ParameterSpec.builder(modelClass, tableName.toLowerCase()).build());
			updateMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase()).build());
			
			methods.add(MethodBuilderFactory.build(null, modifiers, modelClass, (KeyConstant.MSG_METHOD_UPDATE+tableName).trim(), updateMethodParameters));
			
			//----------------------------------------------------DELETE BY ID-------------------------------------------------------------------
			
			List<ParameterSpec> deleteMethodParameters = new ArrayList<>();
			
			deleteMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase()).build());	
			
			methods.add(MethodBuilderFactory.build(null, modifiers, void.class, (KeyConstant.MSG_METHOD_DELETE+tableName).trim(), deleteMethodParameters));
			
			//----------------------------------------------------IS EXIST ID--------------------------------------------------------------------
			
			List<ParameterSpec> isExitMethodParameters = new ArrayList<>();
			
			isExitMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase()).build());
			
			methods.add(MethodBuilderFactory.build(null, modifiers, boolean.class, (KeyConstant.MSG_METHOD_IS_EXIST+tableName).trim(), isExitMethodParameters));
			
			//---------------------------------------------------SERVICE INTERFACE GENERATION---------------------------------------------------
			
			TypeSpec interfaceTypeSpec = InterfaceBuilderFactory.buildService((tableName+KeyConstant.MSG_NAME_SERVICE).trim(), null, methods, null, null);
			
			FileBuilderFactory.build((KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_SERVICE).trim(), interfaceTypeSpec);
		}
		catch(Exception e) {
			log.error("Error : {}, {}", e.getMessage(), e.getCause());
		}
	}
	
//--------------------------------------------------------------------------------GENERATE SERVICE IMPLEMENTATION--------------------------------
	
	@Override
	public void generateServiceImpl(String tableName, Column column)
	{		
		List<MethodSpec> methods = new ArrayList<>();
		
		ClassName modelClass = ClassName.get((KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_MODEL).trim(), tableName);
		
		ClassName serviceInterface = ClassName.get((KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() 
													+ KeyConstant.MSG_PKG_DOT_SERVICE).trim(), tableName+KeyConstant.MSG_NAME_SERVICE);
		
		ClassName repositoryClass = ClassName.get((KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() 
													+ KeyConstant.MSG_PKG_DOT_REPOSITORY).trim(), tableName+KeyConstant.MSG_NAME_REPOSITORY);	
		try
		{
			List<Modifier> modifiers = new ArrayList<>();
			modifiers.add(Modifier.PUBLIC);
			
			List<AnnotationSpec> methodAnnotationSpecs = new ArrayList<>();			
			methodAnnotationSpecs.add(AnnotationSpec.builder(Override.class).build());
			
			//--------------------------------------------------SAVE METHOD--------------------------------------------------------------------			
			
			List<ParameterSpec> saveMethodParameters = new ArrayList<>();
			saveMethodParameters.add(ParameterSpec.builder(modelClass, tableName.toLowerCase()).build());	
			
			MethodSpec saveMethodSpec = MethodBuilderFactory.build(methodAnnotationSpecs, modifiers, modelClass,
																(KeyConstant.MSG_METHOD_SAVE+tableName).trim(), saveMethodParameters);
					
			methods.add(saveMethodSpec.toBuilder().addStatement((KeyConstant.MSG_STATEMENT_RETURN 
															+ (tableName.toLowerCase())+KeyConstant.MSG_NAME_REPOSITORY
															+ KeyConstant.MSG_REPO_METHOD_DOT_SAVE
															+ tableName.toLowerCase()
															+ KeyConstant.MSG_CLOSE_PARENTHESIS).trim()
															).build());
			
			//---------------------------------------------------GET METHOD----------------------------------------------------------------------		
			
			ClassName list = ClassName.get(KeyConstant.MSG_PKG_JAVA_UTIL, KeyConstant.MSG_JAVA_LIST);
			TypeName modelClasses = ParameterizedTypeName.get(list, modelClass);
			
			MethodSpec getMethodSpec = MethodBuilderFactory.build(methodAnnotationSpecs, modifiers, modelClasses, 
																(KeyConstant.MSG_METHOD_GET+tableName+"s").trim(), null);			
			
			methods.add(getMethodSpec.toBuilder().addStatement((KeyConstant.MSG_STATEMENT_RETURN						
																+ (tableName.toLowerCase())+KeyConstant.MSG_NAME_REPOSITORY
																+ KeyConstant.MSG_REPO_METHOD_DOT_FINDALL).trim()
																).build());		
						
			//----------------------------------------------------GET BY ID---------------------------------------------------------------------
			
			List<ParameterSpec> getMethodParameters = new ArrayList<>();
			
			getMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase()).build());	
			
			MethodSpec getByIdMethodSpec = MethodBuilderFactory.build(methodAnnotationSpecs, modifiers, modelClass, 
																		(KeyConstant.MSG_METHOD_GET+tableName).trim(), getMethodParameters);
			
			methods.add(getByIdMethodSpec.toBuilder().addStatement((KeyConstant.MSG_STATEMENT_RETURN	
																	+ (tableName.toLowerCase())+KeyConstant.MSG_NAME_REPOSITORY
																	+ KeyConstant.MSG_REPO_METHOD_DOT_FIND_BY_ID
																	+ column.getName()
																	+ KeyConstant.MSG_CLOSE_PARENTHESIS
																	+ KeyConstant.MSG_REPO_METHOD_DOT_GET).trim()
																).build());	
			
			//----------------------------------------------------UPDATE BY ID-------------------------------[NEED IMPROVEMENT HERE]---------------------
			
			List<ParameterSpec> updateMethodParameters = new ArrayList<>();
			
			updateMethodParameters.add(ParameterSpec.builder(modelClass, tableName.toLowerCase()).build());
			updateMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase()).build());
			
			MethodSpec updateMethodSpec = MethodBuilderFactory.build(methodAnnotationSpecs, modifiers, modelClass, 
																	(KeyConstant.MSG_METHOD_UPDATE+tableName).trim(), updateMethodParameters);
			
			methods.add(updateMethodSpec.toBuilder().addStatement((KeyConstant.MSG_STATEMENT_RETURN 
																	+ (tableName.toLowerCase())+KeyConstant.MSG_NAME_REPOSITORY
																	+ KeyConstant.MSG_REPO_METHOD_DOT_SAVE
																	+ tableName.toLowerCase()
																	+ KeyConstant.MSG_CLOSE_PARENTHESIS).trim()
																	).build());
					
			//----------------------------------------------------DELETE BY ID---------------------------------------------------------------------------
			
			List<ParameterSpec> deleteMethodParameters = new ArrayList<>();
			
			deleteMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase()).build());	
			
			MethodSpec deleteMethodSpec = MethodBuilderFactory.build(methodAnnotationSpecs, modifiers, void.class, 
																	(KeyConstant.MSG_METHOD_DELETE+tableName).trim(), deleteMethodParameters);		
			
			methods.add(deleteMethodSpec.toBuilder().addStatement(((tableName.toLowerCase())+KeyConstant.MSG_NAME_REPOSITORY
																	+ KeyConstant.MSG_REPO_METHOD_DOT_DELETE_BY_ID
																	+ column.getName()
																	+ KeyConstant.MSG_CLOSE_PARENTHESIS).trim()
																).build());			
			
			//----------------------------------------------------IS EXIST ID--------------------------------------------------------------------
			
			List<ParameterSpec> isExitMethodParameters = new ArrayList<>();
			
			isExitMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase()).build());
			
			MethodSpec isExistMethodSpec = MethodBuilderFactory.build(methodAnnotationSpecs, modifiers, boolean.class, 
																	(KeyConstant.MSG_METHOD_IS_EXIST+tableName).trim(), isExitMethodParameters);
														
			methods.add(isExistMethodSpec.toBuilder().addStatement(((KeyConstant.MSG_STATEMENT_RETURN 
																		+tableName.toLowerCase())+KeyConstant.MSG_NAME_REPOSITORY
																		+ KeyConstant.MSG_REPO_METHOD_DOT_IS_EXIST_BY_ID
																		+ column.getName()
																		+ KeyConstant.MSG_CLOSE_PARENTHESIS).trim()
																	).build());	
			
			//---------------------------------------------------SERVICE CLASS IMPLEMENTATION-------------------------------------------------
			
			List<FieldSpec> fields = new ArrayList<>();
			List<AnnotationSpec> fieldAnnotations = new ArrayList<>();
			
			fieldAnnotations.add(AnnotationSpec.builder(Autowired.class).build());
			fields.add(FieldBuilderFactory.build(((tableName.toLowerCase())+KeyConstant.MSG_NAME_REPOSITORY).trim(), 
													repositoryClass, null, fieldAnnotations));			
			
			List<AnnotationSpec> classAnnotationSpecs = new ArrayList<>();
			classAnnotationSpecs.add(AnnotationSpec.builder(Service.class).build());			
			
			TypeSpec interfaceTypeSpec = ClassBuilderFactory.build((tableName+KeyConstant.MSG_NAME_SERVICE_IMPL).trim(), fields, methods, classAnnotationSpecs, serviceInterface);
												
			FileBuilderFactory.build((KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_SERVICE).trim(), interfaceTypeSpec);
		}
		catch(Exception e) {
			log.error("Error : {}, {}", e.getMessage(), e.getCause());
		}	
	}
	
//--------------------------------------------------------------------------------GENERATE CONTROLLER CLASS-----------------------------------------

	@Override
	public void generateController(String tableName, Column column) 
	{
		List<MethodSpec> methods = new ArrayList<>();
		
		ClassName modelClass = ClassName.get((KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_MODEL).trim(), tableName);
		ClassName serviceInterface = ClassName.get((KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() 
													+ KeyConstant.MSG_PKG_DOT_SERVICE).trim(), tableName + KeyConstant.MSG_NAME_SERVICE);					
		List<Modifier> modifiers = new ArrayList<>();
		try
		{
			modifiers.add(Modifier.PUBLIC);	
			
			//--------------------------------------------------SAVE METHOD--------------------------------------------------------------------
			
			List<AnnotationSpec> saveMethodAnnotationSpecs = new ArrayList<>();			
			saveMethodAnnotationSpecs.add(AnnotationSpec.builder(PostMapping.class)
					.addMember(KeyConstant.MSG_STATEMENT_VALUE, "\"/" + KeyConstant.MSG_PATH_SAVE + "\"").build());
			
			
			List<ParameterSpec> saveMethodParameters = new ArrayList<>();
			saveMethodParameters.add(ParameterSpec.builder(modelClass, tableName.toLowerCase())
													.addAnnotation(AnnotationSpec.builder(RequestBody.class).build()).build());				
			
			MethodSpec saveMethodSpec = MethodBuilderFactory.build(saveMethodAnnotationSpecs, modifiers, modelClass,
																(KeyConstant.MSG_METHOD_SAVE + tableName).trim(), saveMethodParameters);
					
			methods.add(saveMethodSpec.toBuilder().addStatement((KeyConstant.MSG_STATEMENT_RETURN 
															+ (tableName.toLowerCase())+KeyConstant.MSG_NAME_SERVICE
															+ "."+KeyConstant.MSG_METHOD_SAVE + tableName
															+ KeyConstant.MSG_OPEN_PARENTHESIS
															+ tableName.toLowerCase()
															+ KeyConstant.MSG_CLOSE_PARENTHESIS).trim()
															).build());
			
			//---------------------------------------------------GET METHOD----------------------------------------------------------------------		
			
			List<AnnotationSpec> getAllMethodAnnotationSpecs = new ArrayList<>();	
			getAllMethodAnnotationSpecs.add(AnnotationSpec.builder(GetMapping.class)
					.addMember(KeyConstant.MSG_STATEMENT_VALUE, "\"/" + KeyConstant.MSG_PATH_GET_ALL + "\"").build());
			
			
			ClassName list = ClassName.get(KeyConstant.MSG_PKG_JAVA_UTIL, KeyConstant.MSG_JAVA_LIST);
			TypeName modelClasses = ParameterizedTypeName.get(list, modelClass);
			
			MethodSpec getMethodSpec = MethodBuilderFactory.build(getAllMethodAnnotationSpecs, modifiers, modelClasses, 
																(KeyConstant.MSG_METHOD_GET+tableName+"s").trim(), null);			
			
			methods.add(getMethodSpec.toBuilder().addStatement((KeyConstant.MSG_STATEMENT_RETURN						
																+ (tableName.toLowerCase())+KeyConstant.MSG_NAME_SERVICE
																+ ("."+KeyConstant.MSG_METHOD_GET + tableName+"s"
																+KeyConstant.MSG_OPEN_PARENTHESIS)
																+ KeyConstant.MSG_CLOSE_PARENTHESIS).trim()
																).build());	
			
			//----------------------------------------------------GET BY ID---------------------------------------------------------------------
			
			List<AnnotationSpec> getMethodAnnotationSpecs = new ArrayList<>();	
			getMethodAnnotationSpecs.add(AnnotationSpec.builder(GetMapping.class)
					.addMember(KeyConstant.MSG_STATEMENT_VALUE, "\"/" + KeyConstant.MSG_PATH_GET_BY_ID + "\"").build());
			
			List<ParameterSpec> getMethodParameters = new ArrayList<>();			
			getMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase())
										.addAnnotation(AnnotationSpec.builder(PathVariable.class)
											.addMember(KeyConstant.MSG_STATEMENT_VALUE, "\""+column.getName()+"\"").build()).build());	
						
			MethodSpec getByIdMethodSpec = MethodBuilderFactory.build(getMethodAnnotationSpecs, modifiers, modelClass, 
																		(KeyConstant.MSG_METHOD_GET + tableName).trim(), getMethodParameters);
			
			methods.add(getByIdMethodSpec.toBuilder().addStatement((KeyConstant.MSG_STATEMENT_RETURN	
																	+ (tableName.toLowerCase())+KeyConstant.MSG_NAME_SERVICE
																	+ "."+KeyConstant.MSG_METHOD_GET + tableName
																	+ KeyConstant.MSG_OPEN_PARENTHESIS
																	+ column.getName()
																	+ KeyConstant.MSG_CLOSE_PARENTHESIS).trim()
																).build());	
			
			//----------------------------------------------------UPDATE BY ID------------------------------------------------------------------------
			
			List<AnnotationSpec> updateMethodAnnotationSpecs = new ArrayList<>();	
			updateMethodAnnotationSpecs.add(AnnotationSpec.builder(PutMapping.class)
					.addMember(KeyConstant.MSG_STATEMENT_VALUE, "\"/" + KeyConstant.MSG_PATH_GET_BY_ID + "\"").build());
			
			List<ParameterSpec> updateMethodParameters = new ArrayList<>();
			
			updateMethodParameters.add(ParameterSpec.builder(modelClass, tableName.toLowerCase())
											.addAnnotation(AnnotationSpec.builder(RequestBody.class).build()).build());
			
			updateMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase())
														.addAnnotation(AnnotationSpec.builder(PathVariable.class)
																.addMember(KeyConstant.MSG_STATEMENT_VALUE, "\""+column.getName()+"\"").build()).build());
			
			MethodSpec updateMethodSpec = MethodBuilderFactory.build(updateMethodAnnotationSpecs, modifiers, modelClass, 
																	(KeyConstant.MSG_METHOD_UPDATE + tableName).trim(), updateMethodParameters);
			
			methods.add(updateMethodSpec.toBuilder().addStatement((KeyConstant.MSG_STATEMENT_RETURN 
																	+ (tableName.toLowerCase())+KeyConstant.MSG_NAME_SERVICE
																	+ "."+KeyConstant.MSG_METHOD_UPDATE	+ tableName
																	+ KeyConstant.MSG_OPEN_PARENTHESIS
																	+ tableName.toLowerCase() + ", " + column.getName()
																	+ KeyConstant.MSG_CLOSE_PARENTHESIS).trim()
																	).build());
			
			//----------------------------------------------------DELETE BY ID---------------------------------------------------------------------------
			
			List<AnnotationSpec> deleteMethodAnnotationSpecs = new ArrayList<>();	
			deleteMethodAnnotationSpecs.add(AnnotationSpec.builder(DeleteMapping.class)
					.addMember(KeyConstant.MSG_STATEMENT_VALUE, "\"/" + KeyConstant.MSG_PATH_GET_BY_ID + "\"").build());
			
			List<ParameterSpec> deleteMethodParameters = new ArrayList<>();
			
			deleteMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase())
														.addAnnotation(AnnotationSpec.builder(PathVariable.class)
																.addMember(KeyConstant.MSG_STATEMENT_VALUE, "\""+column.getName()+"\"").build()).build());			
			
			MethodSpec deleteMethodSpec = MethodBuilderFactory.build(deleteMethodAnnotationSpecs, modifiers, void.class, 
																	(KeyConstant.MSG_METHOD_DELETE + tableName).trim(), deleteMethodParameters);		
			
			methods.add(deleteMethodSpec.toBuilder().addStatement(((tableName.toLowerCase())+KeyConstant.MSG_NAME_SERVICE
																	+ "."+KeyConstant.MSG_METHOD_DELETE + tableName
																	+ KeyConstant.MSG_OPEN_PARENTHESIS
																	+ column.getName()
																	+ KeyConstant.MSG_CLOSE_PARENTHESIS).trim()
																).build());			
			
		//-----------------------------------------------------------------------CONTROLLER CLASS CREATION-----------------------------------------
			
			List<AnnotationSpec> classAnnotations = new ArrayList<>();
			classAnnotations.add(AnnotationSpec.builder(RestController.class).build());
			classAnnotations.add(AnnotationSpec.builder(RequestMapping.class).addMember(KeyConstant.MSG_STATEMENT_VALUE, "\"/" + tableName.toLowerCase() + "s\"").build());
		
			List<AnnotationSpec> fieldAnnotations = new ArrayList<>();
			fieldAnnotations.add(AnnotationSpec.builder(Autowired.class).build());
			
			List<FieldSpec> fields = new ArrayList<>();
			fields.add(FieldBuilderFactory.build(((tableName.toLowerCase())+KeyConstant.MSG_NAME_SERVICE).trim(), 
													serviceInterface, null, fieldAnnotations));				
			
			TypeSpec classTypeSpec = ClassBuilderFactory.build((tableName+KeyConstant.MSG_NAME_CONTROLLER).trim(), fields, methods, classAnnotations);
			
			FileBuilderFactory.build((KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_CONTROLLER).trim(), classTypeSpec);
		}
		catch(Exception e) {
			log.error("Error : {}, {}", e.getMessage(), e.getCause());
		}		
	}

}
