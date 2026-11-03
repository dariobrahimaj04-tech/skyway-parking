"""
Skyway Parking - landing page demo.

This is a tiny Flask web app. Flask's job here is simple:
  1. When someone opens "/", it renders templates/index.html.
  2. If the booking form in the hero was submitted, the dates come in as
     URL parameters (e.g. /?checkin=2026-11-10T08:00&checkout=...).
     We calculate a price quote in Python and pass it to the template,
     which then shows a "Your quote" box inside the hero card.

No JavaScript is needed for the quote - all the logic lives in this file.

Run it with:   python app.py      then open http://localhost:5000
"""

from datetime import datetime, timedelta
import math

from flask import Flask, render_template, request

app = Flask(__name__)

# ---- Business settings (same numbers the existing site uses) -------------
DAILY_RATE = 12.00     # dollars per day
BOOKING_FEE = 5.00     # one-time online booking fee

BUSINESS = {
    "name": "Skyway Parking",
    "airport": "Detroit Metro Airport (DTW)",
    "address": "8501 Inkster Rd, Taylor, MI 48180",
    "phone_display": "(313) 254-2699",
    "phone_link": "+13132542699",
}

# The form uses <input type="datetime-local">, which sends this format.
DATE_FORMAT = "%Y-%m-%dT%H:%M"


def calculate_quote(checkin_text, checkout_text):
    """Turn the two form values into a quote dictionary (or an error).

    Returns a dict like:
        {"days": 4, "parking": 48.0, "fee": 5.0, "total": 53.0, ...}
    or {"error": "..."} if the input doesn't make sense.
    """
    try:
        checkin = datetime.strptime(checkin_text, DATE_FORMAT)
        checkout = datetime.strptime(checkout_text, DATE_FORMAT)
    except (TypeError, ValueError):
        return {"error": "Please pick both a drop-off and a pick-up time."}

    if checkout <= checkin:
        return {"error": "Pick-up has to be after drop-off."}

    # Any part of a day counts as a full day (25 hours = 2 days).
    hours = (checkout - checkin).total_seconds() / 3600
    days = max(1, math.ceil(hours / 24))

    parking = days * DAILY_RATE
    return {
        "days": days,
        "parking": parking,
        "fee": BOOKING_FEE,
        "total": parking + BOOKING_FEE,
        "checkin_pretty": checkin.strftime("%a, %b %d · %I:%M %p"),
        "checkout_pretty": checkout.strftime("%a, %b %d · %I:%M %p"),
    }


@app.route("/")
def home():
    # Default form values: drop off tomorrow at 8 AM, pick up 4 days later.
    tomorrow = (datetime.now() + timedelta(days=1)).replace(
        hour=8, minute=0, second=0, microsecond=0
    )
    checkin = request.args.get("checkin", tomorrow.strftime(DATE_FORMAT))
    checkout = request.args.get(
        "checkout", (tomorrow + timedelta(days=4)).strftime(DATE_FORMAT)
    )

    # Only build a quote once the visitor actually pressed the button.
    quote = None
    if "checkin" in request.args:
        quote = calculate_quote(checkin, checkout)

    return render_template(
        "index.html",
        biz=BUSINESS,
        rate=DAILY_RATE,
        checkin=checkin,
        checkout=checkout,
        quote=quote,
    )


if __name__ == "__main__":
    app.run(debug=True)
