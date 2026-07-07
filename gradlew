#!/usr/bin/env sh
set -e
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi
echo "Gradle command was not found."
echo "Termux: pkg update && pkg install -y gradle openjdk-17 git gh"
echo "GitHub Actions: this project workflow installs Gradle automatically."
exit 127
