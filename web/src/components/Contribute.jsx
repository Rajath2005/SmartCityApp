import { useState } from 'react'
import { CONTRIBUTE_STEPS, DIFFICULTY_LABELS, LABEL_COLORS, REPO } from '../data/staticData'
import './Contribute.css'

function timeAgo(dateStr) {
  const diff = Date.now() - new Date(dateStr).getTime()
  const days = Math.floor(diff / 86400000)
  if (days === 0) return 'today'
  if (days === 1) return 'yesterday'
  if (days < 30) return `${days}d ago`
  if (days < 365) return `${Math.floor(days / 30)}mo ago`
  return `${Math.floor(days / 365)}y ago`
}

function IssueCard({ issue }) {
  const labels = issue.labels || []
  return (
    <a
      href={issue.html_url}
      target="_blank"
      rel="noopener noreferrer"
      className="issue-card card"
    >
      <div className="issue-card__header">
        <span className="issue-card__number">#{issue.number}</span>
        <span className="issue-card__age">{timeAgo(issue.created_at)}</span>
      </div>
      <h4 className="issue-card__title">{issue.title}</h4>
      <div className="issue-card__labels">
        {labels.slice(0, 4).map((l) => (
          <span
            key={l.name}
            className="issue-card__label"
            style={{
              background: `${LABEL_COLORS[l.name] || '#64748b'}22`,
              color: LABEL_COLORS[l.name] || '#64748b',
              borderColor: `${LABEL_COLORS[l.name] || '#64748b'}44`,
            }}
          >
            {l.name}
          </span>
        ))}
      </div>
    </a>
  )
}

export default function Contribute({ issues, loading }) {
  const [filter, setFilter] = useState('all')

  const filtered = filter === 'all'
    ? issues
    : issues.filter((i) =>
        i.labels?.some((l) => l.name.toLowerCase() === filter.toLowerCase())
      )

  return (
    <section id="contribute" className="contribute">
      <div className="container">
        <span className="section-label">Contribute</span>
        <h2 className="section-title">Start contributing today</h2>
        <p className="section-sub">
          The AI Maintainer handles assignment automatically — just claim an issue and open a PR.
          No waiting for maintainer response.
        </p>

        {/* Steps */}
        <div className="contribute__steps">
          {CONTRIBUTE_STEPS.map((s) => (
            <div key={s.step} className="step-card">
              <div className="step-card__num">{s.step}</div>
              <h4 className="step-card__title">{s.title}</h4>
              <p className="step-card__desc">{s.desc}</p>
              {s.code && <code className="step-card__code">{s.code}</code>}
            </div>
          ))}
        </div>

        {/* Issues browser */}
        <div className="contribute__issues">
          <div className="issues-header">
            <h3 className="issues-header__title">
              Open Issues
              {!loading && (
                <span className="issues-header__count">{filtered.length}</span>
              )}
            </h3>
            <div className="issues-filters">
              {DIFFICULTY_LABELS.map((d) => (
                <button
                  key={d.value}
                  className={`filter-btn ${filter === d.value ? 'filter-btn--active' : ''}`}
                  style={{ '--filter-color': d.color }}
                  onClick={() => setFilter(d.value)}
                >
                  {d.label}
                </button>
              ))}
            </div>
          </div>

          {loading ? (
            <div className="issues-grid">
              {[...Array(6)].map((_, i) => (
                <div key={i} className="issue-card card">
                  <div className="skeleton" style={{ height: 14, width: 60, marginBottom: 10 }} />
                  <div className="skeleton" style={{ height: 18, width: '80%', marginBottom: 8 }} />
                  <div className="skeleton" style={{ height: 14, width: '40%' }} />
                </div>
              ))}
            </div>
          ) : filtered.length === 0 ? (
            <div className="issues-empty">
              <span>🎉</span>
              <p>No open issues match this filter — check{' '}
                <a href={REPO.issues_url} target="_blank" rel="noopener noreferrer">all issues on GitHub</a>.
              </p>
            </div>
          ) : (
            <div className="issues-grid">
              {filtered.map((issue) => (
                <IssueCard key={issue.id} issue={issue} />
              ))}
            </div>
          )}

          <div className="issues-cta">
            <a href={REPO.issues_url} target="_blank" rel="noopener noreferrer" className="btn btn-ghost">
              View all issues on GitHub
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <path d="M7 17L17 7M7 7h10v10" />
              </svg>
            </a>
            <a href={REPO.contribute_url} target="_blank" rel="noopener noreferrer" className="btn btn-ghost">
              Read CONTRIBUTING.md
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <path d="M7 17L17 7M7 7h10v10" />
              </svg>
            </a>
          </div>
        </div>
      </div>
    </section>
  )
}
