# Fare Booster – Extra amount motion spec (v2, loop + ripple)

Source of truth: Figma → *Partner Fare Booster* → page **Visuals handoff** → section **PX_experience** →
[Fare Booster – Extra amount motion spec v2](https://www.figma.com/design/3o84iYg6IYYBpD84E228mS/Partner-Fare-Booster?node-id=2594-19).
This file restates it so code can be checked without opening Figma.

All sizes are in **dp at 1×** for a 360 × 800 screen. Positions are relative to the amount.

## When it plays

| | |
|---|---|
| 0 ms | Accept order pop-up appears with “₹1,300 + ₹400” already in place. Nothing moves. |
| 500 ms | Loop starts. Runs forever while the pop-up is on screen. |
| Each loop | Pulse + shimmer + ripple start together and play for **500 ms**, then a **750 ms** pause. One loop = **1,250 ms**. |
| Easing | Every move: `cubic-bezier(0.33, 0, 0.67, 1)` |

## One loop (times from the start of each loop)

| Element | Property | 0 ms | 133 ms | 250 ms | 400 ms | 500 ms | 500–1,250 ms |
|---|---|---|---|---|---|---|---|
| “+₹400” group (from its centre) | Scale | 100% | 120% | 100% | 100% | 100% | 100% |
| Shimmer (43 × 47 soft white highlight, 60% opacity, only inside the amount glyphs) | Centre X | amount left − 22 | → | → | amount right + 22 | hidden | hidden |
| Ripple ring (solid 1 dp #157A4E, centred on the amount, not scaled by the pulse) | Opacity | 0 | → | 60% | → | 0 | 0 |
| | Scale | 94% | → | → | → | 104% | — |
| | Shape | pill | → | → | → | circle / capsule | — |

## Ripple shape for 2, 3 and 4 digits

1. Measure the amount as it renders, including ₹ and any comma (₹40 · ₹400 · ₹1,200).
2. Start pill: width = amount width + 2 × 6 dp, height = 23 dp.
3. End: width = start width − 2 dp; height = the same but **never more than 58 dp** (the ₹400 circle).
   - ≤ 3 digits → ends as a circle.
   - 4 digits → ends as a capsule 58 dp tall (29 dp corner radius).
   - The ring never gets taller than the ₹400 ring and never cuts the ₹ or any digit.
4. Shimmer always crosses the whole amount in 400 ms.
5. Everything else (timing, easing, 94% → 104%, opacity, the 100% → 120% pulse) is the same for every amount.

| Amount (PP Mori 20 bold) | Start pill | End |
|---|---|---|
| ₹40 | ≈ 48 × 23 | ≈ 46 × 46 circle |
| ₹400 | 60 × 23 | 58 × 58 circle |
| ₹1,200 | ≈ 77 × 23 | ≈ 75 × 58 capsule |

## Colours

| | |
|---|---|
| +₹400, ripple | `#157A4E` |
| Base fare | `#283548` |
| Shimmer | `#FFFFFF` @ 60% |
