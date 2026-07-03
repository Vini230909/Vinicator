# Vinicator

A client-side Mindustry (v8, build 158+) Java mod that **hides or fades units near you**, so you can actually see the blocks underneath. Inspired by the "hide units" feature of [foo's client](https://github.com/mindustry-antigrief/mindustry-client), but range-based and configurable per unit type — and usable as a regular mod instead of a custom client.

![icon](icon.png)

## Features

- Hides units within a configurable range (1–100 tiles) of an **anchor point**. Three anchor modes:
  - **Player unit** — units near the unit you control
  - **Mouse cursor** — units near your crosshair
  - **Camera center** — units near the middle of your screen
- **Opacity slider**: 0% removes units in range entirely; higher values draw them as transparent ghosts. Body, shields, engines and trails all fade together.
- **Unit type picker**: a dialog with every unit type as a toggleable icon (select all / clear). New and modded unit types are affected by default.
- Your own controlled unit is never hidden.
- Purely visual and client-side (`hidden: true`): units still exist, shoot and collide normally, and the mod is not required on servers you join.

## Usage

Everything lives in **Settings → Vinicator**:

1. Toggle **Hide units near anchor** on.
2. Pick the **Anchor mode**, **Hide range**, and **Hidden unit opacity**.
3. Open **Unit types...** to choose which units are affected.

## Installation

Grab `Vinicator.jar` from the [releases](../../releases) (or build it yourself, below) and either:

- import it in-game via **Mods → Import mod → Import file**, or
- drop it into your Mindustry `mods` folder.

Requires Mindustry **build 158 or newer**.

## Building

Desktop-only jar (no Android SDK needed):

```
./gradlew jar
```

Output: `build/libs/VinicatorDesktop.jar`

Combined desktop + Android jar (requires an Android SDK with `d8` on your PATH and `ANDROID_HOME` set):

```
./gradlew deploy
```

Output: `build/libs/Vinicator.jar` — this is the one that works on all platforms. The included GitHub Actions workflow builds it automatically on every push.

## How it works

Each frame, units in range are removed from the draw entity group right before the vanilla renderer iterates it (`Trigger.draw`) and put back immediately after (`Trigger.postDraw`) — game logic never notices. When opacity is above 0%, those units are instead drawn into an offscreen framebuffer which is blitted over the scene with a constant-alpha shader, the same capture-and-blit pattern the vanilla renderer uses for animated shields.

## Known quirks

- Faded ghosts render at a single layer (just above flying units), so a faded ground unit may draw over bullets/effects that would normally cover it.
- Shield bubbles of faded units render as simple shapes rather than with the animated shield shader.
- Player name tags belong to the player, not the unit, so they stay visible when a player's unit is faded.
