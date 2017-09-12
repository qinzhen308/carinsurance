package com.paulz.carinsurance.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.base.BaseWebActivity;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.common.GlobeFlags;
import com.paulz.carinsurance.listener.JSInvokeJavaInterface;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.FileUtils;
import com.paulz.carinsurance.utils.Image13Loader;
import com.paulz.carinsurance.utils.ImageUtil;
import com.paulz.carinsurance.view.CommonDialog;
import com.sina.weibo.sdk.utils.ImageUtils;

import java.io.File;

public class CommonWebActivity extends BaseWebActivity{
	
	String pic_url;
	String wap_url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
	}
	@SuppressLint("JavascriptInterface")
	private void initView(){
		if(title==null){
			setActiviyContextView(R.layout.activity_fishion_detail_web, true, false);
		}else {
			setActiviyContextView(R.layout.activity_fishion_detail_web, true, true);
			setTitleText("", title, 0, true);
		}
		mWebView=(WebView)findViewById(R.id.web_fishion);
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		if(width > 650)
		{
			this.mWebView.setInitialScale(190);
		}else if(width > 520)
		{
			this.mWebView.setInitialScale(160);
		}else if(width > 450)
		{
			this.mWebView.setInitialScale(140);
		}else if(width > 300)
		{
			this.mWebView.setInitialScale(120);
		}else
		{
			this.mWebView.setInitialScale(100);
		}
		WebSettings webSettings = mWebView.getSettings();
//		webSettings.setSupportZoom(false);
//		webSettings.setBuiltInZoomControls(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setBlockNetworkImage(false);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//		mWebView.requestFocus();
		webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
		mWebView.addJavascriptInterface(new JSInvokeJavaInterface(this,mWebView),"hkb");

		load(wap_url, false);
		mWebView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				WebView.HitTestResult result = ((WebView) v).getHitTestResult();
				int type = result.getType();
				if(type==WebView.HitTestResult.IMAGE_TYPE){
					String url=result.getExtra();
					if(!AppUtil.isNull(url)){
						showSaveDialog(url);
					}
					return true;
				}
				return false;
			}
		});
	}
	
	private void setExtra(){
		Intent intent=getIntent();
		wap_url=intent.getStringExtra(GlobeFlags.FLAG_FISHION_WAP_URL);
		pic_url=intent.getStringExtra(GlobeFlags.FLAG_FISHION_PIC_URL);
		title=intent.getStringExtra("title");
	}

	/**
	 *
	 * @param context
	 * @param url
	 * @param title  null，不显示titlebar
     */
	public static void invoke(Context context,String url,String title){
		Intent intent = new Intent(context,CommonWebActivity.class);
		intent.putExtra(GlobeFlags.FLAG_FISHION_WAP_URL, url);
		if(title!=null){
			intent.putExtra("title", title);
		}
		context.startActivity(intent);
	}


	private void showSaveDialog(final String url){
		CommonDialog dialog=new CommonDialog(this);
		dialog.setDesc("确定保存图片？");
		dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
			@Override
			public void onClick() {
				saveImage(url);
			}
		});
		dialog.show();
	}

	private void saveImage(String url){
		Glide.with(this).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
			@Override
			public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//				File file=ImageUtil.saveImag(resource,"web_img_"+System.currentTimeMillis()+".png");
				File file=ImageUtil.saveImageToGallery(CommonWebActivity.this,resource);
				if(file!=null&&!AppUtil.isNull(file.getPath())){
					AppUtil.showToast(getApplicationContext(),"图片已保存到："+file.getPath());
				}else {
					AppUtil.showToast(getApplicationContext(),"图片已保存失败");
				}
			}
		});
	}



}
