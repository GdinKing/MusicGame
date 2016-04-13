package com.gdin.guessmusic.utils;

import java.io.IOException;

import com.gdin.guessmusic.model.Song;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

/**
 * 音乐播放类
 * 
 */
public class MyPlayer {

	//
	public final static int INDEX_TONE_ENTER = 0;
	public final static int INDEX_TONE_CANCEL = 1;
	public final static int INDEX_TONE_COIN = 2;
	public final static int INDEX_PASS = 3;
	// 音效文件名
	private final static String[] SOME_NAMES = { "enter.mp3", "cancel.mp3",
			"coin.mp3", "pass.wav" };

	// 歌曲播放
	public static MediaPlayer mMusicPlayer;

	// 音效
	private static MediaPlayer[] mToneMediaPlayer = new MediaPlayer[SOME_NAMES.length];

	/**
	 * 播放音效
	 * 
	 * @param context
	 * @param index
	 */
	public static void playTone(Context context, int index) {
		// 加载声音
		AssetManager assetManager = context.getAssets();
		if (mToneMediaPlayer[index] == null) {
			mToneMediaPlayer[index] = new MediaPlayer();
			try {
				AssetFileDescriptor fileDescriptor = assetManager
						.openFd(SOME_NAMES[index]);
				mToneMediaPlayer[index].setDataSource(
						fileDescriptor.getFileDescriptor(),
						fileDescriptor.getStartOffset(),
						fileDescriptor.getLength());

				mToneMediaPlayer[index].prepare();

			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		// 声音播放
		mToneMediaPlayer[index].start();

	}

	/**
	 * 播放歌曲
	 * 
	 * @param context
	 * @param filename
	 */
	public static void playSong(Context context, String filename) {
		if (mMusicPlayer == null) {
			mMusicPlayer = new MediaPlayer();

		}

		// 强制重置:非第一次播放时
		mMusicPlayer.reset();

		// 加载声音
		AssetManager assetManager = context.getAssets();
		try {
			AssetFileDescriptor fileDescriptor = assetManager.openFd(filename);
			mMusicPlayer
					.setDataSource(fileDescriptor.getFileDescriptor(),
							fileDescriptor.getStartOffset(),
							fileDescriptor.getLength());

			mMusicPlayer.prepare();
			// 声音播放
			mMusicPlayer.start();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	// 暂停播放
	public static void stopTheSong(Context context) {
		if (mMusicPlayer != null) {
			mMusicPlayer.stop();
		}
	}
}
