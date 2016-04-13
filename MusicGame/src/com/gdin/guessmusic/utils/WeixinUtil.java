package com.gdin.guessmusic.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.gdin.guessmusic.MainActivity;
import com.gdin.guessmusic.R;
import com.gdin.guessmusic.utils.Util;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WeixinUtil {
	// c3339657df3cc33d66ed6e8386474857
	private final static String APP_ID = "wxb812debeb1aff213";
	
	private static final int THUMB_SIZE = 150;
	private IWXAPI api;

	private Context mContext;
	
	private static WeixinUtil mInstance;

	// 将应用APP_ID注册到微信
	private WeixinUtil(Context context) {
		api = WXAPIFactory.createWXAPI(context, APP_ID, true);
		api.registerApp(APP_ID);
	}

	public static WeixinUtil getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new WeixinUtil(context);
		}
		return mInstance;
	}
	/**
	 * 发送文本信息到微信
	 * 
	 */
	public void sendRequest(String text,String title,Context context) {
	
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = "http://fx.anzhi.com/share_2346072.html?azfrom=weixincircle&from=timeline&isappinstalled=1";
		WXMediaMessage msg = new WXMediaMessage(webpage);
		
		msg.title = title;
		msg.description = text;
		
		Bitmap thumb = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.icon);
		msg.setThumbImage(thumb);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		// SendMessageToWX.Req.WXSceneSession 发送到微信好友对话
		// SendMessageToWX.Req.WXSceneTimeline 发送到朋友圈
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);
	}

	/**
	 * 发送图片到微信
	 */
	public void sendBitmap(Bitmap bitmap) {
		WXImageObject imgObj = new WXImageObject();

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;

		Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE,
				THUMB_SIZE, true);
		bitmap.recycle();
		msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = "img" + String.valueOf(System.currentTimeMillis());
		req.message = msg;

		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);

	}
}
