import Nav from './components/Nav'
import Hero from './components/Hero'
import About from './components/About'
import Architecture from './components/Architecture'
import Features from './components/Features'
import Contribute from './components/Contribute'
import Roadmap from './components/Roadmap'
import AIMaintainer from './components/AIMaintainer'
import Contributors from './components/Contributors'
import Footer from './components/Footer'
import { useGitHub } from './hooks/useGitHub'

export default function App() {
  const { stats, contributors, issues, loading } = useGitHub()

  return (
    <div className="app">
      <Nav />
      <Hero stats={stats} loading={loading} />
      <About />
      <Architecture />
      <Features />
      <Contribute issues={issues} loading={loading} />
      <Roadmap />
      <AIMaintainer />
      <Contributors contributors={contributors} loading={loading} />
      <Footer />
    </div>
  )
}
