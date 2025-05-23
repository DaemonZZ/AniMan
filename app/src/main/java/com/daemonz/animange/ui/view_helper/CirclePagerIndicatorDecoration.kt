package com.daemonz.animange.ui.view_helper

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.daemonz.animange.log.ALog
import com.google.android.material.carousel.CarouselLayoutManager

class CirclePagerIndicatorDecoration : RecyclerView.ItemDecoration() {
    companion object {
        private val DP = Resources.getSystem().displayMetrics.density
        private const val TAG = "CirclePagerIndicatorDec"
    }

    private val colorActive = Color.parseColor("#615A58")
    private val colorInactive = Color.parseColor("#BBAFAC")
    private val mIndicatorHeight = (DP * 32).toInt()
    private val mIndicatorStrokeWidth = DP * 8
    private val mIndicatorItemLength = DP * 2
    private val mIndicatorItemPadding = DP * 12
    private val mInterpolator = AccelerateDecelerateInterpolator()
    private val mPaint = Paint()

    init {
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeWidth = mIndicatorStrokeWidth
        mPaint.style = Paint.Style.STROKE
        mPaint.isAntiAlias = true
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        // center horizontally, calculate width and subtract half from center
        val itemCount = parent.adapter?.itemCount ?: 0
        val totalLength = mIndicatorItemLength * itemCount
        val paddingBetweenItems = 0.coerceAtLeast(itemCount - 1) * mIndicatorItemPadding
        val indicatorTotalWidth = totalLength + paddingBetweenItems
        val indicatorStartX = (parent.width - indicatorTotalWidth) / 2f
        // center vertically in the allotted space
        val indicatorPosY = parent.height - mIndicatorHeight / 2f

        drawInactiveIndicators(c, indicatorStartX, indicatorPosY, itemCount)

        // find active page (which should be highlighted)
        val layoutManager = parent.layoutManager as CarouselLayoutManager?
        layoutManager?.let {
            val activePosition = getActivePosition(it)
            if (activePosition == RecyclerView.NO_POSITION) {
                return
            }
            // find offset of active page (if the user is scrolling)
            val activeChild = it.findViewByPosition(activePosition)
            ALog.i(TAG, "onDrawOver: $activePosition")
            val left = activeChild!!.left
            val width = activeChild.width
            // on swipe the active item will be positioned from [-width, 0]
            // interpolate offset for smooth animation
            val progress = mInterpolator.getInterpolation(left * -1 / width.toFloat())
            drawHighlights(c, indicatorStartX, indicatorPosY, activePosition, progress, itemCount)
        }

    }

    private fun getActivePosition(layoutManager: CarouselLayoutManager): Int =
        layoutManager.focusedChild?.let { layoutManager.getPosition(it) } ?: -1


    private fun drawHighlights(
        c: Canvas,
        indicatorStartX: Float,
        indicatorPosY: Float,
        highlightPosition: Int,
        progress: Float,
        itemCount: Int
    ) {
        mPaint.color = colorActive
        // width of item indicator including padding
        val itemWidth = mIndicatorItemLength + mIndicatorItemPadding
        if (progress == 0f) {
            // no swipe, draw a normal indicator
            val highlightStart = indicatorStartX + itemWidth * highlightPosition
            c.drawLine(
                highlightStart, indicatorPosY,
                highlightStart + mIndicatorItemLength, indicatorPosY, mPaint
            )
        } else {
            var highlightStart = indicatorStartX + itemWidth * highlightPosition
            // calculate partial highlight
            val partialLength = mIndicatorItemLength * progress
            // draw the cut off highlight
            c.drawLine(
                highlightStart + partialLength, indicatorPosY,
                highlightStart + mIndicatorItemLength, indicatorPosY, mPaint
            )
            // draw the highlight overlapping to the next item as well
            if (highlightPosition < itemCount - 1) {
                highlightStart += itemWidth
                c.drawLine(
                    highlightStart, indicatorPosY,
                    highlightStart + partialLength, indicatorPosY, mPaint
                )
            }
        }
    }

    private fun drawInactiveIndicators(
        c: Canvas,
        indicatorStartX: Float,
        indicatorPosY: Float,
        itemCount: Int
    ) {
        mPaint.color = colorInactive
        // width of item indicator including padding
        val itemWidth = mIndicatorItemLength + mIndicatorItemPadding
        var start = indicatorStartX
        for (i in 0 until itemCount) {
            // draw the line for every item
            c.drawLine(start, indicatorPosY, start + mIndicatorItemLength, indicatorPosY, mPaint)
            start += itemWidth
        }
    }
}