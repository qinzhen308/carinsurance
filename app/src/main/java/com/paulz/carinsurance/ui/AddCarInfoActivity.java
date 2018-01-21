package com.paulz.carinsurance.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.IOSDialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.AbsMutipleAdapter;
import com.paulz.carinsurance.adapter.ViewHolder;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.CarCard;
import com.paulz.carinsurance.model.InsureAppoint;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.DateUtil;
import com.paulz.carinsurance.utils.OCRDecoder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by pualbeben on 17/5/21.
 * 添加车辆信息
 */
@RuntimePermissions
public class AddCarInfoActivity extends BaseActivity {


    @BindView(R.id.btn_help)
    TextView btnHelp;
    @BindView(R.id.tv_car_code)
    TextView tvCarCode;
    @BindView(R.id.btn_car_type)
    TextView btnCarType;
    @BindView(R.id.btn_site_count)
    TextView btnSiteCount;
    @BindView(R.id.et_engine_id)
    EditText etEngineId;
    @BindView(R.id.btn_regist_date)
    TextView btnRegistDate;
    @BindView(R.id.cb_credit)
    CheckBox cbCredit;
    @BindView(R.id.cb_new)
    CheckBox cbNew;
    @BindView(R.id.btn_new_date)
    TextView btnNewDate;
    @BindView(R.id.layout_is_new)
    LinearLayout layoutIsNew;
    @BindView(R.id.btn_next)
    TextView btnNext;
    @BindView(R.id.tv_daikuan_tip)
    TextView tvDaikuanTip;


    @BindView(R.id.btn_add_appoint)
    View btnAddAppoint;

    @BindView(R.id.lv_appoint)
    ListView lvAppoint;

    InsureAppointAdapter appointAdapter;

    String customer_carmodel_id;
    PageData pageData;
    PageCarInfo allData;
    String car_mode;
    String modelId;

    OCRDecoder ocrDecoder;

    public static boolean isScanVin=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isScanVin=false;
        setExtra();
        initView();
        loadData();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_add_car_info, false, true);
        setTitleText("", "信息填写", 0, true);
        ButterKnife.bind(this);
        init();

        ocrDecoder = new OCRDecoder(this, lodDialog, new OCRDecoder.DCallback() {
            @Override
            public void onFinish(CarCard card) {
                if (card != null) {
                    setCarInfo(card);
                } else {
                    AppUtil.showToast(getApplicationContext(), "解析失败，请重新选择");
                }
            }
        });
        appointAdapter=new InsureAppointAdapter(this);
        lvAppoint.setAdapter(appointAdapter);
    }


    private void setCarInfo(CarCard card) {
        tvCarCode.setText(card.getVIN());
        etEngineId.setText(card.getEngine());
        btnRegistDate.setText(card.getRegDate());
        //清除车型
        modelId = "";
        car_mode = "";
        btnCarType.setText("");
        isScanVin=true;
    }


    public static void invoke(Context context) {
        Intent intent = new Intent(context, AddCarInfoActivity.class);
        context.startActivity(intent);
    }

    public static void invoke(Context context, String id) {
        Intent intent = new Intent(context, AddCarInfoActivity.class);
        intent.putExtra("extra_id", id);
        context.startActivity(intent);
    }

    private void init() {

        SpannableString sb = new SpannableString("* 目前只有“人保”支持按揭车报价");
        sb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main)), 0, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvDaikuanTip.setText(sb);
    }

    private void setExtra() {
        customer_carmodel_id = getIntent().getStringExtra("extra_id");
    }

    private void loadData() {
        if (AppUtil.isNull(customer_carmodel_id)) return;
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        params.append("id", customer_carmodel_id);
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ADD_CAR_PAGE), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<PageData> object = GsonParser.getInstance().parseToObj(result, PageData.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        setBaseInfo(object.data);
                        loadCarInfo();
                    } else {
                    }
                }
            }
        });
    }

    private void loadCarInfo() {
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        if (pageData != null) {
            params.append("vinmodelid", pageData.vinmodelid);
            params.append("engineno", pageData.engineno);
            params.append("isguohu", "" + pageData.isguohu);
            params.append("saledate", "" + pageData.saledate);

        }
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ADD_CAR_PAGE2), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<PageCarInfo> object = GsonParser.getInstance().parseToObj(result, PageCarInfo.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        setAllInfo(object.data);
                    } else {
                    }
                }
            }
        });
    }

    private void refreshAppoint() {
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        if (pageData != null) {
            params.append("vinmodelid", pageData.vinmodelid);
            params.append("engineno", pageData.engineno);
            params.append("isguohu", "" + pageData.isguohu);
            params.append("saledate", "" + pageData.saledate);

        }
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ADD_CAR_PAGE2), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<PageCarInfo> object = GsonParser.getInstance().parseToObj(result, PageCarInfo.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        appointAdapter.setList(object.data.teyuelist);
                        appointAdapter.notifyDataSetChanged();
                    } else {
                    }
                }
            }
        });
    }

    private void setBaseInfo(PageData data) {
        this.pageData = data;
        etEngineId.setText(data.engineno);
        cbNew.setChecked(data.isguohu == 1);
        btnNewDate.setText(data.saledate);

    }

    private void setAllInfo(PageCarInfo data) {
        this.allData = data;
        etEngineId.setText(allData.engineno);
        cbNew.setChecked(allData.isguohu == 1);
        btnNewDate.setText(allData.saledate);
        tvCarCode.setText(allData.vin);
        btnCarType.setText(allData.remark);
        btnSiteCount.setText(allData.seat + "座");
        btnRegistDate.setText(allData.regdate);
        car_mode = allData.remark.split(" ")[1];
        modelId = (Integer.valueOf(allData.modelid) + 1) + "";
        cbCredit.setChecked(allData.daikuan ==1);

        appointAdapter.setList(data.teyuelist);
        appointAdapter.notifyDataSetChanged();

    }

    private void clearCarMode() {
        if (SelectCarModelActivity.clearCarMode) {
            modelId = "";
            car_mode = "";
            btnCarType.setText("");
        }
    }


    private void submit() {

        String vin = tvCarCode.getText().toString();
        if (vin.length() == 0) {
            AppUtil.showToast(this, "请输入VIN码");
            return;
        }
        String engineNO = etEngineId.getText().toString();
        if (engineNO.length() == 0) {
            AppUtil.showToast(this, "请输入引擎号");
            return;
        }

        String seatStr = btnSiteCount.getText().toString().trim();
        if (seatStr.length() == 0) {
            AppUtil.showToast(this, "请选择座位数");
            return;
        }

        if (AppUtil.isNull(car_mode)) {
            AppUtil.showToast(this, "请选择车型");
            return;
        }
        if (AppUtil.isNull(modelId)) {
            AppUtil.showToast(this, "请选择车型");
            return;
        }
        String remark = btnCarType.getText().toString();
        if (AppUtil.isNull(remark)) {
            AppUtil.showToast(this, "请选择车型");
            return;
        }
        String regDate = btnRegistDate.getText().toString();
        if (AppUtil.isNull(regDate)) {
            AppUtil.showToast(this, "请选择注册日期");
            return;
        }

        if(cbCredit.isChecked()&&appointAdapter.getCount()==0){
            AppUtil.showToast(this, "请选择特别约定");
            return;
        }

        ParamBuilder params = new ParamBuilder();
        params.append("model", car_mode);
        params.append("remark", remark);
        params.append("seat", seatStr.substring(0, seatStr.length() - 1) + "");
        params.append("vin", vin);
        params.append("engineno", engineNO);
        if (cbNew.isChecked()) {//已过户
            String newDate = btnNewDate.getText().toString();
            if (AppUtil.isNull(newDate)) {
                AppUtil.showToast(this, "请选择过户日期");
                return;
            }
            params.append("isguohu", "1");
            params.append("saledate", newDate);
        } else {
            params.append("isguohu", "0");
        }
      /*  if (cbCredit.isChecked()) {
            String teyuesyr = etTeyuesyrId.getText().toString();
            if (AppUtil.isNull(teyuesyr)) {
                AppUtil.showToast(this, "请填写第一受益人");
                return;
            }
            if (teyuesyr.length() < 2 || !StringUtil.isMatchingChiness(teyuesyr)) {
                AppUtil.showToast(this, "第一受益人名字不正确");
                return;
            }
            params.append("teyuesyr", teyuesyr);
        }*/

        params.append("daikuan", cbCredit.isChecked() ? "1" : "0");
        params.append("regdate", regDate);
//        params.append("modelid",modelId);
        params.append("modelid", Integer.valueOf(modelId) - 1 + "");

        DialogUtil.showDialog(lodDialog);
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SUBMIT_CAR_INFO), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        InsureInfoActivity.invoke(AddCarInfoActivity.this);
                    } else {
                        AppUtil.showToast(getApplicationContext(), object != null ? object.msg : "提交失败");
                    }
                }
            }
        });
    }


    private void selectCity() {

    }


    private void selectBank() {

    }


    @OnCheckedChanged(R.id.cb_new)
    public void checkIsNew(boolean isChecked) {
        layoutIsNew.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }

    @OnCheckedChanged(R.id.cb_credit)
    public void checkIsDaikuan(boolean isChecked) {
        tvDaikuanTip.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }


    @OnClick({R.id.btn_ocr, R.id.btn_help, R.id.btn_car_type, R.id.btn_site_count,R.id.btn_add_appoint,
            R.id.btn_regist_date, R.id.btn_new_date, R.id.btn_next, R.id.tv_car_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_ocr:
                ocrDecoder.decode(new OCRDecoder.ITakePhoto() {
                    @Override
                    public void onPhoto() {
                        AddCarInfoActivityPermissionsDispatcher.showCameraWithCheck(AddCarInfoActivity.this);
                    }
                });
                break;
            case R.id.tv_car_code:
                SelectCarModelActivity.invoke(this, tvCarCode.getText().toString().trim(), btnRegistDate.getText().toString().trim(), false, false, false);
                break;
            case R.id.btn_car_type:
                String vin = tvCarCode.getText().toString().trim();
                if (AppUtil.isNull(vin)) {
                    AppUtil.showToast(getApplicationContext(), "请先通过vin码查询车型");
                }else if(isScanVin){
                    AppUtil.showToast(getApplicationContext(),"请先通过vin码查询车型");
                } else {
                    SelectCarModelActivity.invoke(this, vin, btnRegistDate.getText().toString().trim(), false, true, true);
                }
                break;
            case R.id.btn_site_count:
                String seat = btnSiteCount.getText().toString();
                int seatI = 0;
                if (seat.length() > 0) {
                    seatI = Integer.valueOf("" + seat.charAt(0)) - 2;
                }
                showSiteDialog(seatI);
                break;
            case R.id.btn_regist_date:
                showDatePicker();
                break;
            case R.id.btn_new_date:
                showNewDatePicker();
                break;
            case R.id.btn_next:
                submit();
                break;
            case R.id.btn_help:
//                CommonWebActivity.invoke(this,"https://www.baidu.com","");
                showVinExample();
                break;
            case R.id.btn_add_appoint:
                AppointListActivity.invoke(this);
                break;
        }
    }


    DatePickView datePickView;

    private void showDatePicker() {
        if (datePickView == null) {
            datePickView = new DatePickView(this);
            datePickView.setDatePickListener(new DatePickView.DatePickListener() {
                @Override
                public void onSelected(String date) {
                    if (!DateUtil.beforeToday(date, true)) {
                        AppUtil.showToast(getApplicationContext(), "只能选择今天及今天以前的日期");
                        return;
                    }
                    btnRegistDate.setText(date);
                }
            });
        }
        datePickView.show();
    }

    DatePickView datePickView2;

    private void showNewDatePicker() {
        if (datePickView2 == null) {
            datePickView2 = new DatePickView(this);
            datePickView2.setDatePickListener(new DatePickView.DatePickListener() {
                @Override
                public void onSelected(String date) {
                    if (!DateUtil.inAYear(date)) {//大致计算一年内
                        AppUtil.showToast(getApplicationContext(), "您只能选择一年内的日期");
                        return;
                    }
                    btnNewDate.setText(date);
                }
            });
        }
        datePickView2.show();
    }

    private void showSiteDialog(int cur) {
        String[] items = {"2", "3", "4", "5", "6", "7", "8"};
        IOSDialogUtil.OnSheetItemClickListener l = new IOSDialogUtil.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                btnSiteCount.setText((which + 1) + "座");
            }
        };
        IOSDialogUtil dialogUtil = new IOSDialogUtil(this);
        for (int i = 0; i < 7; i++) {
            if (cur == i) {
                dialogUtil.addSheetItem(items[i], IOSDialogUtil.SheetItemColor.Blue, l);
            } else {
                dialogUtil.addSheetItem(items[i], IOSDialogUtil.SheetItemColor.Black, l);
            }
        }
        dialogUtil.builder().show();
    }


    private class PageData {
        String vinmodelid;
        String engineno;
        int isguohu;
        String saledate;
    }


    private class PageCarInfo {
        String model;
        String remark;
        String modelid;
        String vin;
        String regdate;
        String teyuesyr;
        int seat;
        int daikuan;
        String engineno;
        int isguohu;
        String saledate;
        List<InsureAppoint> teyuelist;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SelectCarModelActivity.REQUEST_CODE) {
                String modelID = data.getStringExtra("extra_car_model_id");
                String carModel = data.getStringExtra("extra_car_model");
                String vin = data.getStringExtra("extra_vin");
                String date = data.getStringExtra("extra_date");
                String seat = data.getStringExtra("extra_seat");
                if (!AppUtil.isNull(modelID)) {
                    this.modelId = modelID;
                }
                if (!AppUtil.isNull(seat)) {
                    btnSiteCount.setText(seat.contains("座") ? seat : (seat + "座"));
                }

                if (!AppUtil.isNull(vin)) {
                    tvCarCode.setText(vin);
                }

                if (!AppUtil.isNull(carModel)) {
                    this.car_mode = carModel.split(" ")[1];
                    btnCarType.setText(carModel);
                }
                if (!AppUtil.isNull(date)) {
//                    btnRegistDate.setText(date);
                }
                clearCarMode();
            } else if (requestCode == OCRDecoder.TAKE_PHOTO || requestCode == OCRDecoder.TAKE_PICTURE) {
                AddCarInfoActivityPermissionsDispatcher.showStorageWithCheck(AddCarInfoActivity.this, requestCode, data);
            } else if (requestCode == SearchCarModelActivity.REQUEST_CODE) {

                String modelID = data.getStringExtra("extra_car_model_id");
                String carModel = data.getStringExtra("extra_car_model");
                String seat = data.getStringExtra("extra_seat");
                if (!AppUtil.isNull(modelID)) {
                    this.modelId = modelID;
                }
                if (!AppUtil.isNull(seat)) {
                    btnSiteCount.setText(seat.contains("座") ? seat : (seat + "座"));
                }

                if (!AppUtil.isNull(carModel)) {
                    this.car_mode = carModel.split(" ")[1];
                    btnCarType.setText(carModel);
                }
            } else if(requestCode==AppointEditlActivity.REQUEST_CODE){
                refreshAppoint();
                /*String op=data.getStringExtra("op");
                if(op.equals("del")){

                }else if(op.equals("add")){

                }else if(op.equals("edit")){

                }*/
            }
        } else if (resultCode == 100) {
            clearCarMode();
        }

    }

    private void showVinExample() {
        final Dialog dialog = new Dialog(this, R.style.CommonDialog);
        Window dialogWindow = getWindow();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_trans_image);
        dialog.findViewById(R.id.common_dialog_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = 0.5f;
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setAttributes(params);
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        dialogWindow.setGravity(Gravity.CENTER);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        SelectCarModelActivity.vinEtCache = "";
        isScanVin=false;
        super.onDestroy();
    }


    private class InsureAppointAdapter extends AbsMutipleAdapter<InsureAppoint, AppointHolder> {

        public InsureAppointAdapter(Activity context) {
            super(context);
        }

        @Override
        public AppointHolder onCreateViewHolder(int position, int viewType, ViewGroup parent) {
            return new AppointHolder(mInflater.inflate(R.layout.item_insure_appoint, null));
        }

        @Override
        public void onBindViewHolder(int position, AppointHolder holder) {
            final InsureAppoint bean =(InsureAppoint)getItem(position);
            holder.tvValue.setText(bean.insurance_teyue_tile);
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(bean.insurance_teyue_type==3){
                        AppointOtherEditActivity.invoke(AddCarInfoActivity.this,bean.insurance_teyue_id,bean.insurance_teyue_tid,true);
                    }else {
                        AppointEditlActivity.invoke(AddCarInfoActivity.this,bean.insurance_teyue_id,bean.insurance_teyue_tid,true);
                    }
                }
            });
        }

    }

    public static class AppointHolder extends ViewHolder {

        @BindView(R.id.tv_value)
        TextView tvValue;

        public AppointHolder(View view) {
            super(view);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AddCarInfoActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void showCamera() {
        ocrDecoder.doTakePhoto();
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
        ocrDecoder.onActivityResult(requestCode, Activity.RESULT_OK, data);
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
