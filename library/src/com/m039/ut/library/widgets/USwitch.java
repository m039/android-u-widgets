/** USwitch.java ---
 *
 * Copyright (C) 2013 Mozgin Dmitry
 *
 * Author: Mozgin Dmitry <flam44@gmail.com>
 *
 *
 */

package com.m039.ut.library.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Checkable;

import com.m039.ut.library.uwidgets.R;

/**
 *
 *
 * Created: 02/27/13
 *
 * @author Mozgin Dmitry
 * @version
 * @since
 */
public class USwitch extends View
    implements Checkable
{

    public static final String TAG = "m039-USwitch";

    public USwitch(Context context) {
        super(context);
    }

    public USwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public static final int SIDE_LEFT = 0;
    public static final int SIDE_RIGHT = 1;

    int mOffSide = SIDE_LEFT;

    public void setOffSide(int side) {
        mOffSide = side;
    }


    public interface OnCheckedChangeListener {
        public void onCheckedChange (USwitch buttonView, boolean isChecked);
    }

    OnCheckedChangeListener mOnCheckedChangeListener = null;

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        mOnCheckedChangeListener = l;
    }

    static Drawable mThumb = null;
    static Drawable mHole = null;
    static Drawable mPlate = null;

    class Drawer {
        Drawable thumb;
        Drawable hole;
        Drawable plate;

        Bitmap offscreen;

        int halfThumbWidth = 0;
        int halfPlateWidth = 0;

        float x = 0;              // only horizontal

        Drawer(Drawable thumb, Drawable hole, Drawable plate) {
            this.thumb = thumb;
            this.hole = hole;
            this.plate = plate;

            offscreen = Bitmap.createBitmap(hole.getIntrinsicWidth(),
                                            hole.getIntrinsicHeight(),
                                            Bitmap.Config.ARGB_8888);

            halfThumbWidth = thumb.getIntrinsicWidth() / 2;
            halfPlateWidth = plate.getIntrinsicWidth() / 2;
        }

        void onDraw(Canvas canvas) {
            if (x <= halfThumbWidth) {
                x = halfThumbWidth;
            } else if(x >= hole.getIntrinsicWidth() - halfThumbWidth) {
                x = hole.getIntrinsicWidth() - halfThumbWidth;
            }

            canvas.save(Canvas.ALL_SAVE_FLAG);

            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.translate(-halfPlateWidth + x, 0);
            plate.draw(canvas);
            canvas.restore();

            hole.draw(canvas);

            // offscrren

            // Canvas oc = new Canvas(offscreen);

            // hole.draw(oc);

            // oc.save(Canvas.MATRIX_SAVE_FLAG);
            // oc.translate(-halfPlateWidth + x, 0);
            // plate.draw(oc);
            // oc.restore();



            //
            // Remove magenta color
            //
            // Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            // p.setColor(Color.MAGENTA);
            // p.setAlpha(255);
            // p.setXfermode(new AvoidXfermode(Color.MAGENTA, 0, AvoidXfermode.Mode.TARGET));
            // oc.drawPaint(p);

            // p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
            // oc.drawPaint(p);

            // oc.drawPaint(p);
            // p.setColor(Color.argb(255,
            //                       Color.red(Color.MAGENTA),
            //                       Color.green(Color.MAGENTA),
            //                       Color.blue(Color.MAGENTA)));

            // canvas.drawBitmap(offscreen, 0, 0, null);

            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.translate(-halfThumbWidth + x, 0);
            thumb.draw(canvas);
            canvas.restore();

            canvas.restore();
        }

        boolean isOn() {
            float v = getValue();

            if (mOffSide == SIDE_LEFT) {
                return (v >= 1);
            } else if(mOffSide == SIDE_RIGHT) {
                return (v <= 0);
            } else {
                return false;
            }
        }

        void setOff() {
            if (mOffSide == SIDE_LEFT) {
                setValue(0);
            } else if(mOffSide == SIDE_RIGHT) {
                setValue(1);
            }
        }

        void setOn() {
            if (mOffSide == SIDE_LEFT) {
                setValue(1);
            } else if(mOffSide == SIDE_RIGHT) {
                setValue(0);
            }
        }


        void setChecked(boolean checked) {
            if (checked) {
                mDrawer.setOn();
            } else {
                mDrawer.setOff();
            }
        }


        /**
         * left is from 0.0f to 1.0f
         */
        void setValue(float left) {
            x = hole.getIntrinsicWidth() * left;
            postInvalidate();
        }

        float getValue() {
            return x / hole.getIntrinsicWidth();
        }


        boolean isLeftSide(float left) {
            return left < hole.getIntrinsicWidth() / 2 ;
        }

        boolean isRightSide(float right) {
            return !isLeftSide(right);
        }
    }

    class DrawerAnimator {
        ObjectAnimator mThumbPositionAnimator = new ObjectAnimator();

        {
            mThumbPositionAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd (Animator animation) {
                        boolean on = mDrawer.isOn();

                        Log.d(TAG, "onAnimationEnd, mDrawer.isOn = " + on);

                        if (on) {
                            USwitch.this.setChecked(true, false);
                        } else {
                            USwitch.this.setChecked(false, false);
                        }
                    }
                });
        }

        void moveOff() {
            if (mOffSide == SIDE_LEFT) {
                move(0);
            } else if(mOffSide == SIDE_RIGHT) {
                move(1);
            }
        }

        void moveOn() {
            if (mOffSide == SIDE_LEFT) {
                move(1);
            } else if(mOffSide == SIDE_RIGHT) {
                move(0);
            }
        }

        void move(float v) {
            ObjectAnimator animator = mThumbPositionAnimator;

            animator.cancel();

            Float from = mDrawer.getValue();
            float to = v;

            animator.setTarget(mDrawer);
            animator.setPropertyName("value");
            animator.setFloatValues(from, to);
            animator.setDuration(300);
            animator.start();
        }


        void setChecked(boolean checked) {
            if (checked) {
                moveOn();
            } else {
                moveOff();
            }
        }

        void cancel() {
            ObjectAnimator animator = mThumbPositionAnimator;
            animator.cancel();
        }

        void onDetachedFromWindow () {
            ObjectAnimator animator = mThumbPositionAnimator;
            animator.cancel();
            animator.setTarget(null);
        }
    }

    Drawer mDrawer = null;
    DrawerAnimator mDrawerAnimator = new DrawerAnimator();

    {
        Resources res = getResources();

        if (mThumb == null) {
            mThumb = res.getDrawable(R.drawable.uwidgets__uswitch__thumb);
        }

        if (mHole == null) {
            mHole = res.getDrawable(R.drawable.uwidgets__uswitch__hole);
            // mHole.setColorFilter(Color.MAGENTA, PorterDuff.Mode.XOR);
        }

        if (mPlate == null) {
            mPlate = res.getDrawable(R.drawable.uwidgets__uswitch__plate);
            // mPlate.setColorFilter(Color.MAGENTA, PorterDuff.Mode.CLEAR);
        }

        for(Drawable d : new Drawable[] {
                mThumb, mHole, mPlate
                }) {
            if (d != null) {
                setDefaultBounds(d);
            }
        }

        if (mThumb != null && mHole != null && mPlate != null) {
            mDrawer = new Drawer(mThumb, mHole, mPlate);
            mDrawer.setChecked(isChecked());
        }
    }

    float mDownX = 0;
    float mDownValue = 0;
    boolean mDownLeft = false;
    boolean mMoved = false;
    
    @Override
    public boolean onTouchEvent (MotionEvent ev) {
        boolean result = super.onTouchEvent(ev);

        Log.d(TAG, "onTouchEvent: ev = " + ev);

        int action = ev.getActionMasked ();

        if (mDrawer != null) {

            if (action == MotionEvent.ACTION_DOWN) {

                mDownX = ev.getX();
                mDownLeft = mDrawer.isLeftSide(mDrawer.x);
                mDownValue = mDrawer.getValue();

                mDrawerAnimator.cancel();

                return true;

            } else if (action == MotionEvent.ACTION_MOVE) {

                float x;

                if (mDownLeft) {
                    x = mDownValue + (ev.getX() - mDownX) / getWidth();
                } else {
                    x = mDownValue - (mDownX - ev.getX()) / getWidth();
                }

                mDrawer.setValue(x);
                mMoved = true;

                return true;

            } else  if (action == MotionEvent.ACTION_CANCEL) {              

                if (mMoved) {
                
                    if (mDownLeft) {
                        mDrawerAnimator.move(0);
                    } else {
                        mDrawerAnimator.move(1);
                    }

                    mMoved = false;
                }
                
            } else  if (action == MotionEvent.ACTION_UP) {

                if (mDrawer.isLeftSide(mDrawer.x)) {
                    mDrawerAnimator.move(mMoved ? 0 : 1);
                } else {
                    mDrawerAnimator.move(mMoved ? 1 : 0);
                }

                mMoved = false;

                return true;
            }

        }

        return result;
    }

    void setDefaultBounds(Drawable d) {
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
    }



    boolean mIsChecked = false;

    @Override
    public boolean isChecked() {
        return mIsChecked;
    }

    @Override
    public void setChecked(boolean checked)  {
        setChecked(checked, false);
    }

    public void setChecked(boolean checked, boolean animate) {
        if (mIsChecked != checked) {

            if (!animate) {

                if (mOnCheckedChangeListener != null) {
                    mOnCheckedChangeListener.onCheckedChange(this, checked);
                }

                if (mDrawer != null) {
                    mDrawer.setChecked(checked);
                }

                mIsChecked = checked;

            } else {

                if (mDrawer != null) {
                    mDrawerAnimator.setChecked(checked);
                }

            }
        }
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }


    @Override
    public void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = 0;

        int widthMode = MeasureSpec.EXACTLY;
        int heightMode = MeasureSpec.EXACTLY;

        for(Drawable d : new Drawable[] {
                mHole
                }) {
            if (d != null) {
                width = Math.max(width, d.getIntrinsicWidth());
                height = Math.max(height, d.getIntrinsicHeight());
            }
        }

        Log.d(TAG, String.format("onMeasure: w = %s, h = %s", width, height));

        super.onMeasure(MeasureSpec.makeMeasureSpec(width, widthMode),
                        MeasureSpec.makeMeasureSpec(height, heightMode));
    }

    @Override
    protected void onDetachedFromWindow () {
        super.onDetachedFromWindow();

        mDrawerAnimator.onDetachedFromWindow();
    }

    @Override
    protected void onDraw (Canvas canvas) {

        if (mDrawer != null) {
            mDrawer.onDraw(canvas);
        }

        super.onDraw(canvas);       
    }

    //
    // Parcelable
    //

    public static final String EXTRA_CHECKED = "com.m039.ut.library.widgets.extra.checked";
    public static final String EXTRA_SUPER_STATE = "com.m039.ut.library.widgets.extra.super_state";

    @Override
    protected void onRestoreInstanceState (Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            setChecked(bundle.getBoolean(EXTRA_CHECKED, mIsChecked));
            super.onRestoreInstanceState(bundle.getParcelable(EXTRA_SUPER_STATE));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState () {
        Bundle state = new Bundle();

        state.putBoolean(EXTRA_CHECKED, mIsChecked);
        state.putParcelable(EXTRA_SUPER_STATE, super.onSaveInstanceState());

        return state;
    }

} // USwitch
