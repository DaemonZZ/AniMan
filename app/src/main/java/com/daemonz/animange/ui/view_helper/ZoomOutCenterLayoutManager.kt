package com.daemonz.animange.ui.view_helper

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.min

class ZoomOutCenterLayoutManager(context: Context) :
    LinearLayoutManager(context, HORIZONTAL, false) {
    private val mShrinkAmount = 0.55f
    private val mShrinkDistance = 0.95f

    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
        lp?.width = width / 3
        return true
    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
        scaleMiddle()
    }

    private fun scaleMiddle() {
        val orientation = orientation
        if (orientation == VERTICAL) {
            val midpoint = height / 2f
            val d0 = 0f
            val d1 = mShrinkDistance * midpoint
            val s0 = 1f
            val s1 = 1f - mShrinkAmount
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                val childMidpoint =
                    (getDecoratedBottom(child!!) + getDecoratedTop(child)) / 2f
                val d = min(d1.toDouble(), abs((midpoint - childMidpoint).toDouble()))
                    .toFloat()
                val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
                child.scaleX = scale
                child.scaleY = scale
            }

        } else {
            val midpoint = width / 2f
            val d0 = 0f
            val d1 = mShrinkDistance * midpoint
            val s0 = 1f
            val s1 = 1f - mShrinkAmount
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                val childMidpoint =
                    (getDecoratedRight(child!!) + getDecoratedLeft(child)) / 2f
                val d = min(d1.toDouble(), abs((midpoint - childMidpoint).toDouble()))
                    .toFloat()
                val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
                child.scaleX = scale
                child.scaleY = scale
            }
        }
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        val orientation = orientation
        if (orientation == VERTICAL) {
            val scrolled = super.scrollVerticallyBy(dy, recycler, state)
            scaleMiddle()
            return scrolled
        } else {
            return 0
        }
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        val orientation = orientation
        if (orientation == HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
            scaleMiddle()
            return scrolled
        } else {
            return 0
        }
    }
}