import { useState } from 'react'
import './Architecture.css'

const JAVA_NODES = [
  { id: 'user', label: 'User / Admin', icon: '👤', x: 50, y: 8, type: 'input' },
  { id: 'cli', label: 'SmartCityApp.java', sub: 'CLI · Auth · Menus', icon: '🖥️', x: 50, y: 30, type: 'core' },
  { id: 'place', label: 'Place.java', sub: 'Model', icon: '📍', x: 20, y: 55, type: 'model' },
  { id: 'usermodel', label: 'User.java', sub: 'Model', icon: '👤', x: 50, y: 55, type: 'model' },
  { id: 'db', label: 'DBConnection.java', sub: 'JDBC · Env vars', icon: '🔌', x: 78, y: 55, type: 'infra' },
  { id: 'mysql', label: 'MySQL 8.4', sub: 'smart_city_guide', icon: '🐬', x: 78, y: 80, type: 'storage' },
  { id: 'users', label: 'users table', icon: '🗃️', x: 55, y: 94, type: 'table' },
  { id: 'places', label: 'places table', icon: '🗃️', x: 78, y: 94, type: 'table' },
]

const JAVA_EDGES = [
  ['user', 'cli'],
  ['cli', 'place'],
  ['cli', 'usermodel'],
  ['cli', 'db'],
  ['db', 'mysql'],
  ['mysql', 'users'],
  ['mysql', 'places'],
]

const AI_LAYERS = [
  {
    label: 'GitHub Actions (Events)',
    icon: '⚙️',
    color: '#2088FF',
    items: ['issue_opened', 'issue_comment', 'pull_request_opened', 'pull_request_synchronize'],
  },
  {
    label: 'Event Layer',
    icon: '📡',
    color: '#00D4FF',
    items: ['event_parser.py → typed GitHubEvent models'],
  },
  {
    label: 'Engine Layer',
    icon: '⚙️',
    color: '#FFB547',
    items: ['rule_engine.py (deterministic)', 'assignment_engine.py', 'review_engine.py'],
  },
  {
    label: 'AI Layer',
    icon: '🧠',
    color: '#A78BFA',
    items: ['prompt_manager.py', 'hf_client.py → HuggingFace API', 'DeepSeek V3 / Qwen / Gemma / Llama'],
  },
  {
    label: 'GitHub Layer',
    icon: '🐙',
    color: '#00F5A0',
    items: ['github_client.py → REST API', 'post_comment · add_label · assign · approve_pr'],
  },
  {
    label: 'Foundation',
    icon: '🏗️',
    color: '#94A3B8',
    items: ['models.py · config.py · utils.py · repository_analyzer.py'],
  },
]

const TYPE_COLORS = {
  input: '#00D4FF',
  core: '#FFB547',
  model: '#A78BFA',
  infra: '#00F5A0',
  storage: '#4479A1',
  table: '#334155',
}

export default function Architecture() {
  const [tab, setTab] = useState('java')

  return (
    <section id="architecture" className="architecture">
      <div className="container">
        <span className="section-label">Architecture</span>
        <h2 className="section-title">How it's built</h2>
        <p className="section-sub">
          Two distinct systems with different design philosophies — one intentionally simple
          for learning, one clean-architected for production-grade automation.
        </p>

        <div className="arch-tabs">
          <button
            className={`arch-tab ${tab === 'java' ? 'arch-tab--active' : ''}`}
            onClick={() => setTab('java')}
          >
            ☕ Java CLI App
          </button>
          <button
            className={`arch-tab ${tab === 'ai' ? 'arch-tab--active' : ''}`}
            onClick={() => setTab('ai')}
          >
            🤖 AI Maintainer
          </button>
        </div>

        {tab === 'java' ? (
          <div className="arch-java card anim-fade-in">
            <div className="arch-java__desc">
              <h3>Monolithic CLI Architecture</h3>
              <p>
                The app is <strong>intentionally a monolith</strong>. All logic lives in
                <code>SmartCityApp.java</code> — a deliberate design choice so beginners
                can read the entire application from top to bottom without jumping between
                files. The roadmap tracks a migration to DAO pattern.
              </p>
            </div>
            <div className="arch-java__diagram">
              {JAVA_LAYERS_DIAGRAM()}
            </div>
            <div className="arch-java__files">
              {[
                { file: 'SmartCityApp.java', size: '857 lines', role: 'Entry point · menus · SQL queries · auth', icon: '🖥️' },
                { file: 'DBConnection.java', size: '66 lines', role: 'MySQL JDBC singleton · reads DB_* env vars', icon: '🔌' },
                { file: 'Place.java', size: 'POJO', role: 'City attraction data model', icon: '📍' },
                { file: 'User.java', size: 'POJO', role: 'User account & role model', icon: '👤' },
              ].map(f => (
                <div key={f.file} className="arch-file">
                  <span className="arch-file__icon">{f.icon}</span>
                  <div>
                    <code className="arch-file__name">{f.file}</code>
                    <span className="arch-file__size">{f.size}</span>
                    <p className="arch-file__role">{f.role}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ) : (
          <div className="arch-ai card anim-fade-in">
            <div className="arch-ai__desc">
              <h3>Clean Architecture · AI Maintainer</h3>
              <p>
                Dependencies point inward — outer layers never know about inner layers.
                The AI receives only prompt strings and returns text. It has{' '}
                <strong>zero knowledge of GitHub</strong>. Engines orchestrate between worlds.
              </p>
            </div>
            <div className="arch-ai__diagram">
              {AI_LAYERS.map((layer, i) => (
                <div key={layer.label} className="arch-ai__layer" style={{ '--layer-color': layer.color }}>
                  <div className="arch-ai__layer-header">
                    <span className="arch-ai__layer-icon">{layer.icon}</span>
                    <span className="arch-ai__layer-label">{layer.label}</span>
                    {i < AI_LAYERS.length - 1 && (
                      <span className="arch-ai__layer-arrow">↓</span>
                    )}
                  </div>
                  <div className="arch-ai__layer-items">
                    {layer.items.map(item => (
                      <span key={item} className="arch-ai__layer-item">{item}</span>
                    ))}
                  </div>
                </div>
              ))}
            </div>
            <div className="arch-ai__principle">
              <div className="arch-ai__principle-rule">
                <span>🔑</span>
                <div>
                  <strong>Key Principle: AI Never Talks to GitHub</strong>
                  <p>GitHub Event → Event Parser → Engine → AI Client → Engine → GitHub Client</p>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </section>
  )
}

function JAVA_LAYERS_DIAGRAM() {
  return (
    <div className="java-diagram">
      <div className="java-layer java-layer--input">
        <span>👤</span> User Input (CLI)
      </div>
      <div className="java-diagram__arrow">↓</div>
      <div className="java-layer java-layer--core">
        <span>🖥️</span> SmartCityApp.java
        <small>Menus · Auth · SQL · Business Logic</small>
      </div>
      <div className="java-diagram__split">
        <div className="java-diagram__branch">
          <div className="java-diagram__arrow">↓</div>
          <div className="java-layer java-layer--model">
            <span>📍</span> Place.java
          </div>
          <div className="java-diagram__arrow" style={{opacity:.3}}>|</div>
          <div className="java-layer java-layer--model">
            <span>👤</span> User.java
          </div>
        </div>
        <div className="java-diagram__branch">
          <div className="java-diagram__arrow">↓</div>
          <div className="java-layer java-layer--infra">
            <span>🔌</span> DBConnection.java
            <small>JDBC · ENV vars</small>
          </div>
          <div className="java-diagram__arrow">↓</div>
          <div className="java-layer java-layer--storage">
            <span>🐬</span> MySQL 8.4
            <small>users · places</small>
          </div>
        </div>
      </div>
    </div>
  )
}
