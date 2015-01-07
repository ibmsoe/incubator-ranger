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

 package org.apache.ranger.audit.model;

import java.util.Date;

import org.apache.ranger.audit.dao.DaoManager;

import com.google.gson.annotations.SerializedName;  


public abstract class AuditEventBase {
	protected AuditEventBase() {
	}

	public abstract void persist(DaoManager daoManager);
	
	protected String trim(String str, int len) {
		String ret = str ;
		if (str != null) {
			if (str.length() > len) {
				ret = str.substring(0,len) ;
			}
		}
		return ret ;
	}
}