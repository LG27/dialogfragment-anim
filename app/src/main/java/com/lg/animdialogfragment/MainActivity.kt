package com.lg.animdialogfragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.iv_dialog).setOnClickListener {
            var data :ArrayList<String> = ArrayList()
            data.add("测试")
            data.add("你好")
            data.add("欢迎")
            val dialog = DemoDialog.getInstance(true, false, Gravity.CENTER)
                .setData(data)
            dialog!!.mWidth = 1f
            dialog.mHeight = 1f
            dialog.mAnimStyle=0
            dialog.showListener = object : OnAnimShowListener {
                    override fun onShow(rootView: View?) {
                        if (rootView != null) {
                            val height: Int =getScreenHeight() / 2 - getStatusBarHeight() - dip2px(15f)
                            val width: Float = getScreenWidth() / 2f - dip2px(30f)
                            val anim1 = ObjectAnimator
                                .ofPropertyValuesHolder(
                                    rootView,
                                    PropertyValuesHolder.ofFloat(
                                        "translationX",
                                        0f,
                                        width,
                                        width,
                                        0f
                                    ),
                                    PropertyValuesHolder.ofFloat(
                                        "translationY",
                                        0f,
                                        -height.toFloat(),
                                        -height.toFloat(),
                                        0f
                                    ),
                                    PropertyValuesHolder.ofFloat("scaleY", 0f, 0f, 0f, 1f),
                                    PropertyValuesHolder.ofFloat("scaleX", 0f, 0f, 0f, 1f),
                                    PropertyValuesHolder.ofFloat("alpha", 0f, 0f, 0.2f, 1f)
                                )
                            val animSet1 = AnimatorSet()
                            animSet1.play(anim1)
                            animSet1.duration = 900
                            animSet1.interpolator = DecelerateInterpolator()
                            animSet1.addListener(object : Animator.AnimatorListener {
                                override fun onAnimationStart(animation: Animator) {
                                    dialog.mAnimStyle =0
                                }

                                override fun onAnimationEnd(animation: Animator) {}
                                override fun onAnimationCancel(animation: Animator) {}
                                override fun onAnimationRepeat(animation: Animator) {}
                            })
                            animSet1.start()
                        }
                    }
            }
            dialog.animDismiss = (object : OnAnimDismissListener{
                override fun onDismiss(rootView: View?) {
                    if (rootView != null) {
                        val height: Int =getScreenHeight() / 2 - getStatusBarHeight() - dip2px(15f)
                        val width: Float = getScreenWidth() / 2f - dip2px(30f)
                        val anim = ObjectAnimator
                            .ofPropertyValuesHolder(
                                rootView,
                                PropertyValuesHolder.ofFloat("translationX", width),
                                PropertyValuesHolder.ofFloat("translationY", -height.toFloat()),
                                PropertyValuesHolder.ofFloat("scaleY", 0.01f),
                                PropertyValuesHolder.ofFloat("scaleX", 0.01f),
                                PropertyValuesHolder.ofFloat("alpha", 0.2f)
                            )
                        val animSet = AnimatorSet()
                        animSet.play(anim)
                        animSet.duration = 500
                        animSet.addListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {
                                dialog.mAnimStyle =0
                            }

                            override fun onAnimationEnd(animation: Animator) {
                                dialog.animDismiss = null
                                dialog.dismiss()
                            }

                            override fun onAnimationCancel(animation: Animator) {}
                            override fun onAnimationRepeat(animation: Animator) {}
                        })
                        animSet.start()
                    } else {
                        dialog.animDismiss = null
                        dialog.dismiss()
                    }
                }
            })
            dialog.show(supportFragmentManager, "")
        }
    }
    @SuppressLint("ObsoleteSdkInt")
    fun getScreenHeight(): Int {
        val wm =this.getSystemService(WINDOW_SERVICE) as WindowManager
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealSize(point)
        } else {
            wm.defaultDisplay.getSize(point)
        }
        return point.y
    }
    fun getStatusBarHeight(): Int {
        // 一般是25dp
        var height: Int = dip2px(20f)
        //获取status_bar_height资源的ID
        val resourceId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            height = this.resources.getDimensionPixelSize(resourceId)
        }
        return height
    }
    fun dip2px(dpValue: Float): Int {
        val scale: Float = this.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
    @SuppressLint("ObsoleteSdkInt")
    fun getScreenWidth(): Int {
        val wm = this.getSystemService(WINDOW_SERVICE) as WindowManager
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealSize(point)
        } else {
            wm.defaultDisplay.getSize(point)
        }
        return point.x
    }
}