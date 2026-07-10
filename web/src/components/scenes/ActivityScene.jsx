import { useMemo } from 'react'
import { motion } from 'framer-motion'
import { REPO, ROADMAP } from '../../data/staticData'
import './ActivityScene.css'

function timeAgo(d) {
  const days = Math.floor((Date.now() - new Date(d)) / 86400000)
  if (days === 0) return 'today'
  if (days === 1) return 'yesterday'
  if (days < 30) return `${days}d`
  return `${Math.floor(days / 30)}mo`
}

const ROADMAP_STATUS = {
  'done':        { label: 'Done',        color: '#10b981' },
  'in-progress': { label: 'In Progress', color: '#f59e0b' },
  'planned':     { label: 'Planned',     color: '#3d5068' },
}

const CONTRIBUTE_STEPS = [
  { num: '01', label: 'Find an Issue', hint: 'Browse the live issues panel — filter by your skill level.' },
  { num: '02', label: 'Claim It', hint: 'Comment "I\'d like to work on this" — AI assigns you instantly.' },
  { num: '03', label: 'Fork & Run', hint: 'Fork, clone, run bash start.sh. MySQL initialises automatically.' },
  { num: '04', label: 'PR & Review', hint: 'Open a PR. AI Maintainer reviews and approves automatically.' },
]

export default function ActivityScene({ issues, contributors }) {
  const recentIssues = useMemo(() => [...issues].slice(0, 8), [issues])

  return (
    <div className="activity-scene">
      <div className="activity-left">
        {/* Contribution journey */}
        <motion.div
          className="activity-section"
          initial={{ opacity: 0, x: -20 }}
          whileInView={{ opacity: 1, x: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.7 }}
        >
          <span className="label-mono" style={{ color: 'var(--cyan)' }}>◎ &nbsp; CONTRIBUTION JOURNEY</span>
          <h2 className="activity-title">
            Four steps to<br /><span className="grad-cyan">your first PR</span>
          </h2>

          <div className="contrib-steps">
            {CONTRIBUTE_STEPS.map((s, i) => (
              <motion.div
                key={s.num}
                className="contrib-step"
                initial={{ opacity: 0, x: -16 }}
                whileInView={{ opacity: 1, x: 0 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.1, duration: 0.5 }}
              >
                <div className="contrib-step__num">{s.num}</div>
                <div className="contrib-step__body">
                  <div className="contrib-step__label">{s.label}</div>
                  <div className="contrib-step__hint">{s.hint}</div>
                </div>
                {i < CONTRIBUTE_STEPS.length - 1 && <div className="contrib-step__line" />}
              </motion.div>
            ))}
          </div>
        </motion.div>

        {/* CTA */}
        <motion.div
          className="activity-cta"
          initial={{ opacity: 0, y: 12 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ delay: 0.5 }}
        >
          <a href={REPO.url} target="_blank" rel="noopener noreferrer" className="btn btn-primary">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z"/></svg>
            Start Contributing
          </a>
          <a href={REPO.contribute_url} target="_blank" rel="noopener noreferrer" className="btn btn-ghost">
            CONTRIBUTING.md ↗
          </a>
        </motion.div>
      </div>

      <div className="activity-right">
        {/* Recent issues feed */}
        <motion.div
          className="activity-feed"
          initial={{ opacity: 0, x: 20 }}
          whileInView={{ opacity: 1, x: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.7, delay: 0.15 }}
        >
          <div className="activity-feed__header">
            <span className="label-mono">OPEN ISSUES FEED</span>
            <span className="activity-feed__live">
              <span className="os-status-dot" style={{ width: 5, height: 5 }} />
              LIVE
            </span>
          </div>
          <div className="activity-feed__list">
            {recentIssues.length === 0
              ? [...Array(6)].map((_, i) => (
                <div key={i} className="feed-item feed-item--skeleton">
                  <div className="skel" style={{ width: 40, height: 9, marginBottom: 6 }} />
                  <div className="skel" style={{ width: '80%', height: 12 }} />
                </div>
              ))
              : recentIssues.map((issue, i) => (
                <motion.a
                  key={issue.id}
                  href={issue.html_url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="feed-item"
                  initial={{ opacity: 0, x: 12 }}
                  whileInView={{ opacity: 1, x: 0 }}
                  viewport={{ once: true }}
                  transition={{ delay: i * 0.06 }}
                  whileHover={{ x: 4 }}
                >
                  <span className="feed-item__num">#{issue.number}</span>
                  <span className="feed-item__title">{issue.title}</span>
                  <span className="feed-item__age">{timeAgo(issue.created_at)}</span>
                </motion.a>
              ))
            }
          </div>
        </motion.div>

        {/* Roadmap summary */}
        <motion.div
          className="activity-roadmap"
          initial={{ opacity: 0, y: 12 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ delay: 0.3 }}
        >
          <div className="label-mono" style={{ marginBottom: 12 }}>ROADMAP SNAPSHOT</div>
          {ROADMAP.slice(0, 4).map(item => {
            const st = ROADMAP_STATUS[item.status]
            return (
              <div key={item.title} className="roadmap-row">
                <span className="roadmap-row__icon">{item.icon}</span>
                <span className="roadmap-row__title">{item.title}</span>
                <span className="roadmap-row__status" style={{ color: st.color }}>{st.label}</span>
              </div>
            )
          })}
        </motion.div>

        {/* Footer */}
        <div className="activity-footer">
          <span className="label-mono">Built with React · Powered by GitHub API</span>
          <span className="label-mono">MIT License · {new Date().getFullYear()}</span>
        </div>
      </div>
    </div>
  )
}
