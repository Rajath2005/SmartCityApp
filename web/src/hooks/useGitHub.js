import { useState, useEffect } from 'react'

const OWNER = 'Rajath2005'
const REPO = 'SmartCityApp'
const BASE = `https://api.github.com/repos/${OWNER}/${REPO}`

const FALLBACK = {
  stats: { stars: 8, forks: 20, open_issues: 36, watchers: 8 },
  contributors: [
    { login: 'Rajath2005', contributions: 34, avatar_url: `https://avatars.githubusercontent.com/u/168326104?v=4`, html_url: 'https://github.com/Rajath2005' },
    { login: 'alexanderliberacion-cmd', contributions: 26, avatar_url: `https://avatars.githubusercontent.com/u/50545626?v=4`, html_url: 'https://github.com/alexanderliberacion-cmd' },
    { login: 'cordeirops', contributions: 5, avatar_url: `https://avatars.githubusercontent.com/u/1?v=4`, html_url: 'https://github.com/cordeirops' },
    { login: 'shikha033', contributions: 4, avatar_url: `https://avatars.githubusercontent.com/u/2?v=4`, html_url: 'https://github.com/shikha033' },
    { login: 'sshrrutiiii', contributions: 4, avatar_url: `https://avatars.githubusercontent.com/u/3?v=4`, html_url: 'https://github.com/sshrrutiiii' },
    { login: 'davidhrabcak', contributions: 3, avatar_url: `https://avatars.githubusercontent.com/u/4?v=4`, html_url: 'https://github.com/davidhrabcak' },
    { login: 'polachandu', contributions: 3, avatar_url: `https://avatars.githubusercontent.com/u/5?v=4`, html_url: 'https://github.com/polachandu' },
    { login: 'rajibul004', contributions: 2, avatar_url: `https://avatars.githubusercontent.com/u/6?v=4`, html_url: 'https://github.com/rajibul004' },
  ],
  issues: [],
}

async function ghFetch(url) {
  const res = await fetch(url, {
    headers: { Accept: 'application/vnd.github+json' },
  })
  if (!res.ok) throw new Error(`GitHub API ${res.status}`)
  return res.json()
}

export function useGitHub() {
  const [stats, setStats] = useState(null)
  const [contributors, setContributors] = useState([])
  const [issues, setIssues] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    let cancelled = false

    async function fetchAll() {
      try {
        const [repoData, contributorsData, issuesData] = await Promise.all([
          ghFetch(BASE),
          ghFetch(`${BASE}/contributors?per_page=20`),
          ghFetch(`${BASE}/issues?state=open&per_page=50&direction=asc`),
        ])

        if (cancelled) return

        setStats({
          stars: repoData.stargazers_count,
          forks: repoData.forks_count,
          open_issues: repoData.open_issues_count,
          watchers: repoData.watchers_count,
        })

        // Filter out bots
        const humans = contributorsData.filter(
          (c) => !c.login.includes('[bot]') && !c.login.includes('Copilot')
        )
        setContributors(humans)

        // Filter out PRs (they appear in issues endpoint)
        const actualIssues = issuesData.filter((i) => !i.pull_request)
        setIssues(actualIssues)
      } catch (err) {
        if (cancelled) return
        console.warn('GitHub API unavailable, using fallback data:', err.message)
        setError(err.message)
        setStats(FALLBACK.stats)
        setContributors(FALLBACK.contributors)
        setIssues(FALLBACK.issues)
      } finally {
        if (!cancelled) setLoading(false)
      }
    }

    fetchAll()
    return () => { cancelled = true }
  }, [])

  return { stats, contributors, issues, loading, error }
}
