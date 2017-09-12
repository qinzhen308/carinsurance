package com.paulz.carinsurance.model;

import android.app.Activity;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class ShareChannel {
	public int id;
	public String name;
	public int icon;

	public ShareChannel(int id,String name,int icon){
		this.id=id;
		this.name=name;
		this.icon=icon;

	}

	public void share(Activity context,String targeUrl,String title,String content,UMShareListener umShareListener){
		new ShareAction(context).setPlatform(SHARE_MEDIA.QQ)
		.setCallback(umShareListener).withText(content)
		.withTitle(title).withTargetUrl(targeUrl)
		.share();
	}

}
