package com.paulz.carinsurance.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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
import com.core.framework.util.DESUtil;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.IOSDialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.AbsMutipleAdapter;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.Area;
import com.paulz.carinsurance.model.CarCard;
import com.paulz.carinsurance.model.InsureAppoint;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.DateUtil;
import com.paulz.carinsurance.utils.OCRDecoder;
import com.paulz.carinsurance.view.CommonDialog;

import java.util.ArrayList;
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
 * Created by pualbeben on 17/5/29.
 * 填写车保信息
 */
@RuntimePermissions
public class CarInsureActivity extends BaseActivity {


    @BindView(R.id.btn_insure_city)
    TextView btnInsureCity;
    @BindView(R.id.tv_car_id)
    TextView tvCarId;
    @BindView(R.id.et_car_number)
    EditText etCarNumber;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.check_box)
    CheckBox checkBox;
    @BindView(R.id.btn_car_code)
    TextView btnCarCode;
    @BindView(R.id.btn_car_type)
    TextView btnCarType;
    @BindView(R.id.btn_site_count)
    TextView btnSiteCount;
    @BindView(R.id.et_engine_id)
    EditText etEngineId;
    @BindView(R.id.cb_new)
    CheckBox cbNew;
    @BindView(R.id.btn_regist_date)
    TextView btnRegistDate;
    @BindView(R.id.btn_force_date)
    TextView btnForceDate;
    @BindView(R.id.btn_business_date)
    TextView btnBusinessDate;
    @BindView(R.id.btn_next)
    TextView btnNext;

    String cid;

    String pid;
    String cityName;
    String model;
    String modelid;

    String regDate;
    String forceDate;
    String bussinessDate;
    String guohuDate;
    @BindView(R.id.btn_new_date)
    TextView btnNewDate;
    @BindView(R.id.layout_is_new)
    LinearLayout layoutIsNew;
    String id;
    @BindView(R.id.btn_delete)
    TextView btnDelete;

    @BindView(R.id.cb_credit)
    CheckBox cbCredit;
    @BindView(R.id.tv_daikuan_tip)
    TextView tvDaikuanTip;


    @BindView(R.id.btn_add_appoint)
    View btnAddAppoint;

    @BindView(R.id.lv_appoint)
    ListView lvAppoint;

    InsureAppointAdapter appointAdapter;

    private PageInfo data;

    OCRDecoder ocrDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("extra_id");
        cid = getIntent().getStringExtra("extra_cid");
        initView();
        loadData();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_car_insure, false, true);
        setTitleText("", AppUtil.isNull(id) ? "添加车辆" : "编辑车辆", 0, true);
        ButterKnife.bind(this);
        if(!AppUtil.isNull(id)){
            btnDelete.setVisibility(View.VISIBLE);
        }
        ocrDecoder=new OCRDecoder(this, lodDialog, new OCRDecoder.DCallback() {
            @Override
            public void onFinish(CarCard card) {
                if(card!=null){
                    setCarInfo(card);
                }else {
                    AppUtil.showToast(getApplicationContext(),"解析失败，请重新选择");
                }
            }
        });
        appointAdapter=new InsureAppointAdapter(this);
        lvAppoint.setAdapter(appointAdapter);
    }

    private void setCarInfo(CarCard card){
        regDate = card.getRegDate();
        btnCarCode.setText(card.getVIN());
        etEngineId.setText(card.getEngine());
        btnRegistDate.setText(card.getRegDate());
        model="";
        modelid="";
        btnCarType.setText("");
    }


    public static void invoke(Context context, String id,String customerId) {
        Intent intent = new Intent(context, CarInsureActivity.class);
        if (!TextUtils.isEmpty(id)) {
            intent.putExtra("extra_id", id);
        }
        if (!TextUtils.isEmpty(customerId)) {
            intent.putExtra("extra_cid", customerId);
        }
        context.startActivity(intent);
    }

    @OnClick({R.id.btn_ocr,R.id.btn_delete,R.id.tv_car_id, R.id.btn_insure_city, R.id.btn_new_date,R.id.btn_add_appoint,
            R.id.btn_car_code, R.id.btn_car_type, R.id.btn_site_count, R.id.btn_regist_date, R.id.btn_force_date,
            R.id.btn_business_date, R.id.btn_next, R.id.btn_help})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_insure_city:
                SelectCityActivity.invoke(this, false,pid,data!=null&&data.data!=null?data.data.citylist:null);
                break;
            case R.id.btn_car_code:
                SelectCarModelActivity.invoke(this, btnCarCode.getText().toString().trim(), btnRegistDate.getText().toString().trim(),true,false,false);

                break;
            case R.id.btn_car_type:
                String vin=btnCarCode.getText().toString().trim();
                if(AppUtil.isNull(vin)){
                    AppUtil.showToast(getApplicationContext(),"请先通过vin码查询车型");
                }else {
                    SelectCarModelActivity.invoke(this, vin, btnRegistDate.getText().toString().trim(),true,true,true);
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
                showDatePicker1();
                break;
            case R.id.btn_force_date:
                showDatePicker3();
                break;
            case R.id.btn_business_date:
                showDatePicker4();
                break;
            case R.id.btn_next:
                add();
                break;
            case R.id.btn_new_date:
                showDatePicker2();
                break;
            case R.id.tv_car_id:
                SelectCarNumberActivity.invoke(this, tvCarId.getText().toString());
                break;
            case R.id.btn_delete:
                showDeleteDialog();
                break;
            case R.id.btn_ocr:
                ocrDecoder.decode(new OCRDecoder.ITakePhoto() {
                    @Override
                    public void onPhoto() {
                        CarInsureActivityPermissionsDispatcher.showCameraWithCheck(CarInsureActivity.this);
                    }
                });
                break;
            case R.id.btn_help:
                showVinExample();
                break;
            case R.id.btn_add_appoint:
                AppointListActivity.invoke(this);
                break;
        }
    }


    private void showDeleteDialog() {
        CommonDialog dialog = new CommonDialog(this);
        dialog.setDesc("确定要删除本车信息？");
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                delete();
            }
        });
        dialog.show();
    }

    private void delete(){
        if(AppUtil.isNull(id))return;
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester=new HttpRequester();
        requester.getParams().put("id",id);
        requester.getParams().put("cid",cid);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CUSTOMER_CAR_DELETE), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK ) {
                        AppUtil.showToast(getApplicationContext(),"删除成功");
                        finish();
                    } else {
                        AppUtil.showToast(getApplicationContext(),object!=null?object.msg:"删除失败");

                    }
                }else {
                    AppUtil.showToast(getApplicationContext(),"删除失败");

                }
            }
        },requester, DESUtil.SECRET_DES);


    }

    @OnCheckedChanged(R.id.check_box)
    public void checkToNoCarid(boolean isChecked) {
        if (isChecked) {
            etCarNumber.setEnabled(false);
            tvCarId.setVisibility(View.GONE);
            etCarNumber.setHint("新车未上牌");
            etCarNumber.setText("");

        } else {
            etCarNumber.setEnabled(true);
            tvCarId.setVisibility(View.VISIBLE);
            etCarNumber.setHint("请输入车牌号");

        }

    }

    @OnCheckedChanged(R.id.cb_new)
    public void checkIsNew(boolean isChecked) {
        layoutIsNew.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }

    @OnCheckedChanged(R.id.cb_credit)
    public void checkIsDaikuan(boolean isChecked) {
        tvDaikuanTip.setVisibility(isChecked ? View.VISIBLE : View.GONE);
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
            if (cur != i) {
                dialogUtil.addSheetItem(items[i], IOSDialogUtil.SheetItemColor.Black, l);
            } else {
                dialogUtil.addSheetItem(items[i], IOSDialogUtil.SheetItemColor.Blue, l);
            }
        }
        dialogUtil.builder().show();
    }


    DatePickView datePickView1;

    private void showDatePicker1() {
        if (datePickView1 == null) {
            datePickView1 = new DatePickView(this);
            datePickView1.setDatePickListener(new DatePickView.DatePickListener() {
                @Override
                public void onSelected(String date) {
                    if(!DateUtil.beforeToday(date,true)){
                        AppUtil.showToast(getApplicationContext(),"只能选择今天及今天以前的日期");
                        return;
                    }
                    regDate = date;
                    btnRegistDate.setText(date);
                }
            });
        }
        datePickView1.show();
    }

    DatePickView datePickView2;

    private void showDatePicker2() {
        if (datePickView2 == null) {
            datePickView2 = new DatePickView(this);
            datePickView2.setDatePickListener(new DatePickView.DatePickListener() {
                @Override
                public void onSelected(String date) {
                    if(!DateUtil.inAYear(date)){//大致计算一年内
                        AppUtil.showToast(getApplicationContext(),"您只能选择一年内的日期");
                        return;
                    }
                    guohuDate = date;
                    btnNewDate.setText(date);
                }
            });
        }
        datePickView2.show();
    }

    DateAfterPickView datePickView3;

    private void showDatePicker3() {
        if (datePickView3 == null) {
            datePickView3 = new DateAfterPickView(this, 2016);
            datePickView3.setDatePickListener(new DateAfterPickView.DatePickListener() {
                @Override
                public void onSelected(String date) {
                    forceDate = date;
                    btnForceDate.setText(date);
                }
            });
        }
        datePickView3.show();
    }

    DateAfterPickView datePickView4;

    private void showDatePicker4() {
        if (datePickView4 == null) {
            datePickView4 = new DateAfterPickView(this, 2016);
            datePickView4.setDatePickListener(new DateAfterPickView.DatePickListener() {
                @Override
                public void onSelected(String date) {
                    bussinessDate = date;
                    btnBusinessDate.setText(date);
                }
            });
        }
        datePickView4.show();
    }

    private void handleData() {
        cityName = data.cityvalue;
        pid = data.cityid;
        model = data.model;
        modelid = (Integer.valueOf(data.modelid)+1)+"";
//        modelid = data.modelid;
        regDate = data.regdate;
        bussinessDate = data.insurance_businessinsdate;
        forceDate = data.insurance_compulsoryinsdate;
        guohuDate = data.saledate;
        btnInsureCity.setText(cityName);
        if (data.iscarnum == 1&&!"未上牌".equals(data.fnum+data.carnum)) {
            tvCarId.setText(data.fnum);
            etCarNumber.setText(data.carnum);
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }
        btnCarCode.setText(data.vin);
        btnCarType.setText(data.remark);
        btnSiteCount.setText(data.seat + "座");
        etEngineId.setText(data.engineno);
        cbNew.setChecked(data.isguohu == 1);
        btnNewDate.setText(data.saledate);
        btnBusinessDate.setText(data.insurance_businessinsdate);
        btnForceDate.setText(data.insurance_compulsoryinsdate);
        btnRegistDate.setText(data.regdate);

        cbCredit.setChecked(data.daikuan==1);

        appointAdapter.setList(data.teyuelist);
        appointAdapter.notifyDataSetChanged();


    }


    private void showVinExample(){
        final Dialog dialog=new Dialog(this,R.style.CommonDialog);
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



    private void loadData() {
        if (TextUtils.isEmpty(id)) return;
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        params.append("edit", "1");
        params.append("id", id);
        params.append("cid", cid);
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CUSTOMER_CAR_ADD), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<PageInfo> object = GsonParser.getInstance().parseToObj(result, PageInfo.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        data = object.data;
                        handleData();
                    } else {

                    }
                }
            }
        });
    }

    private void refreshAppoint() {
        if (TextUtils.isEmpty(id)) return;
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        params.append("edit", "1");
        params.append("id", id);
        params.append("cid", cid);
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CUSTOMER_CAR_ADD), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<PageInfo> object = GsonParser.getInstance().parseToObj(result, PageInfo.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        appointAdapter.setList(object.data.teyuelist);
                        appointAdapter.notifyDataSetChanged();
                    } else {

                    }
                }
            }
        });
    }

    private void clearCarMode(){
        if(SelectCarModelActivity.clearCarMode){
            model="";
            modelid="";
            btnCarType.setText("");
        }
    }


    private void add() {
        boolean isNew = checkBox.isChecked();
        String carid = tvCarId.getText().toString().trim();
        String carNumber = etCarNumber.getText().toString().trim();
        if (AppUtil.isNull(pid)) {
            AppUtil.showToast(getApplicationContext(), "请选择投保城市");
            return;
        }
        if (!isNew) {
            if (carid.length() == 0) {
                AppUtil.showToast(getApplicationContext(), "请选择车牌");
                return;
            }
            if (carNumber.length() == 0) {
                AppUtil.showToast(getApplicationContext(), "请输入车牌");
                return;
            }
            if (carNumber.length() < 5) {
                AppUtil.showToast(getApplicationContext(), "请输入正确车牌号");
                return;
            }
        }
        String vin = btnCarCode.getText().toString().trim();
        if (vin.length() == 0) {
            AppUtil.showToast(getApplicationContext(), "请选择VIN码");
            return;
        }

        if (AppUtil.isNull(model)) {
            AppUtil.showToast(getApplicationContext(), "请选择车型");
            return;
        }

        String engine = etEngineId.getText().toString().trim();
        if (engine.length() == 0) {
            AppUtil.showToast(getApplicationContext(), "请输入引擎号");
            return;
        }
        String seat = btnSiteCount.getText().toString();
        if (seat.length() == 0) {
            AppUtil.showToast(getApplicationContext(), "请选择座位数");
            return;
        }
        if (cbNew.isChecked() && AppUtil.isNull(guohuDate)) {
            AppUtil.showToast(getApplicationContext(), "请选择过户日期");
            return;
        }
        if (AppUtil.isNull(regDate)) {
            AppUtil.showToast(getApplicationContext(), "请选择注册日期");
            return;
        }
//        if (AppUtil.isNull(forceDate)) {
//            AppUtil.showToast(getApplicationContext(), "请选择交强险到期日期");
//            return;
//        }
//        if (AppUtil.isNull(bussinessDate)) {
//            AppUtil.showToast(getApplicationContext(), "请选择商业险到期日期");
//            return;
//        }

        if(cbCredit.isChecked()&&appointAdapter.getCount()==0){
            AppUtil.showToast(this, "请选择特别约定");
            return;
        }


        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester = new HttpRequester();
        requester.getParams().put("cid", cid);
        requester.getParams().put("area", pid);
        requester.getParams().put("vin", vin);
        requester.getParams().put("modelid", Integer.valueOf(modelid)-1+"");
//        requester.getParams().put("modelid", modelid);
        requester.getParams().put("model", model);
        requester.getParams().put("remark", btnCarType.getText().toString().trim());
        requester.getParams().put("seat", seat.substring(0, 1));
        requester.getParams().put("engineno", engine);
        requester.getParams().put("regdate", regDate);
        requester.getParams().put("insurance_compulsoryinsdate", forceDate);
        requester.getParams().put("insurance_businessinsdate", bussinessDate);
        if (cbNew.isChecked()) {
            requester.getParams().put("isguohu", "1");
            requester.getParams().put("saledate", guohuDate);

        } else {
            requester.getParams().put("isguohu", "0");
        }
        if (!isNew) {
            requester.getParams().put("carnum", carNumber);
            requester.getParams().put("fnum", carid);
        }

        requester.getParams().put("daikuan", cbCredit.isChecked() ? "1" : "0");


        String url = AppUrls.getInstance().URL_CUSTOMER_CAR_ADD;
//        if(detail!=null){
//            requester.getParams().put("customer_id",detail.customer_id);
//            url= AppUrls.getInstance().URL_CUSTOMER_EDIT;
//        }else {
//            url=AppUrls.getInstance().URL_CUSTOMER_ADD;
//
//        }
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), url), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        AppUtil.showToast(getApplicationContext(), true ? "添加成功" : "修改成功");
                        finish();
                    } else {
                        AppUtil.showToast(getApplicationContext(), object != null ? object.msg : (true ? "添加失败" : "修改失败"));

                    }
                }else {
                    AppUtil.showToast(getApplicationContext(),"请求失败");
                }
            }
        }, requester, DESUtil.SECRET_DES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SelectCityActivity.REQUEST_CODE) {
                cityName = "";
                pid = "";
                for (int i = 0; i < 2; i++) {
                    if (data.hasExtra("regionId_" + i)) {
                        pid += data.getStringExtra("regionId_" + i) + ",";
                    }
                    if (data.hasExtra("regionName_" + i)) {
                        cityName += data.getStringExtra("regionName_" + i);
                    }
                }
                if (pid.endsWith(",")) {
                    pid = pid.substring(0, pid.length() - 1);
                }
                btnInsureCity.setText(cityName);
            } else if (requestCode == SelectCarModelActivity.REQUEST_CODE) {
                String modelID=data.getStringExtra("extra_car_model_id");
                String carModel=data.getStringExtra("extra_car_model");
                String vin=data.getStringExtra("extra_vin");
                String date=data.getStringExtra("extra_date");
                String seat=data.getStringExtra("extra_seat");
                if(!AppUtil.isNull(vin)){
                    btnCarCode.setText(vin);
                }
                if(!AppUtil.isNull(modelID)){
                    modelid=modelID;
                }
                if(!AppUtil.isNull(seat)){
                    btnSiteCount.setText(seat.contains("座")?seat:(seat+"座"));
                }
                if(!AppUtil.isNull(carModel)){
                    model=carModel.split(" ")[1];
                    btnCarType.setText(carModel);
                }
                if(!AppUtil.isNull(date)){
//                    regDate=date;
//                    btnRegistDate.setText(date);
                }
                clearCarMode();
            } else if (requestCode == SelectCarNumberActivity.REQUEST_CODE) {
                tvCarId.setText(data.getStringExtra("car_number"));
            }else if(requestCode==OCRDecoder.TAKE_PICTURE||requestCode==OCRDecoder.TAKE_PHOTO){
                CarInsureActivityPermissionsDispatcher.showStorageWithCheck(CarInsureActivity.this,requestCode,data);
            }else if(requestCode==AppointEditlActivity.REQUEST_CODE){
                refreshAppoint();
                /*String op=data.getStringExtra("op");
                if(op.equals("del")){

                }else if(op.equals("add")){

                }else if(op.equals("edit")){

                }*/
            }
        }else if(resultCode==100){
            clearCarMode();
        }
        ocrDecoder.onActivityResult(requestCode,resultCode,data);

    }

    @Override
    protected void onDestroy() {
        SelectCarModelActivity.vinEtCache="";
        super.onDestroy();
    }

    private class PageInfo {
        public String fnum;
        public String carnum;
        public int iscarnum;
        public String model;
        public String remark;
        public String modelid;
        public int isguohu;
        public int daikuan;
        public String saledate;
        public String engineno;
        public String vin;
        public String ccid;
        public String regdate;
        public String seat;
        public String insurance_businessinsdate;
        public String insurance_compulsoryinsdate;
        public String cityvalue;
        public String cityid;
        public CityData data;
        List<InsureAppoint> teyuelist;

    }

    private class CityData{
        ArrayList<Area> citylist;
    }


    private class InsureAppointAdapter extends AbsMutipleAdapter<InsureAppoint, AddCarInfoActivity.AppointHolder> {

        public InsureAppointAdapter(Activity context) {
            super(context);
        }

        @Override
        public AddCarInfoActivity.AppointHolder onCreateViewHolder(int position, int viewType, ViewGroup parent) {
            return new AddCarInfoActivity.AppointHolder(mInflater.inflate(R.layout.item_insure_appoint, null));
        }

        @Override
        public void onBindViewHolder(int position, AddCarInfoActivity.AppointHolder holder) {
            final InsureAppoint bean =(InsureAppoint)getItem(position);
            holder.tvValue.setText(bean.insurance_teyue_tile);
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(bean.insurance_teyue_type==3){
                        AppointOtherEditActivity.invoke(CarInsureActivity.this,bean.insurance_teyue_id,bean.insurance_teyue_tid,true);
                    }else {
                        AppointEditlActivity.invoke(CarInsureActivity.this,bean.insurance_teyue_id,bean.insurance_teyue_tid,true);
                    }
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CarInsureActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void showCamera(){

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
        Toast.makeText(this, "您已经禁用拍照功能，请到系统设置开启权限",Toast.LENGTH_SHORT).show();
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showStorage(int requestCode, Intent data){
        ocrDecoder.onActivityResult(requestCode, Activity.RESULT_OK,data);
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
