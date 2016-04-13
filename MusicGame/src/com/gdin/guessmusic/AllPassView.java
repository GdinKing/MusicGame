package com.gdin.guessmusic;

import com.gdin.guessmusic.R;
import com.gdin.guessmusic.utils.MyApplication;
import com.gdin.guessmusic.utils.MyPlayer;
import com.gdin.guessmusic.utils.Util;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;

/**
 * 通关界面
 * 
 */

public class AllPassView extends Activity {

	private ImageButton mBackToMenu;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.all_pass_view);
		MyApplication.getInstance().addActivity(this);

		// 播放音效
		MyPlayer.playTone(AllPassView.this, MyPlayer.INDEX_PASS);
	
		FrameLayout fl = (FrameLayout) findViewById(R.id.layout_bar_coin);
		fl.setVisibility(View.GONE);
		mBackToMenu = (ImageButton) findViewById(R.id.btn_bar_back);
		mBackToMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Util.startActivity(AllPassView.this, MenuActivity.class);
			}
		});
	}

	// 复写返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			Util.startActivity(AllPassView.this, MenuActivity.class);
			
			return true;
		}
		return false;

	}

}
