package uama.hangzhou.gu.manager;

/**
 * Created by gujiajia on 2015/12/21.
 * Email:aileelucky@gmail.com
 */
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import uama.hangzhou.gu.listener.OverScrollListener;

public class RefreshGridLayoutManager extends GridLayoutManager {

    private OverScrollListener mListener;

    public RefreshGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public RefreshGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public RefreshGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scrollRange = super.scrollVerticallyBy(dy, recycler, state);

        mListener.overScrollBy(dy - scrollRange);

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
