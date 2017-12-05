package com.paulz.carinsurance.common.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.paulz.carinsurance.model.UploadProfileConfig;
import com.paulz.carinsurance.ui.view.ImageModelLabelView;
import com.paulz.carinsurance.ui.view.ImageModelTipView;
import com.paulz.carinsurance.ui.view.ImageModelView;
import com.paulz.carinsurance.ui.viewmodel.ImageModelDouble;
import com.paulz.carinsurance.ui.viewmodel.ImageModelTip;
import com.paulz.carinsurance.utils.AppUtil;

import java.util.List;


/**
 * Created by Paul Z on 2017/5/22.
 * 设计思路：
 * 可以使所有列表都用这个adapter，作为一个只做 "视图模型--数据模型或指定类型" 的适配器。
 * 具体bind逻辑在视图模型中通过实现IViewMode来处理。
 * 还可以考虑实现插入其他adapter，从而对本适配器进行扩展 (未实现，后续思路)
 *
 */
public class CommonRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static final int VIEW_TYPE_UPLOAD_IMG_MODEL =1;
    public static final int VIEW_TYPE_UPLOAD_IMG_GROUP_LABEL =2;
    public static final int VIEW_TYPE_UPLOAD_IMG_TIP =3;


    public static final int VIEW_TYPE_LAST_EMPTY=100;

    private List<?> mList;
    private RecyclerView mRecyclerView;
    RecyclerViewScrollHelper scrollHelper;

    public CommonRVAdapter(RecyclerView recyclerView){
        mRecyclerView=recyclerView;
        scrollHelper=new RecyclerViewScrollHelper();
        scrollHelper.bind(recyclerView);
    }


    public void setList(List<?> list){
        mList=list;
    }

    public List<Object> getList(){
        return (List<Object>) mList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder=null;
        if(viewType== VIEW_TYPE_UPLOAD_IMG_MODEL){
            holder=new CommonHolder(new ImageModelView(parent.getContext()));
        }else if(viewType== VIEW_TYPE_UPLOAD_IMG_GROUP_LABEL){
            holder=new CommonHolder(new ImageModelLabelView(parent.getContext()));
        }else if(viewType== VIEW_TYPE_UPLOAD_IMG_TIP){
            holder=new CommonHolder(new ImageModelTipView(parent.getContext()));
        }else {
            View v=new View(parent.getContext());
            v.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,200));
            holder=new LastEmpterHolder(v);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder.itemView instanceof IViewModel){
            ((IViewModel) holder.itemView).bindData(position,getItem(position));
        }
        if(holder.itemView instanceof IEventer){
            ((IEventer) holder.itemView).setEventCallback(mEventCallback);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object o=getItem(position);
        if(o instanceof ImageModelDouble){
            return VIEW_TYPE_UPLOAD_IMG_MODEL;
        }else if(o instanceof UploadProfileConfig.ImageGroup){
            return VIEW_TYPE_UPLOAD_IMG_GROUP_LABEL;

        }else if(o instanceof ImageModelTip){
            return VIEW_TYPE_UPLOAD_IMG_TIP;

        }
        return VIEW_TYPE_LAST_EMPTY;
    }

    private Object getItem(int position){
        return AppUtil.isEmpty(mList)?null:mList.get(position);
    }




    /**
     * 跳转到 指定位置
     * @param position
     */
    public void scrollToPosition(int position){
        if(position>=0&&getItemCount()>0){
            scrollHelper.smoothMoveToPosition(mRecyclerView,position);
        }
    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }

    public static class LastEmpterHolder extends RecyclerView.ViewHolder{

        public LastEmpterHolder(View itemView) {
            super(itemView);
        }
    }

    public static class CommonHolder extends RecyclerView.ViewHolder{

        public CommonHolder(View itemView) {
            super(itemView);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }


    //通用callback，调用者和回传参数自己定义
    private EventCallback mEventCallback;
    public void setCallback(EventCallback eventCallback){
        mEventCallback=eventCallback;
    }

}
