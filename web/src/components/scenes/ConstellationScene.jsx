import { useEffect, useRef, useState } from 'react'
import { motion } from 'framer-motion'
import './ConstellationScene.css'

function seededRand(seed) {
  const x = Math.sin(seed + 1) * 10000
  return x - Math.floor(x)
}

export default function ConstellationScene({ contributors, loading }) {
  const canvasRef = useRef(null)
  const animRef = useRef(null)
  const starsRef = useRef([])
  const mouseRef = useRef({ x: -999, y: -999 })
  const [hovered, setHovered] = useState(null)

  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas || !contributors.length) return
    const ctx = canvas.getContext('2d')
    let w = 0, h = 0

    const resize = () => {
      w = canvas.offsetWidth
      h = canvas.offsetHeight
      canvas.width = w
      canvas.height = h
      buildStars()
    }

    const buildStars = () => {
      starsRef.current = contributors.map((c, i) => {
        const angle = i * 2.399963 // golden angle radians
        const radius = Math.min(w, h) * (0.1 + (i / contributors.length) * 0.38)
        return {
          baseX: w * 0.5 + Math.cos(angle) * radius,
          baseY: h * 0.5 + Math.sin(angle) * radius,
          x: 0, y: 0,
          size: Math.min(Math.max(Math.sqrt(c.contributions) * 0.75, 1.5), 5.5),
          opacity: 0.35 + Math.min(c.contributions / 50, 0.65),
          phase: seededRand(i * 3) * Math.PI * 2,
          driftR: seededRand(i * 7) * 4 + 1,
          contributor: c,
        }
      })
    }

    let hoveredStar = null

    const loop = (ts) => {
      const t = ts * 0.001
      ctx.clearRect(0, 0, w, h)

      // update positions
      starsRef.current.forEach((s) => {
        s.x = s.baseX + Math.sin(t * 0.25 + s.phase) * s.driftR
        s.y = s.baseY + Math.cos(t * 0.18 + s.phase) * s.driftR
      })

      // constellation lines
      starsRef.current.forEach((a, i) => {
        starsRef.current.slice(i + 1).forEach((b) => {
          const d = Math.hypot(a.x - b.x, a.y - b.y)
          if (d < 130) {
            const alpha = (1 - d / 130) * 0.13
            ctx.strokeStyle = `rgba(139,92,246,${alpha})`
            ctx.lineWidth = 0.6
            ctx.beginPath()
            ctx.moveTo(a.x, a.y)
            ctx.lineTo(b.x, b.y)
            ctx.stroke()
          }
        })
      })

      // find hovered
      const mx = mouseRef.current.x, my = mouseRef.current.y
      hoveredStar = null
      let minD = 28
      starsRef.current.forEach((s) => {
        const d = Math.hypot(s.x - mx, s.y - my)
        if (d < minD) { minD = d; hoveredStar = s }
      })

      // draw stars
      starsRef.current.forEach((s) => {
        const isH = s === hoveredStar
        const twinkle = Math.sin(t * 1.4 + s.phase) * 0.12 + 1
        const sz = s.size * twinkle * (isH ? 2.2 : 1)
        const op = s.opacity * (isH ? 1.4 : 1)

        // outer glow
        const grd = ctx.createRadialGradient(s.x, s.y, 0, s.x, s.y, sz * (isH ? 6 : 3.5))
        grd.addColorStop(0, `rgba(${isH ? '200,160,255' : '139,92,246'},${op * 0.35})`)
        grd.addColorStop(1, 'rgba(0,0,0,0)')
        ctx.fillStyle = grd
        ctx.beginPath()
        ctx.arc(s.x, s.y, sz * (isH ? 6 : 3.5), 0, Math.PI * 2)
        ctx.fill()

        // core
        ctx.fillStyle = isH
          ? `rgba(220,190,255,${Math.min(op * 1.5, 1)})`
          : `rgba(170,130,255,${op})`
        ctx.beginPath()
        ctx.arc(s.x, s.y, sz, 0, Math.PI * 2)
        ctx.fill()
      })

      // tooltip
      if (hoveredStar) {
        const c = hoveredStar.contributor
        ctx.save()
        const pad = 10
        const name = `@${c.login}`
        const sub = `${c.contributions} commits`
        ctx.font = `600 12px Inter, sans-serif`
        const nw = ctx.measureText(name).width
        ctx.font = `500 11px JetBrains Mono, monospace`
        const sw = ctx.measureText(sub).width
        const bw = Math.max(nw, sw) + pad * 2
        const bh = 44
        let tx = hoveredStar.x + 14
        let ty = hoveredStar.y - bh / 2
        if (tx + bw > w - 8) tx = hoveredStar.x - bw - 14
        if (ty < 4) ty = 4
        if (ty + bh > h - 4) ty = h - bh - 4

        ctx.fillStyle = 'rgba(5,13,24,0.95)'
        ctx.strokeStyle = 'rgba(139,92,246,0.55)'
        ctx.lineWidth = 1
        ctx.beginPath()
        ctx.roundRect(tx, ty, bw, bh, 6)
        ctx.fill()
        ctx.stroke()

        ctx.fillStyle = '#e2e8f0'
        ctx.font = `600 12px Inter, sans-serif`
        ctx.fillText(name, tx + pad, ty + 17)
        ctx.fillStyle = 'rgba(139,92,246,0.9)'
        ctx.font = `500 11px JetBrains Mono, monospace`
        ctx.fillText(sub, tx + pad, ty + 33)
        ctx.restore()
      }

      animRef.current = requestAnimationFrame(loop)
    }

    const onMove = (e) => {
      const r = canvas.getBoundingClientRect()
      mouseRef.current = { x: e.clientX - r.left, y: e.clientY - r.top }
    }
    const onClick = () => {
      if (hoveredStar?.contributor?.html_url) {
        window.open(hoveredStar.contributor.html_url, '_blank', 'noopener')
      }
    }

    window.addEventListener('resize', resize)
    canvas.addEventListener('mousemove', onMove)
    canvas.addEventListener('click', onClick)
    resize()
    animRef.current = requestAnimationFrame(loop)

    return () => {
      cancelAnimationFrame(animRef.current)
      window.removeEventListener('resize', resize)
      canvas.removeEventListener('mousemove', onMove)
      canvas.removeEventListener('click', onClick)
    }
  }, [contributors])

  return (
    <div className="constellation-scene">
      <canvas ref={canvasRef} className="constellation-canvas" />

      {/* Overlays */}
      <div className="constellation-overlay">
        <motion.div
          initial={{ opacity: 0, y: -12 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.7 }}
          className="constellation-header"
        >
          <span className="label-mono" style={{ color: 'var(--violet)' }}>
            ✦ &nbsp; CONTRIBUTORS &nbsp; ✦
          </span>
          <h2 className="constellation-title">
            {loading ? '…' : contributors.length} contributors<br />
            <span className="grad-violet">mapping the city</span>
          </h2>
        </motion.div>

        <motion.div
          className="constellation-hint"
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          viewport={{ once: true }}
          transition={{ delay: 0.8, duration: 0.6 }}
        >
          <span className="label-mono">hover a star to identify &nbsp;·&nbsp; click to visit profile</span>
        </motion.div>
      </div>
    </div>
  )
}
