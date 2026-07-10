export const REPO = {
  owner: 'Rajath2005',
  name: 'SmartCityApp',
  url: 'https://github.com/Rajath2005/SmartCityApp',
  issues_url: 'https://github.com/Rajath2005/SmartCityApp/issues',
  contribute_url: 'https://github.com/Rajath2005/SmartCityApp/blob/main/CONTRIBUTING.md',
}

export const TECH_STACK = [
  { name: 'Java 21', icon: '☕', color: '#ED8B00', desc: 'Core application language' },
  { name: 'MySQL 8.4', icon: '🐬', color: '#4479A1', desc: 'Persistent data storage' },
  { name: 'Maven', icon: '📦', color: '#C71A36', desc: 'Build tool & dependency management' },
  { name: 'Python 3.12', icon: '🐍', color: '#3776AB', desc: 'AI Maintainer automation' },
  { name: 'GitHub Actions', icon: '⚙️', color: '#2088FF', desc: 'CI/CD automation pipeline' },
  { name: 'HuggingFace', icon: '🤗', color: '#FF9D00', desc: 'AI inference (DeepSeek V3)' },
  { name: 'Checkstyle', icon: '📏', color: '#4CAF50', desc: 'Automated code style enforcement' },
  { name: 'Docker', icon: '🐳', color: '#2496ED', desc: 'Containerised deployment' },
]

export const USER_FEATURES = [
  {
    icon: '🔐',
    title: 'Register & Login',
    desc: 'Secure account creation with SHA-256 password hashing and role-based access control for USER and ADMIN roles.',
  },
  {
    icon: '🏙️',
    title: 'Browse Attractions',
    desc: 'Explore the full catalogue of city places — restaurants, parks, hotels, museums — with name, category, location and GPS coordinates.',
  },
  {
    icon: '🔍',
    title: 'Search by Category',
    desc: 'Filter places by type using partial-match SQL queries. Find all restaurants or parks in seconds.',
  },
  {
    icon: '📍',
    title: 'Search by Location',
    desc: 'Narrow down results by area — Downtown, Main Street, or any location string in the database.',
  },
  {
    icon: '🗺️',
    title: 'Navigation',
    desc: 'Simulated directions and nearby-services lookup for any attraction in the city guide.',
  },
]

export const ADMIN_FEATURES = [
  {
    icon: '➕',
    title: 'Add Place',
    desc: 'Insert new city attractions with ID, name, category, location, description, and GPS coordinates.',
  },
  {
    icon: '✏️',
    title: 'Update Place',
    desc: 'Edit any field of an existing place entry. Press Enter to keep current values — only changes what you type.',
  },
  {
    icon: '🗑️',
    title: 'Delete Place',
    desc: 'Remove outdated or incorrect entries from the database by place ID.',
  },
  {
    icon: '📊',
    title: 'System Monitoring',
    desc: 'View system logs and track user activity and registrations across the platform.',
  },
]

export const ROADMAP = [
  {
    icon: '🏗️',
    title: 'DAO Pattern Refactor',
    desc: 'Migrate SQL logic out of SmartCityApp.java into dedicated Data Access Object classes for better separation of concerns.',
    status: 'in-progress',
    label: 'architecture',
    issueUrl: 'https://github.com/Rajath2005/SmartCityApp/labels/architecture',
  },
  {
    icon: '🔐',
    title: 'BCrypt Password Hashing',
    desc: 'Upgrade from SHA-256 to BCrypt — the industry standard for secure, salted password storage.',
    status: 'planned',
    label: 'security',
    issueUrl: 'https://github.com/Rajath2005/SmartCityApp/labels/best-practices',
  },
  {
    icon: '🌐',
    title: 'Spring Boot REST API',
    desc: 'Expose all features via a REST API, enabling web clients, mobile apps, and third-party integrations.',
    status: 'planned',
    label: 'feature',
    issueUrl: 'https://github.com/Rajath2005/SmartCityApp/labels/feature',
  },
  {
    icon: '🖥️',
    title: 'JavaFX / Swing GUI',
    desc: 'Build a graphical user interface as an alternative to the CLI experience.',
    status: 'planned',
    label: 'feature',
    issueUrl: 'https://github.com/Rajath2005/SmartCityApp/labels/feature',
  },
  {
    icon: '⭐',
    title: 'Ratings & Reviews',
    desc: 'Allow users to rate and write reviews for city attractions, stored and queryable from the database.',
    status: 'planned',
    label: 'feature',
    issueUrl: 'https://github.com/Rajath2005/SmartCityApp/labels/feature',
  },
  {
    icon: '🧪',
    title: 'JUnit Test Suite',
    desc: 'Comprehensive unit tests covering all core classes, JDBC operations, and authentication logic.',
    status: 'planned',
    label: 'testing',
    issueUrl: 'https://github.com/Rajath2005/SmartCityApp/labels/java',
  },
]

export const CONTRIBUTE_STEPS = [
  {
    step: '01',
    title: 'Find an Issue',
    desc: 'Browse the live issues below. Pick one that matches your skill level — beginner, intermediate, or advanced.',
    code: null,
  },
  {
    step: '02',
    title: 'Claim It',
    desc: 'Comment on the issue — the AI Maintainer detects your intent and auto-assigns it to you within seconds.',
    code: '"I would like to work on this"',
  },
  {
    step: '03',
    title: 'Fork, Clone & Run',
    desc: 'Fork the repo, clone it, and run the startup script. MySQL initialises automatically on first run.',
    code: 'bash start.sh',
  },
  {
    step: '04',
    title: 'PR & Get Reviewed',
    desc: 'Open a pull request. The AI Maintainer reviews it automatically — rule checks first, then AI-powered code review.',
    code: null,
  },
]

export const AI_EVENTS = [
  {
    icon: '📝',
    event: 'Issue Opened',
    action: 'Welcomes the reporter, adds appropriate labels based on content analysis',
    color: '#00D4FF',
  },
  {
    icon: '💬',
    event: 'Issue Comment',
    action: 'Detects assignment requests ("I\'d like to work on this") and auto-assigns the contributor',
    color: '#FFB547',
  },
  {
    icon: '🔀',
    event: 'PR Opened',
    action: 'Welcomes first-time contributors, runs rule engine checks, triggers AI code review',
    color: '#00F5A0',
  },
  {
    icon: '🔄',
    event: 'PR Synchronize',
    action: 'Re-runs rule checks and AI review on the updated diff',
    color: '#A78BFA',
  },
]

export const AI_MODELS = [
  { name: 'DeepSeek V3', id: 'deepseek-ai/DeepSeek-V3-0324', role: 'Default', badge: 'active' },
  { name: 'Qwen', id: 'Qwen/Qwen2.5-72B-Instruct', role: 'Alternative', badge: 'supported' },
  { name: 'Gemma', id: 'google/gemma-3-27b-it', role: 'Alternative', badge: 'supported' },
  { name: 'Llama', id: 'meta-llama/Llama-3.3-70B-Instruct', role: 'Alternative', badge: 'supported' },
]

export const DIFFICULTY_LABELS = [
  { value: 'all', label: 'All Issues', color: '#94A3B8' },
  { value: 'beginner-friendly', label: 'Beginner', color: '#00F5A0' },
  { value: 'good first issue', label: 'Good First Issue', color: '#00D4FF' },
  { value: 'intermediate', label: 'Intermediate', color: '#FFB547' },
  { value: 'advanced', label: 'Advanced', color: '#F87171' },
  { value: 'help wanted', label: 'Help Wanted', color: '#A78BFA' },
]

export const LABEL_COLORS = {
  'beginner-friendly': '#00F5A0',
  'good first issue': '#00D4FF',
  'good first contribution': '#00D4FF',
  'intermediate': '#FFB547',
  'advanced': '#F87171',
  'expert': '#EF4444',
  'help wanted': '#A78BFA',
  'bug': '#F87171',
  'enhancement': '#00D4FF',
  'feature': '#00D4FF',
  'documentation': '#94A3B8',
  'architecture': '#FFB547',
  'database': '#4479A1',
  'java': '#ED8B00',
  'OOP': '#10B981',
  'best-practices': '#10B981',
  'performance': '#F59E0B',
  'ci-cd': '#2088FF',
  'hacktoberfest': '#FF6B35',
}
