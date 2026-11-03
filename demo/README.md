# Skyway Parking – Landing Page Demo

A sleek, first-page-only demo (hero + booking card + a couple of sections), built with **Python + Flask**.

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
| `app.py` | The Python server. Renders the page and calculates the price quote ($12/day + $5 fee) when the hero form is submitted. |
| `templates/index.html` | The page layout (Jinja template). `{{ ... }}` = values from Python, `{% if %}` = logic. |
| `static/style.css` | All styling. Brand colors are at the top in `:root`. |

## Logo
The nav uses an SVG mark redrawn from the real sign (pin + swoosh + plane).
To use the official logo file instead, drop it in `static/` and replace the `logo_mark()` call in `index.html`
with `<img src="{{ url_for('static', filename='your-logo.png') }}" height="44">`.
