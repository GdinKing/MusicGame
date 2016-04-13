package com.gdin.guessmusic.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore.Files.FileColumns;

public class DbHelper extends SQLiteOpenHelper {
	/**
	 * 数据库名称常量
	 */
	private static final String DATABASE_NAME = "MyMusic.db";
	/**
	 * 数据库版本常量
	 */
	private static final int DATABASE_VERSION = 3;

	/**
	 * 表常量
	 */
	private final static String TABLE_NAME = "tb_song";
	public final static String SONG_ID = "id";
	public final static String SONG_NAME = "name";
	public final static String SONG_FILENAME = "filename";
	public final static String SONG_TYPE = "type";
	public final static String SONG_DISTRACTOR = "distractor";
	
	
	
	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// 创建table
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + TABLE_NAME + " (" + SONG_ID
				+ " INTEGER primary key autoincrement, " + SONG_FILENAME
				+ " text, " + SONG_NAME + " text, " + SONG_TYPE + " text, "
				+ SONG_DISTRACTOR + " text);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}
	//查询
	public Cursor select() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db
				.query(TABLE_NAME, null, null, null, null, null, null);
		return cursor;
	}

	
	// 增加操作
	public long insert(String filename, String name, String type,
			String distractor) {
		SQLiteDatabase db = this.getWritableDatabase();
		/* ContentValues */
		ContentValues cv = new ContentValues();
		cv.put(SONG_FILENAME, filename);
		cv.put(SONG_NAME, name);
		cv.put(SONG_TYPE, type);
		cv.put(SONG_DISTRACTOR, distractor);

		long row = db.insert(TABLE_NAME, null, cv);
		return row;
	}

	// 删除操作
	public void delete(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = SONG_ID + " = ?";
		String[] whereValue = { Integer.toString(id) };
		db.delete(TABLE_NAME, where, whereValue);
	}
	
	

}
