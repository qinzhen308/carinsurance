package com.paulz.carinsurance.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.core.framework.image.ImgUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.IOSDialogUtil;
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

/**
 * Created by pualbeben on 17/5/21.
 * 用户信息
 */
public class TeamInfoActivity extends BaseActivity {

    private final static int TAKE_PHOTO = 1;// 拍照
    private final static int TAKE_PICTURE = 2;// 本地获取
    private final static int TAKE_CROP = 3;// 裁剪
    @BindView(R.id.label_name)
    TextView labelName;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @BindView(R.id.layout_avatar)
    RelativeLayout layoutAvatar;
    @BindView(R.id.et_slogan)
    EditText etSlogan;
    @BindView(R.id.et_describ)
    EditText etDescrib;
    @BindView(R.id.label_team_manager)
    TextView labelTeamManager;
    @BindView(R.id.et_team_manager)
    EditText etTeamManager;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_company_name)
    EditText etCompanyName;
    @BindView(R.id.et_position)
    EditText etPosition;
    @BindView(R.id.btn_belong)
    TextView btnBelong;
    @BindView(R.id.btn_submit)
    TextView btnSubmit;
    @BindView(R.id.btn_years)
    TextView btnYears;
    @BindView(R.id.tv_label_title)
    TextView tvLabelTitle;
    @BindView(R.id.btn_fee_rate)
    TextView btnFeeRate;
    @BindView(R.id.btn_order_counts)
    TextView btnOrderCounts;


    private File avatar;
    private String mFilePath;


    PageData mData;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("id");
        initView();
        setListener();
        initData();
    }

    private void setListener() {

    }

    private void initView() {
        setActiviyContextView(R.layout.activity_team_info, false, true);
        ButterKnife.bind(this);
       /* if (AppStatic.getInstance().getmUserInfo() != null) {
            Image13Loader.getInstance().loadImage(AppUrls.getInstance().BASE_IMG_URL + AppStatic.getInstance().getmUserInfo().member_avatar, ivAvatar, R.drawable.user2);
        }*/
       if(AppStatic.getInstance().getmUserInfo().teamtype==0){//我是业务员
           setTitleTextRightText("", "团队信息", "去邀请", true);
           tvLabelTitle.setText("团队信息");
           btnSubmit.setVisibility(View.GONE);
           btnBelong.setVisibility(View.GONE);
       }else if(AppStatic.getInstance().getmUserInfo().teamtype==1){//我是团队管理人
           if(AppUtil.isNull(id)){
               //团队管理人查看自己资料
               setTitleTextRightText("", "团队资料", "团队费用比例", true);
               tvLabelTitle.setText("团队信息");
           }else {
               //团队管理人查看所属机构资料
               setTitleText("", "机构资料",0 , true);
               setTitleTextRightText("", "机构资料", "机构费用比例", true);
               tvLabelTitle.setText("机构信息");
           }
       } else if(AppStatic.getInstance().getmUserInfo().teamtype==2){//我是机构管理人
           if(AppUtil.isNull(id)){
               //机构管理人查看自己的机构资料
               setTitleTextRightText("", "机构资料", "团队费用比例", true);
               tvLabelTitle.setText("机构信息");
           }else {
               //构管理人查看该机构的团队资料
               setTitleText("", "团队信息",0 , true);
               btnFeeRate.setVisibility(View.VISIBLE);
               btnOrderCounts.setVisibility(View.VISIBLE);
           }
       }

    }

    @Override
    public void onRightClick() {
        if(AppStatic.getInstance().getmUserInfo().teamtype==0){
            CommonWebActivity.invoke(this,"https://www.baidu.com","去邀请");
        }else{
            lookRate();
        }

    }

    private void lookRate(){

        final CommonDialog dialog = new CommonDialog(this);
        StringBuilder sb = new StringBuilder();
        sb.append(mData.crate);
        for (String b : mData.brate) {
            sb.append("\n").append(b);
        }
        dialog.setDesc(sb.toString());
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void showWorkYearsDialog(){
        final String[] yeas={"5年以下","5-10年","10-15年","15年以上"};
        IOSDialogUtil iosDialogUtil=new IOSDialogUtil(this);
        IOSDialogUtil.OnSheetItemClickListener listener=new IOSDialogUtil.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                btnYears.setText(yeas[which-1]);
            }
        };
        String cur=btnYears.getText().toString();
        for (int i=0;i<yeas.length;i++){
            iosDialogUtil.addSheetItem(yeas[i], yeas[i].equals(cur)?IOSDialogUtil.SheetItemColor.Blue:IOSDialogUtil.SheetItemColor.Black, listener);
        }
        iosDialogUtil.builder();
        iosDialogUtil.show();
    }


    private void handleData() {

        labelName.setText(mData.store_title);
        etName.setText(mData.store_name);
        etSlogan.setText(mData.call);
        etTeamManager.setText(mData.principal);
        etPosition.setText(mData.position);
        etPhone.setText(mData.tel);
        etDescrib.setText(mData.introduce);
        etCompanyName.setText(mData.company);
        if (!AppUtil.isNull(mData.agcid) && Integer.valueOf(mData.agcid) > 0) {
            btnBelong.setText("归属"+mData.agcname);
            btnBelong.setVisibility(View.VISIBLE);

        } else {
            btnBelong.setVisibility(View.GONE);

        }

        btnYears.setText(mData.years);

        Image13Loader.getInstance().loadImage(AppUrls.getInstance().BASE_IMG_URL + mData.avatar, ivAvatar, R.drawable.user2);

        setEditable(mData.edit == 1);

    }

    private void setEditable(boolean editable) {
//        etName.setEnabled(editable);
        etCompanyName.setEnabled(editable);
        etDescrib.setEnabled(editable);
//        etPhone.setEnabled(editable);
        etPosition.setEnabled(editable);
        etSlogan.setEnabled(editable);
//        etTeamManager.setEnabled(editable);
//        layoutAvatar.setEnabled(editable);
        btnYears.setEnabled(editable);
        btnSubmit.setVisibility(editable ? View.VISIBLE : View.GONE);
    }


    @OnClick({R.id.btn_belong, R.id.btn_submit, R.id.btn_years,R.id.btn_fee_rate,R.id.btn_order_counts})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_belong:
                TeamInfoActivity.invoke(this,mData.agcid);
                break;
            case R.id.btn_submit:
                modifyInfo();
                break;
            case R.id.btn_years:
                showWorkYearsDialog();
                break;
            case R.id.btn_fee_rate:
                lookRate();
                break;
            case R.id.btn_order_counts:
                TeamAchievementDetailActivity.invoke(this,id);
                break;
        }
    }


    private void modifyInfo() {
        String company = etCompanyName.getText().toString().trim();
        if (company.length() == 0) {
            AppUtil.showToast(this, "公司名称不能为空");
            return;
        }
        if (company.length() > 20) {
            AppUtil.showToast(this, "公司名称不能超过20个字");
            return;
        }

        String introduce = etDescrib.getText().toString().trim();
        if (introduce.length() == 0) {
            AppUtil.showToast(this, "简介不能为空");
            return;
        }


        String call = etSlogan.getText().toString().trim();
        if (call.length() == 0) {
            AppUtil.showToast(this, "口号不能为空");
            return;
        }
        String position = etPosition.getText().toString().trim();
        if (position.length() == 0) {
            AppUtil.showToast(this, "职位不能为空");
            return;
        }

        String years = btnYears.getText().toString().trim();
        if (years.length() == 0) {
            AppUtil.showToast(this, "请选择从业年限");
            return;
        }

        DialogUtil.showDialog(lodDialog);
        AppUtil.hideSoftInputMethod(this, etName);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester = new HttpRequester();
        requester.getParams().put("company", company);
        requester.getParams().put("introduce", introduce);
        requester.getParams().put("call",call);
        requester.getParams().put("position",position );
        requester.getParams().put("years", years);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ORGANIZATION_INFO_EDIT), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<String> object = GsonParser.getInstance().parseToObj(result, String.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        AppUtil.showToast(getApplicationContext(), "修改成功");
                    } else {
                        AppUtil.showToast(getApplicationContext(), object != null ? object.msg : "修改失败");

                    }
                }
            }
        }, requester, DESUtil.SECRET_DES);
    }


    private void modifyAvatar(File file) {

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester = new HttpRequester();
        requester.getParams().put("img", file);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_MODIFY_AVATAR), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<String> object = GsonParser.getInstance().parseToObj(result, String.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        AppUtil.showToast(getApplicationContext(), "修改成功");
                    } else {
                        AppUtil.showToast(getApplicationContext(), object != null ? object.msg : "修改失败");

                    }
                }
            }
        }, requester);
    }

    private void modifyAvatar(Bitmap avatar) {

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester = new HttpRequester();
        requester.getParams().put("img", "data:image/jpeg;base64," + Base64.encode(ImgUtil.generateByteArray(avatar, 100)));
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_MODIFY_AVATAR), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<String> object = GsonParser.getInstance().parseToObj(result, String.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        AppUtil.showToast(getApplicationContext(), "修改成功");
                    } else {
                        AppUtil.showToast(getApplicationContext(), object != null ? object.msg : "修改失败");

                    }
                }
            }
        }, requester);
    }

    private void showPhotoWindow() {
        new IOSDialogUtil(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem("拍照", IOSDialogUtil.SheetItemColor.Black, new IOSDialogUtil.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
//                        UserInfoActivityPermissionsDispatcher.showCameraWithCheck(TeamInfoActivity.this);

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


    private void initData() {

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        params.append("sid", id);
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_TEAM_INFO), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<PageData> object = GsonParser.getInstance().parseToObj(result, PageData.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        mData = object.data;
                        handleData();
                    } else {
                        AppUtil.showToast(getApplication(), "获取失败");
                        finish();
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
//                        UserInfoActivityPermissionsDispatcher.showStorageWithCheck(this, requestCode, data);
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
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
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


    public static void invoke(Context context, String id) {
        Intent intent = new Intent(context, TeamInfoActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }


    @NeedsPermission(Manifest.permission.CAMERA)
    void showCamera() {
        File dirfile = new File(getFilesDir(), "images");
        if (!dirfile.exists()) {
            dirfile.mkdirs();
        }
        File file = new File(dirfile, "123.jpg");
        Uri uri = FileProvider.getUriForFile(this, "com.paulz.carinsurance.fileprovider", file);
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
    void showStorage(int requestCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                File dir = new File(getFilesDir(), "images");
                mFilePath = new File(dir, "123.jpg").getAbsolutePath();
                mFilePath = ImageUtil.bitmap2File(mFilePath, new Date().getTime() + ".jpg");

                File file = new File(mFilePath);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (Exception e) {
                    }
                }
                startPhotoZoom(FileProvider.getUriForFile(this, "com.paulz.carinsurance.fileprovider", file), 100);
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
        Toast.makeText(this, "您已经禁用存储功能，请到系统设置开启权限", Toast.LENGTH_SHORT).show();
    }



    private class PageData {
        int edit;
        String store_title;
        String store_name;
        String recommonurl;
        String agcid;
        String crate;
        String[] brate;
        String avatar;
        String call;
        String introduce;
        String principal;
        String tel;
        String company;
        String position;
        String years;
        String agcname;

    }

}
