package com.cmput301w20t10.uberapp.activities;

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

    private ArrayAdapter<RideRequest> mListAdapter;
    private RideRequest mListItem;

    public ResizeAnimation(ArrayAdapter<RideRequest> listAdapter, RideRequest listItem, float fromWidth, float fromHeight, float toWidth, float toHeight) {
        mToHeight = toHeight;
        mToWidth = toWidth;
        mFromHeight = fromHeight;
        mFromWidth = fromWidth;
        mView = listItem.getHolder().getTextViewWrap();
        mListAdapter = listAdapter;
        mListItem = listItem;
        setDuration(200);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float height = (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
        float width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth;
        AbsListView.LayoutParams p = (AbsListView.LayoutParams) mView.getLayoutParams();
        p.height = (int) height;
        p.width = (int) width;
        mListItem.setCurrentHeight(p.height);
        mListAdapter.notifyDataSetChanged();
    }
}

