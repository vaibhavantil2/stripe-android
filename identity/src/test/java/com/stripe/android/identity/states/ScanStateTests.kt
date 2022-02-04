package com.stripe.android.identity.states

import com.google.common.truth.Truth.assertThat
import com.stripe.android.identity.ml.AnalyzerOutput
import com.stripe.android.identity.ml.BoundingBox
import com.stripe.android.identity.ml.Category
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ScanStateTests {

    @Test
    fun `Initial can't transition with unmatched AnalyzerOutput`() {
        val initialState = ScanState.Initial(ScanState.ScanType.ID_FRONT)
        val resultState = initialState.consumeTransition(ID_BACK_OUTPUT)

        assertThat(resultState).isSameInstanceAs(initialState)
    }

    @Test
    fun `Initial transitions to Found with matched AnalyzerOutput`() {
        val initialState = ScanState.Initial(ScanState.ScanType.ID_FRONT)
        val resultState = initialState.consumeTransition(ID_FRONT_OUTPUT)

        assertThat(resultState).isInstanceOf(ScanState.Found::class.java)
    }

    // TODO(ccen) add test for Found -> Unsatisfied when #isUnsatisfied is implemented
    // TODO(ccen) add test for Found -> Found when #moreResultsRequired is implemented

    @Test
    fun `Satisfied automatically transitions Finished`() {
        val initialScanType = ScanState.ScanType.ID_FRONT
        val initialState = ScanState.Satisfied(initialScanType)
        val resultState = initialState.consumeTransition(ID_FRONT_OUTPUT)

        assertThat(resultState).isInstanceOf(ScanState.Finished::class.java)
        assertThat(resultState.type).isEqualTo(initialScanType)
    }

    @Test
    fun `Unsatisfied automatically transitions Initial`() {
        val initialScanType = ScanState.ScanType.ID_FRONT
        val initialState = ScanState.Unsatisfied("reason", initialScanType)
        val resultState = initialState.consumeTransition(ID_FRONT_OUTPUT)

        assertThat(resultState).isInstanceOf(ScanState.Initial::class.java)
        assertThat(resultState.type).isEqualTo(initialScanType)
    }

    private companion object {
        val ID_FRONT_OUTPUT = AnalyzerOutput(
            BoundingBox(0f, 0f, 0f, 0f),
            Category.ID_FRONT,
            0f
        )
        val ID_BACK_OUTPUT = AnalyzerOutput(
            BoundingBox(0f, 0f, 0f, 0f),
            Category.ID_BACK,
            0f
        )
    }
}
