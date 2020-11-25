package com.lg.animdialogfragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 *  author: ligang@duia.com create time:  2020/11/25 17:09
 *  tag: class//
 *  description:
 */
@Suppress("PrivatePropertyName", "DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
public abstract class BaseDialogHelper : DialogFragment(), DialogInterface.OnKeyListener,
    DialogInterface.OnShowListener {
    var mWindow: Window? = null
    private val SAVED_GRAVITY = "circle:baseGravity"
    private val SAVED_TOUCH_OUT = "circle:baseTouchOut"
    private val SAVED_CANCELED_BACK = "circle:baseCanceledBack"
    private val SAVED_WIDTH = "circle:baseWidth"
    private val SAVED_ANIM_STYLE = "circle:baseAnimStyle"
    private val SAVED_DIM_ENABLED = "circle:baseDimEnabled"

    var mGravity = Gravity.CENTER //对话框的位置
    var mCanceledOnTouchOutside = true //是否触摸外部关闭
    var mCanceledBack = true //是否返回键关闭

    var mWidth:Float = -1f
    var mHeight:Float = -1f //对话框宽度，范围：0-1；1整屏宽

    var mAnimStyle = 0
    private var isDimEnabled = true
    var cancelListener: DialogInterface.OnCancelListener? = null
    var dismissListener: DialogInterface.OnDismissListener? = null
    var animDismiss: OnAnimDismissListener? = null
    var showListener: OnAnimShowListener? = null

    abstract override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (dialog == null) {  // Returns mDialog
            showsDialog = false
        }
        try {
            super.onActivityCreated(savedInstanceState)
        } catch (e: Throwable) {
        } finally {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置 无标题 无边框
        setStyle(STYLE_NO_TITLE, 0)
        if (savedInstanceState != null) {
            mGravity = savedInstanceState.getInt(SAVED_GRAVITY)
            mCanceledOnTouchOutside = savedInstanceState.getBoolean(SAVED_TOUCH_OUT)
            mCanceledBack = savedInstanceState.getBoolean(SAVED_CANCELED_BACK)
            mWidth = savedInstanceState.getFloat(SAVED_WIDTH)
            mAnimStyle = savedInstanceState.getInt(SAVED_ANIM_STYLE)
            isDimEnabled = savedInstanceState.getBoolean(SAVED_DIM_ENABLED)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SAVED_GRAVITY, mGravity)
        outState.putBoolean(SAVED_TOUCH_OUT, mCanceledOnTouchOutside)
        outState.putBoolean(SAVED_CANCELED_BACK, mCanceledBack)
        outState.putFloat(SAVED_WIDTH, mWidth)
        outState.putInt(SAVED_ANIM_STYLE, mAnimStyle)
        outState.putBoolean(SAVED_DIM_ENABLED, isDimEnabled)
    }

    override fun onStart() {
        if (dialog != null) {
            dialog?.setCanceledOnTouchOutside(mCanceledOnTouchOutside)
            dialog?.setCancelable(mCanceledBack)
            setDialogGravity(dialog!!) //设置对话框布局
            dialog?.setOnKeyListener(this)
            dialog?.setOnShowListener(this)
        }
        super.onStart()
    }

    /**
     * 设置对话框底部显示
     */
    private fun setDialogGravity(dialog: Dialog) {
        // 设置宽度为屏宽、靠近屏幕底部。
        mWindow = dialog.window
        mWindow!!.setBackgroundDrawableResource(android.R.color.transparent)
        val wlp = mWindow!!.attributes
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm) //获取屏幕宽
        if (mWidth != -1f) {
            wlp.width = (dm.widthPixels * mWidth).toInt() //宽度按屏幕大小的百分比设置
        }
        if (mHeight != -1f) {
            wlp.height = (dm.heightPixels * mHeight).toInt() //宽度按屏幕大小的百分比设置
        }
        wlp.gravity = mGravity
//        //动画
        if (mAnimStyle != 0) {
            mWindow!!.setWindowAnimations(mAnimStyle)
        } else {
            mWindow!!.setWindowAnimations(R.style.V489DialogWindowAnim)
        }
        if (isDimEnabled) {
            mWindow!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        } else {
            mWindow!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
        mWindow!!.attributes = wlp
    }

    @Synchronized
    override fun show(manager: FragmentManager, tag: String?) {
        if (!isAdded) {
            val isShow: Boolean
            val transaction = manager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            isShow = try {
                super.show(transaction, tag)
                true
            } catch (e: Throwable) {
                Log.e("LG", "show dialogfragment出错：" + e.message)
                false
            }
            if (!isShow) {
                try {
                    showDialogFragmentAllowStateloss(this)
                    val fragmentTransaction = manager.beginTransaction()
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    fragmentTransaction.add(this, tag)
                    fragmentTransaction.commitAllowingStateLoss()
                } catch (e: Throwable) {
                    Log.e("LG", "fragment add dialogfragment出错：" + e.message)
                }
            }
        }
    }

    open fun showDialogFragmentAllowStateloss(fragment: DialogFragment) {
        var superclass: Class<*> = fragment.javaClass.superclass
        Log.e("BaseDialogHelper", "simpleName = " + superclass.simpleName)
        while (superclass.simpleName != DialogFragment::class.java.simpleName) {
            superclass = superclass.superclass
            Log.e("BaseDialogHelper", "superclass.getSuperclass = " + superclass.simpleName)
        }
        try {
            val mShownByMeField = superclass.getDeclaredField("mShownByMe")
            mShownByMeField.isAccessible = true
            mShownByMeField.setBoolean(fragment, true)
            val mDismissedField = superclass.getDeclaredField("mDismissed")
            mDismissedField.isAccessible = true
            mDismissedField.setBoolean(fragment, false)
        } catch (var6: NoSuchFieldException) {
            Log.d("BaseDialogHelper", Log.getStackTraceString(var6))
            var6.printStackTrace()
        } catch (var7: IllegalAccessException) {
            var7.printStackTrace()
            Log.d("BaseDialogHelper", Log.getStackTraceString(var7))
        }
    }

    override  fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        if (cancelListener != null) {
            cancelListener!!.onCancel(dialog)
        }
    }

   override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (dismissListener != null) {
            dismissListener!!.onDismiss(dialog)
        }
    }

    override fun dismiss() {
        if (activity != null && !activity!!.isFinishing) {
            if (animDismiss != null) {
                animDismiss!!.onDismiss(view)
            } else {
                super.dismissAllowingStateLoss()
                if (dismissListener != null) {
                    dismissListener!!.onDismiss(null)
                }
            }
        }
//        super.dismiss();
    }

    override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss()
            return true
        }
        return false
    }

    override fun onShow(dialog: DialogInterface?) {
        if (showListener != null) {
            showListener!!.onShow(view)
        }
    }
}