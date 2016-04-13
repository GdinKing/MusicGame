package com.gdin.guessmusic;

import com.gdin.guessmusic.R;
import com.gdin.guessmusic.utils.MyApplication;
import com.gdin.guessmusic.utils.Util;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class AboutUsView extends Activity {

	private ImageButton mAboutBackBtn;
	private TextView mIntroduceText;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about_us);

		MyApplication.getInstance().addActivity(this);

		mAboutBackBtn = (ImageButton) findViewById(R.id.about_us_back_btn);
		mAboutBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Util.startActivity(AboutUsView.this, MenuActivity.class);
			}
		});
		mIntroduceText = (TextView) findViewById(R.id.how_to_play_text);
		String str = "1.玩家点击唱片中心按钮开始播放音乐。\n2.玩家根据所听音乐及屏幕上方提示的答案类型猜测答案，并在屏幕下方给出的文字中选择正确答案。"
				+ "\n3.选择文字后，文字会出现在上方答案框，答案正确即过关，错误则闪烁文字，玩家可点击答案框重新更换已选文字。"
				+ "\n4.每过一关可获得金币，金币数在右上方有显示，金币可用来获取答案提示";
		mIntroduceText.setText(str);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			// Util.startActivity(AboutUsView.this, MenuActivity.class);
			moveTaskToBack(true);
			return true;
		}
		return false;

	}
}
