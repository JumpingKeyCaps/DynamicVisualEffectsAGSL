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

## 🚀 Implemented / In Progress Features

This PoC is structured around four major graphic effects, each designed to be manipulated by specific interactions.

---

### 1. 💧 Water Effect *(Implemented)*

Simulates realistic ripples and refractions on a bitmap using a dynamic AGSL shader.

- **Shader logic**: Custom fragment shader deforms UVs based on a blend of up to 16 circular waves. Each wave is defined by center, amplitude, frequency, damping, and age.
  
- **Visual result**: The bitmap appears as a fluid surface, with natural-looking water ripples radiating from touch points. Deformations decay over time for smooth fading.
  
- **User interaction**: Touch or drag to spawn ripples. Multi-touch is fully supported — each gesture creates a separate wave. Wave parameters (amplitude, radius, decay) can be tuned.
  
- **Animation model**: Wave state is tracked in Compose and updated via `withFrameNanos`. Old waves are purged when their lifespan ends. The shader reacts in real-time.
  
- **Component design**: `WaterEffectComposable` wraps the canvas, touch handler, and shader logic cleanly. The system is modular, reactive, and ready for reuse or combination with other effects.



| Water Effect 1 | Water Effect 2 | Water Effect 3 | 
| ![WE1](screenshots/water1.gif) | ![WE2](screenshots/water2.gif) | ![WE3](screenshots/water3.gif) |


---

### 2. 🔥 Fire Effect *(In Development)*

Generates realistic procedural flames that progressively "burn" and consume the bitmap.

- **Technical**:
  - Perlin/Simplex noise = organic flame shapes.
  - Dynamic color mapping: bright yellow → deep red → ash tones.
  - Fire propagation based on time + distance from ignition.
  - Alpha reduction & pixel burn simulation.
- **Interaction**:
  - Long-press or swipe-up to ignite.
  - Intensity/speed controlled by gesture frequency or pressure.

---

### 3. 💾 Distortion & Glitch Art *(Coming Soon)*

Creates digital-style visual noise like chromatic aberration, glitches, and heat waves.

- **Technical**:
  - **Chromatic aberration**: shift RGB channels via UV offsets.
  - **Glitch**: block-based displacement, color inversion, static noise.
  - **Heat waves**: soft animated UV warping using noise.
- **Interaction**:
  - Reacts to **tilt/shake/tap** (accelerometer & gyroscope).
  - Shake = glitch bursts, tilt = continuous distortion.

---

### 4. ✨ Luminous & Energy Effects *(Coming Soon)*

Simulates plasma, shimmering aura, and iridescence like soap bubbles or oil on water.

- **Technical**:
  - **Iridescence**: Thin-film interference using angle-dependent color gradients.
  - **Plasma/Aura**: Mix of noise + trig for energy visuals.
- **Interaction**:
  - Sensor-based view angle affects colors.
  - Touch emits pulses or light bursts.

---

## 🎯 PoC Objectives

- **🧠 AGSL Mastery**: Master complex fragment shaders and animation logic.
- **🖼️ Visual Quality**: Aim for realism or high stylization per effect.
- **🧩 Interactivity**: Link shaders to touch + motion sensors.
- **⚙️ Performance**: Keep smooth framerate, minimize costly ops.
- **🔧 Modularity**: Easily combine or add new shader effects.

---




## 📸 Screenshots


| Fire Effect 1 | Fire Effect 2 | Fire Effect 3 |
|:---:|:---:|:---:|
| Chromatic aberration Effect | Glitch Effect | Heat waves Effect |
|:---:|:---:|:---:|
| Plasma Effect | Iridescence Effect | Aura Effect |
|:---:|:---:|:---:|

---

## 🛠️ Detailed Next Steps

### 🔥 Fire Shader Tasks

- **Noise Integration**:
  - Use Perlin/Simplex (inline or pre-generated).
  - Tune scale/frequency.
- **Color Mapping**:
  - Intensity → RGBA function (fire palette).
- **Propagation Logic**:
  - `fire = noise + (uv.y - y0) * speed - time`.
  - Reduce alpha where fire intensity is high.
- **Animation**:
  - Link all motion to `u_time` for continuity.

---

### 📱 Sensor Integration

- **Sensor Access**:
  - Use Android `SensorManager` to get accel/gyro data.
- **Preprocessing**:
  - Normalize, smooth, and convert to usable vectors.
- **Shader Inputs**:
  - Pass vectors to shaders as `u_acceleration`, `u_orientation`.

---

### 🎛️ Distortion & Iridescence Shaders

- **Distortion Effects**:
  - RGB offsets per channel (chromatic aberration).
  - Glitch: UV jumps, color noise triggered by motion thresholds.
- **Iridescence Effects**:
  - Compute view direction from gyro.
  - Use dot product or trig mapping to modulate color spectrum.

---

### 🧠 Touch & Gesture Control

- **Gesture Detection**:
  - Map tap, long-press, swipe, pinch → shader triggers.
- **Param Mapping**:
  - Gesture data (position, pressure, velocity) → uniforms.

---

## 🔧 Technologies & Tools

- Kotlin + Jetpack Compose  
- AGSL shader (android 13+ only)
- Coroutines and State management for animation and interaction  
- Math & trigonometry for wave modeling  

---


