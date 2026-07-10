import { REPO } from '../data/staticData'
import './Footer.css'

const LINKS = [
  { label: 'GitHub', href: REPO.url },
  { label: 'Issues', href: REPO.issues_url },
  { label: 'Contributing', href: REPO.contribute_url },
  { label: 'MIT License', href: `${REPO.url}/blob/main/LICENSE` },
  { label: 'Code of Conduct', href: `${REPO.url}/blob/main/CODE_OF_CONDUCT.md` },
]

export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer__divider" />
      <div className="container footer__inner">
        <div className="footer__brand">
          <span className="footer__logo">🏙️ SmartCity<span>Hub</span></span>
          <p className="footer__tagline">
            The open-source city guide — built by the community, maintained by AI.
          </p>
          <p className="footer__meta">
            Released under the{' '}
            <a href={`${REPO.url}/blob/main/LICENSE`} target="_blank" rel="noopener noreferrer">
              MIT License
            </a>
            {' · '}
            <a href={REPO.url} target="_blank" rel="noopener noreferrer">
              github.com/Rajath2005/SmartCityApp
            </a>
          </p>
        </div>

        <nav className="footer__links">
          {LINKS.map((l) => (
            <a
              key={l.label}
              href={l.href}
              target="_blank"
              rel="noopener noreferrer"
              className="footer__link"
            >
              {l.label}
            </a>
          ))}
        </nav>
      </div>

      <div className="footer__bottom">
        <div className="container footer__bottom-inner">
          <span>Made with ❤️ by the SmartCityApp community</span>
          <span className="footer__stack">
            Built with React · Powered by the GitHub API
          </span>
        </div>
      </div>
    </footer>
  )
}
