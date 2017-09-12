package com.paulz.carinsurance.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.IOSDialogUtil;
import com.google.gson.Gson;
import com.paulz.carinsurance.common.AppStatic;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.model.CarCard;

import java.io.File;

/**
 * Created by pualbeben on 17/6/29.
 */

public class OCRDecoder {
    public final static int TAKE_PHOTO = 212;// 拍照
    public final static int TAKE_PICTURE = 222;// 本地获取

    Activity activity;
    Dialog lodDialog;
    DCallback callback;

    public OCRDecoder(Activity context, Dialog dialog, DCallback callback){
        lodDialog=dialog;
        activity=context;
        this.callback=callback;
    }

    public void decode(ITakePhoto obj){
        showPhotoWindow(obj);
    }


    private void showPhotoWindow(final ITakePhoto obj) {
        new IOSDialogUtil(activity).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem("拍照", IOSDialogUtil.SheetItemColor.Black, new IOSDialogUtil.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        if(obj!=null)obj.onPhoto();
                    }
                }).addSheetItem("本地获取", IOSDialogUtil.SheetItemColor.Black, new IOSDialogUtil.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                Intent intent2 = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent2.setType("image/*");
                activity.startActivityForResult(intent2, TAKE_PICTURE);
            }
        }).show();
    }



    private void decodePic(File file){
//        AppUtil.showToast(getActivity(),"上传图片-----file="+file+"--------file path="+file==null?"无":file.getPath());
        DialogUtil.showDialog(lodDialog);
        HttpRequester requester=new HttpRequester();
        requester.getParams().clear();
        requester.getParams().put("file",file);
        requester.getParams().put("key", AppStatic.OCRkey);
        requester.getParams().put("secret",AppStatic.OCRSecret);
        requester.getParams().put("typeId","6");
        requester.getParams().put("format","json");
        NetworkWorker.getInstance().post("http://netocr.com/api/recog.do", new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                LogUtil.d("-----ocr decode------"+result);
                if(!activity.isFinishing())DialogUtil.dismissDialog(lodDialog);
                if(status==200){
                    Gson gson=new Gson();
                    CarCard card=gson.fromJson(result, CarCard.class);
                    if(card!=null&&card.success()){
                        if(callback!=null)callback.onFinish(card);

                    }else {
                        if(callback!=null)callback.onFinish(null);
                    }
                }else {
                    if(callback!=null)callback.onFinish(null);
                }

            }
        },requester);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO:
                    File dir = new File(activity.getFilesDir(),"images") ;
                    String mFilePath = new File(dir,"123.jpg").getAbsolutePath();
                    File file = ImageUtil.compressImage(new File(mFilePath));
                    decodePic(file);
                    break;
                case TAKE_PICTURE:
                    Uri imgUri = data.getData();
                    File imgFile = new File(ImageUtil.getRealPathFromURI(activity, imgUri));
                    imgFile=ImageUtil.compressImage(imgFile);
                    decodePic(imgFile);
                    break;

            }
        }
    }

    public interface DCallback{
        void onFinish(CarCard card);
    }


    public void doTakePhoto(){
        File dirfile = new File(activity.getFilesDir(),"images");
        if (!dirfile.exists()) {
            dirfile.mkdirs();
        }
        File file = new File(dirfile,"123.jpg");
        Uri uri= FileProvider.getUriForFile(activity,"com.paulz.carinsurance.fileprovider",file);
        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent1.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent1, TAKE_PHOTO);
    }

    public interface ITakePhoto{
        void onPhoto();
    }

}
