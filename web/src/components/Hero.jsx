import { REPO } from '../data/staticData'
import './Hero.css'

function StatPill({ value, label, loading }) {
  return (
    <div className="hero-stat">
      <span className="hero-stat__value">
        {loading ? <span className="skeleton" style={{ width: 40, height: 24, display: 'inline-block' }} /> : value}
      </span>
      <span className="hero-stat__label">{label}</span>
    </div>
  )
}

export default function Hero({ stats, loading }) {
  return (
    <section className="hero city-grid" id="hero">
      {/* Ambient orbs */}
      <div className="hero-orb hero-orb--cyan" />
      <div className="hero-orb hero-orb--amber" />

      <div className="container hero__inner">
        <div className="hero__badge anim-fade-up" style={{ animationDelay: '.05s' }}>
          <span className="hero__badge-dot" />
          Open Source · Beginner Friendly · Hacktoberfest
        </div>

        <h1 className="hero__title anim-fade-up" style={{ animationDelay: '.12s' }}>
          The Open Source<br />
          <span className="grad-cyan">Smart City Guide</span>
        </h1>

        <p className="hero__sub anim-fade-up" style={{ animationDelay: '.2s' }}>
          A beginner-friendly Java CLI app with an AI-powered maintainer —
          explore the codebase, understand the architecture, and contribute with confidence.
        </p>

        <div className="hero__actions anim-fade-up" style={{ animationDelay: '.28s' }}>
          <a href="#contribute" className="btn btn-primary">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
              <path d="M12 5v14M5 12l7 7 7-7" />
            </svg>
            Contribute Now
          </a>
          <a href={REPO.url} target="_blank" rel="noopener noreferrer" className="btn btn-ghost">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z" />
            </svg>
            View on GitHub
          </a>
        </div>

        {/* Live stats bar */}
        <div className="hero__stats anim-fade-up" style={{ animationDelay: '.36s' }}>
          <StatPill value={`★ ${stats?.stars ?? '—'}`} label="Stars" loading={loading} />
          <div className="hero-stat-sep" />
          <StatPill value={stats?.forks ?? '—'} label="Forks" loading={loading} />
          <div className="hero-stat-sep" />
          <StatPill value={stats?.open_issues ?? '—'} label="Open Issues" loading={loading} />
          <div className="hero-stat-sep" />
          <StatPill value="10+" label="Contributors" loading={false} />
        </div>

        {/* Scroll cue */}
        <div className="hero__scroll anim-fade-in" style={{ animationDelay: '.7s' }}>
          <div className="hero__scroll-line" />
          <span>Scroll to explore</span>
        </div>
      </div>
    </section>
  )
}
