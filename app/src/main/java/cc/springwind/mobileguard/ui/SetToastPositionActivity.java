package cc.springwind.mobileguard.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.base.BaseActivity;
import cc.springwind.mobileguard.utils.Constants;
import cc.springwind.mobileguard.utils.SpTool;

/**
 * Created by HeFan on 2016/7/1.
 */
public class SetToastPositionActivity extends BaseActivity {
    @InjectView(R.id.iv_drag)
    ImageView ivDrag;
    @InjectView(R.id.bt_top)
    Button btTop;
    @InjectView(R.id.bt_bottom)
    Button btBottom;

    private WindowManager mWindowManager;
    private int height;
    private int width;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_position);
        ButterKnife.inject(this);

        initUI();
    }

    private void initUI() {

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        height = mWindowManager.getDefaultDisplay().getHeight();
        width = mWindowManager.getDefaultDisplay().getWidth();

        RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        int locationX = SpTool.getInt(getApplicationContext(), Constants.LOCATION_X, 0);
        int locationY = SpTool.getInt(getApplicationContext(), Constants.LOCATION_Y, 0);

        mLayoutParams.leftMargin = locationX;
        mLayoutParams.topMargin = locationY;

        ivDrag.setLayoutParams(mLayoutParams);

        if (locationY > height / 2) {
            btBottom.setVisibility(View.INVISIBLE);
            btTop.setVisibility(View.VISIBLE);
        } else {
            btBottom.setVisibility(View.VISIBLE);
            btTop.setVisibility(View.INVISIBLE);
        }

        ivDrag.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        int distanceX = moveX - startX;
                        int distanceY = moveY - startY;

                        int left = ivDrag.getLeft() + distanceX;//左侧坐标
                        int top = ivDrag.getTop() + distanceY;//顶端坐标
                        int right = ivDrag.getRight() + distanceX;//右侧坐标
                        int bottom = ivDrag.getBottom() + distanceY;//底部坐标

                        if (left < 0) {
                            return true;
                        }

                        //右边边缘不能超出屏幕
                        if (right > width) {
                            return true;
                        }

                        //上边缘不能超出屏幕可现实区域
                        if (top < 0) {
                            return true;
                        }

                        //下边缘(屏幕的高度-22 = 底边缘显示最大值)
                        if (bottom > height - 22) {
                            return true;
                        }

                        if (top > height / 2) {
                            btBottom.setVisibility(View.INVISIBLE);
                            btTop.setVisibility(View.VISIBLE);
                        } else {
                            btBottom.setVisibility(View.VISIBLE);
                            btTop.setVisibility(View.INVISIBLE);
                        }

                        //2,告知移动的控件,按计算出来的坐标去做展示
                        ivDrag.layout(left, top, right, bottom);

                        //3,重置一次其实坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        int ivleft = ivDrag.getLeft();
                        System.out.println("-->>ivleft:" + ivleft);
                        int ivtop = ivDrag.getTop();
                        System.out.println("-->>ivtop:" + ivtop);
                        SpTool.putInt(getApplicationContext(), Constants.LOCATION_X, ivleft);
                        SpTool.putInt(getApplicationContext(), Constants.LOCATION_Y, ivtop);
                        break;
                }
                return true;
            }
        });
    }
}
