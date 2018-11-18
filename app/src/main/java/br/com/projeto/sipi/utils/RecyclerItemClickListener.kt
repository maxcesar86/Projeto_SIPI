package br.com.projeto.sipi.utils

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class RecyclerItemClickListener(context: Context, recyclerView: RecyclerView, listener: OnItemClickListener) : RecyclerView.OnItemTouchListener{

    private var listener : OnItemClickListener? = listener
    private lateinit var gestureDetector : GestureDetector

    interface OnItemClickListener{
        fun onItemClick(view : View, position : Int)
        fun onLongItemClick(view : View, position: Int)
    }

    override fun onTouchEvent(rv: RecyclerView?, e: MotionEvent?) {
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val childView = rv.findChildViewUnder(e.x, e.y)
        if (childView != null && listener != null && gestureDetector.onTouchEvent(e)) {
            listener!!.onItemClick(childView, rv.getChildAdapterPosition(childView))
            return true
        }
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }

    init {
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val child = recyclerView.findChildViewUnder(e.x, e.y)
                if (child != null) {
                    listener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child))
                }
            }
        })
    }

}