package com.paulz.carinsurance.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.core.framework.image.ImgUtil;
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
import com.paulz.carinsurance.ui.fragment.UserCenterFragment;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.Base64;
import com.paulz.carinsurance.utils.Image13Loader;
import com.paulz.carinsurance.utils.ImageUtil;
import com.paulz.carinsurance.view.CircleImageView;
import com.paulz.carinsurance.view.CommonDialog;

import java.io.File;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by pualbeben on 17/5/21.
 * 用户信息
 */
@RuntimePermissions
public class UserInfoActivity extends BaseActivity {

    private final static int TAKE_PHOTO = 1;// 拍照
    private final static int TAKE_PICTURE = 2;// 本地获取
    private final static int TAKE_CROP = 3;// 裁剪


    @BindView(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.tv_status)
    TextView tvStatus;

    private File avatar;
    private String mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setListener();
    }

    private void setListener() {
        etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_NEXT){
                    modifyName();
                    return true;
                }
                return false;
            }
        });
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_user_info, false, true);
        ButterKnife.bind(this);
        setTitleText("", "账户信息", 0, true);
        tvStatus.setText(AppStatic.getInstance().getmUserInfo()!=null&&AppStatic.getInstance().getmUserInfo().member_realname>0?"已认证":"未认证");
        etName.setText(AppStatic.getInstance().getmUserInfo()!=null?AppStatic.getInstance().getmUserInfo().member_nickname:"[新用户]");
        if(AppStatic.getInstance().getmUserInfo()!=null){
            Image13Loader.getInstance().loadImage(AppUrls.getInstance().BASE_IMG_URL + AppStatic.getInstance().getmUserInfo().member_avatar, ivAvatar, R.drawable.user2);
        }

    }


    @OnClick({R.id.layout_safe, R.id.layout_verify,R.id.layout_avatar,R.id.layout_address,R.id.layout_bank_card,R.id.btn_logout})
    public void otherClick(View view) {
        switch (view.getId()) {
            case R.id.layout_safe:
                SafeActivity.invoke(this);
                break;
            case R.id.layout_verify:
                NameVerifyActivity.invoke(this);
                break;
            case R.id.layout_avatar:
                showPhotoWindow();

                break;
            case R.id.layout_address:
                AddressListActivity.invoke(this,true);
                break;
            case R.id.layout_bank_card:
                BankCardActivity.invoke(this);
                break;
            case R.id.btn_logout:
                showLogoutDialog();
                break;

        }
    }

    private void modifyName(){
        String name=etName.getText().toString().trim();
        if(name.length()==0){
            AppUtil.showToast(this,"名字不能为空");
            return;
        }
        if(name.length()>10){
            AppUtil.showToast(this,"名字不能超过10个字");
            return;
        }
        if (name.length() <2) {
            AppUtil.showToast(getApplicationContext(), "请输入正确姓名");
            return;
        }
        if (!StringUtil.isMatchingChiness(name)) {
            AppUtil.showToast(getApplicationContext(), "请输入中文名字");
            return;
        }
        DialogUtil.showDialog(lodDialog);
        AppUtil.hideSoftInputMethod(this,etName);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester=new HttpRequester();
        requester.getParams().put("nickname",name);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_MODIFY_NAME), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<String> object = GsonParser.getInstance().parseToObj(result, String.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        AppUtil.showToast(getApplicationContext(),"修改成功");
                    } else {
                        AppUtil.showToast(getApplicationContext(),object!=null?object.msg:"修改失败");

                    }
                }
            }
        },requester, DESUtil.SECRET_DES);
    }


    private void modifyAvatar(File file){

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester=new HttpRequester();
        requester.getParams().put("img",file);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_MODIFY_AVATAR), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<String> object = GsonParser.getInstance().parseToObj(result, String.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        AppUtil.showToast(getApplicationContext(),"修改成功");
                    } else {
                        AppUtil.showToast(getApplicationContext(),object!=null?object.msg:"修改失败");

                    }
                }
            }
        },requester);
    }

    private void modifyAvatar(Bitmap avatar){

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester=new HttpRequester();
        requester.getParams().put("img","data:image/jpeg;base64,"+Base64.encode(ImgUtil.generateByteArray(avatar,100)));
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_MODIFY_AVATAR), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<String> object = GsonParser.getInstance().parseToObj(result, String.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        AppUtil.showToast(getApplicationContext(),"修改成功");
                    } else {
                        AppUtil.showToast(getApplicationContext(),object!=null?object.msg:"修改失败");

                    }
                }
            }
        },requester);
    }

    private void showPhotoWindow() {
        new IOSDialogUtil(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem("拍照", IOSDialogUtil.SheetItemColor.Black, new IOSDialogUtil.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        UserInfoActivityPermissionsDispatcher.showCameraWithCheck(UserInfoActivity.this);

                    }
                }).addSheetItem("本地获取", IOSDialogUtil.SheetItemColor.Black, new IOSDialogUtil.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                Intent intent2 = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent2.setType("image/*");
                startActivityForResult(intent2, TAKE_PICTURE);
            }
        }).show();
    }


    private void showLogoutDialog(){
        CommonDialog dialog=new CommonDialog(this);
        dialog.setDesc("确定退出？");
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                logout();
            }
        });
        dialog.show();
    }


    private void logout(){

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_LOGOUT), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK ) {
                        AppUtil.showToast(getApplicationContext(), "退出成功");
//                        AppStatic.getInstance().justClearLoginStatus();
                        AppStatic.getInstance().clearLoginStatus();
//                        MainActivity.invoke(UserInfoActivity.this,true);
                    } else {
                        AppUtil.showToast(getApplicationContext(), object != null ? object.msg : "退出成功");

                    }
                }
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String sdCardState = Environment.getExternalStorageState();
            if (!sdCardState.equals(Environment.MEDIA_MOUNTED)) {
                return;
            } else if (requestCode == 222) {
                finish();
            } else {

                switch (requestCode) {
                    case TAKE_PHOTO:

                    case TAKE_CROP:// // 裁剪成功后显示图片

                    case TAKE_PICTURE:
                    UserInfoActivityPermissionsDispatcher.showStorageWithCheck(this,requestCode,data);
                        break;

                }

            }
        }
    }

    /**
     * 跳转至系统截图界面进行截图
     *
     * @param data
     * @param size
     */
    private void startPhotoZoom(Uri data, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(data, "image/*");
        // crop为true时表示显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, TAKE_CROP);
    }


    public static void invoke(Context context) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UserInfoActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void showCamera(){
        File dirfile = new File(getFilesDir(),"images");
        if (!dirfile.exists()) {
            dirfile.mkdirs();
        }
        File file = new File(dirfile,"123.jpg");
        Uri uri= FileProvider.getUriForFile(this,"com.paulz.carinsurance.fileprovider",file);
        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent1.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent1, TAKE_PHOTO);
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForCamera(final PermissionRequest request) {
        request.proceed();

    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    void showNeverAskForCamera() {
        Toast.makeText(this, "您已经禁用拍照功能，请到系统设置开启权限", Toast.LENGTH_SHORT).show();
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showStorage(int requestCode, Intent data){
        switch (requestCode) {
            case TAKE_PHOTO:
                File dir = new File(getFilesDir(),"images") ;
                mFilePath = new File(dir,"123.jpg").getAbsolutePath();
                mFilePath = ImageUtil.bitmap2File(mFilePath, new Date().getTime() + ".jpg");

                File file = new File(mFilePath);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (Exception e) {
                    }
                }
                startPhotoZoom(FileProvider.getUriForFile(this,"com.paulz.carinsurance.fileprovider",file), 100);
                break;
            case TAKE_CROP:// // 裁剪成功后显示图片

                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Bitmap bitmap = bundle.getParcelable("data");
                    if (bitmap != null) {

                        avatar = ImageUtil.saveImag(bitmap, new Date().getTime() + ".jpg");
                        ivAvatar.setImageBitmap(bitmap);
//							putAvatar(bitmap, pFile);
                        modifyAvatar(bitmap);
                    }

                }
                break;
            case TAKE_PICTURE:

                Uri imgUri_2 = data.getData();

                startPhotoZoom(imgUri_2, 100);
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
