/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eke.cust.chat.db;

import android.content.Context;

import com.eke.cust.chat.domain.EaseUser;
import com.eke.cust.chat.domain.RobotUser;

import java.util.List;
import java.util.Map;

public class UserDao {
	public static final String TABLE_NAME = "uers";
	public static final String COLUMN_NAME_ID = "username";
	public static final String COLUMN_NAME_NICK = "nick";
	public static final String COLUMN_NAME_AVATAR = "avatar";
	
	public static final String PREF_TABLE_NAME = "pref";
	public static final String COLUMN_NAME_DISABLED_GROUPS = "disabled_groups";
	public static final String COLUMN_NAME_DISABLED_IDS = "disabled_ids";

	public static final String ROBOT_TABLE_NAME = "robots";
	public static final String ROBOT_COLUMN_NAME_ID = "username";
	public static final String ROBOT_COLUMN_NAME_NICK = "nick";
	public static final String ROBOT_COLUMN_NAME_AVATAR = "avatar";
	
	
	public UserDao(Context context) {
	}

	/**
	 * save contact list
	 * 
	 * @param contactList
	 */
	public void saveContactList(List<EaseUser> contactList) {
	    CustDBManager.getInstance().saveContactList(contactList);
	}

	/**
	 * get contact list
	 * 
	 * @return
	 */
	public Map<String, EaseUser> getContactList() {
		
	    return CustDBManager.getInstance().getContactList();
	}
	
	/**
	 * delete a contact
	 * @param username
	 */
	public void deleteContact(String username){
	    CustDBManager.getInstance().deleteContact(username);
	}
	
	/**
	 * save a contact
	 * @param user
	 */
	public void saveContact(EaseUser user){
	    CustDBManager.getInstance().saveContact(user);
	}
	
	public void setDisabledGroups(List<String> groups){
	    CustDBManager.getInstance().setDisabledGroups(groups);
    }
    
    public List<String> getDisabledGroups(){
        return CustDBManager.getInstance().getDisabledGroups();
    }
    
    public void setDisabledIds(List<String> ids){
        CustDBManager.getInstance().setDisabledIds(ids);
    }
    
    public List<String> getDisabledIds(){
        return CustDBManager.getInstance().getDisabledIds();
    }
    
    public Map<String, RobotUser> getRobotUser(){
    	return CustDBManager.getInstance().getRobotList();
    }
    
    public void saveRobotUser(List<RobotUser> robotList){
    	CustDBManager.getInstance().saveRobotList(robotList);
    }
}
