#!/usr/bin/env python3
"""
Paste a person's face onto the creeper texture at high resolution.

Usage:
    python3 make_creeper_skin.py path/to/photo.png

The script will:
  1. Load the vanilla creeper texture (64x32) from the Minecraft gradle cache
  2. Upscale it so the face region matches the source photo's resolution
     (capped at 512x512 face = 64x overall scale = 4096x2048 texture)
  3. Paste the photo face onto all 4 creeper head faces at full quality
  4. Save to src/client/resources/assets/centerofminecraft/textures/entity/creeper.png

Minecraft supports any power-of-2 multiple of the base texture size,
so the higher resolution is rendered natively in-game.
"""

import sys
import os
import zipfile
from pathlib import Path
from PIL import Image

# ── Paths ────────────────────────────────────────────────────────────────────

SCRIPT_DIR   = Path(__file__).parent
MC_JAR       = Path.home() / ".gradle/caches/fabric-loom/1.21.11/minecraft-client.jar"
VANILLA_PATH = "assets/minecraft/textures/entity/creeper/creeper.png"
OUT_DIR      = SCRIPT_DIR / "src/client/resources/assets/centerofminecraft/textures/entity"
OUT_FILE     = OUT_DIR / "creeper.png"

# Vanilla texture is 64x32. The head face region is 8x8 px.
VANILLA_W, VANILLA_H = 64, 32
FACE_SIZE_VANILLA    = 8   # face region is 8x8 in the vanilla texture
MAX_FACE_PX          = 512 # cap face resolution at 512x512

# ── Creeper UV map (on the vanilla 64×32 grid, scaled up below) ──────────────
# Each face: (left, top, right, bottom) in vanilla pixel coords
HEAD_FRONT = (8,  8, 16, 16)
HEAD_BACK  = (24, 8, 32, 16)
HEAD_LEFT  = (16, 8, 24, 16)
HEAD_RIGHT = (0,  8,  8, 16)

# ── Helpers ──────────────────────────────────────────────────────────────────

def load_vanilla_creeper() -> Image.Image:
    if not MC_JAR.exists():
        sys.exit(f"Minecraft jar not found at:\n  {MC_JAR}\n"
                 "Run './gradlew runClient' once to download it.")
    with zipfile.ZipFile(MC_JAR) as z:
        with z.open(VANILLA_PATH) as f:
            return Image.open(f).copy().convert("RGBA")


def center_crop_square(img: Image.Image) -> Image.Image:
    w, h = img.size
    side = min(w, h)
    left = (w - side) // 2
    top  = (h - side) // 2
    return img.crop((left, top, left + side, top + side))


def scale_box(box: tuple, scale: int) -> tuple:
    return tuple(v * scale for v in box)


def paste_face(base: Image.Image, face: Image.Image, box: tuple) -> None:
    l, t, r, b = box
    w, h = r - l, b - t
    tile = face.resize((w, h), Image.LANCZOS)
    base.paste(tile, (l, t), tile)

# ── Main ─────────────────────────────────────────────────────────────────────

def main():
    if len(sys.argv) < 2:
        sys.exit("Usage: python3 make_creeper_skin.py path/to/photo.png")

    photo_path = sys.argv[1]
    if not os.path.exists(photo_path):
        sys.exit(f"Photo not found: {photo_path}")

    # Load and square-crop the source photo
    photo     = Image.open(photo_path).convert("RGBA")
    face_src  = center_crop_square(photo)
    photo_res = face_src.size[0]  # resolution of the cropped face

    # Pick a scale factor so the face region matches the photo resolution,
    # keeping it a power of 2 and capping at MAX_FACE_PX
    target_face_px = min(photo_res, MAX_FACE_PX)
    # Round up to next power of 2
    scale = 1
    while scale * FACE_SIZE_VANILLA < target_face_px:
        scale *= 2
    face_px = scale * FACE_SIZE_VANILLA

    print(f"Loading vanilla creeper texture...")
    vanilla = load_vanilla_creeper()

    print(f"Source photo: {photo_res}x{photo_res}px")
    print(f"Scale factor: {scale}x  →  texture {VANILLA_W*scale}x{VANILLA_H*scale}, face region {face_px}x{face_px}px")

    # Upscale vanilla texture with NEAREST so pixels stay crisp
    creeper = vanilla.resize((VANILLA_W * scale, VANILLA_H * scale), Image.NEAREST)

    print("Pasting face onto all 4 head faces...")
    for box in (HEAD_FRONT, HEAD_BACK, HEAD_LEFT, HEAD_RIGHT):
        paste_face(creeper, face_src, scale_box(box, scale))

    OUT_DIR.mkdir(parents=True, exist_ok=True)
    creeper.save(OUT_FILE)
    print(f"Saved to: {OUT_FILE}")
    print("Run './gradlew runClient' and find a creeper to see it!")


if __name__ == "__main__":
    main()
