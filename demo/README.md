# Skyway Parking – Landing Page Demo

A first-page demo built with **Python + Flask**.

- **Design style:** modelled on pascalwebsolutions.com (off-white + navy + blue, Poppins/Inter, square corners, thin borders, dash labels).
- **Content & booking flow:** modelled on airportparkingreservations.com (airport / check-in / check-out booking bar, trust badges, why-book, how-it-works).

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
| `templates/_logo.html` | The Skyway logo drawn as SVG. `on_light=True` gives the navy-lettered version for white backgrounds. |
| `templates/index.html` | The page layout (Jinja template). `{{ ... }}` = values from Python, `{% if %}` = logic. |
| `static/style.css` | All styling. Brand colors are at the top in `:root`. |

## Logo
The logo is redrawn from the official sign as SVG (`templates/_logo.html`).
The hero shows it in its real silver/blue colors on navy; the white nav and footer use a navy-lettered version.
To use an official logo file instead, put it in `static/` and replace a `logo(...)` call with
`<img src="{{ url_for('static', filename='your-logo.png') }}" height="74">`.
