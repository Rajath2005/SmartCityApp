import { useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import './PipelineScene.css'

const STAGES = [
  {
    id: 'github',
    icon: '⚙️',
    label: 'GitHub',
    sub: 'Events',
    color: '#2088FF',
    detail: {
      title: 'GitHub Actions Triggers',
      body: 'Four event types kick off the pipeline: issue_opened, issue_comment, pull_request.opened, and pull_request.synchronize. A single Python script receives the raw JSON payload via the GITHUB_EVENT_PATH env var.',
      code: 'on: [issues, issue_comment, pull_request]',
    },
  },
  {
    id: 'parser',
    icon: '📡',
    label: 'Event Parser',
    sub: 'event_parser.py',
    color: '#00d4ff',
    detail: {
      title: 'event_parser.py',
      body: "Converts the raw webhook JSON into typed Python dataclasses (GitHubEvent). The only layer that knows about GitHub\u2019s JSON structure \u2014 all downstream layers work with clean Python objects.",
      code: '@dataclass\nclass IssueEvent:\n  action: str\n  issue: Issue\n  sender: User',
    },
  },
  {
    id: 'engine',
    icon: '⚙️',
    label: 'Rule Engine',
    sub: 'rule_engine.py',
    color: '#f59e0b',
    detail: {
      title: 'rule_engine.py',
      body: 'Deterministic checks run first — merge conflicts, linked issues, hardcoded secrets, TODO/FIXME markers, Checkstyle violations. If rules block the request, AI is never invoked.',
      code: 'if detect_secrets(diff):\n  return block("hardcoded secret")',
    },
  },
  {
    id: 'ai',
    icon: '🧠',
    label: 'AI Model',
    sub: 'DeepSeek V3',
    color: '#8b5cf6',
    detail: {
      title: 'hf_client.py — HuggingFace',
      body: 'The AI layer receives a plain prompt string and returns plain text. It has zero knowledge of GitHub — no issue numbers, no PR metadata. The review engine assembles the context and parses the output.',
      code: 'model = "deepseek-ai/DeepSeek-V3-0324"\nresponse = hf_client.infer(prompt)',
    },
  },
  {
    id: 'response',
    icon: '🐙',
    label: 'GitHub',
    sub: 'github_client.py',
    color: '#10b981',
    detail: {
      title: 'github_client.py',
      body: 'The output layer. Wraps GitHub REST API calls: post_comment, add_label, assign_issue, and approve_pr. Engines compose what to say; this layer delivers it.',
      code: 'gh.post_comment(issue_number, body)\ngh.add_label(issue_number, "reviewed")',
    },
  },
]

const PATHS = [
  'M 124 180 C 200 180 224 180 280 180',
  'M 316 180 C 392 180 416 180 472 180',
  'M 508 180 C 584 180 608 180 664 180',
  'M 700 180 C 776 180 800 180 856 180',
]

const EVENTS = [
  { label: 'PR Opened', color: '#10b981', delay: 0 },
  { label: 'Issue Comment', color: '#00d4ff', delay: 1.2 },
  { label: 'Issue Opened', color: '#f59e0b', delay: 2.4 },
]

export default function PipelineScene() {
  const [active, setActive] = useState(null)
  const detail = active !== null ? STAGES[active].detail : null

  return (
    <div className="pipeline-scene">
      <div className="pipeline-bg-circuit" />

      <div className="pipeline-header">
        <span className="label-mono" style={{ color: 'var(--violet)' }}>◆ &nbsp; AI MAINTAINER</span>
        <h2 className="pipeline-title">
          The bot<br /><span className="grad-violet">behind the scenes</span>
        </h2>
        <p className="pipeline-sub">
          Every GitHub event flows through this pipeline. Deterministic rules run first —
          the AI only engages when they pass.
        </p>
      </div>

      {/* Live event labels */}
      <div className="pipeline-events">
        {EVENTS.map(ev => (
          <motion.div
            key={ev.label}
            className="pipeline-event-tag"
            style={{ background: `${ev.color}18`, borderColor: `${ev.color}44`, color: ev.color }}
            animate={{ opacity: [0.4, 1, 0.4] }}
            transition={{ duration: 3, repeat: Infinity, delay: ev.delay, ease: 'easeInOut' }}
          >
            <span className="pipeline-event-tag__dot" style={{ background: ev.color }} />
            {ev.label}
          </motion.div>
        ))}
      </div>

      {/* Main pipeline SVG */}
      <div className="pipeline-svg-wrap">
        <svg viewBox="0 0 980 360" className="pipeline-svg" preserveAspectRatio="xMidYMid meet">
          {/* Connection lines */}
          {PATHS.map((p, i) => (
            <g key={i}>
              <path d={p} fill="none" stroke="rgba(255,255,255,0.05)" strokeWidth={2} />
              {/* flowing particle */}
              <motion.circle
                r={4}
                fill={STAGES[i].color}
                filter="url(#glow-sm)"
              >
                <animateMotion dur={`${1.2 + i * 0.15}s`} repeatCount="indefinite" path={p} />
              </motion.circle>
            </g>
          ))}

          <defs>
            <filter id="glow-sm">
              <feGaussianBlur in="SourceGraphic" stdDeviation="3" result="blur" />
              <feMerge><feMergeNode in="blur" /><feMergeNode in="SourceGraphic" /></feMerge>
            </filter>
          </defs>

          {/* Stage boxes */}
          {STAGES.map((s, i) => {
            const cx = 88 + i * 192
            const cy = 180
            const isActive = active === i
            return (
              <g key={s.id} onClick={() => setActive(prev => prev === i ? null : i)} style={{ cursor: 'pointer' }}>
                {/* glow */}
                {isActive && (
                  <motion.rect
                    x={cx - 54} y={cy - 54} width={108} height={108} rx={16}
                    fill={s.color} opacity={0.08}
                    animate={{ opacity: [0.06, 0.14, 0.06] }}
                    transition={{ duration: 2, repeat: Infinity }}
                  />
                )}
                {/* box */}
                <rect
                  x={cx - 48} y={cy - 48} width={96} height={96} rx={12}
                  fill="rgba(5,13,24,0.9)"
                  stroke={isActive ? s.color : `${s.color}44`}
                  strokeWidth={isActive ? 2 : 1}
                />
                {/* top accent */}
                <rect x={cx - 48} y={cy - 48} width={96} height={3} rx={2} fill={s.color} opacity={isActive ? 1 : 0.5} />
                {/* icon */}
                <text x={cx} y={cy - 6} textAnchor="middle" fontSize={26}>{s.icon}</text>
                {/* label */}
                <text x={cx} y={cy + 16} textAnchor="middle" fill={isActive ? '#e2e8f0' : '#94a3b8'} fontSize={11} fontFamily="Inter,sans-serif" fontWeight={700}>{s.label}</text>
                <text x={cx} y={cy + 30} textAnchor="middle" fill="#3d5068" fontSize={9} fontFamily="JetBrains Mono,monospace">{s.sub}</text>
                {/* index */}
                <text x={cx - 42} y={cy - 34} fill={`${s.color}88`} fontSize={9} fontFamily="JetBrains Mono,monospace">0{i + 1}</text>
              </g>
            )
          })}
        </svg>

        {/* Stage detail panel */}
        <AnimatePresence>
          {detail && (
            <motion.div
              className="pipeline-detail"
              key={active}
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: 16 }}
              transition={{ duration: 0.3, ease: [0.16, 1, 0.3, 1] }}
            >
              <button className="pipeline-detail__close" onClick={() => setActive(null)}>✕</button>
              <span className="label-mono" style={{ color: STAGES[active].color }}>STAGE {String(active + 1).padStart(2, '0')}</span>
              <h4 className="pipeline-detail__title">{detail.title}</h4>
              <p className="pipeline-detail__body">{detail.body}</p>
              <pre className="pipeline-detail__code">{detail.code}</pre>
            </motion.div>
          )}
        </AnimatePresence>
      </div>

      {/* Principle callout */}
      <motion.div
        className="pipeline-principle"
        initial={{ opacity: 0, y: 10 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true }}
        transition={{ delay: 0.4 }}
      >
        <span>💡</span>
        <span>
          <strong>AI never talks to GitHub</strong> — the AI layer receives a plain prompt string and
          returns plain text. Engines orchestrate between worlds.
        </span>
      </motion.div>
    </div>
  )
}
