package com.dilapp.radar.skinproducts.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SkinProductsUtils {
	private static DBHelper dbHelper;

	public static void InserSkinProduct(SkinProduct product, SQLiteDatabase db) {
		if (db != null) {
			String sql = "insert into skinproducts (id,name,price,description,homepage,type) values (?,?,?,?,?,?)";
			try {
				db.beginTransaction();
				db.execSQL(sql);
				db.execSQL(sql,
						new Object[] { product.getId(), product.getName(),
								product.getPrice(), product.getDescription(),
								product.getHomepage(), product.getType() });
				db.setTransactionSuccessful();
			} catch (SQLException e) {
				Log.i("err", "insert failed");
			}
			db.endTransaction();
			// CloseDb();
		}
	}

	public static void update(SkinProduct product, SQLiteDatabase db) {
		// 开始事务处理
		db.beginTransaction();
		ContentValues cv = new ContentValues();
		cv.put("id", product.getId());
		cv.put("name", product.getName());
		cv.put("price", product.getPrice());
		cv.put("description", product.getDescription());
		cv.put("homepage", product.getHomepage());
		cv.put("type", product.getType());
		db.update("skinproducts", cv, "id=?",
				new String[] { "" + product.getId() });
		db.setTransactionSuccessful();
		// 结束事务处理
		db.endTransaction();
		db.close();
	}

	public static void findAll(final QueryInterface handler, final SQLiteDatabase db) {
		final ArrayList<SkinProduct> produts = new ArrayList<SkinProduct>();
		final String sql = "select * from skinproducts;";
		if (db != null) {
			try {
				new Thread(new Runnable() {

					@Override
					public void run() {
						Cursor cursor = db.rawQuery(sql, null);
						while (cursor.moveToNext()) {
							SkinProduct product = new SkinProduct();
							product.setId(cursor.getInt(cursor
									.getColumnIndex("id")));
							product.setName(cursor.getString(cursor
									.getColumnIndex("name")));
							product.setPrice(cursor.getFloat(cursor
									.getColumnIndex("price")));
							product.setDescription(cursor.getString(cursor
									.getColumnIndex("description")));
							product.setHomepage(cursor.getString(cursor
									.getColumnIndex("homepage")));
							product.setType(cursor.getInt(cursor
									.getColumnIndex("type")));
							produts.add(product);
						}
						handler.onSucess(produts);
					}
				}).start();

			} catch (Exception e) {
				handler.failure(e);
			}
		} else {
			handler.failure(new Exception("数据库不存在"));
		}
	}

	public static void findById(final QueryInterface handler, final long mid,
			final SQLiteDatabase db) {
		final ArrayList<SkinProduct> produts = new ArrayList<SkinProduct>();
		if (db != null) {
			try {
				new Thread(new Runnable() {
					@Override
					public void run() {
						Cursor cursor = db.rawQuery(
								"select * from skinproducts where id=?",
								new String[] { String.valueOf(mid) });
						while (cursor.moveToNext()) {
							SkinProduct product = new SkinProduct();
							product.setId(cursor.getLong(cursor
									.getColumnIndex("id")));
							product.setName(cursor.getString(cursor
									.getColumnIndex("name")));
							product.setPrice(cursor.getFloat(cursor
									.getColumnIndex("price")));
							product.setDescription(cursor.getString(cursor
									.getColumnIndex("description")));
							product.setHomepage(cursor.getString(cursor
									.getColumnIndex("homepage")));
							product.setType(cursor.getInt(cursor
									.getColumnIndex("type")));
							produts.add(product);
						}
						handler.onSucess(produts);

					}
				}).start();
			} catch (Exception e) {
				handler.failure(e);
			}
		} else {
			handler.failure(new Exception("数据库不存在"));
		}
	}

	/**
	 * 打开数据库
	 */
	public static SQLiteDatabase OpenDb(Context context, int version) {
		dbHelper = new DBHelper(context, "SkProductDB", version);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		return db;
	}

	/**
	 * 关闭数据库
	 */
	public static void CloseDb() {
		if (dbHelper != null) {
			dbHelper.close();
		}
	}
}
