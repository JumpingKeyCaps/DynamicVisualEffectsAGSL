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

### 2. 🌈 Holographic Effects *(In Development)*

Simulates holographic foil and rainbow diffraction effects with dynamic light refraction.

- **Technical**:
  - Angle-dependent rainbow gradients with smooth UV shifts.
  - Layered noise + sine waves to mimic diffraction patterns.
  - Specular highlights and subtle distortion for depth.
- **Interaction**:
  - Sensor-based view angle controls gradient movement.
  - Touch creates shimmering pulses or rainbow flashes.

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

### 7. 🌌 Glowing Particles & Fireflies *(Coming Soon)*

A swarm of dynamic glowing dots (fireflies) that float and react to user gestures.

- **Technical**:
  - Particle position: Managed in Kotlin (Vec2 list) + passed as uniform array.
  - Glow effect: Soft radial alpha + color gradient.
  - Movement: Brownian / Perlin / orbit logic for drift.
- **Interaction**:
  - Tap/drag = attract or repel nearby particles.
  - Shake = scatter all fireflies briefly.

---


### 8. 🧠 Pixel Sorting & Datamosh *(To Evaluate)*

Creates glitch aesthetics by algorithmically "stretching" or reordering pixels in segments of the image.

- **Technical**:
  - Pixel sorting: Sort horizontal or vertical lines based on pixel luminance or hue.
  - Block warping: Randomly shift image blocks (UV offset by region).
  - Color bleeding: Optional chromatic offset during sorting.
  - Artifacts: Simulate video decoding glitches using time-based block shifts.
- **Interaction**:
  - Triggered by tap/tilt/shake.
  - Speed of shake affects glitch severity and block size.
- **Notes**:
  - Requires moderate GPU effort — not real-time sorting per frame unless small regions only.

---

### 9. 🔍 Fisheye & Cathodic CRT Effect *(To Evaluate)*

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

### 10. ❄️ Frosted Glass / Frost Effect

Blurs and distorts the image as if viewed through frosted glass.

- **Technical**:
  - Multiple samples around each pixel with random offsets (approximate blur).
  - Overlay of animated frost crystals using noise and fractal shapes.
- **Interaction**:
  - Touch to add frost, shake to reset.
    
---

### 11. 🔆 Neon Glow & Outline

Creates a colored glowing outline around shapes in the image.

- **Technical**:
  - Simplified Sobel edge detection on luminance.
  - Blur and progressive coloring for glow effect.
- **Interaction**:
  - Glow intensity follows swipe velocity.


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




