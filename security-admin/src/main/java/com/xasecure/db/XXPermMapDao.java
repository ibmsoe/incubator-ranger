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

 package com.xasecure.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;

import com.xasecure.entity.XXPermMap;
import com.xasecure.entity.XXResource;

import com.xasecure.common.*;
import com.xasecure.common.db.*;
import com.xasecure.entity.*;

public class XXPermMapDao extends BaseDao<XXPermMap> {
	static final Logger logger = Logger.getLogger(XXResourceDao.class);

    public XXPermMapDao( XADaoManagerBase daoManager ) {
		super(daoManager);
    }

	public List<XXPermMap> findByResourceId(Long resourceId) {
		if (resourceId != null) {
			try {
				return getEntityManager()
						.createNamedQuery("XXPermMap.findByResourceId", XXPermMap.class)
						.setParameter("resourceId", resourceId)
						.getResultList();
			} catch (NoResultException e) {
				logger.debug(e.getMessage());
			}
		} else {
			logger.debug("ResourceId not provided.");
			return new ArrayList<XXPermMap>();
		}
		return null;
	}
}

