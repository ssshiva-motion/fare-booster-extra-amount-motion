package com.porter.farebooster.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.porter.farebooster.motion.ExtraAmountMotionSpec
import com.porter.farebooster.motion.FareBoosterExtraAmount

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Fare Booster – extra amount motion",
        state = rememberWindowState(width = 1100.dp, height = 420.dp),
    ) {
        SampleScreen()
    }
}

@Composable
fun SampleScreen() {
    var shownCount by remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFE9EDF3)).padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicText(
            text = "Replay (show pop-up again)",
            style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
            modifier = Modifier
                .background(Color(0xFF0A57FF), RoundedCornerShape(8.dp))
                .clickable { shownCount++ }
                .padding(horizontal = 16.dp, vertical = 10.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            listOf("₹40" to "2 digits", "₹400" to "3 digits · reference", "₹1,200" to "4 digits").forEach { (amount, label) ->
                FareCard(amount = amount, label = label, shownKey = shownCount)
            }
        }
    }
}

/** The "Category+Fare" card from the accept order pop-up, reduced to the fare row. */
@Composable
private fun FareCard(amount: String, label: String, shownKey: Any) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BasicText(label, style = TextStyle(color = ExtraAmountMotionSpec.BaseFare, fontWeight = FontWeight.Bold))
        Row(
            modifier = Modifier
                .width(312.dp)
                .background(Color(0xFFFCFDFD), RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Bottom,
        ) {
            BasicText(
                text = "₹1,300",
                style = TextStyle(fontSize = 28.sp, lineHeight = 40.sp, fontWeight = FontWeight.Bold, color = ExtraAmountMotionSpec.BaseFare),
            )
            FareBoosterExtraAmount(amount = amount, shownKey = shownKey)
        }
    }
}
