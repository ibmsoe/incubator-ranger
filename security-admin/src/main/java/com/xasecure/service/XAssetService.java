/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

 package com.xasecure.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xasecure.common.JSONUtil;
import com.xasecure.common.MessageEnums;
import com.xasecure.common.PasswordUtils;
import com.xasecure.common.PropertiesUtil;
import com.xasecure.common.SearchField;
import com.xasecure.common.SearchField.DATA_TYPE;
import com.xasecure.common.SearchField.SEARCH_TYPE;
import com.xasecure.common.StringUtil;
import com.xasecure.entity.*;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.xasecure.common.AppConstants;
import com.xasecure.common.view.VTrxLogAttr;
import com.xasecure.db.XADaoManager;
import com.xasecure.util.XAEnumUtil;
import com.xasecure.view.*;

@Service
@Scope("singleton")
public class XAssetService extends XAssetServiceBase<XXAsset, VXAsset> {

	@Autowired
	JSONUtil jsonUtil;

	@Autowired
	XADaoManager appDaoMgr;
	
	@Autowired
	StringUtil stringUtil;
	
	static HashMap<String, VTrxLogAttr> trxLogAttrs = new HashMap<String, VTrxLogAttr>();
	static {
		trxLogAttrs.put("name", new VTrxLogAttr("name", "Repository Name", false));
		trxLogAttrs.put("description", new VTrxLogAttr("description", "Repository Description", false));
		trxLogAttrs.put("activeStatus", new VTrxLogAttr("activeStatus", "Repository Status", true));
		trxLogAttrs.put("config", new VTrxLogAttr("config", "Connection Configurations", false));
	}

	private String hiddenPasswordString;
	
	@Autowired
	XAEnumUtil xaEnumUtil;
	
	public XAssetService(){
		super();
		hiddenPasswordString = PropertiesUtil.getProperty("xa.password.hidden", "*****");
		searchFields.add(new SearchField("status", "obj.activeStatus",
				SearchField.DATA_TYPE.INT_LIST, SearchField.SEARCH_TYPE.FULL));
		searchFields.add(new SearchField("name", "obj.name", DATA_TYPE.STRING,
				SEARCH_TYPE.PARTIAL));
		searchFields.add(new SearchField("type", "obj.assetType",
				DATA_TYPE.INTEGER, SEARCH_TYPE.FULL));
	}

	@Override
	protected void validateForCreate(VXAsset vObj) {
		XXAsset xxAsset = appDaoMgr.getXXAsset()
				.findByAssetName(vObj.getName());
		if (xxAsset != null) {
			String errorMessage = "Repository Name already exists";
			throw restErrorUtil.createRESTException(errorMessage,
					MessageEnums.INVALID_INPUT_DATA, null, null,
					vObj.toString());
		}
		if(vObj.getName()==null || vObj.getName().trim().length()==0){
			String errorMessage = "Repository Name can't be empty";
			throw restErrorUtil.createRESTException(errorMessage,
					MessageEnums.INVALID_INPUT_DATA, null, null,
					vObj.toString());
		}
		
		validateConfig(vObj);
	}

	@Override
	protected void validateForUpdate(VXAsset vObj, XXAsset mObj) {
		if (!vObj.getName().equalsIgnoreCase(mObj.getName())) {
			validateForCreate(vObj);
		}else{		
		validateConfig(vObj);
		}
	}

	@Override
	protected XXAsset mapViewToEntityBean(VXAsset vObj, XXAsset mObj,
			int OPERATION_CONTEXT) {
		String oldConfig = (mObj != null) ? mObj.getConfig() : null;
		super.mapViewToEntityBean(vObj, mObj, OPERATION_CONTEXT);
		String config = vObj.getConfig();
		if (config != null && !config.isEmpty()) {
			Map<String, String> configMap = jsonUtil.jsonToMap(config);
			Entry<String, String> passwordEntry = getPasswordEntry(configMap);
			if (passwordEntry != null) {
				// If "*****" then get password from db and update
				String password = passwordEntry.getValue();
				if (password != null) {
					if (password.equals(hiddenPasswordString)) {
						if (oldConfig != null && !oldConfig.isEmpty()) {
							Map<String, String> oldConfigMap = jsonUtil
									.jsonToMap(oldConfig);
							Entry<String, String> oldPasswordEntry 
									= getPasswordEntry(oldConfigMap);
							if (oldPasswordEntry != null) {
								configMap.put(oldPasswordEntry.getKey(),
										oldPasswordEntry.getValue());
							}
						}
					}
					config = jsonUtil.readMapToString(configMap);
				}
			}
		}
		mObj.setConfig(config);
		return mObj;
	}

	@Override
	protected VXAsset mapEntityToViewBean(VXAsset vObj, XXAsset mObj) {
		vObj = super.mapEntityToViewBean(vObj, mObj);
		String config = vObj.getConfig();
		if (config != null && !config.isEmpty()) {
			Map<String, String> configMap = jsonUtil.jsonToMap(config);
			Entry<String, String> passwordEntry = getPasswordEntry(configMap);
			if (passwordEntry != null) {
				configMap.put(passwordEntry.getKey(), hiddenPasswordString);
			}
			config = jsonUtil.readMapToString(configMap);
		}
		vObj.setConfig(config);
		return vObj;
	}
	
	private Entry<String, String> getPasswordEntry(Map<String, String> configMap) {
		Entry<String, String> entry = null;
		
		for(Entry<String, String> e : configMap.entrySet()) {
			if(e.getKey().toLowerCase().contains("password")){
				entry = e;
				break;
			}
		}
		
		return entry;
	}
	
	private Entry<String, String> getIsEncryptedEntry(Map<String, String> configMap){
		Entry<String, String> entry = null;		
		for(Entry<String, String> e : configMap.entrySet()) {
			if(e.getKey().toLowerCase().contains("isencrypted")){
				entry = e;
				break;
			}
		}
		return entry;
	}
	
	public void validateConfig(VXAsset vObj) {
		HashMap<String, Object> configrationMap = null;
		if (vObj.getAssetType() == AppConstants.ASSET_HDFS) {
			ObjectMapper objectMapper = new ObjectMapper();
			TypeReference<HashMap<String, Object>> typeRef = new TypeReference
					<HashMap<String, Object>>() {};
			try {
				configrationMap = objectMapper.readValue(vObj.getConfig(),
						typeRef);
			} catch (Exception e) {
				logger.error("Error in config json", e);
			}

			if (configrationMap != null) {
				String fs_default_name = configrationMap.get("fs.default.name")
						.toString();

				if (fs_default_name.isEmpty()) {
					throw restErrorUtil.createRESTException(
							"serverMsg.fsDefaultNameEmptyError",
							MessageEnums.INVALID_INPUT_DATA, null, "fs.default.name",
							vObj.toString());
				}
				/*String expression="^+(hdfs://)\\s*(.*?):[0-9]{1,5}";
//				String expression = "^+(hdfs://)[a-z,A-Z,0-9,.]*+:[0-9]{1,5}";
				Pattern pattern = Pattern.compile(expression,
						Pattern.CASE_INSENSITIVE);
				// String inputStr = "hdfs://192.168.1.16:2";
				Matcher matcher = pattern.matcher(fs_default_name);
				if (!matcher.matches()) {					
					throw restErrorUtil.createRESTException(
							"serverMsg.fsDefaultNameValidationError",
							MessageEnums.INVALID_INPUT_DATA, null, "fs.default.name",
							vObj.toString());
				}*/
			}
		}
	}

	public List<XXTrxLog> getTransactionLog(VXAsset vResource, String action){
		return getTransactionLog(vResource, null, action);
	}

	public List<XXTrxLog> getTransactionLog(VXAsset vObj, XXAsset mObj, String action){
		if(vObj == null && (action == null || !action.equalsIgnoreCase("update"))){
			return null;
		}
		
		List<XXTrxLog> trxLogList = new ArrayList<XXTrxLog>();
		Field[] fields = vObj.getClass().getDeclaredFields();
		
		try {
			Field nameField = vObj.getClass().getDeclaredField("name");
			nameField.setAccessible(true);
			String objectName = ""+nameField.get(vObj);
	
			for(Field field : fields){
				field.setAccessible(true);
				String fieldName = field.getName();
				if(!trxLogAttrs.containsKey(fieldName)){
					continue;
				}
				
				VTrxLogAttr vTrxLogAttr = trxLogAttrs.get(fieldName);
				
				XXTrxLog xTrxLog = new XXTrxLog();
				xTrxLog.setAttributeName(vTrxLogAttr.getAttribUserFriendlyName());
			
				String value = null;
				boolean isEnum = vTrxLogAttr.isEnum();
				if(isEnum){
					String enumName = XXAsset.getEnumName(fieldName);
					int enumValue = field.get(vObj) == null ? 0 : Integer.parseInt(""+field.get(vObj));
					value = xaEnumUtil.getLabel(enumName, enumValue);
				} else {
					value = ""+field.get(vObj);
				}
				
				if(action.equalsIgnoreCase("create")){
					if(stringUtil.isEmpty(value)){
						continue;
					}
					xTrxLog.setNewValue(value);
				} else if(action.equalsIgnoreCase("delete")){
					xTrxLog.setPreviousValue(value);
				} else if(action.equalsIgnoreCase("update")){
					String oldValue = null;
					Field[] mFields = mObj.getClass().getDeclaredFields();
					for(Field mField : mFields){
						mField.setAccessible(true);
						String mFieldName = mField.getName();
						if(fieldName.equalsIgnoreCase(mFieldName)){
							if(isEnum){
								String enumName = XXAsset.getEnumName(mFieldName);
								int enumValue = mField.get(mObj) == null ? 0 : Integer.parseInt(""+mField.get(mObj));
								oldValue = xaEnumUtil.getLabel(enumName, enumValue);
							} else {
								oldValue = mField.get(mObj)+"";
							}
							break;
						}
					}
					if(fieldName.equalsIgnoreCase("config")){
						Map<String, String> vConfig = jsonUtil.jsonToMap(value);
						Map<String, String> xConfig = jsonUtil.jsonToMap(oldValue);
						
						Map<String, String> newConfig = new HashMap<String, String>();
						Map<String, String> oldConfig = new HashMap<String, String>();
						
						for (Entry<String, String> entry: vConfig.entrySet()) {
							String key = entry.getKey();
						    if (!xConfig.containsKey(key)) {
						    	newConfig.put(key, entry.getValue());
						    } else if(!entry.getValue().equalsIgnoreCase(xConfig.get(key))){
						    	if(key.equalsIgnoreCase("password") && entry
						    			.getValue().equalsIgnoreCase(hiddenPasswordString)){
						    		continue;
						    	}
						    	newConfig.put(key, entry.getValue());
						    	oldConfig.put(key, xConfig.get(key));
						    }
						}
						
						oldValue = jsonUtil.readMapToString(oldConfig);
						value = jsonUtil.readMapToString(newConfig);
					}
					if(value.equalsIgnoreCase(oldValue)){
						continue;
					}
					xTrxLog.setPreviousValue(oldValue);
					xTrxLog.setNewValue(value);
				}
				
				xTrxLog.setAction(action);
				xTrxLog.setObjectClassType(AppConstants.CLASS_TYPE_XA_ASSET);
				xTrxLog.setObjectId(vObj.getId());
				xTrxLog.setObjectName(objectName);
				trxLogList.add(xTrxLog);
				
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		return trxLogList;
	}
	
	public String getConfigWithEncryptedPassword(String config,boolean isForced){
		try {
			if (config != null && !config.isEmpty()) {
				Map<String, String> configMap = jsonUtil.jsonToMap(config);
				Entry<String, String> passwordEntry = getPasswordEntry(configMap);
				Entry<String, String> isEncryptedEntry = getIsEncryptedEntry(configMap);
				if (passwordEntry != null){
					if(isEncryptedEntry==null || !isEncryptedEntry.getValue().equalsIgnoreCase("true")||isForced==true){
						String password=passwordEntry.getValue();
						String encryptPassword=PasswordUtils.encryptPassword(password);
						String decryptPassword=PasswordUtils.decryptPassword(encryptPassword);
						if(decryptPassword.equalsIgnoreCase(password)){
							configMap.put(passwordEntry.getKey(),
									encryptPassword);
							configMap.put("isencrypted", "true");
						}
					}
				}
				config = jsonUtil.readMapToString(configMap);
			}										
		} catch (IOException e) {
			String errorMessage = "Password encryption error";
			throw restErrorUtil.createRESTException(errorMessage,
					MessageEnums.INVALID_INPUT_DATA, null, null,
					e.getMessage());	
		}
		return config;
	}
	public String getConfigWithDecryptedPassword(String config){
		try {
			if (config != null && !config.isEmpty()) {
				Map<String, String> configMap = jsonUtil.jsonToMap(config);
				Entry<String, String> passwordEntry = getPasswordEntry(configMap);
				Entry<String, String> isEncryptedEntry = getIsEncryptedEntry(configMap);
				if (isEncryptedEntry!=null && passwordEntry != null){					
					if (!stringUtil.isEmpty(isEncryptedEntry.getValue())
							&& isEncryptedEntry.getValue().equalsIgnoreCase(
									"true")) {
						String encryptPassword = passwordEntry.getValue();
						String decryptPassword = PasswordUtils
								.decryptPassword(encryptPassword);
						configMap.put(passwordEntry.getKey(), decryptPassword);
					}
				}
				config = jsonUtil.readMapToString(configMap);
			}										
		} catch (IOException e) {
			String errorMessage = "Password decryption error";
			throw restErrorUtil.createRESTException(errorMessage,
					MessageEnums.INVALID_INPUT_DATA, null, null,
					e.getMessage());	
		}
		return config;
	}
}
