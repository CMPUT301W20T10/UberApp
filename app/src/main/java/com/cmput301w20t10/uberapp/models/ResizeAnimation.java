package com.cmput301w20t10.uberapp.models;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;


/*
 * ResizeAnimation class is code from Stack overflow used to change size of listview item during expansion
 * URL of question: https://stackoverflow.com/questions/12522348
 * Asked by: Ethan Allen, https://stackoverflow.com/users/546509/ethan-allen
 * Answered by: Leonardo Cardoso, https://stackoverflow.com/users/1255990/leonardo-cardoso
 */
public class ResizeAnimation extends Animation {
    private View mView;
    private float mToHeight;
    private float mFromHeight;

    private float mToWidth;
    private float mFromWidth;

    private ArrayAdapter<RideRequest_old> mRequestAdapter;
    private RideRequest_old mRequestItem;

    /**
     * Animation of expanding and collapsing of listitem
     * @param requestAdapter
     * @param rideRequest
     * @param fromWidth
     * @param fromHeight
     * @param toWidth
     * @param toHeight
     */
    public ResizeAnimation(ArrayAdapter<RideRequest_old> requestAdapter, RideRequest_old rideRequest, float fromWidth, float fromHeight, float toWidth, float toHeight) {
        mToHeight = toHeight;
        mToWidth = toWidth;
        mFromHeight = fromHeight;
        mFromWidth = fromWidth;
        mView = rideRequest.getHolder().getTextViewWrap();
        mRequestAdapter = requestAdapter;
        mRequestItem = rideRequest;
        setDuration(200);
    }

    /**
     * Transformation of listitem
     * @param interpolatedTime
     * @param t
     */
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float height = (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
        float width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth;
        AbsListView.LayoutParams p = (AbsListView.LayoutParams) mView.getLayoutParams();
        p.height = (int) height;
        p.width = (int) width;
        mRequestItem.setCurrentHeight(p.height);
        mRequestAdapter.notifyDataSetChanged();
    }
}

