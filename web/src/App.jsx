import { useState, useEffect, useRef } from 'react'
import { useGitHub } from './hooks/useGitHub'
import OSShell from './components/OSShell'
import StatusScene from './components/scenes/StatusScene'
import ConstellationScene from './components/scenes/ConstellationScene'
import ArchitectureScene from './components/scenes/ArchitectureScene'
import IssuesScene from './components/scenes/IssuesScene'
import PipelineScene from './components/scenes/PipelineScene'
import ActivityScene from './components/scenes/ActivityScene'
import './styles/global.css'

export const SCENES = [
  { id: 'status',        label: 'Repository Status', glyph: '◉' },
  { id: 'contributors',  label: 'Contributors',       glyph: '✦' },
  { id: 'architecture',  label: 'Architecture',       glyph: '◈' },
  { id: 'issues',        label: 'Active Issues',      glyph: '◇' },
  { id: 'ai-pipeline',   label: 'AI Maintainer',      glyph: '◆' },
  { id: 'activity',      label: 'Activity Feed',      glyph: '◎' },
]

export default function App() {
  const [activeScene, setActiveScene] = useState(0)
  const { stats, contributors, issues, loading } = useGitHub()
  const containerRef = useRef(null)

  useEffect(() => {
    const el = containerRef.current
    if (!el) return
    const onScroll = () => {
      const idx = Math.round(el.scrollTop / el.clientHeight)
      setActiveScene(Math.max(0, Math.min(idx, SCENES.length - 1)))
    }
    el.addEventListener('scroll', onScroll, { passive: true })
    return () => el.removeEventListener('scroll', onScroll)
  }, [])

  const scrollTo = (i) => {
    const el = containerRef.current
    if (el) el.scrollTo({ top: i * el.clientHeight, behavior: 'smooth' })
  }

  return (
    <div className="os-root">
      <OSShell
        scenes={SCENES}
        activeScene={activeScene}
        onSelect={scrollTo}
        stats={stats}
        loading={loading}
      />
      <div className="os-scenes" ref={containerRef}>
        <div className="os-scene"><StatusScene stats={stats} contributors={contributors} loading={loading} /></div>
        <div className="os-scene"><ConstellationScene contributors={contributors} loading={loading} /></div>
        <div className="os-scene"><ArchitectureScene /></div>
        <div className="os-scene"><IssuesScene issues={issues} loading={loading} /></div>
        <div className="os-scene"><PipelineScene /></div>
        <div className="os-scene"><ActivityScene issues={issues} contributors={contributors} /></div>
      </div>
    </div>
  )
}
