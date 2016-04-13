package com.gdin.guessmusic;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gdin.guessmusic.R;
import com.gdin.guessmusic.data.Const;
import com.gdin.guessmusic.model.IDialogButtonListener;
import com.gdin.guessmusic.model.IButtonClickListener;
import com.gdin.guessmusic.model.Song;
import com.gdin.guessmusic.model.WordButton;
import com.gdin.guessmusic.myui.MyGridView;
import com.gdin.guessmusic.utils.MyApplication;
import com.gdin.guessmusic.utils.MyLog;
import com.gdin.guessmusic.utils.MyPlayer;
import com.gdin.guessmusic.utils.Util;
import com.gdin.guessmusic.utils.WeixinUtil;
import com.gdin.guessmusic.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MainActivity extends Activity implements IButtonClickListener {
	private static final String TAG = "MainActivity";
	private static int DELETE_CLICK_COUNTS = 0; // 点击删除按钮的次数

	private static int j = 0;
	/**
	 * 答案状态
	 */
	private final static int STATUS_ANSWER_RIGHT = 1; // 正确
	private final static int STATUS_ANSWER_WRONG = 2;// 错误
	private final static int STATUS_ANSWER_LACK = 3;// 不完整
	// 闪烁次数
	private final static int SPARK_TIMES = 6;

	// 唱片相关动画
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;

	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;

	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;

	// 答案类型
	private TextView mTypeText;

	// 唱片控件
	private ImageView mViewPan;
	// 拨杆控件
	private ImageView mViewPanBar;

	// Play 按键事件
	private ImageButton mBtnPlayStart;

	// 当前动画是否正在运行
	private boolean mIsRunning = false;

	// 文字框容器
	private ArrayList<WordButton> mAllWords;

	private ArrayList<WordButton> mBtnSelectWords;

	private MyGridView mMyGridView;

	// 已选择文字框UI容器
	private LinearLayout mViewWordsContainer;
	private RelativeLayout mFloatButton;
	// 当前的歌曲
	private Song mCurrentSong;

	private TextView mCurrentSongNamePassView;

	// 当前关的索引
	private int mCurrentStageIndex = -1;

	private TextView mCurrentStagePassView;

	private TextView mCurrentStageView;

	private LinearLayout mPassView;

	// 当前金币的数量
	private int mCurrentCoins = Const.TOTAL_COINS;

	private TextView mViewCurrentCoins;

	// 增加金币按钮
	private ImageButton mAddCoinsBtn;
	// 分享按钮
	private ImageButton mShareBtn;

	private ImageButton mRightShareBtn;
	// 微信API
	private IWXAPI wxApi;
	// 后退按钮
	private ImageButton mBackBtn;
	public final static int ID_DIALOG_DELETE = 1;
	public final static int ID_DIALOG_TIP = 2;
	public final static int ID_DIALOG_LACK = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		MyApplication.getInstance().addActivity(this);

		// 读取游戏数据
		int[] datas = Util.loadData(MainActivity.this);

		mCurrentStageIndex = datas[Const.INDEX_LOAD_DATA_STAGE];
		mCurrentCoins = datas[Const.INDEX_LOAD_DATA_COINS];

		// 初始化控件
		mViewPan = (ImageView) findViewById(R.id.imageView1);
		mViewPanBar = (ImageView) findViewById(R.id.imageView2);

		mMyGridView = (MyGridView) findViewById(R.id.gridview);

		mPassView = (LinearLayout) findViewById(R.id.pass_view);

		mShareBtn = (ImageButton) findViewById(R.id.btn_share);
		mRightShareBtn = (ImageButton) findViewById(R.id.btn_right_share);

		mTypeText = (TextView) findViewById(R.id.song_type_text);

		mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);

		mViewCurrentCoins = (TextView) findViewById(R.id.txt_bar_coins);

		mViewCurrentCoins.setText(mCurrentCoins + "");

		// 微信接口实例化
		wxApi = WXAPIFactory.createWXAPI(this, WXEntryActivity.APP_ID);
		wxApi.registerApp(WXEntryActivity.APP_ID);

		// 注册监听
		mMyGridView.registOnWordButtonClick(this);
		// 初始化动画
		mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		mPanLin = new LinearInterpolator();
		mPanAnim.setInterpolator(mPanLin);
		mPanAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 开启拨杆退出动画
				mViewPanBar.setAnimation(mBarOutAnim);

				mIsRunning = false;
				mBtnPlayStart.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

		mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
		mBarInLin = new LinearInterpolator();
		mBarInAnim.setFillAfter(true);
		mBarInAnim.setInterpolator(mBarInLin);
		mBarInAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 开始唱片动画
				mViewPan.startAnimation(mPanAnim);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

		mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
		mBarOutLin = new LinearInterpolator();
		mBarOutAnim.setFillAfter(true);
		mBarOutAnim.setInterpolator(mBarOutLin);
		mBarOutAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 整套动画播放完毕
				mViewPanBar.setAnimation(mBarOutAnim);

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);

		mBtnPlayStart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mPassView.getVisibility() == View.VISIBLE) {
					return;
				} else {

					handlePlayButton();
				}

			}
		});
		// 后退按钮
		mBackBtn = (ImageButton) findViewById(R.id.btn_bar_back);
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Util.startActivity(MainActivity.this, MenuActivity.class);
				moveTaskToBack(true);

			}
		});
		

		// 初始化游戏数据
		initCurrentStageData();

		// 处理删除文字
		handleDeleteWord();

		// 处理提示按键结果
		handleTipAnswer();

		// 分享
		wechatCircleShare();
		handleRightShare();
	}

	// 复写返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			// Util.startActivity(MainActivity.this, MenuActivity.class);
			// moveTaskToBack(true);
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, MenuActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	/**
	 * 分享
	 */
	private void wechatCircleShare() {

		mShareBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String title = "我在玩《爱上猜歌》，你也一起来玩吧！";
				// 分享到朋友圈
				wechatShare(1, "", title);
				
//				Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//						R.drawable.a);
//				WeixinUtil.getInstance(getApplicationContext()).sendBitmap(bitmap);
			}
		});
	}

	/**
	 * 答案正确过关时分享按钮
	 */
	private void handleRightShare() {

		mRightShareBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String text = "我在《爱上猜歌》玩到第" + (mCurrentStageIndex + 1)
						+ "关了，太有意思了，一起来玩吧！";
				String title = "爱上猜歌";
				// 分享给朋友
				wechatShare(0, text, title);

			}
		});
	}

	/**
	 * 处理圆盘中间的播放按钮，就是开始播放音乐
	 */
	private void handlePlayButton() {
		if (mViewPanBar != null) {
			if (!mIsRunning) {
				mIsRunning = true;

				// 开始拨杆进入动画
				mViewPanBar.startAnimation(mBarInAnim);
				mBtnPlayStart.setVisibility(View.INVISIBLE);

				// 播放音乐
				MyPlayer.playSong(MainActivity.this,
						mCurrentSong.getSongFileName());
				handleMusicStopAnim();
			}

		}
	}

	@Override
	public void onPause() {

		// 保存游戏数据
		Util.savaData(MainActivity.this, mCurrentStageIndex - 1, mCurrentCoins);

		mViewPan.clearAnimation();
		// 暂停音乐
		MyPlayer.stopTheSong(MainActivity.this);
		super.onPause();
	}

	// 音乐停止时动画也停止
	private void handleMusicStopAnim() {
		MediaPlayer player = MyPlayer.mMusicPlayer;
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mViewPan.clearAnimation();
				mViewPanBar.setAnimation(mBarOutAnim);
			}
		});

	}

	// 加载歌曲信息
	private Song loadStageSongInfo(int stageIndex) {
		Song song = new Song();

		song.setSongFileName(Util
				.getFileName(MainActivity.this, stageIndex + 1));
		song.setSongName(Util.getSongName(MainActivity.this, stageIndex + 1));
		// 加载答案类型
		mTypeText.setText(Util.getSongType(MainActivity.this, stageIndex + 1));

		return song;
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		// 分享成功后获得金币
		if (WXEntryActivity.shareSuccess) {
			mCurrentCoins += 50;
			mViewCurrentCoins.setText(mCurrentCoins + "");
			WXEntryActivity.shareSuccess = false;
		}
	}
	// 加载当前关卡数据
	private void initCurrentStageData() {


		// 加载歌曲名字
		mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);

		// 初始化已选文字
		mBtnSelectWords = initWordSelect();
		int size = Util.dp2px(MainActivity.this, 35);
		LayoutParams params = new LayoutParams(size, size);// 设置答案框大小
		// 清空原来的答案
		mViewWordsContainer.removeAllViews();

		// 增加新的答案框
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			mViewWordsContainer.addView(mBtnSelectWords.get(i).mViewButton,
					params);
		}
		// 显示当前关索引
		mCurrentStageView = (TextView) findViewById(R.id.text_game_level);
		if (mCurrentStageView != null) {
			mCurrentStageView.setText((mCurrentStageIndex + 1) + "");
		}

		// 初始化所有文字框
		mAllWords = initAllWord();
		// 更新数据- MyGridView
		mMyGridView.updateData(mAllWords);

	}

	/*-------------------------待选文字-------------------------*/
	// 生成所有文字
	private String[] generateWords() {
		String[] words = new String[MyGridView.COUNTS_WORDS];

		Random random = new Random();

		// 生成歌曲名
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			words[i] = mCurrentSong.getNameCharacters()[i] + "";
		}

		// 生成干扰文字
		for (int i = mCurrentSong.getNameLength(); i < MyGridView.COUNTS_WORDS; i++) {

			words[i] = Util.getRandomChar(MainActivity.this,
					mCurrentStageIndex + 1, i - mCurrentSong.getNameLength())
					+ "";
		}

		// 打乱文字顺序：首先从所有元素中随机选取一个与第一个元素进行交换，
		// 然后在第二个之后选择一个元素与第二个交换，直到最后一个元素。
		for (int i = MyGridView.COUNTS_WORDS - 1; i >= 0; i--) {
			int index = random.nextInt(i + 1);
			String temp = words[index];
			words[index] = words[i];
			words[i] = temp;
		}

		return words;
	}

	/**
	 * 初始化所有待选文字框
	 */
	private ArrayList<WordButton> initAllWord() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();

		// 待选文字
		String[] words = generateWords();

		for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
			WordButton button = new WordButton();

			button.mWordString = words[i];

			data.add(button);
		}

		return data;
	}

	/*-------------------------已选文字-------------------------*/
	/**
	 * 初始化已选文字框
	 * 
	 * @return
	 */
	private ArrayList<WordButton> initWordSelect() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();

		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			View view = Util.getView(MainActivity.this,
					R.layout.self_ui_gridview_item);

			final WordButton holder = new WordButton();

			holder.mViewButton = (Button) view.findViewById(R.id.item_btn);
			
			holder.mViewButton.setTextColor(Color.WHITE);
			holder.mViewButton.setText("");
			holder.mIsVisiable = false;

			holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
			holder.mViewButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (mPassView.getVisibility() == View.VISIBLE) {
						return;
					} else {
						clearAnswer(holder); // 清除答案
					}

				}
			});
			data.add(holder);
		}

		return data;
	}

	// 按钮点击事件
	@Override
	public void onWordButtonClick(WordButton wordButton) {
		setSelectWord(wordButton);

		// 获得答案状态
		int checkResult = checkAnswer();

		// 检查答案
		if (checkResult == STATUS_ANSWER_RIGHT) {
			// 正确，过关并获得奖励
			handlePassEvent();

		} else if (checkResult == STATUS_ANSWER_WRONG) {
			// 错误，闪烁文字提示用户
			sparkWords();

		} else if (checkResult == STATUS_ANSWER_LACK) {
			// 缺失
			// 设置文字颜色为白色
			for (int i = 0; i < mBtnSelectWords.size(); i++) {
				mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
			}
		}
	}

	/**
	 * 清除答案
	 * 
	 * @param wordButton
	 */
	private void clearAnswer(WordButton wordButton) {
		wordButton.mViewButton.setText("");
		wordButton.mWordString = "";
		wordButton.mIsVisiable = false;

		// 设置待选框可见性
		setButtonVisiable(mAllWords.get(wordButton.mIndex), View.VISIBLE);
	}

	/**
	 * 设置答案
	 * 
	 * @param wordButton
	 */
	private void setSelectWord(WordButton wordButton) {
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				// 设置答案文字框的内容及可见性
				mBtnSelectWords.get(i).mViewButton
						.setText(wordButton.mWordString);
				mBtnSelectWords.get(i).mViewButton.setTextSize(25);
				mBtnSelectWords.get(i).mIsVisiable = true;
				mBtnSelectWords.get(i).mWordString = wordButton.mWordString;
				// 记录索引
				mBtnSelectWords.get(i).mIndex = wordButton.mIndex;
			
				// 设置待选框的可见性
				setButtonVisiable(wordButton, View.INVISIBLE);
				break;
			}
		}
	}

	// 设置待选框可见性
	private void setButtonVisiable(WordButton button, int visibility) {
		button.mViewButton.setVisibility(visibility);
		button.mIsVisiable = (visibility == View.VISIBLE) ? true : false;

	}

	// 检查答案
	private int checkAnswer() {
		// 检查长度
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			// 如果有空的，说明答案不完整
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				return STATUS_ANSWER_LACK;
			}
		}
		// 检查答案正确性
		StringBuffer sb = new StringBuffer();
		for (int j = 0; j < mBtnSelectWords.size(); j++) {
			sb.append(mBtnSelectWords.get(j).mWordString); // 组合答案框的文字
		}
		return (sb.toString().equals(mCurrentSong.getSongName()) ? STATUS_ANSWER_RIGHT
				: STATUS_ANSWER_WRONG);
	}

	/**
	 * 闪烁文字
	 */
	private void sparkWords() {
		// 定时器相关
		TimerTask task = new TimerTask() {
			boolean mChange = false;
			int mSpardTimes = 0;

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// 闪烁的次数
						if (++mSpardTimes > SPARK_TIMES) {
							return;
						}
						// 执行闪烁逻辑：交替显示红色和白色文字
						for (int i = 0; i < mBtnSelectWords.size(); i++) {
							mBtnSelectWords.get(i).mViewButton
									.setTextColor(mChange ? Color.RED
											: Color.WHITE);
						}
						mChange = !mChange;
					}

				});
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 1, 150);

	}

	/**
	 * 处理过关界面及事件
	 */
	private void handlePassEvent() {
		// 显示过关界面
		mPassView.setVisibility(View.VISIBLE);

		// 停止未完成的动画
		mViewPan.clearAnimation();
		// 停止播放音乐
		MyPlayer.stopTheSong(MainActivity.this);

		// 播放音效
		MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_TONE_COIN);
		// 当前关的索引
		mCurrentStagePassView = (TextView) findViewById(R.id.text_current_stage_pass);
		if (mCurrentStagePassView != null) {
			mCurrentStagePassView.setText((mCurrentStageIndex + 1) + "");
		}
		// 显示歌曲名称
		mCurrentSongNamePassView = (TextView) findViewById(R.id.text_current_song_name_pass);
		if (mCurrentSongNamePassView != null) {
			mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
		}
		// 金币数增加
		mCurrentCoins += Const.COINS_REWARDS;
		mViewCurrentCoins.setText(mCurrentCoins + "");
		if (judegeAppPassed()) {
			// 进入通关界面
			Util.startActivity(MainActivity.this, AllPassView.class);
			// 重置关卡数
			mCurrentStageIndex = 0;
			// 保存游戏数据
			Util.savaData(MainActivity.this, mCurrentStageIndex, mCurrentCoins);

		} else {
			// 下一关按键处理
			ImageButton btnPass = (ImageButton) findViewById(R.id.btn_next);
			btnPass.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mPassView.setVisibility(View.GONE);
					// 加载关卡数据
					initCurrentStageData();
				}
			});
		}
	}

	// 判断是否通关
	private boolean judegeAppPassed() {
		return mCurrentStageIndex == Const.SONG_COUNTS - 1;
	}

	// 删除文字

	private void deleteOneWord() {
		DELETE_CLICK_COUNTS++;
		int length = MyGridView.COUNTS_WORDS - mCurrentSong.getNameLength();
		// 减少金币
		if (!handleCoins(-getDeleteWordCoins())) {
			// 金币不够，显示提示框
			showConfirmDialog(ID_DIALOG_LACK);
			return;
		}

		if (DELETE_CLICK_COUNTS <= length) {
			// 将索引对应的wordButton设置为不可见
			setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);
		} else {
			// 提示不能再删除答案
			new AlertDialog.Builder(this).setTitle("无法继续删除")
					.setPositiveButton("确定", null).show();
			return;
		}
	}

	// 找到一个非答案文字的其他文字，并且当前是可见的
	private WordButton findNotAnswerWord() {
		Random random = new Random();

		WordButton buf = null;
		while (true) {
			int index = random.nextInt(MyGridView.COUNTS_WORDS);
			buf = mAllWords.get(index);
			if (buf.mIsVisiable && !isTheAnswerWord(buf)) { // 不是答案并且可见
				return buf;
			}
		}

	}

	// 判断文字是否是答案
	private boolean isTheAnswerWord(WordButton button) {
		boolean result = false;
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			if (button.mWordString.equals(""
					+ mCurrentSong.getNameCharacters()[i])) {
				result = true;
				break;
			}
		}

		return result;
	}

	/**
	 * 增加或减少指定数量的金币
	 * 
	 * @param counts
	 * @return true 增加/减少成功，false 失败
	 */
	private boolean handleCoins(int counts) {
		// 判断当前总的金币数是否可被减少
		if (mCurrentCoins + counts >= 0) {
			mCurrentCoins += counts;

			mViewCurrentCoins.setText(mCurrentCoins + "");
			return true;
		} else {
			// 金币不够
			return false;
		}
	}

	/**
	 * 从配置文件读取删除操作所要用的金币数
	 * 
	 * @return
	 */
	private int getDeleteWordCoins() {
		return this.getResources().getInteger(R.integer.pay_delete_word);
	}

	/**
	 * 从配置文件读取提示操作所要用的金币数
	 * 
	 * @return
	 */
	private int getTipWordCoins() {
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}

	// 自定义AlertDialog事件响应
	// 删除错误答案
	private IDialogButtonListener mBtnOkDeleteClickListener = new IDialogButtonListener() {

		@Override
		public void onClick() {
			// 执行事件
			deleteOneWord();
		}

	};
	// 答案提示
	private IDialogButtonListener mBtnOkTipClickListener = new IDialogButtonListener() {

		@Override
		public void onClick() {
			// 执行事件
			tipAnswer();
		}

	};

	

	// 显示对话框
	private void showConfirmDialog(int id) {
		switch (id) {

		case ID_DIALOG_LACK:
			Toast.makeText(MainActivity.this, "金币不足！分享微信即可增加！", 0).show();

			break;
		case ID_DIALOG_DELETE:
			Util.showDialog(MainActivity.this, "确认花掉" + getDeleteWordCoins()
					+ "个金币去掉一个错误答案？", mBtnOkDeleteClickListener);
			break;
		case ID_DIALOG_TIP:

			Util.showDialog(MainActivity.this, "确认花掉" + getTipWordCoins()
					+ "个金币获得一个文字提示？", mBtnOkTipClickListener);

			break;

		default:
			break;
		}
	}

	/**
	 * 处理删除待选文字
	 */

	private void handleDeleteWord() {
		ImageButton button = (ImageButton) findViewById(R.id.btn_delete_word);

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPassView.getVisibility() == View.VISIBLE) {
					return;
				} else {
					// 显示删除对话框
					showConfirmDialog(ID_DIALOG_DELETE);
					// deleteOneWord();

				}

			}

		});
	}

	/**
	 * 找到一个答案文字
	 * 
	 * @param index
	 *            当前需要填入答案框的索引
	 * @return
	 */
	private WordButton findIsAnswerWord(int index) {
		WordButton buf = null;
		for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
			buf = mAllWords.get(i);
			if (buf.mWordString.equals(""
					+ mCurrentSong.getNameCharacters()[index])) {
				return buf;
			}
		}

		return null;
	}

	/**
	 * 提示文字
	 */
	private void tipAnswer() {
		boolean tipWord = false;
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				if (!handleCoins(-getTipWordCoins())) {
					// 金币数量不够时提示对话框
					showConfirmDialog(ID_DIALOG_LACK);
					return;
				}
				// 根据当前的答案框的条件来选择对应的文字并填入

				onWordButtonClick(findIsAnswerWord(i));
				tipWord = true;

				break;
			}
		}
		// 没有找到可以填充的答案
		if (!tipWord) {
			// 闪烁文字提示用户
			sparkWords();
		}
	}

	/**
	 * 处理提示按键事件
	 */
	private void handleTipAnswer() {
		ImageButton button = (ImageButton) findViewById(R.id.btn_tip_answer);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mPassView.getVisibility() == View.VISIBLE) {

					return;
				} else {

					showConfirmDialog(ID_DIALOG_TIP);
				}
			}
		});
	}

	/**
	 * 微信分享
	 * 
	 * @param flag
	 * @param desc
	 * @param title
	 */
	private void wechatShare(int flag, String desc, String title) {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = getResources().getString(R.string.weixin_url);
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;
		msg.description = desc;
		Bitmap thumb = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon);
		msg.setThumbImage(thumb);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession
				: SendMessageToWX.Req.WXSceneTimeline;
		wxApi.sendReq(req);
	}

}
