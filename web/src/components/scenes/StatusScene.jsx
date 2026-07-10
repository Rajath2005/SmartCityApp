import { useEffect, useRef, useState } from 'react'
import { motion, useInView, animate } from 'framer-motion'
import { REPO } from '../../data/staticData'
import './StatusScene.css'

function Counter({ to, loading }) {
  const ref = useRef(null)
  const [val, setVal] = useState(0)
  const inView = useInView(ref, { once: true })

  useEffect(() => {
    if (!inView || loading || !to) return
    const ctrl = animate(0, to, {
      duration: 2.2,
      ease: [0.16, 1, 0.3, 1],
      onUpdate: v => setVal(Math.floor(v)),
    })
    return ctrl.stop
  }, [inView, to, loading])

  return <span ref={ref}>{loading ? '—' : val.toLocaleString()}</span>
}

const BOOT_LINES = [
  '> Initializing SmartCity OS v1.0.0…',
  '> Connecting to GitHub API…',
  '> Loading repository metadata…',
  '> Mounting contributor database…',
  '> Verifying AI Maintainer module…',
  '> All systems operational.',
]

// Plays a non-blocking typewriter strip above the stats.
// Once done it fades out — stats are always visible underneath.
function BootTicker() {
  const [idx, setIdx] = useState(0)
  const [visible, setVisible] = useState(true)

  useEffect(() => {
    if (idx >= BOOT_LINES.length) {
      const t = setTimeout(() => setVisible(false), 1200)
      return () => clearTimeout(t)
    }
    const t = setTimeout(() => setIdx(i => i + 1), 200)
    return () => clearTimeout(t)
  }, [idx])

  if (!visible) return null

  return (
    <motion.div
      className="boot-ticker"
      animate={{ opacity: idx >= BOOT_LINES.length ? 0 : 1 }}
      transition={{ duration: 0.6 }}
    >
      <span className="boot-ticker__line">
        {BOOT_LINES[Math.min(idx, BOOT_LINES.length - 1)]}
      </span>
      {idx < BOOT_LINES.length && <span className="term-cursor" />}
    </motion.div>
  )
}

export default function StatusScene({ stats, contributors, loading }) {
  return (
    <div className="status-scene scanlines">
      <div className="status-bg-ring" />
      <div className="status-bg-glow" />

      <div className="status-inner">
        {/* ── Left: headline ── */}
        <div className="status-left">
          <motion.div
            className="label-mono"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.1, duration: 0.6 }}
          >
            SMARTCITY OS &nbsp;·&nbsp; v1.0.0 &nbsp;·&nbsp; OPEN SOURCE
          </motion.div>

          <motion.h1
            className="status-headline"
            initial={{ opacity: 0, y: 32 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2, duration: 0.9, ease: [0.16, 1, 0.3, 1] }}
          >
            The Open<br />
            <span className="grad-cyan">Source</span><br />
            City Guide
          </motion.h1>

          <motion.p
            className="status-sub"
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.5, duration: 0.7 }}
          >
            A beginner-friendly Java CLI app with an AI-powered maintainer.
            Explore the architecture, claim an issue, and ship your first contribution.
          </motion.p>

          <motion.div
            className="status-actions"
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.7, duration: 0.6 }}
          >
            <a href={REPO.url} target="_blank" rel="noopener noreferrer" className="btn btn-primary">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
                <path d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z"/>
              </svg>
              View Repository
            </a>
            <a href={REPO.issues_url} target="_blank" rel="noopener noreferrer" className="btn btn-ghost">
              Browse Issues ↗
            </a>
          </motion.div>
        </div>

        {/* ── Right: terminal panel ── */}
        <motion.div
          className="status-panel"
          initial={{ opacity: 0, x: 40 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 0.3, duration: 0.9, ease: [0.16, 1, 0.3, 1] }}
        >
          {/* Terminal chrome */}
          <div className="term-chrome">
            <div className="term-chrome__dots">
              <span className="dot dot--red"   />
              <span className="dot dot--amber" />
              <span className="dot dot--green" />
            </div>
            <span className="term-chrome__title label-mono">smartcity-os — system status</span>
          </div>

          {/* Non-blocking boot ticker */}
          <BootTicker />

          {/* Stats — always visible */}
          <motion.div
            className="status-stats"
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.5, duration: 0.6, ease: [0.16, 1, 0.3, 1] }}
          >
            {/* Metric strip */}
            <div className="stat-strip">
              {[
                { key: 'stars',        label: 'Stars',        value: stats?.stars },
                { key: 'forks',        label: 'Forks',        value: stats?.forks },
                { key: 'issues',       label: 'Open Issues',  value: stats?.open_issues },
                { key: 'contributors', label: 'Contributors', value: contributors?.length || stats?.subscribers_count },
              ].map((s, i) => (
                <div key={s.key} className="stat-strip__col">
                  <span className="stat-strip__label label-mono">{s.label}</span>
                  <span className="stat-strip__num">
                    <Counter to={s.value} loading={loading} />
                  </span>
                  {i < 3 && <div className="stat-strip__divider" />}
                </div>
              ))}
            </div>

            {/* Health board */}
            <div className="health-board">
              <div className="health-board__header">
                <span className="label-mono">LIVE HEALTH</span>
                <span className="health-board__all-ok">
                  <span className="health-pulse" />
                  All systems nominal
                </span>
              </div>

              <div className="health-rows">
                {[
                  { name: 'GitHub API',     uptime: '100.0' },
                  { name: 'MySQL 8.4',      uptime: '99.9'  },
                  { name: 'AI Maintainer',  uptime: '100.0' },
                  { name: 'Java CLI v1.0',  uptime: '100.0' },
                  { name: 'GitHub Actions', uptime: '99.8'  },
                ].map((svc, i) => (
                  <motion.div
                    key={svc.name}
                    className="health-row"
                    initial={{ opacity: 0, x: -8 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: 0.6 + i * 0.07, duration: 0.3 }}
                  >
                    <span className="health-row__dot" />
                    <span className="health-row__name">{svc.name}</span>
                    <div className="health-row__bar-track">
                      <motion.div
                        className="health-row__bar-fill"
                        initial={{ scaleX: 0 }}
                        animate={{ scaleX: parseFloat(svc.uptime) / 100 }}
                        transition={{ delay: 0.7 + i * 0.07, duration: 0.8, ease: [0.16, 1, 0.3, 1] }}
                        style={{ originX: 0 }}
                      />
                    </div>
                    <span className="health-row__pct">{svc.uptime}%</span>
                  </motion.div>
                ))}
              </div>
            </div>
          </motion.div>
        </motion.div>
      </div>

      {/* Scroll hint */}
      <motion.div
        className="status-scroll-hint"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 2.5, duration: 0.8 }}
      >
        <div className="status-scroll-hint__line" />
        <span className="label-mono">scroll to explore</span>
      </motion.div>
    </div>
  )
}
