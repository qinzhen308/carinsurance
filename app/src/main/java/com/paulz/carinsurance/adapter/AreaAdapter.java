package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.model.Area;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pualbeben on 17/5/22.
 */

public class AreaAdapter extends AbsMutipleAdapter<Area, AreaAdapter.ViewHolderImpl> {
    private int selected=-1;

    int gray;
    int selectColor;

    public AreaAdapter(Activity context) {
        super(context);
        gray=context.getResources().getColor(R.color.grey_deep);
        selectColor=context.getResources().getColor(R.color.main_color);
    }

    public void setSelected(int selected){
        this.selected=selected;
    }

    @Override
    public ViewHolderImpl onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new ViewHolderImpl(mInflater.inflate(R.layout.category_layout_list_item, null));
    }

    @Override
    public void onBindViewHolder(final int position, ViewHolderImpl holder) {
        final Area bean = (Area) getItem(position);
        if(position==selected){
            holder.parentCategoryName.setTextColor(selectColor);
        }else {
            holder.parentCategoryName.setTextColor(gray);
        }
        holder.parentCategoryName.setText(bean.name);
        holder.parentIndicatorCategory.setVisibility(View.INVISIBLE);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("extra_area_id",bean.id);
                intent.putExtra("extra_area_name",bean.name);
                ((Activity)mContext).setResult(Activity.RESULT_OK,intent);
                ((Activity)mContext).finish();
            }
        });

    }

    public static class ViewHolderImpl extends ViewHolder {
        @BindView(R.id.parent_category_name)
        TextView parentCategoryName;
        @BindView(R.id.parent_indicator_category)
        View parentIndicatorCategory;

        public ViewHolderImpl(View view) {
            super(view);
        }
    }



}
