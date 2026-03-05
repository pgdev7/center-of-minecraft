#!/usr/bin/env python3
"""
Paste a person's face onto the creeper texture.

Usage:
    python3 make_creeper_skin.py path/to/photo.png

The script will:
  1. Load the vanilla creeper texture from the Minecraft gradle cache
  2. Crop your photo to a square (centered), resize to 8x8 px
  3. Paste it onto all 4 creeper head faces (front, back, left, right)
  4. Save the result to src/main/resources/assets/centerofminecraft/textures/entity/creeper.png

The mod will automatically use this texture for all creepers.
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

# ── Creeper texture UV map (all coords in pixels on the 64×32 texture) ───────
# Each face: (left, top, right, bottom)
HEAD_FRONT = (8,  8, 16, 16)
HEAD_BACK  = (24, 8, 32, 16)
HEAD_LEFT  = (16, 8, 24, 16)   # left side of head (player's left)
HEAD_RIGHT = (0,  8,  8, 16)   # right side of head

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
    side  = min(w, h)
    left  = (w - side) // 2
    top   = (h - side) // 2
    return img.crop((left, top, left + side, top + side))


def make_face_tile(photo_path: str, size: int = 8) -> Image.Image:
    photo = Image.open(photo_path).convert("RGBA")
    face  = center_crop_square(photo)
    # Use LANCZOS for nicer pixel-art downscale
    return face.resize((size, size), Image.LANCZOS)


def paste_face(base: Image.Image, face: Image.Image, box: tuple) -> None:
    """Paste face tile into box region, resizing to fit."""
    l, t, r, b = box
    w, h = r - l, b - t
    tile = face.resize((w, h), Image.NEAREST)
    base.paste(tile, (l, t), tile)

# ── Main ─────────────────────────────────────────────────────────────────────

def main():
    if len(sys.argv) < 2:
        sys.exit("Usage: python3 make_creeper_skin.py path/to/photo.png")

    photo_path = sys.argv[1]
    if not os.path.exists(photo_path):
        sys.exit(f"Photo not found: {photo_path}")

    print(f"Loading vanilla creeper texture from Minecraft jar...")
    creeper = load_vanilla_creeper()

    print(f"Processing photo: {photo_path}")
    face = make_face_tile(photo_path)

    print("Pasting face onto creeper head faces...")
    paste_face(creeper, face, HEAD_FRONT)
    paste_face(creeper, face, HEAD_BACK)
    paste_face(creeper, face, HEAD_LEFT)
    paste_face(creeper, face, HEAD_RIGHT)

    OUT_DIR.mkdir(parents=True, exist_ok=True)
    creeper.save(OUT_FILE)
    print(f"Saved to: {OUT_FILE}")
    print("Run './gradlew runClient' and find a creeper in-game to see it!")


if __name__ == "__main__":
    main()
