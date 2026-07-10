import { useState, useMemo } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { DIFFICULTY_LABELS, LABEL_COLORS, REPO } from '../../data/staticData'
import './IssuesScene.css'

function timeAgo(d) {
  const days = Math.floor((Date.now() - new Date(d)) / 86400000)
  if (days === 0) return 'today'
  if (days === 1) return 'yesterday'
  if (days < 30) return `${days}d ago`
  return `${Math.floor(days / 30)}mo ago`
}

function IssueNode({ issue, idx }) {
  const labels = issue.labels || []
  const diffLabel = labels.find(l => LABEL_COLORS[l.name])
  const accent = diffLabel ? LABEL_COLORS[diffLabel.name] : '#3d5068'

  return (
    <motion.a
      href={issue.html_url}
      target="_blank"
      rel="noopener noreferrer"
      className="issue-node"
      style={{ '--accent': accent }}
      initial={{ opacity: 0, scale: 0.85, y: 20 }}
      animate={{ opacity: 1, scale: 1, y: 0 }}
      exit={{ opacity: 0, scale: 0.85, y: -10 }}
      transition={{ duration: 0.4, delay: idx * 0.04, ease: [0.16, 1, 0.3, 1] }}
      whileHover={{ y: -4, borderColor: accent, boxShadow: `0 8px 32px ${accent}22` }}
    >
      <div className="issue-node__top">
        <span className="issue-node__num">#{issue.number}</span>
        <span className="issue-node__age">{timeAgo(issue.created_at)}</span>
      </div>
      <p className="issue-node__title">{issue.title}</p>
      <div className="issue-node__labels">
        {labels.slice(0, 3).map(l => (
          <span key={l.name} className="issue-node__label" style={{
            color: LABEL_COLORS[l.name] || '#64748b',
            background: `${LABEL_COLORS[l.name] || '#64748b'}14`,
            borderColor: `${LABEL_COLORS[l.name] || '#64748b'}35`,
          }}>{l.name}</span>
        ))}
      </div>
      <div className="issue-node__accent-bar" />
    </motion.a>
  )
}

function SkeletonNode() {
  return (
    <div className="issue-node issue-node--skeleton">
      <div className="skel" style={{ width: 60, height: 10, marginBottom: 12 }} />
      <div className="skel" style={{ width: '85%', height: 14, marginBottom: 6 }} />
      <div className="skel" style={{ width: '60%', height: 12 }} />
    </div>
  )
}

const FILTERS = [
  { value: 'all', label: 'All' },
  ...DIFFICULTY_LABELS.filter(d => d.value !== 'all'),
]

export default function IssuesScene({ issues, loading }) {
  const [filter, setFilter] = useState('all')

  const filtered = useMemo(() => {
    if (filter === 'all') return issues
    return issues.filter(i => i.labels?.some(l => l.name.toLowerCase() === filter))
  }, [issues, filter])

  return (
    <div className="issues-scene dot-grid">
      {/* Header */}
      <div className="issues-header">
        <div className="issues-header__left">
          <span className="label-mono" style={{ color: 'var(--violet)' }}>◇ &nbsp; ACTIVE ISSUES</span>
          <h2 className="issues-title">
            {loading ? '…' : filtered.length} open tasks
          </h2>
        </div>

        <div className="issues-filters">
          {FILTERS.map(f => (
            <button
              key={f.value}
              className={`issues-filter ${filter === f.value ? 'issues-filter--on' : ''}`}
              style={{ '--fc': LABEL_COLORS[f.value] || 'var(--text-2)' }}
              onClick={() => setFilter(f.value)}
            >
              {f.label}
            </button>
          ))}
        </div>
      </div>

      {/* Grid */}
      <div className="issues-grid-wrap">
        <div className="issues-grid">
          {loading ? (
            [...Array(9)].map((_, i) => <SkeletonNode key={i} />)
          ) : filtered.length === 0 ? (
            <div className="issues-empty">
              <span>🎉</span>
              <p>No issues match this filter.</p>
              <a href={REPO.issues_url} target="_blank" rel="noopener noreferrer" className="btn btn-ghost">
                View all on GitHub ↗
              </a>
            </div>
          ) : (
            <AnimatePresence mode="popLayout">
              {filtered.map((issue, i) => {
                const labels = issue.labels || []
                const diffLabel = labels.find(l => LABEL_COLORS[l.name])
                const accent = diffLabel ? LABEL_COLORS[diffLabel.name] : '#3d5068'
                return (
                  <motion.a
                    key={issue.id}
                    href={issue.html_url}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="issue-node"
                    style={{ '--accent': accent }}
                    initial={{ opacity: 0, scale: 0.88, y: 16 }}
                    animate={{ opacity: 1, scale: 1, y: 0 }}
                    exit={{ opacity: 0, scale: 0.88, y: -10 }}
                    transition={{ duration: 0.35, delay: Math.min(i * 0.04, 0.5), ease: [0.16, 1, 0.3, 1] }}
                    whileHover={{ y: -4, borderColor: accent, boxShadow: `0 8px 32px ${accent}22` }}
                  >
                    <div className="issue-node__top">
                      <span className="issue-node__num">#{issue.number}</span>
                      <span className="issue-node__age">{timeAgo(issue.created_at)}</span>
                    </div>
                    <p className="issue-node__title">{issue.title}</p>
                    <div className="issue-node__labels">
                      {labels.slice(0, 3).map(l => (
                        <span key={l.name} className="issue-node__label" style={{
                          color: LABEL_COLORS[l.name] || '#64748b',
                          background: `${LABEL_COLORS[l.name] || '#64748b'}14`,
                          borderColor: `${LABEL_COLORS[l.name] || '#64748b'}35`,
                        }}>{l.name}</span>
                      ))}
                    </div>
                    <div className="issue-node__accent-bar" />
                  </motion.a>
                )
              })}
            </AnimatePresence>
          )}
        </div>
      </div>

      {/* Footer CTA */}
      <div className="issues-cta">
        <a href={REPO.issues_url} target="_blank" rel="noopener noreferrer" className="btn btn-ghost">
          All issues on GitHub ↗
        </a>
        <a href={REPO.contribute_url} target="_blank" rel="noopener noreferrer" className="btn btn-ghost">
          Read CONTRIBUTING.md ↗
        </a>
      </div>
    </div>
  )
}
