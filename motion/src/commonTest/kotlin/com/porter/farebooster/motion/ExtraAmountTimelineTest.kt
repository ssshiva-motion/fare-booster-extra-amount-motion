package com.porter.farebooster.motion

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Acceptance checks taken straight from the design spec. */
class ExtraAmountTimelineTest {

    private fun frame(ms: Int) = extraAmountFrameAt(ms.toLong())

    private fun near(expected: Float, actual: Float, eps: Float = 0.001f) =
        assertTrue(abs(expected - actual) <= eps, "expected $expected but was $actual")

    @Test
    fun nothingMovesDuringTheFirst500ms() {
        listOf(0L, 250L, 499L).forEach { assertEquals(ExtraAmountFrame.Idle, extraAmountFrameAt(it)) }
    }

    @Test
    fun pulseGoesTo120At133msAndBackAt250ms() {
        near(1f, extraAmountFrameAt(500).pulseScale)
        near(1.2f, frame(500 + 133).pulseScale)
        near(1f, frame(500 + 250).pulseScale)
        near(1f, frame(500 + 900).pulseScale)
    }

    @Test
    fun shimmerCrossesIn400msThenHides() {
        near(0f, extraAmountFrameAt(500).shimmerProgress)
        near(1f, frame(500 + 400).shimmerProgress)
        assertTrue(frame(500 + 200).shimmerVisible)
        assertFalse(frame(500 + 400).shimmerVisible)
    }

    @Test
    fun ringFadesInTo60At250msAndOutBy500ms() {
        near(0f, extraAmountFrameAt(500).ringAlpha)
        near(0.6f, frame(500 + 250).ringAlpha)
        near(0f, frame(500 + 500).ringAlpha)
        near(0f, frame(500 + 1000).ringAlpha)
    }

    @Test
    fun ringScalesFrom94To104AndMorphsOver500ms() {
        near(0.94f, extraAmountFrameAt(500).ringScale)
        near(1.04f, frame(500 + 500).ringScale)
        near(0f, extraAmountFrameAt(500).ringMorph)
        near(1f, frame(500 + 500).ringMorph)
    }

    @Test
    fun loopRepeatsEvery1250msForever() {
        for (t in listOf(0L, 100L, 133L, 250L, 400L, 600L)) {
            assertEquals(extraAmountFrameAt(500 + t), extraAmountFrameAt(500 + 1250 + t))
            assertEquals(extraAmountFrameAt(500 + t), extraAmountFrameAt(500 + 1250L * 40 + t))
        }
    }

    @Test
    fun easingIsTheSpecCurve() {
        // cubic-bezier(0.33, 0, 0.67, 1) is symmetric: halfway in time is halfway in value.
        near(0.5f, frame(500 + 250).ringMorph, 0.01f)
    }

    // ---- 2 / 3 / 4-digit rule (dp, density 1) ----
    private fun ring(amountWidth: Float) = ringGeometry(amountWidth, gap = 6f, pillHeight = 23f, endInset = 2f, maxHeight = 58f)

    @Test
    fun threeDigitReferenceIs60x23To58x58() {
        val g = ring(48f)
        assertEquals(RingGeometry(60f, 23f, 58f, 58f), g)
        assertFalse(g.endsAsCapsule)
    }

    @Test
    fun twoDigitsEndAsASmallerCircle() {
        val g = ring(36f)
        assertEquals(46f, g.endWidth)
        assertEquals(46f, g.endHeight)
        assertFalse(g.endsAsCapsule)
    }

    @Test
    fun fourDigitsEndAsA58TallCapsuleThatNeverCutsTheDigits() {
        val amount = 65f
        val g = ring(amount)
        assertEquals(58f, g.endHeight)
        assertTrue(g.endWidth > amount, "ring must stay outside the digits")
        assertTrue(g.endsAsCapsule)
    }
}
