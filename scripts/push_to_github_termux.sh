#!/data/data/com.termux/files/usr/bin/bash
set -euo pipefail
REPO="omissi/voicepro-recorder-final"
cd "$(dirname "$0")/.."
if [ ! -d .git ]; then
  git init
  git branch -M main
  gh repo create "$REPO" --private --source=. --remote=origin --push || true
fi
git status
git add .
git commit -m "Complete VoicePro Recorder final app" || true
git push -u origin main
