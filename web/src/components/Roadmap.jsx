import { ROADMAP } from '../data/staticData'
import './Roadmap.css'

const STATUS = {
  'done':        { label: 'Done',        color: 'var(--green)',  bg: 'var(--green-glow)' },
  'in-progress': { label: 'In Progress', color: 'var(--amber)',  bg: 'var(--amber-glow)' },
  'planned':     { label: 'Planned',     color: 'var(--text-3)', bg: 'rgba(74,96,128,.08)' },
}

export default function Roadmap() {
  const done = ROADMAP.filter(r => r.status === 'done').length
  const inProgress = ROADMAP.filter(r => r.status === 'in-progress').length
  const total = ROADMAP.length

  return (
    <section id="roadmap" className="roadmap">
      <div className="container">
        <span className="section-label">Roadmap</span>
        <h2 className="section-title">Where the project is headed</h2>
        <p className="section-sub">
          Each roadmap item is a contribution opportunity. Pick one that interests you,
          open a GitHub issue, and help shape the future of SmartCityApp.
        </p>

        {/* Progress bar */}
        <div className="roadmap__progress">
          <div className="roadmap__progress-labels">
            <span className="roadmap__progress-title">Overall Progress</span>
            <span className="roadmap__progress-counts">
              <span style={{ color: 'var(--green)' }}>{done} done</span>
              {' · '}
              <span style={{ color: 'var(--amber)' }}>{inProgress} in progress</span>
              {' · '}
              <span style={{ color: 'var(--text-3)' }}>{total - done - inProgress} planned</span>
            </span>
          </div>
          <div className="roadmap__bar">
            <div
              className="roadmap__bar-fill roadmap__bar-fill--done"
              style={{ width: `${(done / total) * 100}%` }}
            />
            <div
              className="roadmap__bar-fill roadmap__bar-fill--progress"
              style={{ width: `${(inProgress / total) * 100}%` }}
            />
          </div>
        </div>

        <div className="roadmap__list">
          {ROADMAP.map((item, i) => {
            const st = STATUS[item.status]
            return (
              <a
                key={item.title}
                href={item.issueUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="roadmap-item card"
                style={{ animationDelay: `${i * .06}s` }}
              >
                <div className="roadmap-item__left">
                  <span className="roadmap-item__icon">{item.icon}</span>
                  <div className="roadmap-item__track">
                    <div
                      className="roadmap-item__dot"
                      style={{ background: st.color, boxShadow: `0 0 8px ${st.color}` }}
                    />
                    {i < ROADMAP.length - 1 && <div className="roadmap-item__line" />}
                  </div>
                </div>
                <div className="roadmap-item__body">
                  <div className="roadmap-item__header">
                    <h4 className="roadmap-item__title">{item.title}</h4>
                    <span
                      className="badge roadmap-item__status"
                      style={{ color: st.color, background: st.bg, borderColor: `${st.color}44` }}
                    >
                      {item.status === 'in-progress' && <span className="roadmap-item__pulse" />}
                      {st.label}
                    </span>
                  </div>
                  <p className="roadmap-item__desc">{item.desc}</p>
                  <span className="roadmap-item__label badge badge-cyan">{item.label}</span>
                </div>
              </a>
            )
          })}
        </div>
      </div>
    </section>
  )
}
