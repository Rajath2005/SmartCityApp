import { AI_EVENTS, AI_MODELS } from '../data/staticData'
import './AIMaintainer.css'

const ACTIONS = [
  {
    icon: '🤝',
    title: 'Auto-Assigns Issues',
    desc: 'Reads every comment. When it detects "I\'d like to work on this" intent (even phrased differently), it assigns the contributor instantly — no maintainer involvement needed.',
    color: 'var(--cyan)',
  },
  {
    icon: '👋',
    title: 'Welcomes Contributors',
    desc: 'When a first-time contributor opens a PR, the bot posts a personalised welcome message with review expectations and next steps.',
    color: 'var(--green)',
  },
  {
    icon: '🔍',
    title: 'Reviews Pull Requests',
    desc: 'Runs deterministic rule checks first (merge conflicts, linked issues, hardcoded secrets, TODO/FIXME). Only escalates to AI inference when rules pass — keeping costs low.',
    color: 'var(--amber)',
  },
  {
    icon: '📏',
    title: 'Enforces Code Quality',
    desc: 'Validates Java Checkstyle compliance and catches common violations — missing Javadoc, magic numbers, overly long lines — before a human reviewer sees the PR.',
    color: 'var(--purple)',
  },
]

export default function AIMaintainer() {
  return (
    <section id="ai-maintainer" className="ai-maintainer">
      <div className="container">
        <span className="section-label">AI Maintainer</span>
        <h2 className="section-title">
          The bot behind the scenes
        </h2>
        <p className="section-sub">
          A Python automation system built with Clean Architecture — not a GitHub App, not a simple script.
          It handles the repetitive work so human maintainers can focus on direction.
        </p>

        {/* What it does */}
        <div className="aim-actions">
          {ACTIONS.map((a) => (
            <div key={a.title} className="aim-action card" style={{ '--action-color': a.color }}>
              <span className="aim-action__icon">{a.icon}</span>
              <h4 className="aim-action__title">{a.title}</h4>
              <p className="aim-action__desc">{a.desc}</p>
            </div>
          ))}
        </div>

        {/* Event flow */}
        <div className="aim-flow card">
          <h3 className="aim-flow__title">Event Processing Flow</h3>
          <p className="aim-flow__sub">Every GitHub event follows this pipeline. AI is only invoked when deterministic rules don't block the request.</p>
          <div className="aim-flow__pipeline">
            {AI_EVENTS.map((e, i) => (
              <div key={e.event} className="aim-flow__step">
                <div className="aim-flow__step-event" style={{ '--ev-color': e.color }}>
                  <span>{e.icon}</span>
                  <strong>{e.event}</strong>
                </div>
                <div className="aim-flow__step-arrow">→</div>
                <div className="aim-flow__step-action">
                  <p>{e.action}</p>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Models */}
        <div className="aim-models">
          <h3 className="aim-models__title">Pluggable AI Models</h3>
          <p className="aim-models__sub">
            The <code>BaseAIClient</code> abstract class means any model can be swapped in
            with zero changes to the review engine. The active model is set in <code>bot.yml</code>.
          </p>
          <div className="aim-models__grid">
            {AI_MODELS.map((m) => (
              <div key={m.name} className={`aim-model-card card ${m.badge === 'active' ? 'glow-cyan' : ''}`}>
                <div className="aim-model-card__header">
                  <span className="aim-model-card__name">🧠 {m.name}</span>
                  <span className={`badge ${m.badge === 'active' ? 'badge-cyan' : 'badge-purple'}`}>
                    {m.badge === 'active' ? '● Active' : 'Supported'}
                  </span>
                </div>
                <code className="aim-model-card__id">{m.id}</code>
                <p className="aim-model-card__role">{m.role} model</p>
              </div>
            ))}
          </div>
        </div>

        {/* Principle callout */}
        <div className="aim-principle">
          <div className="aim-principle__icon">💡</div>
          <div>
            <strong>Design principle: AI never talks to GitHub</strong>
            <p>
              The AI layer receives plain prompt strings and returns plain text.
              It has zero knowledge of GitHub APIs, issue numbers, or PR metadata.
              Engines orchestrate between the two worlds, keeping each layer independently testable.
            </p>
          </div>
        </div>
      </div>
    </section>
  )
}
