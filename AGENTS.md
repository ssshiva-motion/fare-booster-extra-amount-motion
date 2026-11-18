# Instructions for AI coding agents

You are adding the **Fare Booster extra-amount motion** to the partner app's accept order pop-up.
Everything you need is in this repo. Do not invent values — they all come from [SPEC.md](SPEC.md).

## What to copy

Copy these three files into the shared (`commonMain`) UI module of the app, keeping them together:

```
motion/src/commonMain/kotlin/com/porter/farebooster/motion/ExtraAmountMotionSpec.kt   ← all numbers from the design spec
motion/src/commonMain/kotlin/com/porter/farebooster/motion/ExtraAmountTimeline.kt     ← pure timing + ring-size math
motion/src/commonMain/kotlin/com/porter/farebooster/motion/FareBoosterExtraAmount.kt  ← the composable
```

Also copy the tests into the module's `commonTest`:

```
motion/src/commonTest/kotlin/com/porter/farebooster/motion/ExtraAmountTimelineTest.kt
```

Change the package name to match the target module. Dependencies needed: `compose.runtime`, `compose.foundation`,
`compose.ui`, `compose.animation` (Compose Multiplatform) — or the AndroidX equivalents on Android-only code.

If the app is not Compose (e.g. SwiftUI, XML Views, React Native, Flutter), port `ExtraAmountTimeline.kt` 1:1 —
it has no UI dependencies — and draw the three layers (pulse, shimmer, ring) with the platform's tools.
`web/single-screen.html` is a working reference implementation of the same logic.

## Where to put it

In the accept order pop-up, in the **Category+Fare → Fare** row, replace the static “+ ₹400” texts with:

```kotlin
Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
    Text(baseFare, style = Display28)                  // "₹1,300", #283548
    FareBoosterExtraAmount(
        amount = formattedExtra,                       // "₹40" / "₹400" / "₹1,200" – include ₹ and commas
        shownKey = order.id,                           // restarts the motion each time a new pop-up is shown
        animate = !reduceMotionEnabled,                // respect the OS "remove animations" setting
        textStyle = Heading20Bold,                     // must be PP Mori so ring sizes match the spec
    )
}
```

## Rules you must keep

- Start delay **500 ms**; loop **1,250 ms** (500 ms motion + 750 ms pause); repeats until the pop-up closes.
- Easing everywhere: `CubicBezierEasing(0.33f, 0f, 0.67f, 1f)`.
- The ring is **not** scaled by the pulse and is drawn centred on the amount.
- Ring end height is capped at **58 dp**; wider amounts become a capsule. The ring must never cut the ₹ or a digit.
- Shimmer is visible only inside the text glyphs (`BlendMode.SrcAtop` on an offscreen layer).
- Read the clock only inside `graphicsLayer` / draw lambdas (no recomposition per frame).
- Do not change values in `ExtraAmountMotionSpec` unless the Figma spec changes.

## Done when

1. `./gradlew :motion:desktopTest` (or the copied `ExtraAmountTimelineTest`) passes.
2. On device, with ₹40, ₹400 and ₹1,200, the motion matches `media/preview-2-3-4-digits.mp4`.
3. With animations turned off in OS settings, the amount is shown with no motion.
4. Open a PR into `main` with a screen recording of the three amounts.
