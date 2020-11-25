package com.lg.animdialogfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

/**
 *  author: ligang@duia.com create time:  2020/11/25 17:43
 *  tag: class//
 *  description:
 */
class DemoDialog : BaseDialogHelper() {

    private var data: ArrayList<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ai_dialog_banji_living_red, container, false)
    }

    companion object {
        fun getInstance(isBack: Boolean, isTouch: Boolean, gravity: Int): DemoDialog {
            val dialogFragment = DemoDialog()
            dialogFragment.mCanceledBack = isBack
            dialogFragment.mCanceledOnTouchOutside = isTouch
            dialogFragment.mGravity = gravity
            return dialogFragment
        }
    }

    fun setData(data: ArrayList<String>?): DemoDialog? {
        this.data = data
        return this
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            data = savedInstanceState.getStringArrayList("title")
        }
        if (view != null) {
            val tv = view!!.findViewById<TextView>(R.id.tv_content)
            if (data != null) {
                val buffer = StringBuilder()
                for (tmp in data!!) {
                    buffer.append(tmp)
                    buffer.append("\n")
                }
                tv.text = buffer.toString()
            }
            view!!.findViewById<View>(R.id.cl_root).setOnClickListener {
                dismiss()
            }
            view!!.findViewById<View>(R.id.v_click).setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (data != null) {
            outState.putStringArrayList("title", data)
        }
    }
}