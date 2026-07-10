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
      const [repoResult, contributorsResult, issuesResult] = await Promise.allSettled([
        ghFetch(BASE),
        ghFetch(`${BASE}/contributors?per_page=20`),
        ghFetch(`${BASE}/issues?state=open&per_page=50&direction=asc`),
      ])

      if (cancelled) return

      // Repo stats — fall back independently
      if (repoResult.status === 'fulfilled') {
        const d = repoResult.value
        setStats({
          stars: d.stargazers_count,
          forks: d.forks_count,
          open_issues: d.open_issues_count,
          watchers: d.watchers_count,
        })
      } else {
        console.warn('GitHub repo fetch failed, using fallback stats:', repoResult.reason?.message)
        setStats(FALLBACK.stats)
        setError(repoResult.reason?.message)
      }

      // Contributors — fall back independently
      if (contributorsResult.status === 'fulfilled') {
        const humans = contributorsResult.value.filter(
          (c) => !c.login.includes('[bot]') && !c.login.includes('Copilot')
        )
        setContributors(humans)
      } else {
        console.warn('GitHub contributors fetch failed, using fallback:', contributorsResult.reason?.message)
        setContributors(FALLBACK.contributors)
      }

      // Issues — fall back independently
      if (issuesResult.status === 'fulfilled') {
        // Filter out PRs (they appear in issues endpoint)
        const actualIssues = issuesResult.value.filter((i) => !i.pull_request)
        setIssues(actualIssues)
      } else {
        console.warn('GitHub issues fetch failed, using fallback:', issuesResult.reason?.message)
        setIssues(FALLBACK.issues)
      }

      if (!cancelled) setLoading(false)
    }

    fetchAll()
    return () => { cancelled = true }
  }, [])

  return { stats, contributors, issues, loading, error }
}
