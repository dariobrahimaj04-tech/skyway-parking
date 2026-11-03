# Skyway Parking – Landing Page Mockup

A design proposal for the Skyway Parking home page, built with **Python + Flask**.
It's a mockup: the page, booking widget and live price quote work, but "Reserve this spot" doesn't book anything yet.

## Design idea
- **Colors come from the logo:** night-slate (the glass behind the sign), silver (lettering, ring, plane), pin blue and the car's sky blue.
- **Headings use Saira**, the same typeface family as the logo lettering; body text is Manrope.
- **Booking widget = a boarding pass.** The big search box from airport-parking reservation sites, styled as a parking pass with a tear-off stub for the price.
- Logo motifs reused across the page: a faint ring behind the hero, a swoosh line that draws itself in, runway lights, a shuttle-route card (lot → McNamara / Evans terminals).

## Run it
```bash
cd demo
pip install -r requirements.txt
python app.py
```
Open http://localhost:5000

## What's where
| File | What it does |
|------|--------------|
| `app.py` | The Python server. Renders the page, calculates the quote ($12/day + $5 fee) when the booking form is submitted, and builds the sample-price table. |
| `templates/index.html` | The page (Jinja template). `{{ ... }}` = values from Python, `{% if %}` / `{% for %}` = logic. |
| `static/style.css` | All styling. Brand colors are at the top in `:root`. |
| `static/skyway-logo.svg` | The logo. **Generated** – don't edit by hand. |
| `tools/build_logo.py` | Python script that draws the logo: turns "SKYWAY"/"PARKING" into vector shapes with fontTools and places every part using measurements from the real sign. |

## Rebuilding the logo
```bash
pip install fonttools
python tools/build_logo.py
```
Fonts used: Saira (SIL Open Font License), in `tools/fonts/`.
