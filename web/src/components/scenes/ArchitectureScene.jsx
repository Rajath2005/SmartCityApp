import { useState, useRef, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import './ArchitectureScene.css'

const JAVA_NODES = [
  { id: 'user',    x: 400, y: 48,  label: 'User / Admin', sub: 'CLI input',         icon: '👤', color: '#00d4ff', type: 'input' },
  { id: 'app',     x: 400, y: 170, label: 'SmartCityApp.java', sub: '857 lines · menus · auth · SQL', icon: '🖥️', color: '#f59e0b', type: 'core' },
  { id: 'place',   x: 180, y: 310, label: 'Place.java',   sub: 'POJO model',         icon: '📍', color: '#8b5cf6', type: 'model' },
  { id: 'usermdl', x: 400, y: 310, label: 'User.java',    sub: 'POJO model',         icon: '👤', color: '#8b5cf6', type: 'model' },
  { id: 'db',      x: 620, y: 310, label: 'DBConnection.java', sub: 'JDBC · env vars', icon: '🔌', color: '#10b981', type: 'infra' },
  { id: 'mysql',   x: 620, y: 430, label: 'MySQL 8.4',    sub: 'smart_city_guide',   icon: '🐬', color: '#4479A1', type: 'storage' },
  { id: 'users',   x: 500, y: 530, label: 'users',        sub: 'table',              icon: '🗃️', color: '#334155', type: 'table' },
  { id: 'places',  x: 680, y: 530, label: 'places',       sub: 'table',              icon: '🗃️', color: '#334155', type: 'table' },
]
const JAVA_EDGES = [
  ['user','app'], ['app','place'], ['app','usermdl'], ['app','db'], ['db','mysql'], ['mysql','users'], ['mysql','places']
]

const AI_NODES = [
  { id: 'gh',     x: 400, y: 48,  label: 'GitHub Events',   sub: 'issue · pr · comment', icon: '⚙️', color: '#2088FF', type: 'input' },
  { id: 'parser', x: 400, y: 170, label: 'event_parser.py', sub: 'typed GitHubEvent models', icon: '📡', color: '#00d4ff', type: 'core' },
  { id: 'rules',  x: 200, y: 310, label: 'rule_engine.py',  sub: 'deterministic checks',icon: '⚙️', color: '#f59e0b', type: 'model' },
  { id: 'review', x: 400, y: 310, label: 'review_engine.py',sub: 'orchestrator',       icon: '🔍', color: '#f59e0b', type: 'model' },
  { id: 'ai',     x: 600, y: 310, label: 'hf_client.py',    sub: 'DeepSeek V3 · Qwen', icon: '🧠', color: '#8b5cf6', type: 'infra' },
  { id: 'ghcli',  x: 400, y: 450, label: 'github_client.py',sub: 'comment · label · assign', icon: '🐙', color: '#10b981', type: 'storage' },
]
const AI_EDGES = [
  ['gh','parser'], ['parser','rules'], ['parser','review'], ['review','ai'], ['rules','ghcli'], ['review','ghcli'], ['ai','ghcli']
]

const NODE_DETAILS = {
  app:     { title: 'SmartCityApp.java', desc: 'The monolithic entry point — 857 lines of intentionally readable Java. All menus, auth flows, and SQL queries live here so beginners can trace the full app from top to bottom without jumping files.', tags: ['Java 21', 'JDBC', 'Scanner', 'SHA-256'] },
  db:      { title: 'DBConnection.java', desc: 'A singleton JDBC connection manager. Reads DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD from environment variables — zero hardcoded credentials.', tags: ['Singleton', 'JDBC', 'ENV vars'] },
  place:   { title: 'Place.java',        desc: 'Simple POJO representing a city attraction: id, name, category, location, description, latitude, longitude.', tags: ['POJO', 'Model'] },
  usermdl: { title: 'User.java',         desc: 'POJO for user accounts with username, SHA-256 hashed password, and role (USER or ADMIN).', tags: ['POJO', 'RBAC'] },
  mysql:   { title: 'MySQL 8.4',         desc: 'Local MySQL server. The users and places tables are created by db_setup.sql on first run via start.sh.', tags: ['MySQL 8.4', 'SQL', 'InnoDB'] },
  parser:  { title: 'event_parser.py',   desc: 'Converts raw GitHub webhook payloads into typed GitHubEvent dataclasses. The only layer that knows GitHub JSON shape.', tags: ['Python', 'Dataclasses', 'Webhooks'] },
  rules:   { title: 'rule_engine.py',    desc: 'Deterministic rule checks: merge conflicts, linked issues, TODO/FIXME, hardcoded secrets. Runs before AI to keep inference costs low.', tags: ['Rules', 'Deterministic', 'Pre-AI'] },
  ai:      { title: 'hf_client.py',      desc: 'Abstract BaseAIClient + HuggingFaceClient. Sends plain prompt strings, returns plain text. Zero knowledge of GitHub — keeps layers decoupled.', tags: ['DeepSeek V3', 'HuggingFace', 'Abstract'] },
  ghcli:   { title: 'github_client.py',  desc: 'The only layer that calls GitHub REST API. Wraps post_comment, add_label, assign_issue, approve_pr. Engines never talk to GitHub directly.', tags: ['REST API', 'GitHub', 'Output'] },
}

function GraphNode({ node, active, onClick, tab }) {
  const isActive = active === node.id
  return (
    <motion.g
      className="arch-node-group"
      onClick={() => onClick(node.id)}
      style={{ cursor: 'pointer' }}
      whileHover={{ scale: 1.05 }}
      transition={{ type: 'spring', stiffness: 400, damping: 20 }}
    >
      {/* Glow behind active node */}
      {isActive && (
        <motion.circle
          cx={node.x} cy={node.y} r={34}
          fill={node.color}
          opacity={0.15}
          animate={{ r: [34, 42, 34] }}
          transition={{ duration: 2, repeat: Infinity, ease: 'easeInOut' }}
        />
      )}
      {/* Node circle */}
      <circle
        cx={node.x} cy={node.y} r={26}
        fill={`${node.color}18`}
        stroke={isActive ? node.color : `${node.color}55`}
        strokeWidth={isActive ? 2 : 1}
      />
      {/* Icon */}
      <foreignObject x={node.x - 12} y={node.y - 14} width={24} height={24}>
        <span style={{ fontSize: 18, lineHeight: 1 }}>{node.icon}</span>
      </foreignObject>
      {/* Label below */}
      <text
        x={node.x} y={node.y + 38}
        textAnchor="middle"
        fill={isActive ? '#e2e8f0' : '#94a3b8'}
        fontSize={11}
        fontFamily="Inter, sans-serif"
        fontWeight={isActive ? 700 : 500}
      >
        {node.label}
      </text>
      <text
        x={node.x} y={node.y + 51}
        textAnchor="middle"
        fill="#3d5068"
        fontSize={9}
        fontFamily="JetBrains Mono, monospace"
      >
        {node.sub}
      </text>
    </motion.g>
  )
}

function AnimatedEdge({ from, to, nodes, idx }) {
  const a = nodes.find(n => n.id === from)
  const b = nodes.find(n => n.id === to)
  if (!a || !b) return null
  const mx = (a.x + b.x) / 2
  const my = (a.y + b.y) / 2 - 20
  const d = `M ${a.x} ${a.y} Q ${mx} ${my} ${b.x} ${b.y}`
  const len = 200

  return (
    <g>
      <path d={d} fill="none" stroke="rgba(255,255,255,0.06)" strokeWidth={1.5} />
      <motion.path
        d={d}
        fill="none"
        stroke={a.color}
        strokeWidth={1.5}
        strokeDasharray={`${len * 0.15} ${len * 0.85}`}
        animate={{ strokeDashoffset: [0, -len] }}
        transition={{ duration: 2.5 + idx * 0.3, repeat: Infinity, ease: 'linear', delay: idx * 0.4 }}
        opacity={0.6}
      />
    </g>
  )
}

export default function ArchitectureScene() {
  const [tab, setTab] = useState('java')
  const [active, setActive] = useState(null)

  const nodes = tab === 'java' ? JAVA_NODES : AI_NODES
  const edges = tab === 'java' ? JAVA_EDGES : AI_EDGES
  const detail = active && NODE_DETAILS[active]

  const handleNode = (id) => setActive(prev => prev === id ? null : id)

  return (
    <div className="arch-scene">
      <div className="arch-bg-grid" />

      {/* Header */}
      <div className="arch-header">
        <div>
          <span className="label-mono" style={{ color: 'var(--cyan)' }}>◈ &nbsp; ARCHITECTURE</span>
          <h2 className="arch-title">How it's built</h2>
        </div>
        <div className="arch-tabs">
          <button className={`arch-tab ${tab === 'java' ? 'arch-tab--on' : ''}`} onClick={() => { setTab('java'); setActive(null) }}>
            ☕ Java CLI App
          </button>
          <button className={`arch-tab ${tab === 'ai' ? 'arch-tab--on' : ''}`} onClick={() => { setTab('ai'); setActive(null) }}>
            🤖 AI Maintainer
          </button>
        </div>
      </div>

      <div className="arch-body">
        {/* Graph */}
        <div className="arch-graph-wrap">
          <svg viewBox="0 0 800 590" className="arch-svg" key={tab}>
            <defs>
              <filter id="glow">
                <feGaussianBlur in="SourceGraphic" stdDeviation="3" result="blur" />
                <feMerge><feMergeNode in="blur" /><feMergeNode in="SourceGraphic" /></feMerge>
              </filter>
            </defs>
            {edges.map(([a, b], i) => (
              <AnimatedEdge key={`${a}-${b}`} from={a} to={b} nodes={nodes} idx={i} />
            ))}
            {nodes.map(n => (
              <GraphNode key={n.id} node={n} active={active} onClick={handleNode} tab={tab} />
            ))}
          </svg>
          <p className="arch-graph-hint label-mono">click a node to inspect</p>
        </div>

        {/* Detail panel */}
        <AnimatePresence>
          {detail && (
            <motion.div
              className="arch-detail"
              key={active}
              initial={{ opacity: 0, x: 24 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: 24 }}
              transition={{ duration: 0.3, ease: [0.16, 1, 0.3, 1] }}
            >
              <button className="arch-detail__close" onClick={() => setActive(null)}>✕</button>
              <h3 className="arch-detail__title">{detail.title}</h3>
              <p className="arch-detail__desc">{detail.desc}</p>
              <div className="arch-detail__tags">
                {detail.tags.map(t => (
                  <span key={t} className="badge badge-cyan">{t}</span>
                ))}
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </div>
  )
}
