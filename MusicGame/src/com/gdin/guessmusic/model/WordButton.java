package com.gdin.guessmusic.model;

import android.widget.Button;

/**
 * 文字按钮
 * @author gdin
 *
 */
public class WordButton {
	public int mIndex;
	public boolean mIsVisiable;
	public String mWordString;
	
	public Button mViewButton;

	public WordButton() {
		mIsVisiable = true;
		mWordString = "";
	}
	
	
}
