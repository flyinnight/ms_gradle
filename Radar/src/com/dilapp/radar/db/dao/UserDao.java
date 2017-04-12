package com.dilapp.radar.db.dao;

import com.dilapp.radar.domain.server.User;

import android.database.Cursor;

public interface UserDao {

	public void saveUserInfo(String content);

	public boolean deleteUserId(long id);

	public boolean deleteUserAll();

	public Cursor getFirst();

	public boolean updateUser(User bean);

}
