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

public abstract class XGroupUserServiceBase<T extends XXGroupUser, V extends VXGroupUser>
		extends AbstractBaseResourceService<T, V> {
	public static final String NAME = "XGroupUser";

	public XGroupUserServiceBase() {

	}

	@SuppressWarnings("unchecked")
	@Override
	protected XXGroupUser mapViewToEntityBean(VXGroupUser vObj, XXGroupUser mObj, int OPERATION_CONTEXT) {
		mObj.setName( vObj.getName());
		mObj.setParentGroupId( vObj.getParentGroupId());
		mObj.setUserId( vObj.getUserId());
		return mObj;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected VXGroupUser mapEntityToViewBean(VXGroupUser vObj, XXGroupUser mObj) {
		vObj.setName( mObj.getName());
		vObj.setParentGroupId( mObj.getParentGroupId());
		vObj.setUserId( mObj.getUserId());
		return vObj;
	}

	/**
	 * @param searchCriteria
	 * @return
	 */
	public VXGroupUserList searchXGroupUsers(SearchCriteria searchCriteria) {
		VXGroupUserList returnList = new VXGroupUserList();
		List<VXGroupUser> xGroupUserList = new ArrayList<VXGroupUser>();

		@SuppressWarnings("unchecked")
		List<XXGroupUser> resultList = (List<XXGroupUser>)searchResources(searchCriteria,
				searchFields, sortFields, returnList);

		// Iterate over the result list and create the return list
		for (XXGroupUser gjXGroupUser : resultList) {
			@SuppressWarnings("unchecked")
			VXGroupUser vXGroupUser = populateViewBean((T)gjXGroupUser);
			xGroupUserList.add(vXGroupUser);
		}

		returnList.setVXGroupUsers(xGroupUserList);
		return returnList;
	}

}
