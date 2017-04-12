package com.dilapp.radar.skinproducts.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private long id;
	private String name;
	private float price;
	private String description;
	private String homepage;
	private int type;

	private static final int VERSION = 1;
	String sql = "create table if not exists skinproducts"
			+ "(id int primary key,name varchar,price REAL," +
			"description varchar,homepage varchar,type varchar)";

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public DBHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (db != null) {
			if (oldVersion < newVersion) {
				db.execSQL(sql);
			}
		}
	}
}
