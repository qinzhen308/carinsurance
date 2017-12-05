package com.paulz.carinsurance.common.recyclerview;

/**
 * Created by Paul Z on 2017/5/25.
 */

public interface IViewModel<T> {

    public void bindData(int position, T data);
}
