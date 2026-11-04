"""
Builds static/skyway-logo.svg - the Skyway Parking logo, redrawn from the sign.

Why a script?
  * The words "SKYWAY" and "PARKING" are converted from the Saira font into
    plain vector shapes, so the logo looks identical in every browser and
    never depends on a web font loading.
  * Every piece is placed with real measurements taken from the photo of the
    sign (on a 980 x 620 canvas), so each part has its own clear space and
    nothing overlaps.

Layout, top to bottom:
    silver ring  - a circle cut off flat just above the lettering, so the "K"
                   and the "A" interrupt its two ends with an even gap
    SKYWAY       - sits in the gap between the ring and the blue pin
    blue pin     - two blue sides that start just below the text
    PARKING, car - inside the pin, each with clear space around it
    swoosh       - orbits the pin: behind the left side, in front at the bottom
    road         - lane markings in the point of the pin, below the swoosh
    airplane     - where the swoosh flies off, just right of the last "Y"

Run:   python tools/build_logo.py      (needs: pip install fonttools)
"""

from pathlib import Path

from fontTools.pens.boundsPen import BoundsPen
from fontTools.pens.svgPathPen import SVGPathPen
from fontTools.pens.transformPen import TransformPen
from fontTools.ttLib import TTFont

HERE = Path(__file__).parent
OUT = HERE.parent / "static" / "skyway-logo.svg"
FONT_BOLD = HERE / "fonts" / "saira-wide-600.ttf"   # Saira SemiExpanded SemiBold
FONT_MED = HERE / "fonts" / "saira-500.ttf"         # Saira Medium

CX = 412  # horizontal center of the mark


# ----------------------------------------------------------------- helpers ---
def fmt(v):
    """Short number formatting for the SVG (12.50 -> 12.5)."""
    return f"{v:.1f}".rstrip("0").rstrip(".")


def text_to_path(font_file, text, left, right, baseline, cap_height):
    """Turn `text` into one SVG path whose letters exactly span left..right.

    Letters are scaled so capitals are `cap_height` tall, then the spacing
    between letters is adjusted so the ink starts at `left` and ends at `right`.
    """
    font = TTFont(font_file)
    glyphs = font.getGlyphSet()
    cmap = font.getBestCmap()
    scale = cap_height / font["OS/2"].sCapHeight

    letters = []
    pen_x = 0
    for ch in text:
        name = cmap[ord(ch)]
        bounds = BoundsPen(glyphs)
        glyphs[name].draw(bounds)
        x_min, _, x_max, _ = bounds.bounds
        letters.append({"name": name, "x": pen_x, "ink_min": x_min, "ink_max": x_max})
        pen_x += glyphs[name].width

    ink_left = letters[0]["x"] + letters[0]["ink_min"]
    ink_right = letters[-1]["x"] + letters[-1]["ink_max"]
    extra = ((right - left) - (ink_right - ink_left) * scale) / (len(text) - 1)

    pen = SVGPathPen(glyphs)
    for i, letter in enumerate(letters):
        x = left + (letter["x"] - ink_left) * scale + i * extra
        # Fonts draw with y pointing up, SVG with y pointing down -> flip.
        glyphs[letter["name"]].draw(TransformPen(pen, (scale, 0, 0, -scale, x, baseline)))
    return pen.getCommands()


def smooth(points):
    """Draw a smooth curve through a list of (x, y) points (Catmull-Rom spline).

    Lets us trace a shape by listing points measured on the sign, instead of
    hand-tuning Bezier control points.
    """
    out = [f"M{fmt(points[0][0])} {fmt(points[0][1])}"]
    for i in range(len(points) - 1):
        p0 = points[max(i - 1, 0)]
        p1, p2 = points[i], points[i + 1]
        p3 = points[min(i + 2, len(points) - 1)]
        c1 = (p1[0] + (p2[0] - p0[0]) / 6, p1[1] + (p2[1] - p0[1]) / 6)
        c2 = (p2[0] - (p3[0] - p1[0]) / 6, p2[1] - (p3[1] - p1[1]) / 6)
        out.append(f"C{fmt(c1[0])} {fmt(c1[1])} {fmt(c2[0])} {fmt(c2[1])} {fmt(p2[0])} {fmt(p2[1])}")
    return " ".join(out)


def mirror(points):
    """Mirror points left <-> right around the center line."""
    return [(2 * CX - x, y) for x, y in points]


# ---------------------------------------------------------------- geometry ---
# Silver ring: a circle (center 413,204 / outer radius 204 / inner 171),
# cut off by a flat line 10 px above the top of the letters.
TEXT_TOP, BASELINE = 162, 292
RING_CX, RING_CY, RING_R, RING_W = 413, 204, 187.5, 33
RING_CUT = TEXT_TOP - 10

# SKYWAY: capitals 130 tall, spanning x 72..752 (measured on the sign).
skyway = text_to_path(FONT_BOLD, "SKYWAY", 72, 752, BASELINE, BASELINE - TEXT_TOP)

# PARKING: 38 tall, starting 14 px below SKYWAY, ~30 px clear of the blue sides.
parking = text_to_path(FONT_MED, "PARKING", 292, 530, 344, 38)

# The blue pin is one outline: its outer edge sweeps from the top of each side
# down to the point; its inner edge closes up behind the swoosh, so the lower
# part below the swoosh is solid (that is where the road goes).
# Points are for the left half; the right half is the mirror image.
outer_left = [(209, 303), (216, 330), (225, 352), (242, 380), (262, 410), (288, 442),
              (318, 474), (350, 512), (382, 556), (CX, 604)]
inner_left = [(CX, 464), (390, 465), (366, 461), (338, 442), (313, 410), (285, 380),
              (264, 352), (252, 330), (246, 303)]

PIN = (
    smooth(outer_left)                                   # left outer edge, down to the point
    + " L" + smooth(mirror(outer_left)[::-1])[1:]        # right outer edge, back up
    + " L" + smooth(mirror(inner_left)[::-1])[1:]        # right inner edge, down behind the swoosh
    + " L" + smooth(inner_left)[1:]                      # left inner edge, back up
    + " Z"
)


# Road in the tip: two lane lines narrowing toward the point, dashes between.
ROAD_TOP, ROAD_BOT = 484, 588
ROAD_HALF_TOP, ROAD_HALF_BOT = 17, 3


def road_half(y):
    t = (y - ROAD_TOP) / (ROAD_BOT - ROAD_TOP)
    return ROAD_HALF_TOP + (ROAD_HALF_BOT - ROAD_HALF_TOP) * t


road = [
    f'<path d="M{CX - ROAD_HALF_TOP} {ROAD_TOP} L{CX - ROAD_HALF_BOT} {ROAD_BOT} '
    f'M{CX + ROAD_HALF_TOP} {ROAD_TOP} L{CX + ROAD_HALF_BOT} {ROAD_BOT}" '
    f'fill="none" stroke="#14223a" stroke-width="3" stroke-linecap="round"/>'
]
for y in range(ROAD_TOP + 6, ROAD_BOT - 8, 14):
    half = road_half(y + 4) - 2
    road.append(f'<rect x="{fmt(CX - half)}" y="{y}" width="{fmt(2 * half)}" height="7" rx="1"/>')


# Swoosh, traced from the sign. Lower edge: from the plane, round the left
# loop to the back end (which tucks behind the left side of the pin).
# Upper edge: from the back end, round again, and back to the plane.
swoosh_lower = [(790, 291), (745, 318), (716, 337), (658, 371), (595, 403), (523, 433), (461, 453),
                (382, 472), (320, 483), (262, 486), (215, 481), (178, 470), (156, 455), (147, 440),
                (151, 423), (166, 407), (192, 391), (226, 375), (250, 366)]
swoosh_upper = [(250, 366), (226, 382), (198, 395), (176, 407), (163, 421), (158, 436), (160, 450),
                (172, 461), (195, 468), (229, 471), (323, 463), (384, 452), (426, 442), (465, 432),
                (530, 412), (611, 382), (681, 351), (735, 323), (790, 291)]
# Join both edges into one closed shape ("[1:]" drops the second "M" move).
SWOOSH = smooth(swoosh_lower) + " L" + smooth(swoosh_upper)[1:] + " Z"

# Car (front view), drawn in a 72 x 70 box and moved into place
# above the swoosh: x 376..448, y 356..426.
CAR_X, CAR_Y = 376, 356

# ----------------------------------------------------------------- the SVG ---
svg = f"""<svg xmlns="http://www.w3.org/2000/svg" viewBox="60 -6 880 616" role="img" aria-label="Skyway Parking">
  <!-- Skyway Parking logo. Generated by tools/build_logo.py - edit that file, not this one. -->
  <defs>
    <linearGradient id="sw-silver" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0" stop-color="#ffffff"/>
      <stop offset=".55" stop-color="#e6eaef"/>
      <stop offset="1" stop-color="#bcc5d0"/>
    </linearGradient>
    <linearGradient id="sw-silver-h" x1="0" y1="0" x2="1" y2="0">
      <stop offset="0" stop-color="#cdd5de"/>
      <stop offset=".45" stop-color="#ffffff"/>
      <stop offset="1" stop-color="#d9dfe6"/>
    </linearGradient>
    <linearGradient id="sw-pin" x1="0" y1="300" x2="0" y2="604" gradientUnits="userSpaceOnUse">
      <stop offset="0" stop-color="#2f70d8"/>
      <stop offset="1" stop-color="#5aaaf0"/>
    </linearGradient>
    <linearGradient id="sw-sky" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0" stop-color="#9ad9fb"/>
      <stop offset="1" stop-color="#3f97e8"/>
    </linearGradient>
    <linearGradient id="sw-car" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0" stop-color="#9fe0fc"/>
      <stop offset="1" stop-color="#4fb4ee"/>
    </linearGradient>
    <clipPath id="sw-ring-cut"><rect x="0" y="-50" width="1000" height="{RING_CUT + 50}"/></clipPath>
    <clipPath id="sw-front"><rect x="0" y="400" width="1000" height="300"/></clipPath>
  </defs>

  <!-- swoosh, back part (the pin is drawn over it) -->
  <path d="{SWOOSH}" fill="url(#sw-silver-h)"/>

  <!-- silver ring, cut flat just above the letters -->
  <circle cx="{RING_CX}" cy="{RING_CY}" r="{RING_R}" fill="none" stroke="url(#sw-silver)"
          stroke-width="{RING_W}" clip-path="url(#sw-ring-cut)"/>

  <!-- blue pin -->
  <g fill="url(#sw-pin)">
    <path d="{PIN}"/>
  </g>

  <!-- road markings in the tip -->
  <g fill="#14223a">{"".join(road)}</g>

  <!-- PARKING -->
  <path d="{parking}" fill="url(#sw-sky)" stroke="#eef6fd" stroke-width="1.6" paint-order="stroke"/>

  <!-- car, front view -->
  <g transform="translate({CAR_X} {CAR_Y})">
    <rect x="-5" y="19" width="9" height="6" rx="2" fill="#6cc1f3"/>
    <rect x="68" y="19" width="9" height="6" rx="2" fill="#6cc1f3"/>
    <path d="M13 18 L19 5 Q21 1 26 1 L46 1 Q51 1 53 5 L59 18 Z" fill="url(#sw-car)"/>
    <path d="M19 17 L23 7 L49 7 L53 17 Z" fill="#14223a"/>
    <path d="M6 22 Q8 17 15 17 L57 17 Q64 17 66 22 L69 30 Q71 34 71 40 L71 54 Q71 58 67 58 L5 58 Q1 58 1 54 L1 40 Q1 34 3 30 Z" fill="url(#sw-car)"/>
    <path d="M5 32 L22 35 L22 41 L6 39 Z" fill="#ffffff"/>
    <path d="M67 32 L50 35 L50 41 L66 39 Z" fill="#ffffff"/>
    <rect x="26" y="36" width="20" height="8" rx="2" fill="#14223a"/>
    <rect x="18" y="49" width="36" height="4" rx="2" fill="#14223a"/>
    <rect x="4" y="56" width="12" height="14" rx="3" fill="#14223a"/>
    <rect x="56" y="56" width="12" height="14" rx="3" fill="#14223a"/>
  </g>

  <!-- swoosh, front part (crosses in front of the pin) -->
  <path d="{SWOOSH}" fill="url(#sw-silver-h)" clip-path="url(#sw-front)"/>

  <!-- SKYWAY -->
  <path d="{skyway}" fill="url(#sw-silver)"/>

  <!-- airplane: drawn pointing right, then tilted to climb -->
  <g transform="translate(742 264) rotate(-17.6)" fill="url(#sw-silver)">
    <path d="M0 -4 C40 -9 150 -10 182 -9 C196 -8 204 -3 206 0 C204 4 195 7 182 7 C150 8 40 7 0 4 Z"/>
    <path d="M118 5 L80 58 L66 60 L90 6 Z"/>
    <path d="M112 -8 L94 -40 L84 -41 L94 -8 Z"/>
    <rect x="84" y="17" width="26" height="10" rx="5"/>
    <rect x="92" y="-27" width="17" height="7" rx="3.5"/>
    <path d="M14 -5 L10 -36 L2 -36 L4 -4 Z"/>
    <path d="M20 5 L4 26 L-4 26 L4 4 Z"/>
    <path d="M190 -5 L199 -3 L193 -1 Z" fill="#14223a"/>
  </g>
</svg>
"""

OUT.write_text(svg)
print(f"wrote {OUT}  ({len(svg) // 1024} KB)")
