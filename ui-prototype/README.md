# FoodAI UI Prototype (Design v1)

Static, design-forward prototype for:
- Consumer responsive web (Food + Grocery + Rides entry points)
- Driver **Command Center** with multi-stop batching + AI route coach

## Open
Open `index.html` in a browser.

Optional local server (recommended for navigation/state):
- PowerShell: `cd ui-prototype; python -m http.server 5173`
- Then open: `http://localhost:5173`

## Auth pages
- Signup: `http://localhost:5173/signup.html`
- Login: `http://localhost:5173/login.html`

These pages call the `user-service` endpoints:
- `POST http://localhost:8082/api/v1/auth/signup`
- `POST http://localhost:8082/api/v1/auth/login`

## Notes
- This is intentionally “design first” (light interactivity, no backend).
- Copy and adapt the patterns into a real frontend (Next.js/React/etc.) when ready.

