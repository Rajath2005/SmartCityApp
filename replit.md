# SmartCity Hub — Contributor Portal

A React web application (on the `feature/web-dashboard` branch) serving as the open-source contributor portal for [SmartCityApp](https://github.com/Rajath2005/SmartCityApp). It documents the project, displays live GitHub data, and helps contributors find and claim issues.

## How to run

The workflow `Start application` runs the web dev server:

```bash
cd web && npm run dev
```

The app runs at **port 5000** and is visible in the Replit preview pane.

## Web app stack

- **Framework:** React 18 + Vite 5
- **Styling:** Plain CSS with CSS custom properties (design tokens)
- **Data:** GitHub API (`/repos/Rajath2005/SmartCityApp`) — falls back to static data if rate-limited
- **Fonts:** Inter + JetBrains Mono (Google Fonts)

## Web app structure

```
web/
  index.html                        # HTML shell with Google Fonts
  vite.config.js                    # Vite config (port 5000, host true)
  package.json                      # React + Vite deps
  src/
    main.jsx                        # React entry point
    App.jsx                         # Root component — composes all sections
    styles/global.css               # Design tokens, reset, shared utilities
    data/staticData.js              # All static content & fallback data
    hooks/useGitHub.js              # GitHub API hook (stats, contributors, issues)
    components/
      Nav.jsx / Nav.css             # Fixed navbar with scroll effect + mobile menu
      Hero.jsx / Hero.css           # Full-height hero with live repo stats
      About.jsx / About.css         # Project overview + tech stack grid
      Architecture.jsx / Architecture.css  # Tabbed architecture diagrams
      Features.jsx / Features.css   # User & Admin feature cards + DB schema
      Contribute.jsx / Contribute.css      # Steps + live filterable issues browser
      Roadmap.jsx / Roadmap.css     # Roadmap with progress bar
      AIMaintainer.jsx / AIMaintainer.css  # AI Maintainer documentation
      Contributors.jsx / Contributors.css  # Live contributors grid + CTA
      Footer.jsx / Footer.css       # Footer with links
```

## Java CLI app (original project)

The original Java CLI application lives in `src/` and `db_setup.sql`. To run it:

```bash
bash start.sh
```

This initialises MySQL, loads the schema, and launches `target/app.jar`.

## User preferences

- Keep existing project structure (Java/Maven/MySQL) as-is.
- Web dashboard is on the `feature/web-dashboard` branch.
