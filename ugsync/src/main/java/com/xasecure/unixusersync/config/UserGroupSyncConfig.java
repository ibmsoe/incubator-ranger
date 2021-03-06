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

 package com.xasecure.unixusersync.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import com.hortonworks.credentialapi.CredentialReader;
import com.xasecure.usergroupsync.UserGroupSink;
import com.xasecure.usergroupsync.UserGroupSource;

public class UserGroupSyncConfig  {

	public static final String CONFIG_FILE = "unixauthservice.properties" ;
	
	public static final String  UGSYNC_ENABLED_PROP = "usergroupSync.enabled" ;
	
	public static final String  UGSYNC_PM_URL_PROP = 	"usergroupSync.policymanager.baseURL" ;
	
	public static final String  UGSYNC_MIN_USERID_PROP  = 	"usergroupSync.unix.minUserId" ;
	
	public static final String  UGSYNC_MAX_RECORDS_PER_API_CALL_PROP  = 	"usergroupSync.policymanager.MaxRecordsPerAPICall" ;

	public static final String  UGSYNC_MOCK_RUN_PROP  = 	"usergroupSync.policymanager.mockRun" ;
	
	private static final String SSL_KEYSTORE_PATH_PARAM = "keyStore" ;

	private static final String SSL_KEYSTORE_PATH_PASSWORD_PARAM = "keyStorePassword" ;
	
	private static final String SSL_TRUSTSTORE_PATH_PARAM = "trustStore" ;
	
	private static final String SSL_TRUSTSTORE_PATH_PASSWORD_PARAM = "trustStorePassword" ;
	
	private static final String UGSYNC_SLEEP_TIME_IN_MILLIS_BETWEEN_CYCLE_PARAM = "usergroupSync.sleepTimeInMillisBetweenSyncCycle" ;
	
	private static final long UGSYNC_SLEEP_TIME_IN_MILLIS_BETWEEN_CYCLE_UNIX_DEFAULT_VALUE = 300000L ;
	
	private static final long UGSYNC_SLEEP_TIME_IN_MILLIS_BETWEEN_CYCLE_LDAP_DEFAULT_VALUE = 21600000L ;

	private static final String UGSYNC_SOURCE_CLASS_PARAM = "usergroupSync.source.impl.class";

	private static final String UGSYNC_SINK_CLASS_PARAM = "usergroupSync.sink.impl.class";

	private static final String UGSYNC_SOURCE_CLASS = "com.xasecure.unixusersync.process.UnixUserGroupBuilder";

	private static final String UGSYNC_SINK_CLASS = "com.xasecure.unixusersync.process.PolicyMgrUserGroupBuilder";

	private static final String LGSYNC_SOURCE_CLASS = "com.xasecure.ldapusersync.process.LdapUserGroupBuilder";
	
	private static final String LGSYNC_LDAP_URL = "ldapGroupSync.ldapUrl";
	
	private static final String LGSYNC_LDAP_BIND_DN = "ldapGroupSync.ldapBindDn";
	
	private static final String LGSYNC_LDAP_BIND_KEYSTORE = "ldapGroupSync.ldapBindKeystore";
	
	private static final String LGSYNC_LDAP_BIND_ALIAS = "ldapGroupSync.ldapBindAlias";
	
	private static final String LGSYNC_LDAP_BIND_PASSWORD = "ldapGroupSync.ldapBindPassword";	
	
	private static final String LGSYNC_LDAP_AUTHENTICATION_MECHANISM = "ldapGroupSync.ldapAuthenticationMechanism";
	
	private static final String LGSYNC_USER_SEARCH_BASE = "ldapGroupSync.userSearchBase";
	
	private static final String LGSYNC_USER_OBJECT_CLASS = "ldapGroupSync.userObjectClass";
	
	private static final String LGSYNC_USER_SEARCH_FILTER = "ldapGroupSync.userSearchFilter";
	
	private static final String LGSYNC_USER_NAME_ATTRIBUTE = "ldapGroupSync.userNameAttribute";
	
	private static final String LGSYNC_USER_GROUP_NAME_ATTRIBUTE = "ldapGroupSync.userGroupNameAttribute";
	
	private static final String DEFAULT_AUTHENTICATION_MECHANISM = "simple";
	
	private static final String DEFAULT_USER_OBJECT_CLASS = "person";
	
	private static final String DEFAULT_USER_NAME_ATTRIBUTE = "cn";
	
	public static final String UGSYNC_NONE_CASE_CONVERSION_VALUE = "none" ;
	public static final String UGSYNC_LOWER_CASE_CONVERSION_VALUE = "lower" ;
	public static final String UGSYNC_UPPER_CASE_CONVERSION_VALUE = "upper" ;
	 
	private static final String UGSYNC_USERNAME_CASE_CONVERSION_PARAM = "ldapGroupSync.username.caseConversion" ;
	private static final String UGSYNC_GROUPNAME_CASE_CONVERSION_PARAM = "ldapGroupSync.groupname.caseConversion" ;
	 
	private static final String DEFAULT_UGSYNC_USERNAME_CASE_CONVERSION_VALUE = UGSYNC_LOWER_CASE_CONVERSION_VALUE  ;
	private static final String DEFAULT_UGSYNC_GROUPNAME_CASE_CONVERSION_VALUE = UGSYNC_LOWER_CASE_CONVERSION_VALUE ;
	
	private static final String DEFAULT_USER_GROUP_NAME_ATTRIBUTE = "memberof,ismemberof";

	private Properties prop = new Properties() ;
	
	private static UserGroupSyncConfig me = null ;
	
	public static UserGroupSyncConfig getInstance() {
		if (me == null) {
			synchronized(UserGroupSyncConfig.class) {
				UserGroupSyncConfig temp = me ;
				if (temp == null) {
					me = new UserGroupSyncConfig() ;
				}
			}
		}
		return me ;
	}
	
	
	private UserGroupSyncConfig() {
		init() ;
	}
	
	
	private void init() {
		try {
			InputStream in = getFileInputStream(CONFIG_FILE) ;
			if (in != null) {
				prop.load(in) ;
			}
		} catch (Throwable e) {
			throw new RuntimeException("Unable to load configuration file [" + CONFIG_FILE + "]", e) ;
		}
	}
	
	
	private InputStream getFileInputStream(String path) throws FileNotFoundException {

		InputStream ret = null;

		File f = new File(path);

		if (f.exists()) {
			ret = new FileInputStream(f);
		} else {
			ret = getClass().getResourceAsStream(path);
			
			if (ret == null) {
				if (! path.startsWith("/")) {
					ret = getClass().getResourceAsStream("/" + path);
				}
			}
			
			if (ret == null) {
				ret = ClassLoader.getSystemClassLoader().getResourceAsStream(path) ;
				if (ret == null) {
					if (! path.startsWith("/")) {
						ret = ClassLoader.getSystemResourceAsStream("/" + path);
					}
				}
			}
		}

		return ret;
	}
	
	
	public boolean isUserSyncEnabled() {
		String val = prop.getProperty(UGSYNC_ENABLED_PROP) ;
		return (val != null && val.trim().equalsIgnoreCase("true")) ;
	}

	
	public boolean isMockRunEnabled() {
		String val = prop.getProperty(UGSYNC_MOCK_RUN_PROP) ;
		return (val != null && val.trim().equalsIgnoreCase("true")) ;
	}
	
	
	public String getPolicyManagerBaseURL() {
		return prop.getProperty(UGSYNC_PM_URL_PROP) ;
	}
	
	
	public String getMinUserId() {
		return prop.getProperty(UGSYNC_MIN_USERID_PROP) ;
	}
	
	public String getMaxRecordsPerAPICall() {
		return prop.getProperty(UGSYNC_MAX_RECORDS_PER_API_CALL_PROP) ;
	}
	
	
	public String getSSLKeyStorePath() {
		return  prop.getProperty(SSL_KEYSTORE_PATH_PARAM) ;
	}

	
	public String getSSLKeyStorePathPassword() {
		return  prop.getProperty(SSL_KEYSTORE_PATH_PASSWORD_PARAM) ;
	}
	
	public String getSSLTrustStorePath() {
		return  prop.getProperty(SSL_TRUSTSTORE_PATH_PARAM) ;
	}
	
	
	public String getSSLTrustStorePathPassword() {
		return  prop.getProperty(SSL_TRUSTSTORE_PATH_PASSWORD_PARAM) ;
	}
	
	
	public long getSleepTimeInMillisBetweenCycle() throws Throwable {
		String val =  prop.getProperty(UGSYNC_SLEEP_TIME_IN_MILLIS_BETWEEN_CYCLE_PARAM) ;
		if (val == null) {
			if (LGSYNC_SOURCE_CLASS.equals(getUserGroupSource())) {
				return UGSYNC_SLEEP_TIME_IN_MILLIS_BETWEEN_CYCLE_LDAP_DEFAULT_VALUE ;
			} else {
				return UGSYNC_SLEEP_TIME_IN_MILLIS_BETWEEN_CYCLE_UNIX_DEFAULT_VALUE ;
			}
		}
		else {
			long ret = Long.parseLong(val) ;
			return ret;
		}
		
	}
	
	
	public UserGroupSource getUserGroupSource() throws Throwable {
		String val =  prop.getProperty(UGSYNC_SOURCE_CLASS_PARAM) ;

		if(val == null) {
			val = UGSYNC_SOURCE_CLASS;
		}

		Class<UserGroupSource> ugSourceClass = (Class<UserGroupSource>)Class.forName(val);

		UserGroupSource ret = ugSourceClass.newInstance();

		return ret;
	}

	
	public UserGroupSink getUserGroupSink() throws Throwable {
		String val =  prop.getProperty(UGSYNC_SINK_CLASS_PARAM) ;

		if(val == null) {
			val = UGSYNC_SINK_CLASS;
		}

		Class<UserGroupSink> ugSinkClass = (Class<UserGroupSink>)Class.forName(val);

		UserGroupSink ret = ugSinkClass.newInstance();

		return ret;
	}

	
	public String getLdapUrl() throws Throwable {
		String val =  prop.getProperty(LGSYNC_LDAP_URL);
		if(val == null || val.trim().isEmpty()) {
			throw new Exception(LGSYNC_LDAP_URL + " for LdapGroupSync is not specified");
		}
		return val;
	}

	
	public String getLdapBindDn() throws Throwable {
		String val =  prop.getProperty(LGSYNC_LDAP_BIND_DN);
		if(val == null || val.trim().isEmpty()) {
			throw new Exception(LGSYNC_LDAP_BIND_DN + " for LdapGroupSync is not specified");
		}
		return val;
	}
	
	
	public String getLdapBindPassword() {
		//update credential from keystore
		if(prop!=null && prop.containsKey(LGSYNC_LDAP_BIND_KEYSTORE) &&  prop.containsKey(LGSYNC_LDAP_BIND_ALIAS)){	
			String path=prop.getProperty(LGSYNC_LDAP_BIND_KEYSTORE);
			String alias=prop.getProperty(LGSYNC_LDAP_BIND_ALIAS);
			if(path!=null && alias!=null){
				if(!path.trim().isEmpty() && !alias.trim().isEmpty()){
					String password=CredentialReader.getDecryptedString(path.trim(),alias.trim());
					if(password!=null&& !password.trim().isEmpty() && !password.trim().equalsIgnoreCase("none")){
						prop.setProperty(LGSYNC_LDAP_BIND_PASSWORD,password);
						//System.out.println("Password IS :"+password);
					}
				}
			}		
		}
		return prop.getProperty(LGSYNC_LDAP_BIND_PASSWORD);
	}
	
	
	public String getLdapAuthenticationMechanism() {
		String val =  prop.getProperty(LGSYNC_LDAP_AUTHENTICATION_MECHANISM);
		if(val == null || val.trim().isEmpty()) {
			return DEFAULT_AUTHENTICATION_MECHANISM;
		}
		return val;
	}
	
	
	public String getUserSearchBase()  throws Throwable {
		String val =  prop.getProperty(LGSYNC_USER_SEARCH_BASE);
		if(val == null || val.trim().isEmpty()) {
			throw new Exception(LGSYNC_USER_SEARCH_BASE + " for LdapGroupSync is not specified");
		}
		return val;
	}
	
	
	public int getUserSearchScope() {
		String val =  prop.getProperty(LGSYNC_USER_SEARCH_BASE);
		if (val == null || val.trim().isEmpty()) {
			return 2; //subtree scope
		}
		
		val = val.trim().toLowerCase();
		if (val.equals(0) || val.startsWith("base")) {
			return 0; // object scope
		} else if (val.equals(1) || val.startsWith("one")) {
			return 1; // one level scope
		} else {
			return 2; // subtree scope
		}
	}
	
	
	public String getUserObjectClass() {
		String val =  prop.getProperty(LGSYNC_USER_OBJECT_CLASS);
		if (val == null || val.trim().isEmpty()) {
			return DEFAULT_USER_OBJECT_CLASS;
		}
		return val;
	}
	
	public String getUserSearchFilter() {
		return prop.getProperty(LGSYNC_USER_SEARCH_FILTER);
	}

	
	public String getUserNameAttribute() {
		String val =  prop.getProperty(LGSYNC_USER_NAME_ATTRIBUTE);
		if(val == null || val.trim().isEmpty()) {
			return DEFAULT_USER_NAME_ATTRIBUTE;
		}
		return val;
	}
	
	public String getUserGroupNameAttribute() {
		String val =  prop.getProperty(LGSYNC_USER_GROUP_NAME_ATTRIBUTE);
		if(val == null || val.trim().isEmpty()) {
			return DEFAULT_USER_GROUP_NAME_ATTRIBUTE;
		}
		return val;
	}
	
	public Set<String> getUserGroupNameAttributeSet() {
		String uga =  getUserGroupNameAttribute();
		StringTokenizer st = new StringTokenizer(uga, ",");
		Set<String> userGroupNameAttributeSet = new HashSet<String>();
		while (st.hasMoreTokens()) {
			userGroupNameAttributeSet.add(st.nextToken().trim());
		}
		return userGroupNameAttributeSet;
	}
	
	public String getUserNameCaseConversion() {
 		String ret = prop.getProperty(UGSYNC_USERNAME_CASE_CONVERSION_PARAM, DEFAULT_UGSYNC_USERNAME_CASE_CONVERSION_VALUE) ;
 		return ret.trim().toLowerCase() ;
 	}
 
 	public String getGroupNameCaseConversion() {
 		String ret = prop.getProperty(UGSYNC_GROUPNAME_CASE_CONVERSION_PARAM, DEFAULT_UGSYNC_GROUPNAME_CASE_CONVERSION_VALUE) ;
 		return ret.trim().toLowerCase() ;
 	}
 
 	public String getProperty(String aPropertyName) {
 		return prop.getProperty(aPropertyName) ;
 	}
 
 	public String getProperty(String aPropertyName, String aDefaultValue) {
 		return prop.getProperty(aPropertyName, aDefaultValue) ;
 	}
	
}
