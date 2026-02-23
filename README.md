#  Interactive AGSL Project – Dynamic Visual Effects PoC


![Status](https://img.shields.io/badge/status-WIP-red)
![Platform](https://img.shields.io/badge/platform-Android-green?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=android&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?logo=androidstudio&logoColor=white)


![PoC](https://img.shields.io/badge/type-PoC-blueviolet)
![AGSL](https://img.shields.io/badge/AGSL-enabled-0D47A1?logo=opengl&logoColor=white)
![Shaders](https://img.shields.io/badge/fragment%20shaders-dynamic-673AB7)
![Touch Input](https://img.shields.io/badge/input-touch-orange)
![Sensors](https://img.shields.io/badge/sensors-accelerometer%20%7C%20gyroscope-yellowgreen)
![Performance](https://img.shields.io/badge/optimized-for%20realtime-blue)

---

This project is an ambitious Proof of Concept (PoC) aiming to explore and master the advanced capabilities of **AGSL (Android Graphics Shader Language)** to create a suite of dynamic and highly interactive visual effects on bitmaps.

The goal is to demonstrate the power of shaders combined with **touch input** and **device motion sensors** to transform a simple bitmap into an immersive and reactive visual experience.

---

## 💯 PoC Objectives

- **🧠 AGSL Mastery**: Master complex fragment shaders and animation logic.
- **🖼️ Visual Quality**: Aim for realism or high stylization per effect.
- **🧩 Interactivity**: Link shaders to touch + motion sensors.
- **⚙️ Performance**: Keep smooth framerate, minimize costly ops.
- **🔧 Modularity**: Easily combine or add new shader effects.

---

## 🔧 Technologies & Tools

- Kotlin + Jetpack Compose  
- AGSL shader (android 13+ only)
- RuntimeShader / ShaderBrush
- Coroutines and State management for animation and interaction  
- Math & trigonometry for wave modeling  

---

### 📱 Sensor Integration

- **Sensor Access**:
  - Use Android `SensorManager` to get accel/gyro data.
- **Preprocessing**:
  - Normalize, smooth, and convert to usable vectors.
- **Shader Inputs**:
  - Pass vectors to shaders as `u_acceleration`, `u_orientation`.

---

### 👆 Touch & Gesture Control

(not available on all shaders)

- **Gesture Detection**:
  - Map tap, long-press, swipe, pinch → shader triggers.
- **Param Mapping**:
  - Gesture data (position, pressure, velocity) → uniforms.

## 🚧 Implemented / In Progress Features

This PoC is structured around major graphic effects, each designed to be manipulated by specific interactions.

---

### 1. 🌊 Water Effect *(Implemented)*

Simulates realistic ripples and refractions on a bitmap using a dynamic AGSL shader.

- **Shader logic**: Custom fragment shader deforms UVs based on a blend of up to 16 circular waves. Each wave is defined by center, amplitude, frequency, damping, and age.
  
- **Visual result**: The bitmap appears as a fluid surface, with natural-looking water ripples radiating from touch points. Deformations decay over time for smooth fading.
  
- **User interaction**: Touch or drag to spawn ripples. Multi-touch is fully supported — each gesture creates a separate wave. Wave parameters (amplitude, radius, decay) can be tuned.
  
- **Animation model**: Wave state is tracked in Compose and updated via `withFrameNanos`. Old waves are purged when their lifespan ends. The shader reacts in real-time.
  
- **Component design**: `WaterEffectComposable` wraps the canvas, touch handler, and shader logic cleanly. The system is modular, reactive, and ready for reuse or combination with other effects.

--

| Water Effect Shader 1 | Water Effect Shader 2 | Water Effect Shader 3 | 
|:---:|:---:|:---:|
| ![P1](screenshots/water1.gif) | ![P2a](screenshots/water2.gif) |  ![P2a](screenshots/water3.gif) |


---

### 2. 🌈 Holographic Effect - Real-Time / Sensor-Driven *(Implemented)*

Simulates a dynamic holographic foil effect, with angle-sensitive iridescence, rainbow interference patterns, and chromatic distortion — all modulated in real time by the device’s physical orientation.

#### Technical Highlights:

 - Procedural iridescence using multi-layered FBM noise and enhanced HSV rainbow mapping.

 - Surface normals are perturbed with fine micro-details to create realistic light interference zones.

 - Fresnel modulation enhances realism by increasing brightness at glancing angles.

 - Dynamic chromatic aberration adds subtle color shift based on device tilt roll.

 - Realtime device tilt input (pitch/roll) drives view-dependent shimmer and distortion.

 - Darkness-based masking ensures the effect appears only in low-luminance areas (e.g., shadowed or black ink zones).


#### Interaction Design:

 - Reacts to device orientation (pitch & roll) via TYPE_ROTATION_VECTOR sensor.

 - Fully GPU-based with no bitmap mutation — can be applied live on images, cards or backgrounds.

 - All parameters (intensity, relief, color scale…) are externally controllable via Composables.

`Ideal for mimicking collectible card effects, stickers, security patterns, or oil-slick-like visuals`

--

| Holographic Effect | Rainbows Effect | Rainbows Effect alt | Iridescent Effect |
|:---:|:---:|:---:|:---:|
| ![P1](screenshots/holo1.gif) | ![P2](screenshots/holo5.gif) |  ![P3](screenshots/holo4.gif) | ![P4](screenshots/holo6.gif) |



---

### 3. 🌫️ Liquid Topographic Flow *(Implemented)*

Creates animated contour lines that behave like a flowing liquid surface. The effect blends smooth sinusoidal warping with multi-octave FBM noise to produce organic, fluid-like motions across a virtual topographic map.

- **Technical**:  
  - UV warping driven by time-based sinusoidal fields.  
  - FBM noise adds micro-variations and undulation to contour spacing.  
  - Procedural topographic lines generated via `fract()` distance mapping.  
  - Dual-pass rendering for crisp lines and soft glow halos.  
  - Highly tunable shader parameters: density, thickness, noise scale, glow width, contrast, flow speed.

- **Visual Result**:  
  Smooth, flowing iso-lines that evoke molten terrain, liquid cartography, or wave-driven elevation maps.

- **Interaction**:  
  Fully animatable through uniforms (speed, intensity, density) and compatible with touch or sensor input to influence distortion fields or flow direction.

---

**Standalone Repository:** [FlowRibbon: Hypnotic AGSL Shader](https://github.com/JumpingKeyCaps/FlowRibbonAGSL)

--

| Topographic Effect 1 | Topographic Effect 2 | Topographic Effect 3 | Topographic Effect 4 | 
|:---:|:---:|:---:|:---:|
| ![P1](screenshots/topog1.gif) | ![P2](screenshots/topog2.gif) |  ![P3](screenshots/topog3.gif) |  ![P4](screenshots/topog4.gif) |

---

### 4. 📺 Cathode-AGSL: Advanced CRT Simulation Engine *(Implemented / Stand-Alone)*

High-fidelity post-processing engine for **Android Jetpack Compose** that emulates the physical behavior of **CRT monitors** and the thermal reaction of phosphor.

Unlike simple overlay filters, this shader pipeline transforms flat UI elements into an **immersive analog medium** with:

- **Signal Jitter & H-Sync Instability:** stochastic pixel shifts for realistic analog “snaps”  
- **Spherical Lens Distortion:** curved screen effect for vintage CRT feel  
- **Shadow Mask & RGB Aberration:** sub-pixel precision for authentic phosphor separation  
- **Thermal Reveal / Incandescence:** dynamic glow and heat trails following UI elements  

The engine is fully GPU-accelerated, frame-accurate, and tunable via **uniforms**, allowing developers to inject dynamic heat, glitch bursts, or lens curvature in real time.

---

**Standalone Repository:** [Cathode-AGSL Shader Engine](https://github.com/JumpingKeyCaps/Cathode-AGSL)

--

| CRT + text Laser displayer shaders   | CRT shader over pictures gallery | Signal Jitter & H-Sync Instability | CRT shader over text (scroll demo) |
|:---:|:---:|:---:|:---:|
| ![P1](screenshots/cathodedemo2.gif) | ![P2](screenshots/cathodedemo3.gif) | ![P3](screenshots/cathodedemo7.gif) | ![P4](screenshots/cathodedemo1.gif) |


---

### 5. 🧬 AGSL Organic Cell Engine *(Implemented / Stand-Alone)*

High-performance **interactive background engine** for Jetpack Compose that brings **living, breathing organic patterns** to your UI. Based on **Dynamic Voronoi Tessellation**, it simulates cell membranes that merge, split, and react to touch in real-time, giving the impression of a physical, fluid surface rather than a flat shader.

Key Features:

- **Reactive Touch Fields:** Finger input creates repulsion/attraction forces on cell nuclei for realistic viscosity effects  
- **Dual-Layer Voronoi:** Macro and Detail layers provide smooth motion plus intricate parallax depth  
- **Emotion-Driven Color Palettes:** Smooth transitions between Calm (Idle), Active, and Alert states  
- **Glitch-Free Fluidity:** Extended neighborhood search (5x5 grid) ensures topology integrity even under extreme distortion  
- **GPU Optimized:** Per-pixel AGSL calculations with minimal CPU overhead for high frame-rate performance

---

**Standalone Repository:** [AGSL Organic Cell Engine](https://github.com/JumpingKeyCaps/OrganicCellEngineAGSL)

--


| Alt Organic Cell Shader | Advanced Organic Cell layers Shader | Interactive Organic Cell Shader | Alt Interactive Organic Cell Shader | 
|:---:|:---:|:---:|:---:|
| ![P1](screenshots/classic2.gif) | ![P2](screenshots/advanced.gif) |  ![P3](screenshots/interactive1.gif) | ![P2](screenshots/interactive2.gif) |



---

```
More coming soon ... 😉❤️
```

---
`“Pushing pixels, bending light, and making Android screens behave like magic.”`


