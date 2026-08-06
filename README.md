# Bokeh Synthesis (Android Portrait Mode)

This project explores an alternative approach to synthesizing background blur ("bokeh") on Android phones (5.0+), using signals already produced by the camera's own autofocus system rather than relying on a second camera, a dedicated depth sensor, or a trained segmentation model.

## The Problem

Most phones achieve background blur one of two ways:

- **Multi-camera stereo depth** (e.g. iPhone's dual-camera Portrait mode) — uses the physical offset between two lenses to triangulate depth. Requires hardware that many phones may not have, and sacrifices a lens (generally the widest).
- **Single-image segmentation** — a trained model guesses which pixels belong to the subject vs. background. Doesn't measure real depth, and struggles with anything the model wasn't trained on.

Both approaches require either specialized hardware or a pre-trained model, and neither is guaranteed to be available or reliable across the huge range of Android devices in the wild.

## The Approach (high level)

Every camera already has a way to sense depth for free: focus. This project captures a short burst of frames while sweeping the lens through its focus range, then analyzes how sharpness shifts frame-to-frame to estimate relative depth without needing a second lens, a depth sensor, or a trained model.
