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

 package com.xasecure.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.xasecure.biz.UserMgr;
import com.xasecure.common.XAConstants;
import com.xasecure.common.XAConfigUtil;
import com.xasecure.common.MessageEnums;
import com.xasecure.common.RESTErrorUtil;
import com.xasecure.common.SearchCriteria;
import com.xasecure.common.SearchUtil;
import com.xasecure.common.StringUtil;
import com.xasecure.entity.XXPortalUser;
import com.xasecure.view.VXPasswordChange;
import com.xasecure.view.VXResponse;
import com.xasecure.view.VXStringList;
import com.xasecure.view.VXPortalUser;
import com.xasecure.view.VXPortalUserList;
import com.xasecure.util.XARestUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xasecure.common.annotation.XAAnnotationClassName;
import com.xasecure.common.annotation.XAAnnotationJSMgrName;
import com.xasecure.common.annotation.XAAnnotationRestAPI;
import com.xasecure.db.XADaoManager;


@Path("users")
@Component
@Scope("request")
@XAAnnotationJSMgrName("UserMgr")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserREST {
	static Logger logger = Logger.getLogger(UserREST.class);

	@Autowired
	StringUtil stringUtil;

	@Autowired
	XADaoManager daoManager;

	@Autowired
	XAConfigUtil configUtil;

	@Autowired
	RESTErrorUtil restErrorUtil;

	@Autowired
	SearchUtil searchUtil;

	@Autowired
	UserMgr userManager;

	@Autowired
	XARestUtil msRestUtil;

	/**
	 * Implements the traditional search functionalities for UserProfile
	 * 
	 * @param request
	 * @return
	 */
	@GET
	@Produces({ "application/xml", "application/json" })
	@PreAuthorize("hasRole('ROLE_SYS_ADMIN')")
	public VXPortalUserList searchUsers(@Context HttpServletRequest request) {
		String[] approvedSortByParams = new String[] { "requestDate",
				"approvedDate", "activationDate", "emailAddress", "firstName",
				"lastName" };
		@SuppressWarnings("deprecation")
		SearchCriteria searchCriteria = searchUtil.extractCommonCriterias(
				request, approvedSortByParams);

		// userId
		searchUtil.extractLong(request, searchCriteria, "userId", "User Id");

		// loginId
		searchUtil.extractString(request, searchCriteria, "loginId",
				"Login Id", null);

		// emailAddress
		searchUtil.extractString(request, searchCriteria, "emailAddress",
				"Email Address", null);

		// firstName
		searchUtil.extractString(request, searchCriteria, "firstName",
				"First Name", StringUtil.VALIDATION_NAME);

		// lastName
		searchUtil.extractString(request, searchCriteria, "lastName",
				"Last Name", StringUtil.VALIDATION_NAME);

		// status
		searchUtil.extractEnum(request, searchCriteria, "status", "Status",
				"statusList", XAConstants.ActivationStatus_MAX);

		// publicScreenName
		searchUtil.extractString(request, searchCriteria, "publicScreenName",
				"Public Screen Name", StringUtil.VALIDATION_NAME);
		// roles
		searchUtil.extractStringList(request, searchCriteria, "role", "Role",
				"roleList", configUtil.getRoles(), StringUtil.VALIDATION_NAME);

		return userManager.searchUsers(searchCriteria);
	}

	/**
	 * Return the VUserProfile for the given userId
	 * 
	 * @param userId
	 * @return
	 */
	@GET
	@Path("{userId}")
	@Produces({ "application/xml", "application/json" })
	public VXPortalUser getUserProfileForUser(@PathParam("userId") Long userId) {
		try {
			VXPortalUser userProfile = userManager.getUserProfile(userId);
			if (userProfile != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("getUserProfile() Found User userId=" + userId);
				}
			} else {
				logger.debug("getUserProfile() Not found userId=" + userId);
			}
			return userProfile;
		} catch (Throwable t) {
			logger.error("getUserProfile() no user session. error="
					+ t.toString());
		}
		return null;
	}

	@POST
	@Consumes({ "application/json", "application/xml" })
	@Produces({ "application/xml", "application/json" })
	@PreAuthorize("hasRole('ROLE_SYS_ADMIN')")
	public VXPortalUser create(VXPortalUser userProfile,
			@Context HttpServletRequest servletRequest) {
		logger.info("create:" + userProfile.getEmailAddress());

		return userManager.createUser(userProfile);
	}
	
	// API to add user with default account
	@POST
	@Path("/default")
	@Consumes({ "application/json", "application/xml" })
	@Produces({ "application/xml", "application/json" })
	@PreAuthorize("hasRole('ROLE_SYS_ADMIN')")
	public VXPortalUser createDefaultAccountUser(VXPortalUser userProfile,
			@Context HttpServletRequest servletRequest) {
		logger.info("create:" + userProfile.getEmailAddress());
		return userManager.createDefaultAccountUser(userProfile);
	}


	@PUT
	@Consumes({ "application/json", "application/xml" })
	@Produces({ "application/xml", "application/json" })
	@XAAnnotationRestAPI(updates_classes = "VUserProfile")
	public VXPortalUser update(VXPortalUser userProfile,
			@Context HttpServletRequest servletRequest) {
		logger.info("update:" + userProfile.getEmailAddress());
		XXPortalUser gjUser = daoManager.getXXPortalUser().getById(userProfile.getId());
		userManager.checkAccess(gjUser);
		if (gjUser != null) {
			msRestUtil.validateVUserProfileForUpdate(gjUser, userProfile);
			gjUser = userManager.updateUser(userProfile);
			return userManager.mapXXPortalUserVXPortalUser(gjUser);
		} else {
			logger.info("update(): Invalid userId provided: userId="
					+ userProfile.getId());
			throw restErrorUtil.createRESTException("serverMsg.userRestUser",
					MessageEnums.DATA_NOT_FOUND, null, null,
					userProfile.toString());
		}
	}

	@PUT
	@Path("/{userId}/roles")
	@Produces({ "application/xml", "application/json" })
	public VXResponse setUserRoles(@PathParam("userId") Long userId,
			VXStringList roleList) {
		userManager.checkAccess(userId);
		userManager.setUserRoles(userId, roleList.getVXStrings());
		VXResponse response = new VXResponse();
		response.setStatusCode(VXResponse.STATUS_SUCCESS);
		return response;
	}

	/**
	 * Deactivate the user
	 * 
	 * @param userId
	 * @return
	 */
	@POST
	@Path("{userId}/deactivate")
	@Produces({ "application/xml", "application/json" })
	@PreAuthorize("hasRole('ROLE_SYS_ADMIN')")
	@XAAnnotationClassName(class_name = VXPortalUser.class)
	public VXPortalUser deactivateUser(@PathParam("userId") Long userId) {
		XXPortalUser gjUser = daoManager.getXXPortalUser().getById(userId);
		if (gjUser == null) {
			logger.info("update(): Invalid userId provided: userId=" + userId);
			throw restErrorUtil.createRESTException("serverMsg.userRestUser",
					MessageEnums.DATA_NOT_FOUND, null, null, "" + userId);
		}
		return userManager.deactivateUser(gjUser);
	}

	/**
	 * This method returns the VUserProfile for the current session
	 * 
	 * @param request
	 * @return
	 */
	@GET
	@Path("/profile")
	@Produces({ "application/xml", "application/json" })
	public VXPortalUser getUserProfile(@Context HttpServletRequest request) {
		try {
			logger.debug("getUserProfile(). httpSessionId="
					+ request.getSession().getId());
			VXPortalUser userProfile = userManager.getUserProfileByLoginId();
			return userProfile;
		} catch (Throwable t) {
			logger.error(
					"getUserProfile() no user session. error=" + t.toString(),
					t);
		}
		return null;
	}

	@GET
	@Path("/firstnames")
	@Produces({ "application/xml", "application/json" })
	public String suggestUserFirstName(@QueryParam("letters") String letters,
			@Context HttpServletRequest req) {
		return null;
	}

	/**	  
	 * @param userId
	 * @param changePassword
	 * @return
	 */
	@POST
	@Path("{userId}/passwordchange")
	@Produces({ "application/xml", "application/json" })
	public VXResponse changePassword(@PathParam("userId") Long userId,
			VXPasswordChange changePassword) {
		logger.info("changePassword:" + userId);

		XXPortalUser gjUser = daoManager.getXXPortalUser().getById(userId);
		if (gjUser == null) {
			logger.warn("SECURITY:changePassword(): Invalid userId provided: userId="
					+ userId);
			throw restErrorUtil.createRESTException("serverMsg.userRestUser",
					MessageEnums.DATA_NOT_FOUND, null, null, "" + userId);
		}

		userManager.checkAccess(gjUser);
		changePassword.setId(userId);
 		VXResponse ret = userManager.changePassword(changePassword);
		return ret;
	}

	/**	 
	 * 
	 * @param userId
	 * @param changeEmail
	 * @return
	 */
	@POST
	@Path("{userId}/emailchange")
	@Produces({ "application/xml", "application/json" })
	public VXPortalUser changeEmailAddress(@PathParam("userId") Long userId,
			VXPasswordChange changeEmail) {
		logger.info("changeEmail:" + userId);

		XXPortalUser gjUser = daoManager.getXXPortalUser().getById(userId);
		if (gjUser == null) {
			logger.warn("SECURITY:changeEmail(): Invalid userId provided: userId="
					+ userId);
			throw restErrorUtil.createRESTException("serverMsg.userRestUser",
					MessageEnums.DATA_NOT_FOUND, null, null, "" + userId);
		}

		userManager.checkAccess(gjUser);
		changeEmail.setId(userId);
		VXPortalUser ret = userManager.changeEmailAddress(gjUser, changeEmail);
		return ret;
	}

}