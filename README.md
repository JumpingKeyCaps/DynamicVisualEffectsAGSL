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

## 🎯 PoC Objectives

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

--

| Water Effect Shader 1 | Water Effect Shader 2 | Water Effect Shader 3 | 
|:---:|:---:|:---:|
| ![P1](screenshots/water1.gif) | ![P2a](screenshots/water2.gif) |  ![P2a](screenshots/water3.gif) |


---

### 2. 🌈 Holographic Effect - Real-Time / Sensor-Driven *(Implemented)*

Simulates a dynamic holographic foil effect, with angle-sensitive iridescence, rainbow interference patterns, and chromatic distortion — all modulated in real time by the device’s physical orientation.

#### 🧪 Technical Highlights:

 - Procedural iridescence using multi-layered FBM noise and enhanced HSV rainbow mapping.

 - Surface normals are perturbed with fine micro-details to create realistic light interference zones.

 - Fresnel modulation enhances realism by increasing brightness at glancing angles.

 - Dynamic chromatic aberration adds subtle color shift based on device tilt roll.

 - Realtime device tilt input (pitch/roll) drives view-dependent shimmer and distortion.

 - Darkness-based masking ensures the effect appears only in low-luminance areas (e.g., shadowed or black ink zones).


#### 🧠 Interaction Design:

 - Reacts to device orientation (pitch & roll) via TYPE_ROTATION_VECTOR sensor.

 - Fully GPU-based with no bitmap mutation — can be applied live on images, cards or backgrounds.

 - All parameters (intensity, relief, color scale…) are externally controllable via Composables.

`Ideal for mimicking collectible card effects, stickers, security patterns, or oil-slick-like visuals`

--

| Holographic Effect 1 | Holographic Effect 2 | Holographic Effect 3 | 
|:---:|:---:|:---:|
| ![P1](screenshots/holo3.gif) | ![P2](screenshots/holo1.gif) |  ![P3](screenshots/holo2.gif) |
| Rainbows Effect | Rainbows Effect alt | Iridescent Effect | 
| ![P4](screenshots/holo4.gif) | ![P5](screenshots/holo5.gif) |  ![P6](screenshots/holo6.gif) |


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
 
#### Distortion Shaders Tasks

- **Distortion Effects**:
  - RGB offsets per channel (chromatic aberration).
  - Glitch: UV jumps, color noise triggered by motion thresholds.


---

### 4. ✨ Luminous & Energy Effects *(Coming Soon)*

Simulates plasma, shimmering aura, and iridescence like soap bubbles or oil on water.

- **Technical**:
  - **Iridescence**: Thin-film interference using angle-dependent color gradients.
  - **Plasma/Aura**: Mix of noise + trig for energy visuals.
- **Interaction**:
  - Sensor-based view angle affects colors.
  - Touch emits pulses or light bursts.
 
#### Iridescence Shaders Tasks

- **Iridescence Effects**:
  - Compute view direction from gyro.
  - Use dot product or trig mapping to modulate color spectrum.

---

### 5. 🔥 Fire Effect *(Coming Soon)*

Generates realistic procedural flames that progressively "burn" and consume the bitmap.

- **Technical**:
  - Perlin/Simplex noise = organic flame shapes.
  - Dynamic color mapping: bright yellow → deep red → ash tones.
  - Fire propagation based on time + distance from ignition.
  - Alpha reduction & pixel burn simulation.
- **Interaction**:
  - Long-press or swipe-up to ignite.
  - Intensity/speed controlled by gesture frequency or pressure.
 
####  Fire Shader Tasks

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

### 6. ⚡ Electric Sparks & Lightning *(Coming Soon)*

Generates stylized lightning bolts and crackling energy sparks across the screen.

- **Technical**:
  - Ray effect: Animate noise-based jagged lines from origin point.
  - Glow pulse: Use radial blur for glow around spark lines.
  - Flash animation: Sync light pulses with strike origin.
- **Interaction**:
  - Tap to emit spark.
  - Shake triggers bigger lightning strike + screen flash.

---

### 7. 🔍 Fisheye & Cathodic CRT Effect *(To Evaluate)*

Simulates vintage CRT screen visuals with fisheye distortion, scanlines, chromatic aberration, and screen curvature.

- **Technical**:  
  - UV remapping for fisheye/pincushion distortion.  
  - Animated scanlines and vignette.  
  - RGB channel offset for chromatic aberration.  
  - Optional glow or blur for phosphorescent look.

- **Interaction**:  
  - Triggered by tap/tilt/shake.
  - Speed of shake affects glitch severity and block size.

---

### 8. ❄️ Frosted Glass / Frost Effect

Blurs and distorts the image as if viewed through frosted glass.

- **Technical**:
  - Multiple samples around each pixel with random offsets (approximate blur).
  - Overlay of animated frost crystals using noise and fractal shapes.
- **Interaction**:
  - Touch to add frost, shake to reset.

---

### 📱 Sensor Integration

- **Sensor Access**:
  - Use Android `SensorManager` to get accel/gyro data.
- **Preprocessing**:
  - Normalize, smooth, and convert to usable vectors.
- **Shader Inputs**:
  - Pass vectors to shaders as `u_acceleration`, `u_orientation`.

---

### 🧠 Touch & Gesture Control

- **Gesture Detection**:
  - Map tap, long-press, swipe, pinch → shader triggers.
- **Param Mapping**:
  - Gesture data (position, pressure, velocity) → uniforms.

---




