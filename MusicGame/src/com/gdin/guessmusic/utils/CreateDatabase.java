package com.gdin.guessmusic.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.gdin.guessmusic.R;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

/**
 * 创建数据库
 * 
 */
public class CreateDatabase {

	// private final String DATABASE_PATH = android.os.Environment
	// .getExternalStorageDirectory().getAbsolutePath() + "/mymusic";
	// 瀛樺偍鍦ㄦ湰鏈哄唴瀛�
	private final String DATABASE_PATH = "/data/data/com.gdin.guessmusic/musicdb";

	private final String DATABASE_FILENAME = "MyMusic.db";

	public SQLiteDatabase openDatabase(Context context) {
		try {
			// 鑾峰緱MyMusic.db鏂囦欢鐨勭粷瀵硅矾寰�
			String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
			File dir = new File(DATABASE_PATH);
			// 濡傛灉DATABASE_PATH鐩綍涓嶄腑瀛樺湪锛屽垱寤鸿繖涓洰褰�
			if (!dir.exists())
				dir.mkdir();
			// 濡傛灉鍦―ATABASE_PATH鐩綍涓笉瀛樺湪MyMusic.db鏂囦欢锛�
			// 鍒欎粠res/assets鐩綍涓鍒惰繖涓枃浠跺埌鍏朵腑

			if (!(new File(databaseFilename)).exists()) {
				// 鑾峰緱灏佽MyMusic.db鏂囦欢鐨処nputStream瀵硅薄
				InputStream is = context.getAssets().open("MyMusic.db");
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				byte[] buffer = new byte[1024];
				int count = 0;
				// 寮�濮嬪鍒禡yMusic.db鏂囦欢
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
			// 鎵撳紑DATABASE_PATH鐩綍涓殑MyMusic.db鏂囦欢
			SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
					databaseFilename, null);
			return database;
		} catch (Exception e) {
		}
		return null;
	}
}
