package uama.hangzhou.gu.zoom;

/**
 * Created by gujiajia on 2015/5/9.
 */
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class ImageViewPager extends ViewPager {

    private static final String TAG = "HackyViewPager";

    public ImageViewPager(Context context) {
        super(context);
    }

    public ImageViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            // don't mind
            Log.e(TAG, "hacky viewpager error1");
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            //  don't mind
            Log.e(TAG, "hacky viewpager error2");
            return false;
        }
    }

}