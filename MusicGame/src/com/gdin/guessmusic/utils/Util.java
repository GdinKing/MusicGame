package com.gdin.guessmusic.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gdin.guessmusic.R;
import com.gdin.guessmusic.data.Const;
import com.gdin.guessmusic.model.IDialogButtonListener;

public class Util {

	private static AlertDialog mAlertDialog;
	private static String distractor;
	private static String type;
	private static String filename = "";
	private static String name = "";

	/**
	 * 工具-获取视图
	 * 
	 * @param context
	 * @param layoutId
	 * @return
	 */
	public static View getView(Context context, int layoutId) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(layoutId, null);
		return layout;
	}

    /**
     * 将dp值转换为px值
     */
    public static int dp2px(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);
        return px;
    }
    
	/**
	 * 获取文件名
	 */

	public static String getFileName(Context context, int id) {

		searchSong(context, id);
		return filename;
	}

	/**
	 * 获取歌名
	 */

	public static String getSongName(Context context, int id) {

		searchSong(context, id);
		return name;
	}

	/**
	 * 获取答案类型
	 */

	public static String getSongType(Context context, int id) {

		searchSong(context, id);
		return type;
	}

	/**
	 * 工具-获取干扰文字
	 * 
	 * @return
	 */
	public static char getRandomChar(Context context, int id,int index) {
		//new Random().nextInt(distractor.length())
		searchSong(context, id);
		return distractor.charAt(index);
	}

	public static void searchSong(Context context, int id) {
		CreateDatabase cd = new CreateDatabase();
		try {
			SQLiteDatabase sld = cd.openDatabase(context);

			String sql = "select * from tb_song where id='" + id + "'";
			Cursor cur = sld.rawQuery(sql, null);
			while (cur.moveToNext()) {
				filename = cur.getString(1);
				name = cur.getString(2);
				type = cur.getString(3);
				distractor = cur.getString(4); // distractor
			}
			cur.close();
			sld.close();
		} catch (Exception e) {
		}

	}

	/**
	 * 页面跳转
	 * 
	 * @param context
	 * @param clazz
	 */
	public static void startActivity(Context context, Class clazz) {
		Intent intent = new Intent();
		intent.setClass(context, clazz);
		context.startActivity(intent);

		// 关闭当前Activity
		//((Activity) context).finish();
	}

	/**
	 * 显示自定义对话框
	 * 
	 * @param context
	 * @param message
	 *            显示的消息
	 * @param listener
	 *            按钮的点击事件
	 */
	public static void showDialog(final Context context, String message,
			final IDialogButtonListener listener) {
		View dialogView = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(context,
				R.style.Theme_Transparent);
		dialogView = getView(context, R.layout.dialog_view);

		ImageButton btnOkView = (ImageButton) dialogView
				.findViewById(R.id.btn_dialog_ok);
		ImageButton btnCancelView = (ImageButton) dialogView
				.findViewById(R.id.btn_dialog_cancel);
		TextView txtMessageView = (TextView) dialogView
				.findViewById(R.id.text_dialog_message);

		txtMessageView.setText(message);
		// 是
		btnOkView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 关闭对话框
				if (mAlertDialog != null) {
					mAlertDialog.cancel();
				}
				// 事件回调
				if (listener != null) {
					listener.onClick();
				}
				// 播放音效
				MyPlayer.playTone(context, MyPlayer.INDEX_TONE_ENTER);
			}
		});
		// 否
		btnCancelView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 关闭对话框
				if (mAlertDialog != null) {
					mAlertDialog.cancel();
				}
				// 播放音效
				MyPlayer.playTone(context, MyPlayer.INDEX_TONE_CANCEL);
			}
		});
		// 为dialog设置view
		builder.setView(dialogView);

		mAlertDialog = builder.create();
		// 显示对话框
		mAlertDialog.show();
	}

	/**
	 * 保存关卡数据
	 * 
	 * @param context
	 * @param stageIndex
	 *            关卡数
	 * @param coins
	 *            金币数
	 */
	public static void savaData(Context context, int stageIndex, int coins) {
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(Const.FILE_NAME_SAVE_DATA,
					Context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream(fos);
			dos.writeInt(stageIndex);
			dos.writeInt(coins);
		} catch (Exception e) {

			e.printStackTrace();
		} finally {

			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	/**
	 * 读取关卡数据
	 * 
	 * @param context
	 * @return
	 */
	public static int[] loadData(Context context) {
		FileInputStream fis = null;

		int[] datas = { -1, Const.TOTAL_COINS };

		try {
			fis = context.openFileInput(Const.FILE_NAME_SAVE_DATA);

			DataInputStream dis = new DataInputStream(fis);
			datas[Const.INDEX_LOAD_DATA_STAGE] = dis.readInt();
			datas[Const.INDEX_LOAD_DATA_COINS] = dis.readInt();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return datas;
	}

	/**
	 * 将图片转换为字节流数组
	 * 
	 * @param thumbBmp
	 * @param b
	 * @return
	 */
	public static byte[] bmpToByteArray(Bitmap thumbBmp, boolean b) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (b) {
			thumbBmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
		}
		return baos.toByteArray();
	}
}
