import { USER_FEATURES, ADMIN_FEATURES } from '../data/staticData'
import './Features.css'

function FeatureCard({ item, variant }) {
  return (
    <div className={`feature-card card ${variant === 'admin' ? 'glow-amber' : 'glow-cyan'}`}>
      <span className="feature-card__icon">{item.icon}</span>
      <h4 className="feature-card__title">{item.title}</h4>
      <p className="feature-card__desc">{item.desc}</p>
    </div>
  )
}

export default function Features() {
  return (
    <section id="features" className="features">
      <div className="container">
        <span className="section-label">Features</span>
        <h2 className="section-title">What the app can do</h2>
        <p className="section-sub">
          Two distinct roles, each with their own menu system and database permissions.
          The CLI routes all interactions through a single Scanner loop.
        </p>

        <div className="features__grid">
          {/* User column */}
          <div className="features__column">
            <div className="features__col-header features__col-header--user">
              <span className="features__col-icon">👤</span>
              <div>
                <h3 className="features__col-title">User Features</h3>
                <p className="features__col-sub">For residents & tourists exploring the city</p>
              </div>
              <span className="badge badge-cyan">USER role</span>
            </div>
            <div className="features__cards">
              {USER_FEATURES.map((f) => (
                <FeatureCard key={f.title} item={f} variant="user" />
              ))}
            </div>
          </div>

          <div className="features__divider" />

          {/* Admin column */}
          <div className="features__column">
            <div className="features__col-header features__col-header--admin">
              <span className="features__col-icon">🛠️</span>
              <div>
                <h3 className="features__col-title">Admin Features</h3>
                <p className="features__col-sub">For city data managers and maintainers</p>
              </div>
              <span className="badge badge-amber">ADMIN role</span>
            </div>
            <div className="features__cards">
              {ADMIN_FEATURES.map((f) => (
                <FeatureCard key={f.title} item={f} variant="admin" />
              ))}
            </div>
          </div>
        </div>

        {/* DB schema strip */}
        <div className="features__schema">
          <h3 className="features__schema-title">Database Schema</h3>
          <div className="features__schema-tables">
            <div className="schema-table">
              <div className="schema-table__header">
                <span>🗃️</span> <code>users</code>
              </div>
              <ul className="schema-table__cols">
                {['id INT PK AUTO_INCREMENT', 'username VARCHAR(20) UNIQUE', 'password VARCHAR(255)', 'role VARCHAR(20)'].map(c => (
                  <li key={c}><code>{c}</code></li>
                ))}
              </ul>
            </div>
            <div className="schema-table">
              <div className="schema-table__header">
                <span>🗃️</span> <code>places</code>
              </div>
              <ul className="schema-table__cols">
                {['id INT PK', 'name VARCHAR(100)', 'category VARCHAR(50)', 'location VARCHAR(100)', 'description TEXT', 'latitude DOUBLE', 'longitude DOUBLE'].map(c => (
                  <li key={c}><code>{c}</code></li>
                ))}
              </ul>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}
