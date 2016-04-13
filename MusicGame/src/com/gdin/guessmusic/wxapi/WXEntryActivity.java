package com.gdin.guessmusic.wxapi;


import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/** 
 * 微信分享回调 
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;
	private final static String TAG = "weixin";

	// APP_ID
	public static final String APP_ID = "wxb812debeb1aff213";

	public static boolean shareSuccess = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		api = WXAPIFactory.createWXAPI(this, APP_ID, false);
		api.registerApp(APP_ID);
		api.handleIntent(getIntent(), this);

	}

	@Override
	public void onReq(BaseReq arg0) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.i(TAG, "resp.errCode:" + resp.errCode + ",resp.errStr:"
				+ resp.errStr);

		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			shareSuccess = true;
			new AlertDialog.Builder(this)
					.setMessage("分享成功！获得50金币！")
					.setPositiveButton("确 认",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {

									WXEntryActivity.this.finish();
								}
							}).show();
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			this.finish();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			this.finish();
			break;
		}
	}

}