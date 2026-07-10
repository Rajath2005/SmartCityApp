#!/usr/bin/env bash
# Start MySQL (if not already running) and then launch the Smart City Guide CLI app.
# All binaries are resolved from PATH so Nix store path changes don't break startup.

set -euo pipefail

MYSQL_DATA="$HOME/.mysql/smart_city_data"
MYSQL_SOCKET="$HOME/.mysql/mysql.sock"
MYSQL_PID="$HOME/.mysql/mysql.pid"
MYSQL_LOG="$HOME/.mysql/mysql.log"

export DB_HOST="${DB_HOST:-127.0.0.1}"
export DB_PORT="${DB_PORT:-3306}"
export DB_NAME="${DB_NAME:-smart_city_guide}"
# Only root bootstrap is supported; DB_USER is passed to the app but MySQL is
# initialised as root. Set DB_USER=root (the default) for full access.
export DB_USER="${DB_USER:-root}"
export DB_PASSWORD="${DB_PASSWORD:-}"

# Resolve binaries from PATH (populated by Nix modules)
MYSQLD=$(which mysqld 2>/dev/null) || { echo "❌ mysqld not found in PATH"; exit 1; }
MYSQLADMIN=$(which mysqladmin 2>/dev/null) || { echo "❌ mysqladmin not found in PATH"; exit 1; }
MYSQL_CMD=$(which mysql 2>/dev/null) || { echo "❌ mysql client not found in PATH"; exit 1; }
MYSQL_BASE=$(dirname "$(dirname "$MYSQLD")")
# Prefer JDK 21 over whatever PATH default is (GraalVM 19 is the default module)
JAVA_CMD=$(ls /nix/store/*openjdk-21*/bin/java 2>/dev/null | grep -v debug | sort -V | tail -1)
if [ -z "$JAVA_CMD" ]; then
  JAVA_CMD=$(which java 2>/dev/null) || { echo "❌ java not found"; exit 1; }
fi

mkdir -p "$HOME/.mysql"

# ---------------------------------------------------------------------------
# Initialise MySQL data directory (first run only)
# ---------------------------------------------------------------------------
if [ ! -d "$MYSQL_DATA" ]; then
  echo "🔧 Initialising MySQL data directory..."
  "$MYSQLD" --initialize-insecure \
    --basedir="$MYSQL_BASE" \
    --datadir="$MYSQL_DATA" \
    --user="$(whoami)" 2>&1 | grep -v "^\[Note\]" || true
  echo "✅ MySQL data directory initialised."
fi

# ---------------------------------------------------------------------------
# Start MySQL server (skip if already running)
# ---------------------------------------------------------------------------
if ! "$MYSQLADMIN" --socket="$MYSQL_SOCKET" ping --silent 2>/dev/null; then
  echo "🚀 Starting MySQL server..."
  "$MYSQLD" \
    --basedir="$MYSQL_BASE" \
    --datadir="$MYSQL_DATA" \
    --socket="$MYSQL_SOCKET" \
    --pid-file="$MYSQL_PID" \
    --port="$DB_PORT" \
    --log-error="$MYSQL_LOG" \
    --bind-address=127.0.0.1 \
    --user="$(whoami)" \
    --daemonize=ON

  echo "⏳ Waiting for MySQL to be ready..."
  for i in $(seq 1 30); do
    if "$MYSQLADMIN" --socket="$MYSQL_SOCKET" ping --silent 2>/dev/null; then
      echo "✅ MySQL is ready."
      break
    fi
    if [ "$i" -eq 30 ]; then
      echo "❌ MySQL did not become ready in time. Check $MYSQL_LOG for details."
      exit 1
    fi
    sleep 1
  done
fi

# ---------------------------------------------------------------------------
# Bootstrap database schema (idempotent via db_setup.sql)
# Sets root password on very first run; subsequent runs use the stored password.
# ---------------------------------------------------------------------------
run_sql() {
  "$MYSQL_CMD" --socket="$MYSQL_SOCKET" -u root --password="$DB_PASSWORD" \
    --connect-expired-password "$@" 2>/dev/null
}

# Try passwordless first (initial insecure install), then fall back to $DB_PASSWORD
if "$MYSQL_CMD" --socket="$MYSQL_SOCKET" -u root --password="" \
    --connect-expired-password -e "SELECT 1;" >/dev/null 2>&1; then
  echo "🔑 Setting root password and loading schema..."
  "$MYSQL_CMD" --socket="$MYSQL_SOCKET" -u root --password="" \
    --connect-expired-password \
    -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';" 2>/dev/null || true
  "$MYSQL_CMD" --socket="$MYSQL_SOCKET" -u root --password="$DB_PASSWORD" \
    --connect-expired-password < db_setup.sql 2>/dev/null || true
  echo "✅ Schema loaded."
elif run_sql -e "SELECT 1;" >/dev/null 2>&1; then
  # Already bootstrapped; ensure schema exists (safe because db_setup.sql uses IF NOT EXISTS)
  run_sql < db_setup.sql 2>/dev/null || true
else
  echo "❌ Cannot connect to MySQL. Check DB_PASSWORD or $MYSQL_LOG."
  exit 1
fi

echo ""
echo "🏙️  Launching Smart City Guide..."
echo ""

exec "$JAVA_CMD" -jar target/app.jar
