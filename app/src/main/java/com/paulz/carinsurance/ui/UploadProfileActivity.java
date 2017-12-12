package com.paulz.carinsurance.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.IOSDialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.common.recyclerview.CommonRVAdapter;
import com.paulz.carinsurance.common.recyclerview.EventCallback;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.UploadProfileConfig;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.ui.viewmodel.ImageModelDouble;
import com.paulz.carinsurance.ui.viewmodel.ImageModelTip;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.ImageUtil;
import com.paulz.carinsurance.view.CommonDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
 * Created by pualbeben on 17/12/2.
 * 上传证件页面
 */
@RuntimePermissions
public class UploadProfileActivity extends BaseActivity {
    private final static int TAKE_PHOTO = 1;// 拍照
    private final static int TAKE_PICTURE = 2;// 本地获取
    private final static int TAKE_CROP = 3;// 裁剪


    UploadProfileConfig config;

    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    @BindView(R.id.btn_submit)
    TextView btnSubmit;

    CommonRVAdapter mAdapter;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private HashMap<String, UploadProfileConfig.ImageModel> uploadingItems = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loadData();
    }

    private void initView() {
        setContentView(R.layout.activity_upload_profile);
        ButterKnife.bind(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        rvContent.setLayoutManager(layoutManager);
        mAdapter = new CommonRVAdapter(rvContent);
        rvContent.setAdapter(mAdapter);

        mAdapter.setCallback(new EventCallback() {
            @Override
            public void onEvent(int what, Object... object) {
                if (what == EVENT_1) {
                    showPhotoWindow((UploadProfileConfig.ImageModel) object[0]);
                } else if (what == EVENT_2) {
                    showDeleteDialog((UploadProfileConfig.ImageModel) object[0]);

                } else if (what == EVENT_3) {
                    showReplaceDialog((UploadProfileConfig.ImageModel) object[0]);

                }
            }
        });
    }



    @OnClick({R.id.btn_submit,R.id.btn_back, R.id.btn_server1, R.id.btn_server2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_server1:
            case R.id.btn_server2:
                CommonWebActivity.invoke(this, APIUtil.parseGetUrlHasMethod(new ParamBuilder().getParamList(), AppUrls.getInstance().BASE_DOMAIN + "/index.php?s=/Api/tools/customerservice"), "联系客服");
                break;
            case R.id.btn_submit:
                submit();
                break;
        }
    }

    public static void invoke(Context context, String ordersn) {
        Intent intent = new Intent(context, UploadProfileActivity.class);
        intent.putExtra("sn", ordersn);
        context.startActivity(intent);
    }

    private void loadData() {

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        params.append("sn", getIntent().getStringExtra("sn"));
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_UPLOAD_PROFILE_INIT), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<UploadProfileConfig> object = GsonParser.getInstance().parseToObj(result, UploadProfileConfig.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        config = object.data;
                        handleData();
                    } else {
                        AppUtil.showToast(getApplicationContext(), "加载失败");
                    }
                }
            }
        });

    }

    private void handleData() {
        if (config == null) {
            AppUtil.showToast(this, "获取配置失败");
            finish();
            return;
        }
        boolean enable = true;
        if ("1".equals(config.edit)) {
            btnSubmit.setVisibility(View.VISIBLE);
            enable = true;
        } else {
            btnSubmit.setVisibility(View.GONE);
            enable = false;
        }

        List models = new ArrayList();
        models.add(new ImageModelTip(config.message));
        if (AppUtil.isEmpty(config.list)) {
            btnSubmit.setEnabled(false);
        } else {
            for (UploadProfileConfig.ImageGroup group : config.list) {
                if (AppUtil.isEmpty(group.imglist)) {
                    continue;
                }
                models.add(group);
                ImageModelDouble modelDouble = null;
                for (int i = 0; i < group.imglist.size(); i++) {

                    //同步正在上传数据的状态
                    UploadProfileConfig.ImageModel item = group.imglist.get(i);
                    UploadProfileConfig.ImageModel uploadItem = uploadingItems.get(item.id);
                    if (uploadItem != null) {
                        item.uploading = uploadItem.uploading;
                        item.imgFile = uploadItem.imgFile;
                        item.img = uploadItem.img;
                    }
                    if (i % 2 == 0) {
                        modelDouble = new ImageModelDouble();
                        modelDouble.enable = enable;
                        models.add(modelDouble);
                        modelDouble.left = item;
                    } else {
                        modelDouble.right = item;
                    }
                }

            }
        }

        mAdapter.setList(models);
        mAdapter.notifyDataSetChanged();

    }

    private void syncOldData() {


    }

    private void showDeleteDialog(final UploadProfileConfig.ImageModel item) {
        CommonDialog dialog = new CommonDialog(this);
        dialog.setDesc("是否删除该图片");
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                deleteImg(item);
            }
        });
        dialog.show();
    }

    private void showReplaceDialog(final UploadProfileConfig.ImageModel item) {
        CommonDialog dialog = new CommonDialog(this);
        dialog.setDesc("是否重新上传图片");
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                showPhotoWindow(item);
            }
        });
        dialog.show();
    }


    public void submit() {

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester = new HttpRequester();
        requester.getParams().put("sn", config.sn);
        requester.getParams().put("statustag", config.statustag);

        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_UPLOAD_PROFILE_COMMIT), new NetworkWorker.ICallback() {
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

    public void deleteImg(UploadProfileConfig.ImageModel item) {

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester = new HttpRequester();
        requester.getParams().put("id", item.imgid);

        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_UPLOAD_PROFILE_DELETE_IMG), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK) {
                        loadData();
                    } else {
                        AppUtil.showToast(getApplicationContext(), "提交失败");
                    }
                }
            }
        }, requester, DESUtil.SECRET_DES);
    }

    public void addImg(final UploadProfileConfig.ImageModel item) {
        item.uploading = true;
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester = new HttpRequester();
        requester.getParams().put("sn", config.sn);
        requester.getParams().put("tplid", item.id);
        requester.getParams().put("file", item.imgFile);
        uploadingItems.put(item.id, item);

        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_UPLOAD_PROFILE_ADD_IMG), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                uploadingItems.remove(item.id);
                item.uploading = false;
                item.imgFile = null;

                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK) {
//                        item.uploading=false;
                        loadData();
                    } else {
                        mAdapter.notifyDataSetChanged();
                        AppUtil.showToast(getApplicationContext(), "提交失败");
                    }
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }, requester, DESUtil.SECRET_DES);
    }


    UploadProfileConfig.ImageModel tempItem;

    private void showPhotoWindow(final UploadProfileConfig.ImageModel item) {
        tempItem = item;
        new IOSDialogUtil(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem("拍照", IOSDialogUtil.SheetItemColor.Black, new IOSDialogUtil.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        UploadProfileActivityPermissionsDispatcher.showCameraWithCheck(UploadProfileActivity.this);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case TAKE_PHOTO:

                case TAKE_PICTURE:
                    UploadProfileActivityPermissionsDispatcher.showStorageWithCheck(this, requestCode, data);

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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtil.temp_img_crop_uri);
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

    private void setImg(File file, UploadProfileConfig.ImageModel item) {
        if (file == null || !file.exists()) {
            Toast.makeText(this, "图片选取失败，请重试...", Toast.LENGTH_SHORT).show();
            return;
        }
        item.uploading = true;
        item.imgFile = file;
        item.img = "";
        mAdapter.notifyDataSetChanged();
        addImg(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UploadProfileActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void showCamera() {
        File dirfile = new File(this.getFilesDir(), "images");
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

    @OnPermissionDenied(Manifest.permission.CAMERA)
    void showDeniedForCamera() {

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
                String mFilePath = new File(dir, "123.jpg").getAbsolutePath();
                File file = ImageUtil.compressImage(new File(mFilePath));
                setImg(file, tempItem);

                break;
            case TAKE_PICTURE:
                Uri imgUri = data.getData();
                File imgFile = new File(ImageUtil.getRealPathFromURI(this, imgUri));
                imgFile = ImageUtil.compressImage(imgFile);
                setImg(imgFile, tempItem);

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

}
