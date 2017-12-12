package com.paulz.carinsurance.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.IOSDialogUtil;
import com.core.framework.util.StringUtil;
import com.google.gson.Gson;
import com.paulz.carinsurance.BuildConfig;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.base.BaseFragment;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppStatic;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.Banner;
import com.paulz.carinsurance.model.IdCard;
import com.paulz.carinsurance.model.MsgUnread;
import com.paulz.carinsurance.model.wrapper.MsgWraper;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.permissiongen.internal.PermissionUtil;
import com.paulz.carinsurance.ui.AddCarInfoActivity;
import com.paulz.carinsurance.ui.MsgCenterActivity;
import com.paulz.carinsurance.ui.RecordActivity;
import com.paulz.carinsurance.ui.SelectCarNumberActivity;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.ImageUtil;
import com.paulz.carinsurance.view.banner.AutoScollBanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;


/**
 * 首页
 *
 * @author paulz
 */
@RuntimePermissions
public class HomeFragment extends BaseFragment {

    private final static int TAKE_PHOTO = 1;// 拍照
    private final static int TAKE_PICTURE = 2;// 本地获取
    private final static int TAKE_CROP = 3;// 裁剪


    @BindView(R.id.banner_view)
    AutoScollBanner bannerView;
    @BindView(R.id.tv_car_id)
    TextView tvCarId;
    @BindView(R.id.tv_msg_count)
    TextView tvMsgCount;
    @BindView(R.id.et_car_number)
    EditText etCarNumber;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.check_box)
    CheckBox checkBox;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_id_number)
    EditText etIdNumber;
    @BindView(R.id.btn_submit)
    TextView btnSubmit;
    Unbinder unbinder;
    private boolean isLoading;


    @Override
    public void onResume() {

        super.onResume();
        loadMsgCount();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void heavyBuz() {

    }


    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setView(inflater, R.layout.fragment_home, false);
        unbinder = ButterKnife.bind(this, baseLayout);
        return baseLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadBaseData();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick ({R.id.tv_record,R.id.tv_car_id,R.id.btn_submit,R.id.iv_camera,R.id.tv_msg_count,R.id.btn_msg})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_record:
                RecordActivity.invoke(getActivity());
                break;
            case R.id.tv_car_id:
                String id=tvCarId.getText().toString();
                SelectCarNumberActivity.invoke(this,id);
                break;
            case R.id.btn_submit:
                submit();
                break;
            case R.id.iv_camera:
                showPhotoWindow();
                break;
            case R.id.tv_msg_count:
            case R.id.btn_msg:
                MsgCenterActivity.invoke(getActivity(),0);
        }
    }

    @OnCheckedChanged(R.id.check_box)
    public void checkToNoCarid(boolean isChecked){
        if(isChecked){
            etCarNumber.setEnabled(false);
            tvCarId.setVisibility(View.GONE);
            etCarNumber.setHint("新车未上牌");
            etCarNumber.setText("");

        }else {
            etCarNumber.setEnabled(true);
            tvCarId.setVisibility(View.VISIBLE);
            etCarNumber.setHint("请输入车牌号");

        }

    }


    private void loadData(HomeData data){
        if(data==null||AppUtil.isEmpty(data.adimg)){
            bannerView.showEmptyBanner();
        }else {
            bannerView.showBannerViews(data.adimg);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK&&requestCode==SelectCarNumberActivity.REQUEST_CODE){
            tvCarId.setText(data.getStringExtra("car_number"));
        }else if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO:
                    HomeFragmentPermissionsDispatcher.showStorageWithCheck(HomeFragment.this,requestCode,data);
                    break;
                case TAKE_PICTURE:
                    HomeFragmentPermissionsDispatcher.showStorageWithCheck(HomeFragment.this,requestCode,data);
                    break;

            }

        }
    }

    private void loadBaseData(){
        ParamBuilder params=new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_HOME_DATA), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(status==200){
                    BaseObject<HomeData> obj=GsonParser.getInstance().parseToObj(result,HomeData.class);
                    if(obj!=null&&obj.status==BaseObject.STATUS_OK){
                        loadData(obj.data);
                    }
                    if(obj!=null&&obj.status==BaseObject.STATUS_OK){
                        if(obj==null||obj.data==null||obj.data.firstcarnumber==null||obj.data.firstcarnumber.length()!=1){
                            tvCarId.setText("川A");
                        }else {
                            tvCarId.setText(obj.data.firstcarnumber+"A");
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            loadMsgCount();
        }
    }

    private void loadMsgCount(){
        ParamBuilder params=new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_MSG_UNREAD), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(status==200){
                    BaseObject<MsgUnread> obj=GsonParser.getInstance().parseToObj(result,MsgUnread.class);
                    if(obj!=null&&obj.status==BaseObject.STATUS_OK){
                        if(obj.data.total>99){
                            tvMsgCount.setText("99+");
                            tvMsgCount.setVisibility(View.VISIBLE);
                        }else if(obj.data.total>0){
                            tvMsgCount.setText(obj.data.total);
                            tvMsgCount.setVisibility(View.VISIBLE);
                        }else {
                            tvMsgCount.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

    }



    public class HomeData{
        String firstcarnumber;
        List<Banner> adimg;
    }


    private void submit(){
        String carId1=tvCarId.getText().toString();
        String carId2=etCarNumber.getText().toString().trim();
        String idNumber=etIdNumber.getText().toString().trim();
        String name=etName.getText().toString().trim();
        boolean noCarid=checkBox.isChecked();
        if(!noCarid){
            if(AppUtil.isNull(carId1)){
                AppUtil.showToast(getActivity(),"请选择车牌");
                return;
            }
            if(AppUtil.isNull(carId2)){
                AppUtil.showToast(getActivity(),"请输入车牌号");
                return;
            }
            if(carId2.length()<5){
                AppUtil.showToast(getActivity(),"请输入正确车牌号");
                return;
            }
        }

        if(AppUtil.isNull(name)){
            AppUtil.showToast(getActivity(),"请输入名字");
            return;
        }
        if(name.length()<2){
            AppUtil.showToast(getActivity(),"请输入正确名字");
            return;
        }
        if(!StringUtil.isMatchingChiness(name)){
            AppUtil.showToast(getActivity(),"请输入中文");
            return;
        }
        if(AppUtil.isNull(idNumber)){
            AppUtil.showToast(getActivity(),"请输入身份证");
            return;
        }else if(!StringUtil.isMatchingPersonId(idNumber)){
            AppUtil.showToast(getActivity(),"请填入正确身份证号");

            return;
        }
        DialogUtil.showDialog(lodDialog);

        ParamBuilder params=new ParamBuilder();
        HttpRequester requester=new HttpRequester();
        requester.getParams().put("sfz",idNumber);
        if(noCarid){
            requester.getParams().put("isreg","1");
        }else {
            requester.getParams().put("carnum",carId2);
            requester.getParams().put("fnum",carId1);
            requester.getParams().put("isreg","0");
        }
        requester.getParams().put("name",name);

        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_TEMP_SUBMIT_CAR_INFO), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(!getActivity().isFinishing())DialogUtil.dismissDialog(lodDialog);
                if(status==200){
                    BaseObject<Object> obj=GsonParser.getInstance().parseToObj(result,Object.class);
                    if(obj!=null&&obj.status==BaseObject.STATUS_OK){
                        AddCarInfoActivity.invoke(getActivity());
                    }else if(obj!=null){
                        AppUtil.showToast(getActivity(),obj.msg);
                    }else {
                        AppUtil.showToast(getActivity(),"提交失败");
                    }
                }
            }
        },requester, DESUtil.SECRET_DES);
    }


    private void showPhotoWindow() {
        new IOSDialogUtil(getActivity()).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem("拍照", IOSDialogUtil.SheetItemColor.Black, new IOSDialogUtil.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        HomeFragmentPermissionsDispatcher.showCameraWithCheck(HomeFragment.this);
                    }
                }).addSheetItem("本地获取", IOSDialogUtil.SheetItemColor.Black, new IOSDialogUtil.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                Intent intent2 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent2.setType("image/*");
                startActivityForResult(intent2, TAKE_PICTURE);
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
        requester.getParams().put("typeId","2");
        requester.getParams().put("format","json");
        NetworkWorker.getInstance().post("http://netocr.com/api/recog.do", new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(!getActivity().isFinishing())DialogUtil.dismissDialog(lodDialog);
                if(status==200){
                    Gson gson=new Gson();
                    IdCard card=gson.fromJson(result, IdCard.class);
                    if(card!=null&&card.success()){
                        etIdNumber.setText(card.getId());
                        etName.setText(card.getName());
                    }else {
                        
                    }
                }

            }
        },requester);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        HomeFragmentPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void showCamera(){
        File dirfile = new File(getActivity().getFilesDir(),"images");
        if (!dirfile.exists()) {
            dirfile.mkdirs();
        }
        File file = new File(dirfile,"123.jpg");
        Uri uri=FileProvider.getUriForFile(getActivity(),"com.paulz.carinsurance.fileprovider",file);
        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent1.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
        Toast.makeText(getActivity(), "您已经禁用拍照功能，请到系统设置开启权限",Toast.LENGTH_SHORT).show();
    }


    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showStorage(int requestCode, Intent data){

        switch (requestCode) {
            case TAKE_PHOTO:
                File dir = new File(getActivity().getFilesDir(),"images") ;
                String mFilePath = new File(dir,"123.jpg").getAbsolutePath();
                File file = ImageUtil.compressImage(new File(mFilePath));
                decodePic(file);
                break;
            case TAKE_PICTURE:
                Uri imgUri = data.getData();
                File imgFile = new File(ImageUtil.getRealPathFromURI(getActivity(), imgUri));
                imgFile=ImageUtil.compressImage(imgFile);
                decodePic(imgFile);
                break;

        }

    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForStorage(final PermissionRequest request) {
        request.proceed();

    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showNeverAskForStorage() {
        Toast.makeText(getActivity(), "您已经禁用存储功能，请到系统设置开启权限",Toast.LENGTH_SHORT).show();
    }


}
