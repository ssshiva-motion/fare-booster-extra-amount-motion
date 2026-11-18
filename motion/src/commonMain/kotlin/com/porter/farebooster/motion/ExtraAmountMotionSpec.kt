package com.porter.farebooster.motion

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/**
 * Every value from the design spec
 * "Fare Booster – Extra amount motion spec v2 (loop + ripple)".
 *
 * Sizes are in dp at 1× for a 360 × 800 screen. Times are in milliseconds.
 * Change values here only if the design spec changes.
 */
object ExtraAmountMotionSpec {

    // ---- Timing ----
    /** Nothing moves for this long after the accept order pop-up appears. */
    const val START_DELAY_MS = 500

    /** One loop = 500 ms of motion + 750 ms pause. Repeats forever while the pop-up is shown. */
    const val LOOP_MS = 1250

    /** Every move uses this curve. */
    val Easing: Easing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

    // ---- Pulse (whole "+₹400" group, from its centre) ----
    const val PULSE_PEAK_MS = 133
    const val PULSE_END_MS = 250
    const val PULSE_PEAK_SCALE = 1.2f

    // ---- Shimmer (soft highlight, visible only inside the amount glyphs) ----
    const val SHIMMER_END_MS = 400
    val ShimmerSize = DpSize(43.dp, 47.dp)
    const val SHIMMER_ALPHA = 0.6f

    /** Highlight centre starts this far before the amount's left edge and ends this far after its right edge. */
    val ShimmerOvershoot = 22.dp

    // ---- Ripple ring (centred on the amount; not affected by the pulse) ----
    const val RING_FADE_PEAK_MS = 250
    const val RING_END_MS = 500
    const val RING_PEAK_ALPHA = 0.6f
    const val RING_SCALE_FROM = 0.94f
    const val RING_SCALE_TO = 1.04f
    val RingStroke = 1.dp

    /** Gap between the amount text and the ring on each side at 0 ms. */
    val RingGap = 6.dp
    val RingPillHeight = 23.dp

    /** End width = start pill width − this. */
    val RingEndInset = 2.dp

    /** The 3-digit (₹400) circle. The ring is never taller than this; wider amounts end as a capsule. */
    val RingMaxHeight = 58.dp

    // ---- Colours ----
    val Positive = Color(0xFF157A4E)
    val BaseFare = Color(0xFF283548)
}
