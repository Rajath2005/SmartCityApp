import './Contributors.css'
import { REPO } from '../data/staticData'

function ContributorCard({ contributor, rank }) {
  const medals = ['🥇', '🥈', '🥉']
  return (
    <a
      href={contributor.html_url}
      target="_blank"
      rel="noopener noreferrer"
      className="contributor-card card glow-cyan"
    >
      <div className="contributor-card__avatar-wrap">
        <img
          src={contributor.avatar_url}
          alt={contributor.login}
          className="contributor-card__avatar"
          loading="lazy"
          onError={(e) => {
            e.target.src = `https://ui-avatars.com/api/?name=${contributor.login}&background=0D1828&color=00D4FF&bold=true`
          }}
        />
        {rank < 3 && (
          <span className="contributor-card__medal">{medals[rank]}</span>
        )}
      </div>
      <div className="contributor-card__info">
        <span className="contributor-card__name">@{contributor.login}</span>
        <span className="contributor-card__count">
          {contributor.contributions} commit{contributor.contributions !== 1 ? 's' : ''}
        </span>
      </div>
    </a>
  )
}

function SkeletonCard() {
  return (
    <div className="contributor-card card">
      <div className="contributor-card__avatar-wrap">
        <div className="skeleton contributor-card__avatar" />
      </div>
      <div className="contributor-card__info">
        <div className="skeleton" style={{ height: 14, width: 80 }} />
        <div className="skeleton" style={{ height: 12, width: 60, marginTop: 6 }} />
      </div>
    </div>
  )
}

export default function Contributors({ contributors, loading }) {
  return (
    <section id="contributors" className="contributors">
      <div className="container">
        <span className="section-label">Community</span>
        <h2 className="section-title">Built by contributors</h2>
        <p className="section-sub">
          Every commit counts. The project is shaped by developers learning Java, open-source,
          and software engineering — from first PR to repeated contributor.
        </p>

        <div className="contributors__grid">
          {loading
            ? [...Array(8)].map((_, i) => <SkeletonCard key={i} />)
            : contributors.map((c, i) => (
                <ContributorCard key={c.login} contributor={c} rank={i} />
              ))}
        </div>

        <div className="contributors__cta">
          <div className="contributors__join-card card glow-cyan">
            <span className="contributors__join-icon">🚀</span>
            <div>
              <h3 className="contributors__join-title">Want to see your name here?</h3>
              <p className="contributors__join-desc">
                Claim an issue, make a change, open a PR. The AI Maintainer will assign
                and review it automatically. Your first contribution could be live in days.
              </p>
            </div>
            <a href={REPO.url} target="_blank" rel="noopener noreferrer" className="btn btn-primary">
              Start Contributing
            </a>
          </div>
        </div>
      </div>
    </section>
  )
}
