package com.paulz.carinsurance.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.IOSDialogUtil;
import com.core.framework.util.StringUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppStatic;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.Image13Loader;
import com.paulz.carinsurance.utils.ImageUtil;

import java.io.File;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by pualbeben on 17/5/21.
 * 实名认证
 */
@RuntimePermissions
public class NameVerifyActivity extends BaseActivity {
    private final static int TAKE_PHOTO = 1;// 拍照
    private final static int TAKE_PICTURE = 2;// 本地获取
    private final static int TAKE_CROP = 3;// 裁剪


    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_id)
    EditText etId;
    @BindView(R.id.iv_img1)
    ImageView ivImg1;
    @BindView(R.id.iv_img2)
    ImageView ivImg2;

    PageInfo data;
    @BindView(R.id.btn_submit)
    TextView btnSubmit;

    File[] files=new File[2];
    private int curAddPicPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loadData();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_name_verify, false, true);
        setTitleText("", "实名认证", 0, true);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_img2, R.id.iv_img1, R.id.btn_submit})
    public void otherClick(View view) {
        switch (view.getId()) {
            case R.id.iv_img2:
                showPhotoWindow(1);
                break;
            case R.id.iv_img1:
                showPhotoWindow(0);
                break;
            case R.id.btn_submit:
                submit();
                break;
        }
    }

    public static void invoke(Context context) {
        Intent intent = new Intent(context, NameVerifyActivity.class);
        context.startActivity(intent);
    }

    private void loadData() {

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        params.append("id", ""+AppStatic.getInstance().getUser().member_realname);
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_NAME_VERIFY), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<PageInfo> object = GsonParser.getInstance().parseToObj(result, PageInfo.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        data = object.data;
                        handleData();
                    } else {
                        AppUtil.showToast(getApplicationContext(), "加载失败");
                    }
                }
            }
        });

    }

    private void handleData() {
        if (data == null) {
            return;
        }
        etName.setText(data.authenticate_name==null?"":data.authenticate_name);
        etId.setText(data.authenticate_sfz==null?"":data.authenticate_sfz);
        if(AppUtil.isNull(data.authenticate_status)){

        }else if (data.authenticate_status.equals("2")) {
            tvStatus.setText("已认证");
            tvStatus.setTextColor(getResources().getColor(R.color.green_light));
            etName.setEnabled(false);
            etId.setEnabled(false);
            btnSubmit.setVisibility(View.GONE);
            ivImg1.setEnabled(false);
            ivImg2.setEnabled(false);
            etName.setText("*"+data.authenticate_name.substring(1,data.authenticate_name.length()));
            etId.setText(data.authenticate_sfz.substring(0,3)+"***********"+data.authenticate_sfz.substring(data.authenticate_sfz.length()-4,data.authenticate_sfz.length()));
        } else if (data.authenticate_status.equals("1")) {
            tvStatus.setText("认证中");
            tvStatus.setTextColor(getResources().getColor(R.color.base_yellow));
            etName.setEnabled(false);
            etId.setEnabled(false);
            btnSubmit.setVisibility(View.GONE);
            ivImg1.setEnabled(false);
            ivImg2.setEnabled(false);
        } else if(data.authenticate_status.equals("0")){
            tvStatus.setText("认证失败");
            tvStatus.setTextColor(getResources().getColor(R.color.base_red));
            btnSubmit.setText("重新审核");
        }



        Image13Loader.getInstance().loadImage(AppUrls.getInstance().IMG_AUTHEIMG+data.authenticate_fimg,ivImg1);
        Image13Loader.getInstance().loadImage(AppUrls.getInstance().IMG_AUTHEIMG+data.authenticate_rimg,ivImg2);

    }


    public void submit() {
        String name=etName.getText().toString().trim();
        if(name.length()==0){
            AppUtil.showToast(this,"请填入真实姓名");
            return;
        }
        if(name.length()<2){
            AppUtil.showToast(this,"请填入正确真实姓名");
            return;
        }
        if(!StringUtil.isMatchingChiness(name)){
            AppUtil.showToast(this,"真实姓名只能是中文");
            return;
        }
        String id=etId.getText().toString().trim();
        if(id.length()==0){
            AppUtil.showToast(this,"请填写身份证");
            return;
        }

        if(!StringUtil.isMatchingPersonId(id)){
            AppUtil.showToast(this,"请填写正确身份证");
            return;
        }

        if(files[0]==null||TextUtils.isEmpty(files[0].getName())){
            AppUtil.showToast(this,"请选择身份证正面照片");
            return;
        }

        if(files[1]==null||TextUtils.isEmpty(files[1].getName())){
            AppUtil.showToast(this,"请选择身份证反面照片");
            return;
        }

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester = new HttpRequester();
        requester.getParams().put("authenticate_name", name);
        requester.getParams().put("authenticate_sfz", id);
        requester.getParams().put("fimg", files[0]);
        requester.getParams().put("rimg", files[1]);

        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_NAME_VERIFY_SUBMIT), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK) {
                        AppUtil.showToast(getApplicationContext(), "提交成功");
                        finish();
                    } else {
                        AppUtil.showToast(getApplicationContext(), "提交失败");
                    }
                }
            }
        }, requester, DESUtil.SECRET_DES);
    }


    private void showPhotoWindow(final int index) {
        curAddPicPosition=index;
        new IOSDialogUtil(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem("拍照", IOSDialogUtil.SheetItemColor.Black, new IOSDialogUtil.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        NameVerifyActivityPermissionsDispatcher.showCameraWithCheck(NameVerifyActivity.this);
                    }
                }).addSheetItem("本地获取", IOSDialogUtil.SheetItemColor.Black, new IOSDialogUtil.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                Intent intent2 = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent2.setType("image/*");
                startActivityForResult(intent2, TAKE_PICTURE);
            }
        }).show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

                switch (requestCode) {
                    case TAKE_PHOTO:

                    case TAKE_PICTURE:
                        NameVerifyActivityPermissionsDispatcher.showStorageWithCheck(this,requestCode,data);

                        break;

                }
        }
    }


    /**
     * 跳转至系统截图界面进行截图
     *
     * @param data
     */
    private void startPhotoZoom(Uri data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,ImageUtil.temp_img_crop_uri);
        // crop为true时表示显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 1);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 250);
        intent.putExtra("return-data", false);
        startActivityForResult(intent, TAKE_CROP);
    }

    private void setImg(File file, final int position){
        if(file==null||!file.exists()){
            Toast.makeText(this,"图片选取失败，请重试...",Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("crop","set进来  file"+file.getPath()+"--innerPo="+position+"curP"+curAddPicPosition);
        files[position]=file;
        if(position==0){
            Glide.with(this).load(file).error(R.drawable.img_list_default).placeholder(R.drawable.img_list_default).into(ivImg1);
        }else {
            Glide.with(this).load(file).error(R.drawable.img_list_default).placeholder(R.drawable.img_list_default).into(ivImg2);
        }
    }

    private class PageInfo {
        public String authenticate_id;
        public String authenticate_name;
        public String authenticate_sfz;
        public String authenticate_fimg;
        public String authenticate_rimg;
        public String authenticate_res;
        public String authenticate_status;
        public String authenticate_createtime;
        public String authenticate_updatetime;

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        NameVerifyActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void showCamera(){
        File dirfile = new File(this.getFilesDir(),"images");
        if (!dirfile.exists()) {
            dirfile.mkdirs();
        }
        File file = new File(dirfile,"123.jpg");
        Uri uri=FileProvider.getUriForFile(this,"com.paulz.carinsurance.fileprovider",file);
        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent1.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent1, TAKE_PHOTO);

    }

    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForCamera(final PermissionRequest request) {
        request.proceed();

    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    void showDeniedForCamera() {

    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    void showNeverAskForCamera() {
        Toast.makeText(this, "您已经禁用拍照功能，请到系统设置开启权限",Toast.LENGTH_SHORT).show();
    }


    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showStorage(int requestCode, Intent data){
        switch (requestCode) {
            case TAKE_PHOTO:
                File dir = new File(getFilesDir(),"images") ;
                String mFilePath = new File(dir,"123.jpg").getAbsolutePath();
                File file = ImageUtil.compressImage(new File(mFilePath));
                setImg(file,curAddPicPosition);

                break;
            case TAKE_PICTURE:
                Uri imgUri = data.getData();
                File imgFile = new File(ImageUtil.getRealPathFromURI(this, imgUri));
                imgFile=ImageUtil.compressImage(imgFile);
                setImg(imgFile,curAddPicPosition);

                break;

        }
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForStorage(final PermissionRequest request) {
        request.proceed();

    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showNeverAskForStorage() {
        Toast.makeText(this, "您已经禁用存储功能，请到系统设置开启权限",Toast.LENGTH_SHORT).show();
    }

}
