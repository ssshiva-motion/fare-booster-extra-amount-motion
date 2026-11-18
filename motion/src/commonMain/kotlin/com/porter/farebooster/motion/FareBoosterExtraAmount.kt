package com.porter.farebooster.motion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.porter.farebooster.motion.ExtraAmountMotionSpec as Spec

/**
 * "+₹400" extra amount on the accept order pop-up, with the Fare Booster motion:
 * after a 500 ms delay, every 1,250 ms the group pulses, a highlight sweeps through
 * the digits and a ripple ring appears around the amount.
 *
 * Put it right after the base fare text, e.g.
 * ```
 * Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
 *     Text("₹1,300", style = Display28)
 *     FareBoosterExtraAmount(amount = "₹400")
 * }
 * ```
 *
 * @param amount formatted amount including ₹ and any comma, e.g. "₹40", "₹400", "₹1,200".
 * @param shownKey change this each time the pop-up is shown to restart from 0 ms (e.g. the order id).
 * @param animate pass `false` when the user has reduced motion / animations turned off.
 * @param textStyle Heading/20/bold. Use the app's PP Mori font so ring sizes match the spec.
 */
@Composable
fun FareBoosterExtraAmount(
    amount: String,
    modifier: Modifier = Modifier,
    shownKey: Any? = Unit,
    animate: Boolean = true,
    color: Color = Spec.Positive,
    textStyle: TextStyle = TextStyle(
        fontSize = 20.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Bold,
    ),
) {
    val clock = rememberMotionClock(running = animate, key = shownKey)
    val style = textStyle.copy(color = color)
    var amountSize by remember { mutableStateOf(IntSize.Zero) }

    // Box draws the ring on top, outside the pulse layer, so the ring is not scaled by the pulse.
    Box(
        modifier = modifier.drawWithContent {
            drawContent()
            val frame = frameOf(clock, animate)
            if (frame.ringAlpha > 0f && amountSize.width > 0) drawRipple(amountSize, frame, color)
        },
    ) {
        Row(
            modifier = Modifier
                .graphicsLayer {
                    val s = frameOf(clock, animate).pulseScale
                    scaleX = s
                    scaleY = s
                }
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(text = "+", style = style)
            BasicText(
                text = amount,
                style = style,
                modifier = Modifier
                    .onSizeChanged { amountSize = it }
                    .shimmer { frameOf(clock, animate) },
            )
        }
    }
}

/** Reading the clock only inside layer/draw lambdas keeps the animation off the recomposition path. */
private fun frameOf(clock: State<Long>, animate: Boolean): ExtraAmountFrame =
    if (animate) extraAmountFrameAt(clock.value) else ExtraAmountFrame.Idle

/** Milliseconds since the pop-up was shown; restarts when [key] changes. */
@Composable
private fun rememberMotionClock(running: Boolean, key: Any?): State<Long> =
    produceState(0L, running, key) {
        value = 0L
        if (!running) return@produceState
        var start = -1L
        while (true) {
            withFrameMillis { now ->
                if (start < 0L) start = now
                value = now - start
            }
        }
    }

/** 43 × 47 soft white highlight at 60 %, painted only on the text's own pixels. */
private fun Modifier.shimmer(frame: () -> ExtraAmountFrame): Modifier = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val f = frame()
        if (!f.shimmerVisible) return@drawWithContent
        val w = Spec.ShimmerSize.width.toPx()
        val h = Spec.ShimmerSize.height.toPx()
        val over = Spec.ShimmerOvershoot.toPx()
        val startX = -over
        val endX = size.width + over
        val cx = startX + (endX - startX) * f.shimmerProgress
        val cy = size.height / 2f
        drawOval(
            brush = Brush.radialGradient(
                0f to Color.White,
                0.35f to Color.White.copy(alpha = 0.85f),
                1f to Color.White.copy(alpha = 0f),
                center = Offset(cx, cy),
                radius = h / 2f,
            ),
            topLeft = Offset(cx - w / 2f, cy - h / 2f),
            size = Size(w, h),
            alpha = Spec.SHIMMER_ALPHA,
            blendMode = BlendMode.SrcAtop,
        )
    }

/** Ring centred on the amount: pill → circle (or 58 dp-tall capsule), 94 % → 104 %, fade 0 → 60 % → 0. */
private fun DrawScope.drawRipple(amountSize: IntSize, frame: ExtraAmountFrame, color: Color) {
    val geometry = ringGeometry(
        amountWidth = amountSize.width.toFloat(),
        gap = Spec.RingGap.toPx(),
        pillHeight = Spec.RingPillHeight.toPx(),
        endInset = Spec.RingEndInset.toPx(),
        maxHeight = Spec.RingMaxHeight.toPx(),
    )
    val w = geometry.widthAt(frame.ringMorph)
    val h = geometry.heightAt(frame.ringMorph)
    // The amount is the last child of the Row and the Row is vertically centred in this Box.
    val center = Offset(size.width - amountSize.width / 2f, size.height / 2f)
    scale(scale = frame.ringScale, pivot = center) {
        drawRoundRect(
            color = color,
            topLeft = Offset(center.x - w / 2f, center.y - h / 2f),
            size = Size(w, h),
            cornerRadius = CornerRadius(h / 2f, h / 2f),
            alpha = frame.ringAlpha,
            style = Stroke(width = Spec.RingStroke.toPx()),
        )
    }
}
