package com.gdin.guessmusic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.gdin.guessmusic.R;
import com.gdin.guessmusic.model.IDialogButtonListener;
import com.gdin.guessmusic.utils.CreateDatabase;
import com.gdin.guessmusic.utils.DbHelper;
import com.gdin.guessmusic.utils.MyApplication;

import com.gdin.guessmusic.utils.Util;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

public class MenuActivity extends Activity {

	private ImageButton mBeginBtn;
	private ImageButton mAboutUsBtn;
	private ImageButton mQuitBtn;

	private DbHelper helper;

	private Context context;
	private final String DATABASE_PATH = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/mymusic";
	private final String DATABASE_FILENAME = "MyMusic.db";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_menu);

		MyApplication.getInstance().addActivity(this);

		// 初始化
		mBeginBtn = (ImageButton) findViewById(R.id.begin_game_btn);
		mAboutUsBtn = (ImageButton) findViewById(R.id.about_us_btn);
		mQuitBtn = (ImageButton) findViewById(R.id.back_game_btn);
		// 开始游戏
		mBeginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Util.startActivity(MenuActivity.this, MainActivity.class);
			}
		});
		
		// 关于我们
		mAboutUsBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Util.startActivity(MenuActivity.this, AboutUsView.class);
			}
		});
		// 退出
		mQuitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showConfirmDialog();
			}
		});

	}

	// 复写返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			showConfirmDialog();
			return true;
		}
		return false;

	}

	// 结束进程
	private IDialogButtonListener mBtnOkQuitClickListener = new IDialogButtonListener() {

		@Override
		public void onClick() {
			// 执行事件
			//MenuActivity.this.finish();
			MyApplication.getInstance().exit();//关闭所有activity
			int id = android.os.Process.myPid();
			if (id != 0) {
				android.os.Process.killProcess(id);
			}
		}

	};

	// 弹出提示
	private void showConfirmDialog() {
		Util.showDialog(MenuActivity.this, "是否退出游戏？", mBtnOkQuitClickListener);
	}

}
