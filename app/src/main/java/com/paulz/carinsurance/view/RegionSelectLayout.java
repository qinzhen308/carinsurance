package com.paulz.carinsurance.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.controller.AbstractListAdapter;
import com.paulz.carinsurance.model.Area;
import com.paulz.carinsurance.utils.AppUtil;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.develop.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类view
 * paul'z
 */
public class RegionSelectLayout extends LinearLayout implements AdapterView.OnItemClickListener {
    private Context mContext;
    private ListView mListView;
    private ListView mListView1;
    private ListView mListView2;

    private MListAdapter mListAdapter;
    private MListAdapter mListAdapter1;
    private MListAdapter mListAdapter2;

    private List<Area> all;
    public int selectParentPosition[] = {0,0,0};//存选中的城市 region的(type-1)作为数组的index  0省1市2区
    //private int maxTextLength;
    private boolean isAutoScroll;

    public RegionSelectLayout(Context context) {
        super(context);
        mContext = context;
        initView();
        registerListener();
    }

    public RegionSelectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        registerListener();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.category_layout_base_view, this);
        mListView = (ListView) findViewById(R.id.listView);
        mListView1 = (ListView) findViewById(R.id.listView2);
        mListView2 = (ListView) findViewById(R.id.listView3);

        mListAdapter = new MListAdapter(mContext,0);
        mListAdapter1 = new MListAdapter(mContext,1);
        mListAdapter2= new MListAdapter(mContext,2);
        mListView.setAdapter(mListAdapter);

        mListView1.setAdapter(mListAdapter1);
        mListView2.setAdapter(mListAdapter2);
        listviews=new AbsListView[]{mListView,mListView1,mListView2};
        adapters=new AbstractListAdapter[]{mListAdapter,mListAdapter1,mListAdapter2};
    }

    public void setList(List<Area> listData,int... ids) {
    	if(AppUtil.isEmpty(listData))return;
        all = listData;
        //getMaxTextLength();
        mListAdapter.setList(all);
        mListAdapter.notifyDataSetChanged();
        if(ids!=null){
            for(int i=0,size=ids.length>3?3:ids.length;i<size;i++){
                selectParentPosition[i]=ids[i];
            }
            initSelected();
        }else {
            setListAndGridSelect(0,all.get(0),0);
        }
    }


    /**
     * 获取一级分类最大字数
     */
    /*private void getMaxTextLength() {
        maxTextLength = 0;
        for (int i = 0, length = parentCategoryData.size(); i < length; i++) {
            if (parentCategoryData.get(i).name.length() > maxTextLength) {
                maxTextLength = parentCategoryData.get(i).name.length();
            }
        }
    }*/

    AbstractListAdapter adapters[];
    AbsListView listviews[];
    
    public void setListAndGridSelect(final int position,Area region,int deep) {
        try {
        	selectParentPosition[deep] = position;
            for(int i=deep;i<adapters.length;i++){
            	if(i==deep){
            		//这里是为了改变视图选中颜色
            		adapters[i].notifyDataSetChanged();
            	}else if(i==deep+1){
            		//选中的下一级，展示数据
            		selectParentPosition[i]=0;
            		adapters[i].setList(region.child);
            		listviews[i].setAdapter(adapters[i]);
				}else if(i==deep+2){
					//选中项的下下一级或下更多级，不展示
					selectParentPosition[i]=0;
                    if(!AppUtil.isEmpty(region.child)){
                        adapters[i].setList(region.child.get(0).child);
                    }else {
                        adapters[i].setList(new ArrayList<Area>());
                    }
					listviews[i].setAdapter(adapters[i]);
				}else {
                    adapters[i].setList(new ArrayList<Area>());
                    listviews[i].setAdapter(adapters[i]);
                }
            }

        } catch (Exception e) {
            LogUtil.w(e);
        }
    }

    public void initSelected() {
        mListAdapter.setList(all);
        mListAdapter.notifyDataSetChanged();
        Area tag0=all.get(selectParentPosition[0]);
        listviews[0].setSelection(selectParentPosition[0]);
        if(!AppUtil.isEmpty(tag0.child)){
            mListAdapter1.setList(tag0.child);
            mListAdapter1.notifyDataSetChanged();
            listviews[1].setSelection(selectParentPosition[1]);
            Area tag1=tag0.child.get(selectParentPosition[1]);
            if(!AppUtil.isEmpty(tag1.child)){
                mListAdapter2.setList(tag1.child);
                mListAdapter2.notifyDataSetChanged();
                listviews[2].setSelection(selectParentPosition[2]);
            }
        }

    }
    


    private void registerListener() {
        mListView.setOnItemClickListener(this);
        mListView.setScrollContainer(true);
        mListView.setFocusable(true);
        mListView.setFocusableInTouchMode(true);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCREEN_STATE_OFF && isAutoScroll) {
                    isAutoScroll = false;
                    if (mListAdapter1.getCount() == 1) {
                        LogUtil.d("未定义效果");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        mListView1.setOnItemClickListener(this);
        mListView1.setScrollContainer(true);
        mListView1.setFocusable(true);
        mListView1.setFocusableInTouchMode(true);
        mListView2.setOnItemClickListener(this);
        mListView2.setScrollContainer(true);
        mListView2.setFocusable(true);
        mListView2.setFocusableInTouchMode(true);
    }
    
    public void selectAndBack(){
    	Activity activity=((Activity)mContext);
		Intent intent=new Intent();
		for(int i=0;i<selectParentPosition.length;i++){
			AbstractListAdapter adapter=adapters[i];
			if(adapter.getCount()==0){
				break;
			}else {
				intent.putExtra("regionId_"+i, ((Area)adapter.getItem(selectParentPosition[i])).id);
				intent.putExtra("regionName_"+i, ((Area)adapter.getItem(selectParentPosition[i])).name);
			}
		}
        intent.putExtra("extra_all",((Activity) mContext).getIntent().getBooleanExtra("extra_all",false));
		activity.setResult(activity.RESULT_OK, intent);
		activity.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
    	Area regionInfo=(Area) parent.getItemAtPosition(position);
        int deep=((MListAdapter)parent.getAdapter()).deep;
    	if (selectParentPosition[deep] != position) {
    		setListAndGridSelect(position,regionInfo,deep);
    		if (parent == mListView1) {//一级分类的单击事件
				
	        } else if (parent == mListView2) {//二级的单击事件
	            
	        }
    		//处理滑动的效果的
    		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
    			if ((position == 0 && view.getTop() == 0) || (parent.getLastVisiblePosition() == parent.getCount() - 1 && parent.getHeight() == parent.getChildAt(parent.getCount() - 1 - parent.getFirstVisiblePosition()).getBottom())) {
    				
    			} else {
    				isAutoScroll = true;
    			}
//    			((ListView)parent).smoothScrollToPositionFromTop(position, 0, 500);
    			((ListView)parent).setSelection(position);

    		} else {
    			parent.setSelection(position);
    		}
    		
    	}
        
    }


    class MListAdapter extends AbstractListAdapter<Area> {
        int deep;

        public MListAdapter(Context context,int deep) {
            super(context);
            this.deep=deep;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.category_layout_list_item, null);
                holder.mItemTv = (TextView) convertView.findViewById(R.id.parent_category_name);
                holder.indicator =  convertView.findViewById(R.id.parent_indicator_category);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Area area=mList.get(position);
            holder.mItemTv.setText(area.name);
            if (selectParentPosition[deep] == position) {
                convertView.setBackgroundColor(getResources().getColor(R.color.white));
                holder.mItemTv.setTextColor(getResources().getColor(R.color.text_grey));
                holder.indicator.setVisibility(View.GONE);

            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.gray1));
                holder.mItemTv.setTextColor(getResources().getColor(R.color.black));
                holder.indicator.setVisibility(View.GONE);
            }
            return convertView;
        }

        class ViewHolder {
            private TextView mItemTv;
            private View indicator;
        }


        /**
         * 保证每个一级分类都凑够四个数，为了ui对齐好看
         *
         * @param str
         * @return
         */
        /*private String setTitleLengthTo4(String str) {
            StringBuilder sBuffer = new StringBuilder();
            sBuffer.append(str);
            for (int i = 0, size = maxTextLength - str.length(); i < size; i++) {
                sBuffer.append(getResources().getText(R.string.blank_space));
            }
            return sBuffer.toString();
        }*/
    }

    private int measureImage() {
        if (ScreenUtil.WIDTH == 0) {
            ScreenUtil.setDisplay((Activity) mContext);
        }
        //一级分类和二级分类宽度比是1.4:3.6   二级分类的列数为3
        return ((int) ((ScreenUtil.WIDTH) / 5 * 3.6) / 3) - ScreenUtil.dip2px(mContext, 20);
    }
}
