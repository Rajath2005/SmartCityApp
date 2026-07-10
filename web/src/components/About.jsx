import { TECH_STACK } from '../data/staticData'
import './About.css'

function TechBadge({ item }) {
  return (
    <div className="tech-badge card glow-cyan" title={item.desc}>
      <span className="tech-badge__icon">{item.icon}</span>
      <div>
        <div className="tech-badge__name">{item.name}</div>
        <div className="tech-badge__desc">{item.desc}</div>
      </div>
    </div>
  )
}

export default function About() {
  return (
    <section id="about" className="about">
      <div className="container">
        <div className="about__header">
          <span className="section-label">About the Project</span>
          <h2 className="section-title">Two projects. One repository.</h2>
          <p className="section-sub">
            SmartCityApp is more than a CLI app. It's a full open-source
            ecosystem — a Java application built by the community, maintained
            by an AI, and designed to teach software engineering from the ground up.
          </p>
        </div>

        <div className="about__systems">
          {/* Java App */}
          <div className="about-system card glow-cyan">
            <div className="about-system__header">
              <span className="about-system__emoji">☕</span>
              <div>
                <h3 className="about-system__title">Java CLI Application</h3>
                <span className="badge badge-cyan">The Product</span>
              </div>
            </div>
            <p className="about-system__desc">
              An interactive console app for exploring and managing city attractions.
              Intentionally monolithic — one file, readable from top to bottom —
              so beginners can understand real Java and JDBC without abstraction layers.
            </p>
            <ul className="about-system__facts">
              <li><span>📁</span> 857-line entry point · 3 source files</li>
              <li><span>🗄️</span> MySQL 8.4 via raw JDBC — no ORM</li>
              <li><span>🔐</span> SHA-256 password hashing</li>
              <li><span>👥</span> Role-based access · USER and ADMIN</li>
            </ul>
          </div>

          {/* AI Maintainer */}
          <div className="about-system card glow-amber">
            <div className="about-system__header">
              <span className="about-system__emoji">🤖</span>
              <div>
                <h3 className="about-system__title">AI Maintainer</h3>
                <span className="badge badge-amber">The Automation</span>
              </div>
            </div>
            <p className="about-system__desc">
              A sophisticated Python system living in <code>.github/ai/</code> that
              automates repository maintenance. Clean Architecture, Hugging Face
              inference, and GitHub Actions — it reviews PRs, assigns issues, and
              welcomes contributors automatically.
            </p>
            <ul className="about-system__facts">
              <li><span>🏗️</span> Clean Architecture · 8 modules</li>
              <li><span>🧠</span> DeepSeek V3 · Qwen · Gemma · Llama</li>
              <li><span>⚡</span> Rule engine first — AI only when needed</li>
              <li><span>🔄</span> GitHub Actions · 3 workflows</li>
            </ul>
          </div>
        </div>

        {/* Tech stack grid */}
        <div className="about__tech">
          <h3 className="about__tech-title">Technology Stack</h3>
          <div className="about__tech-grid">
            {TECH_STACK.map((item) => (
              <TechBadge key={item.name} item={item} />
            ))}
          </div>
        </div>
      </div>
    </section>
  )
}
