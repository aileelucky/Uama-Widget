package uama.hangzhou.gu.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import gu.hangzhou.uama.widget.R;
import uama.hangzhou.gu.listener.OverScrollListener;
import uama.hangzhou.gu.manager.RefreshGridLayoutManager;
import uama.hangzhou.gu.manager.RefreshLinearLayoutManager;
import uama.hangzhou.gu.manager.RefreshStaggeredGridLayoutManager;

/**
 * Created by 顾佳 on 2015/12/21.
 * Email:aileelucky@gmail.com
 */
public class RefreshRecyclerView extends RecyclerView implements Runnable {
    private Context mContext;

    private ArrayList<View> mHeaderViews;
    private ArrayList<View> mFootViews = new ArrayList<>();
    private Adapter mAdapter;

    private boolean isLoadingData = false; // 是否正在加载数据

    private LoadDataListener mLoadDataListener;

    private TextView tvLoadMore;
    private ProgressWheel pbLoadMore;
    private boolean canLoadMore;
    private Handler mHandler = new MyHandler();
    private OverScrollListener mOverScrollListener = new OverScrollListener() {
        @Override
        public void overScrollBy(int dy) {
            if (!isLoadingData) {
                mHandler.obtainMessage(0, dy, 0, null).sendToTarget();
                onScrollChanged(0, 0, 0, 0);
            }
        }
    };

    public RefreshRecyclerView(Context context) {
        this(context, null);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mHeaderViews = new ArrayList<>();
        setOverScrollMode(OVER_SCROLL_NEVER);
        canLoadMore = false;
        post(this);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
    }

    /**
     * 自定义Handler刷新数据
     */
    private static class MyHandler extends Handler {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    updateViewSize(msg);
                    break;
            }
        }

        private void updateViewSize(Message msg) {
            // 重新设置View的宽高
            if (msg.obj != null) {
                View view = ((View) msg.obj);
                view.layout(view.getLeft(), 0, view.getRight(), view.getBottom());
            }
        }
    }

    /**
     * 设置刷新和加载更多数据的监听
     *
     * @param listener {@link LoadDataListener}
     */
    public void setLoadDataListener(LoadDataListener listener) {
        mLoadDataListener = listener;
    }

    /**
     * 加载更多数据完成后调用，必须在UI线程中
     */
    public void loadMoreComplete() {
        isLoadingData = false;
        if (mFootViews.size() > 0) {
            mFootViews.get(0).setVisibility(GONE);
        }
    }

    //是否可以上拉加载
    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mFootViews.isEmpty()) {
            // 新建脚部
            LayoutInflater mInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout mFooterView = (RelativeLayout) mInflater.inflate(R.layout.load_more_footers, this, false);
            mFootViews.add(mFooterView);
            tvLoadMore = (TextView) mFooterView
                    .findViewById(R.id.no_more_textView);
            pbLoadMore = (ProgressWheel) mFooterView
                    .findViewById(R.id.load_more_progressBar);
            pbLoadMore.setVisibility(VISIBLE);

        }
        // 根据是否有头部/脚部视图选择适配器
        if (mFootViews.isEmpty()) {
            super.setAdapter(adapter);
        } else {
            adapter = new WrapAdapter(mFootViews, adapter);
            super.setAdapter(adapter);
        }
        mAdapter = adapter;
    }


    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
    }

    @Override
    public void run() {
        LayoutManager manager = getLayoutManager();
        if (manager instanceof RefreshLinearLayoutManager) {
            ((RefreshLinearLayoutManager) manager).setOverScrollListener(mOverScrollListener);
        } else if (manager instanceof RefreshGridLayoutManager) {
            layoutGridAttach((RefreshGridLayoutManager) manager);
        } else if (manager instanceof RefreshStaggeredGridLayoutManager) {
            layoutStaggeredGridHeadAttach((RefreshStaggeredGridLayoutManager) manager);
        }
        if (mAdapter != null && ((WrapAdapter) mAdapter).getFootersCount() > 0) {
            // 脚部先隐藏
            mFootViews.get(0).setVisibility(GONE);
        }
    }

    /**
     * 给StaggeredGridLayoutManager附加头部和滑动过度监听
     *
     * @param manager {@link RefreshStaggeredGridLayoutManager}
     */
    private void layoutStaggeredGridHeadAttach(RefreshStaggeredGridLayoutManager manager) {
        manager.setOverScrollListener(mOverScrollListener);
        // 从前向后查找Header并设置为充满一行
        View view;
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            if (((WrapAdapter) mAdapter).isHeader(i)) {
                view = getChildAt(i);
                ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams())
                        .setFullSpan(true);
                view.requestLayout();
            } else {
                break;
            }
        }
    }

    /**
     * 给{@link RefreshGridLayoutManager}附加头部脚部和滑动过度监听
     *
     * @param manager {@link RefreshGridLayoutManager}
     */
    private void layoutGridAttach(final RefreshGridLayoutManager manager) {
        // GridView布局
        manager.setOverScrollListener(mOverScrollListener);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return ((WrapAdapter) mAdapter).isHeader(position) ||
                        ((WrapAdapter) mAdapter).isFooter(position) ? manager.getSpanCount() : 1;
            }
        });
        requestLayout();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
//        if (headerImage == null) return;
//        View view = (View) headerImage.getParent();
//        // 上推的时候减小高度至默认高度
//        if (view.getTop() < 0 && headerImage.getLayoutParams().height > headerImageHeight) {
//            headerImage.getLayoutParams().height += view.getTop();
//            mHandler.obtainMessage(0, view.getTop(), 0, view).sendToTarget();
//        }
//
//        updateHeaderAlpha();

    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        // 当前不滚动，且不是正在刷新或加载数据
        if (state == RecyclerView.SCROLL_STATE_IDLE && mLoadDataListener != null && !isLoadingData) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            // 获取最后一个正在显示的Item的位置
            if (layoutManager instanceof RefreshGridLayoutManager) {
                lastVisibleItemPosition = ((RefreshGridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof RefreshStaggeredGridLayoutManager) {
                int[] into = new int[((RefreshStaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((RefreshStaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((RefreshLinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }

            if (layoutManager.getChildCount() > 0 && lastVisibleItemPosition >= layoutManager.getItemCount() - 1) {

                if (mFootViews.size() > 0) {
                    mFootViews.get(0).setVisibility(VISIBLE);
                    if (tvLoadMore != null && pbLoadMore != null) {
                        if (canLoadMore) {
                            // 加载更多
                            isLoadingData = true;
                            tvLoadMore.setVisibility(GONE);
                            pbLoadMore.setVisibility(VISIBLE);
                            mLoadDataListener.onLoadMore();
                        } else {
                            if (lastVisibleItemPosition >= 20) {
                                tvLoadMore.setVisibility(VISIBLE);
                            }
                            pbLoadMore.setVisibility(GONE);
                        }
                    }
                }
            }
        }
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                break;
        }

        return super.onTouchEvent(ev);
    }

    /**
     * 自定义带有头部/脚部的适配器
     */
    private class WrapAdapter extends RecyclerView.Adapter<ViewHolder> {

        private RecyclerView.Adapter mAdapter;
        private ArrayList<View> mFootViews;
        final ArrayList<View> EMPTY_INFO_LIST = new ArrayList<>();
        private int headerPosition = 0;

        public WrapAdapter(ArrayList<View> mFootViews, RecyclerView.Adapter mAdapter) {
            this.mAdapter = mAdapter;
            if (mFootViews == null) {
                this.mFootViews = EMPTY_INFO_LIST;
            } else {
                this.mFootViews = mFootViews;
            }
        }

        /**
         * @param position 位置
         * @return 当前布局是否为Header
         */
        public boolean isHeader(int position) {
            return position >= 0 && position < mHeaderViews.size();
        }

        /**
         * @param position 位置
         * @return 当前布局是否为Footer
         */
        public boolean isFooter(int position) {
            return position < getItemCount() && position >= getItemCount() - mFootViews.size();
        }

        /**
         * @return Header的数量
         */
        public int getHeadersCount() {
            return mHeaderViews.size();
        }

        /**
         * @return Footer的数量
         */
        public int getFootersCount() {
            return mFootViews.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == RecyclerView.INVALID_TYPE) {
                return new HeaderViewHolder(mHeaderViews.get(headerPosition++));
            } else if (viewType == RecyclerView.INVALID_TYPE - 1) {
                StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT, StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
                params.setFullSpan(true);
                mFootViews.get(0).setLayoutParams(params);
                return new HeaderViewHolder(mFootViews.get(0));
            }
            return mAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int numHeaders = getHeadersCount();
            if (position < numHeaders) {
                return;
            }
            int adjPosition = position - numHeaders;
            int adapterCount;
            if (mAdapter != null) {
                adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    mAdapter.onBindViewHolder(holder, adjPosition);
                }
            }
        }

        @Override
        public int getItemCount() {
            if (mAdapter != null) {
                return getHeadersCount() + getFootersCount() + mAdapter.getItemCount();
            } else {
                return getHeadersCount() + getFootersCount();
            }
        }

        @Override
        public int getItemViewType(int position) {
            int numHeaders = getHeadersCount();
            if (position < numHeaders) {
                return RecyclerView.INVALID_TYPE;
            }
            int adjPosition = position - numHeaders;
            int adapterCount;
            if (mAdapter != null) {
                adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemViewType(adjPosition);
                }
            }
            return RecyclerView.INVALID_TYPE - 1;
        }

        @Override
        public long getItemId(int position) {
            int numHeaders = getHeadersCount();
            if (mAdapter != null && position >= numHeaders) {
                int adjPosition = position - numHeaders;
                int adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        private class HeaderViewHolder extends RecyclerView.ViewHolder {
            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    /**
     * 刷新和加载更多数据的监听接口
     */
    public interface LoadDataListener {

        /**
         * 执行刷新
         */
//        void onRefresh();

        /**
         * 执行加载更多
         */
        void onLoadMore();

    }

    public void notifyData() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
