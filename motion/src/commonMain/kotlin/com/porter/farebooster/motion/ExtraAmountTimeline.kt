package com.porter.farebooster.motion

import androidx.compose.animation.core.Easing
import com.porter.farebooster.motion.ExtraAmountMotionSpec as Spec
import kotlin.math.min

/**
 * The state of every animated property at one moment.
 * Pure data so it can be unit-tested without a UI.
 */
data class ExtraAmountFrame(
    /** Scale of the whole "+₹400" group. */
    val pulseScale: Float,
    /** 0 = highlight centre at (amount left − 22 dp), 1 = at (amount right + 22 dp). */
    val shimmerProgress: Float,
    val shimmerVisible: Boolean,
    /** 0 = start pill, 1 = end circle/capsule. */
    val ringMorph: Float,
    val ringScale: Float,
    val ringAlpha: Float,
) {
    companion object {
        /** Pop-up is shown but nothing moves (first 500 ms, or reduced motion). */
        val Idle = ExtraAmountFrame(
            pulseScale = 1f,
            shimmerProgress = 0f,
            shimmerVisible = false,
            ringMorph = 0f,
            ringScale = Spec.RING_SCALE_FROM,
            ringAlpha = 0f,
        )
    }
}

/** Eased value of one keyframe segment; holds `from` before it and `to` after it. */
internal fun segment(
    timeMs: Float,
    startMs: Int,
    endMs: Int,
    from: Float,
    to: Float,
    easing: Easing = Spec.Easing,
): Float {
    if (timeMs <= startMs) return from
    if (timeMs >= endMs) return to
    val fraction = (timeMs - startMs) / (endMs - startMs)
    return from + (to - from) * easing.transform(fraction)
}

/**
 * Frame at [elapsedMs] since the accept order pop-up appeared.
 * After the 500 ms delay the loop repeats every 1,250 ms with no end.
 */
fun extraAmountFrameAt(elapsedMs: Long): ExtraAmountFrame {
    if (elapsedMs < Spec.START_DELAY_MS) return ExtraAmountFrame.Idle
    val t = ((elapsedMs - Spec.START_DELAY_MS) % Spec.LOOP_MS).toFloat()

    val pulse = if (t < Spec.PULSE_PEAK_MS) {
        segment(t, 0, Spec.PULSE_PEAK_MS, 1f, Spec.PULSE_PEAK_SCALE)
    } else {
        segment(t, Spec.PULSE_PEAK_MS, Spec.PULSE_END_MS, Spec.PULSE_PEAK_SCALE, 1f)
    }

    val ringAlpha = if (t < Spec.RING_FADE_PEAK_MS) {
        segment(t, 0, Spec.RING_FADE_PEAK_MS, 0f, Spec.RING_PEAK_ALPHA)
    } else {
        segment(t, Spec.RING_FADE_PEAK_MS, Spec.RING_END_MS, Spec.RING_PEAK_ALPHA, 0f)
    }

    return ExtraAmountFrame(
        pulseScale = pulse,
        shimmerProgress = segment(t, 0, Spec.SHIMMER_END_MS, 0f, 1f),
        shimmerVisible = t < Spec.SHIMMER_END_MS,
        ringMorph = segment(t, 0, Spec.RING_END_MS, 0f, 1f),
        ringScale = segment(t, 0, Spec.RING_END_MS, Spec.RING_SCALE_FROM, Spec.RING_SCALE_TO),
        ringAlpha = ringAlpha,
    )
}

/** Ring size at 0 ms (pill) and at 500 ms (circle or capsule), in the same unit as the inputs. */
data class RingGeometry(
    val startWidth: Float,
    val startHeight: Float,
    val endWidth: Float,
    val endHeight: Float,
) {
    val endsAsCapsule: Boolean get() = endWidth > endHeight

    fun widthAt(morph: Float) = startWidth + (endWidth - startWidth) * morph
    fun heightAt(morph: Float) = startHeight + (endHeight - startHeight) * morph
}

/**
 * 2/3/4-digit rule from the spec:
 * - start pill hugs the amount: width = amount width + 2 × gap, height = 23
 * - end width = pill width − 2 (never cuts the ₹ or a digit)
 * - end height = end width, but never more than the 3-digit circle (58)
 */
fun ringGeometry(
    amountWidth: Float,
    gap: Float,
    pillHeight: Float,
    endInset: Float,
    maxHeight: Float,
): RingGeometry {
    val startWidth = amountWidth + 2 * gap
    val endWidth = startWidth - endInset
    return RingGeometry(
        startWidth = startWidth,
        startHeight = pillHeight,
        endWidth = endWidth,
        endHeight = min(endWidth, maxHeight),
    )
}
