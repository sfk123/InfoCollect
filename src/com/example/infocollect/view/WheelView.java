package com.example.infocollect.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;




import java.util.ArrayList;
import java.util.List;

import com.example.infocollect.R;
import com.example.infocollect.util.MyUtil;



public class WheelView extends ScrollView {
    public static final String TAG = WheelView.class.getSimpleName();

    public static class OnWheelViewListener {
        public void onSelected(int selectedIndex, String item) {
        }

        ;
    }


    private Context context;
//    private ScrollView scrollView;

    private LinearLayout views;

    public WheelView(Context context) {
        super(context);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    //    String[] items;
    List<String> items;

    private List<String> getItems() {
        return items;
    }


    /**
     * 锟斤拷锟斤拷锟芥换锟斤拷锟斤拷示锟斤拷锟斤拷图锟斤拷锟斤拷锟斤拷锟节碉拷一锟斤拷wheelView锟斤拷setOnWheelViewListener锟斤拷锟斤拷锟叫碉拷锟矫ｏ拷锟芥换锟节讹拷锟斤拷wheelView锟斤拷锟斤拷示锟斤拷锟斤拷图
     */
    public void replace(List<String> list){
        this.removeAllViews();
        init(context);
        setItems(list);
    }

    public void setItems(List<String> list) {
        if (null == items) {
            items = new ArrayList<String>();
        }
        items.clear();
        if(list!=null){
            items.addAll(list);

        }
        // 前锟斤拷秃锟斤拷娌谷�
        for (int i = 0; i < offset; i++) {
            items.add(0, new String());
            items.add(new String());
        }

        initData();

    }


    public static final int OFF_SET_DEFAULT = 1;
    int offset = OFF_SET_DEFAULT;  // 偏锟斤拷锟斤拷锟斤拷锟斤拷要锟斤拷锟斤拷前锟斤拷锟斤拷锟斤拷锟芥补全锟斤拷

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    int displayItemCount;  // 每页锟斤拷示锟斤拷锟斤拷锟斤拷

    int selectedIndex = 1;


    private void init(Context context) {
        this.context = context;
        this.setVerticalScrollBarEnabled(false);

        views = new LinearLayout(context);
        views.setOrientation(LinearLayout.VERTICAL);
        this.addView(views);

        scrollerTask = new Runnable() {

            public void run() {

                int newY = getScrollY();
                if (initialY - newY == 0) { // stopped
                    if(itemHeight==0){
                        itemHeight=1;
                    }
                    final int remainder = initialY % itemHeight;
                    final int divided = initialY / itemHeight;
                    if (remainder == 0) {
                        selectedIndex = divided + offset;

                        onSeletedCallBack();
                    } else {
                        if (remainder > itemHeight / 2) {
                            WheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    WheelView.this.smoothScrollTo(0, initialY - remainder + itemHeight);
                                    selectedIndex = divided + offset + 1;
                                    onSeletedCallBack();
                                }
                            });
                        } else {
                            WheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    WheelView.this.smoothScrollTo(0, initialY - remainder);
                                    selectedIndex = divided + offset;
                                    onSeletedCallBack();
                                }
                            });
                        }
                    }
                } else {
                    initialY = getScrollY();
                    WheelView.this.postDelayed(scrollerTask, newCheck);
                }
            }
        };
        startScrollerTask();
    }

    int initialY;

    Runnable scrollerTask;
    int newCheck = 50;

    public void startScrollerTask() {

        initialY = getScrollY();
        this.postDelayed(scrollerTask, newCheck);
    }

    private void initData() {
        displayItemCount = offset * 2 + 1;

        for (String item : items) {
            views.addView(createView(item));
        }

        refreshItemView(0);
    }

    int itemHeight = 0;

    private TextView createView(String item) {
        TextView tv = new TextView(context);
        tv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setSingleLine(true);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tv.setText(item);
        tv.setGravity(Gravity.CENTER);
        int padding = MyUtil.dip2px(context, 5);
        tv.setPadding(padding, padding, padding, padding);
        if (0 == itemHeight) {
            itemHeight = MyUtil.getViewMeasuredHeight(tv);
            views.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.getLayoutParams();
            this.setLayoutParams(new LinearLayout.LayoutParams(lp.width, itemHeight * displayItemCount));
        }
        return tv;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        refreshItemView(t);

        if (t > oldt) {
//          Logger.d(TAG, "锟斤拷锟铰癸拷锟斤拷");
            scrollDirection = SCROLL_DIRECTION_DOWN;
        } else {
//          Logger.d(TAG, "锟斤拷锟较癸拷锟斤拷");
            scrollDirection = SCROLL_DIRECTION_UP;

        }


    }

    private void refreshItemView(int y) {
        int position = y / itemHeight + offset;
        int remainder = y % itemHeight;
        int divided = y / itemHeight;

        if (remainder == 0) {
            position = divided + offset;
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1;
            }
        }

        int childSize = views.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) views.getChildAt(i);
            if (null == itemView) {
                return;
            }
            if (position == i) {
            	//选锟叫碉拷TextView锟斤拷色
                itemView.setTextColor(Color.parseColor("#000000"));
            } else {
            	 //未选锟叫碉拷TextView锟斤拷色
                itemView.setTextColor(Color.parseColor("#bbbbbb"));
            }
        }
    }

    /**
     * 鑾峰彇閫変腑鍖哄煙鐨勮竟锟�
     */
    int[] selectedAreaBorder;

    private int[] obtainSelectedAreaBorder() {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = new int[2];
            selectedAreaBorder[0] = itemHeight * offset;
            selectedAreaBorder[1] = itemHeight * (offset + 1);
        }
        return selectedAreaBorder;
    }


    private int scrollDirection = -1;
    private static final int SCROLL_DIRECTION_UP = 0;
    private static final int SCROLL_DIRECTION_DOWN = 1;

    Paint paint;
    int viewWidth;

    @Override
    public void setBackgroundDrawable(Drawable background) {

        if (viewWidth == 0) {
            viewWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        }

        if (null == paint) {
            paint = new Paint();
          //锟街革拷锟竭碉拷锟斤拷色
            paint.setColor(context.getResources().getColor(R.color.line_color));
            paint.setStrokeWidth(MyUtil.dip2px(context, 1f));
        }

        background = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
            	//锟街革拷锟竭筹拷锟斤拷
//                canvas.drawLine(viewWidth * 1/6, obtainSelectedAreaBorder()[0], viewWidth * 5/6, obtainSelectedAreaBorder()[0], paint);
//                canvas.drawLine(viewWidth * 1/6, obtainSelectedAreaBorder()[1], viewWidth * 5/6, obtainSelectedAreaBorder()[1], paint);
                canvas.drawLine(0, obtainSelectedAreaBorder()[0], viewWidth, obtainSelectedAreaBorder()[0], paint);
                canvas.drawLine(0, obtainSelectedAreaBorder()[1], viewWidth , obtainSelectedAreaBorder()[1], paint);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }
        };
        super.setBackgroundDrawable(background);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        setBackgroundDrawable(null);
    }

    /**
     * 閫変腑鍥炶皟
     */
    private void onSeletedCallBack() {
        if (null != onWheelViewListener) {
//        	System.out.println("閫変腑锟�"+items.get(selectedIndex));
            onWheelViewListener.onSelected(selectedIndex, items.get(selectedIndex));
        }

    }

    public void setSeletion(int position) {
        final int p = position;
        selectedIndex = p + offset;
        this.post(new Runnable() {
            @Override
            public void run() {
                WheelView.this.smoothScrollTo(0, p * itemHeight);
            }
        });

    }

    public String getSeletedItem() {
        return items.get(selectedIndex);
    }

    public int getSeletedIndex() {
        return selectedIndex - offset;
    }


    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            startScrollerTask();
        }
        return super.onTouchEvent(ev);
    }

    private OnWheelViewListener onWheelViewListener;

    public OnWheelViewListener getOnWheelViewListener() {
        return onWheelViewListener;
    }

    public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
        this.onWheelViewListener = onWheelViewListener;
    }


}



