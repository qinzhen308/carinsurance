package com.paulz.carinsurance.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.core.framework.util.DialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.AbsMutipleAdapter;
import com.paulz.carinsurance.adapter.ViewHolder;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.AppointDetail;
import com.paulz.carinsurance.model.AppointDetailEdit;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pualbeben on 17/5/21.
 * 用户信息
 */

public class AppointOtherEditActivity extends BaseActivity {
    public static final int REQUEST_CODE = 1001;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_remark)
    EditText etRemark;
    @BindView(R.id.btn_submit)
    TextView btnSubmit;
    @BindView(R.id.btn_delete)
    TextView btnDelete;


    private String id;
    private String tid;
    private boolean isEdit;

    AppointDetail mData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isEdit = getIntent().getBooleanExtra("extra_is_edit", false);
        id = getIntent().getStringExtra("extra_id");
        tid = getIntent().getStringExtra("extra_tid");
        initView();
        loadData();
    }


    private void initView() {
        setActiviyContextView(R.layout.activity_appoint_edit_other, false, true);
        ButterKnife.bind(this);
        setTitleText("", "特别约定详情", 0, true);
        if (isEdit) {
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }

    }


    private void loadData() {

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        if (isEdit) {
            params.append("tid", id);
        } else {
            params.append("id", id);

        }
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_APPOINT_DETAIL), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<AppointDetail> object = GsonParser.getInstance().parseToObj(result, AppointDetail.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        mData = object.data;
                        handleData();
                    } else {
                        AppUtil.showToast(getApplicationContext(), "加载失败");
                    }
                }
            }
        });

    }

    private void handleData() {
        tvTitle.setText(mData.title);
        etRemark.setText(mData.des);

    }


    private class InputItem {
        String key;
        String remark;
    }


    private void submit() {

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester = new HttpRequester();
        String remark = etRemark.getText().toString();
        if (AppUtil.isNull(remark)) {
            AppUtil.showToast(getApplicationContext(), "请补充特约内容");
            return;
        }
        requester.getParams().put(mData.buttonlist.get(0), remark);

        if (isEdit) {
            requester.getParams().put("id", tid);

        } else {
            requester.getParams().put("id", id);
        }

        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_APPOINT_SUBMIT), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        setResult(RESULT_OK);
                        finish();
                    } else {

                        AppUtil.showToast(getApplicationContext(), "提交失败");
                    }
                }
            }
        }, requester, DESUtil.SECRET_DES);

    }


    private void delete() {

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester = new HttpRequester();

        requester.getParams().put("id", id);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_APPOINT_DELETE), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK) {
                        AppUtil.showToast(getApplicationContext(), "删除成功");

                        setResult(RESULT_OK);
                        finish();

                    } else {

                        AppUtil.showToast(getApplicationContext(), "删除失败");
                    }
                }
            }
        }, requester, DESUtil.SECRET_DES);

    }


    public static void invoke(Activity context, String id, boolean isEdit) {
        Intent intent = new Intent(context, AppointOtherEditActivity.class);
        intent.putExtra("extra_id", id);
        intent.putExtra("extra_is_edit", isEdit);
        context.startActivityForResult(intent, REQUEST_CODE);
    }

    public static void invoke(Activity context, String id, String tid, boolean isEdit) {
        Intent intent = new Intent(context, AppointOtherEditActivity.class);
        intent.putExtra("extra_id", id);
        intent.putExtra("extra_tid", tid);
        intent.putExtra("extra_is_edit", isEdit);
        context.startActivityForResult(intent, REQUEST_CODE);
    }

    @OnClick({R.id.btn_submit, R.id.btn_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                submit();
                break;
            case R.id.btn_delete:
                delete();
                break;
        }
    }


    public class ItemAdapter extends AbsMutipleAdapter<Object, ViewHolder> {


        public ItemAdapter(Activity context) {
            super(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(int position, int viewType, ViewGroup parent) {
            if (viewType == 0) {
                return new ItemContentHOlder(mInflater.inflate(R.layout.item_appoint_detail_content, null));
            } else if (viewType == 1) {
                return new ItemInputHOlder(mInflater.inflate(R.layout.item_appoint_detail_input, null));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(int position, ViewHolder holder) {
            int type = getItemViewType(position);
            if (type == 0) {
                ItemContentHOlder hOlder = (ItemContentHOlder) holder;
                final String item = (String) getItem(position);
                hOlder.tvContent.setText(item);
            } else if (type == 1) {
                ItemInputHOlder hOlder = (ItemInputHOlder) holder;
                final InputItem item = (InputItem) getItem(position);
                hOlder.etRemark.setText(item.remark);
                hOlder.etRemark.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        item.remark = s.toString();
                    }
                });
            }

        }

        @Override
        public int getItemViewType(int position) {
            Object o = getItem(position);
            if (o instanceof String) {
                return 0;
            } else if (o instanceof InputItem) {
                return 1;
            }
            return 0;
        }
    }

    public class ItemInputHOlder extends ViewHolder {

        @BindView(R.id.et_remark)
        EditText etRemark;

        public ItemInputHOlder(View view) {
            super(view);
        }
    }

    public class ItemContentHOlder extends ViewHolder {
        @BindView(R.id.tv_content)
        TextView tvContent;

        public ItemContentHOlder(View view) {
            super(view);
        }
    }


}
