package uama.hangzhou.gu.manager;

/**
 * Created by gujiajia on 2015/12/21.
 * Email:aileelucky@gmail.com
 */
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import uama.hangzhou.gu.listener.OverScrollListener;

public class RefreshLinearLayoutManager extends LinearLayoutManager {

    private OverScrollListener mListener;

    public RefreshLinearLayoutManager(Context context) {
        super(context);
    }

    public RefreshLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public RefreshLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scrollRange = super.scrollVerticallyBy(dy, recycler, state);
        if(mListener != null){
            mListener.overScrollBy(dy - scrollRange);
        }
        return scrollRange;
    }

    /**
     * 设置滑动过度监听
     *
     * @param listener
     */
    public void setOverScrollListener(OverScrollListener listener) {
        mListener = listener;
    }

}
