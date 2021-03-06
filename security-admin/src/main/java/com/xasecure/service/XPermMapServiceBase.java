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

/**
 * 
 */

import java.util.ArrayList;
import java.util.List;

import com.xasecure.common.*;
import com.xasecure.entity.*;
import com.xasecure.view.*;
import com.xasecure.service.*;

public abstract class XPermMapServiceBase<T extends XXPermMap, V extends VXPermMap>
		extends AbstractBaseResourceService<T, V> {
	public static final String NAME = "XPermMap";

	public XPermMapServiceBase() {

	}

	@SuppressWarnings("unchecked")
	@Override
	protected XXPermMap mapViewToEntityBean(VXPermMap vObj, XXPermMap mObj, int OPERATION_CONTEXT) {
		mObj.setPermGroup( vObj.getPermGroup());
		mObj.setResourceId( vObj.getResourceId());
		mObj.setGroupId( vObj.getGroupId());
		mObj.setUserId( vObj.getUserId());
		mObj.setPermFor( vObj.getPermFor());
		mObj.setPermType( vObj.getPermType());
		mObj.setIsRecursive( vObj.getIsRecursive());
		mObj.setIsWildCard( vObj.isIsWildCard());
		mObj.setGrantOrRevoke( vObj.isGrantOrRevoke());
		mObj.setIpAddress( vObj.getIpAddress());
		return mObj;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected VXPermMap mapEntityToViewBean(VXPermMap vObj, XXPermMap mObj) {
		vObj.setPermGroup( mObj.getPermGroup());
		vObj.setResourceId( mObj.getResourceId());
		vObj.setGroupId( mObj.getGroupId());
		vObj.setUserId( mObj.getUserId());
		vObj.setPermFor( mObj.getPermFor());
		vObj.setPermType( mObj.getPermType());
		vObj.setIsRecursive( mObj.getIsRecursive());
		vObj.setIsWildCard( mObj.isIsWildCard());
		vObj.setGrantOrRevoke( mObj.isGrantOrRevoke());
		vObj.setIpAddress( mObj.getIpAddress());
		return vObj;
	}

	/**
	 * @param searchCriteria
	 * @return
	 */
	public VXPermMapList searchXPermMaps(SearchCriteria searchCriteria) {
		VXPermMapList returnList = new VXPermMapList();
		List<VXPermMap> xPermMapList = new ArrayList<VXPermMap>();

		@SuppressWarnings("unchecked")
		List<XXPermMap> resultList = (List<XXPermMap>)searchResources(searchCriteria,
				searchFields, sortFields, returnList);

		// Iterate over the result list and create the return list
		for (XXPermMap gjXPermMap : resultList) {
			@SuppressWarnings("unchecked")
			VXPermMap vXPermMap = populateViewBean((T)gjXPermMap);
			xPermMapList.add(vXPermMap);
		}

		returnList.setVXPermMaps(xPermMapList);
		return returnList;
	}

}
