#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd -- "$SCRIPT_DIR/../../.." && pwd)"
export OLLAMA_MODEL="${OLLAMA_MODEL:-qwen3:1.7b}"
COMPOSE=(docker compose --project-name lh-agent-quickstart --file "$SCRIPT_DIR/compose.yaml")

if [[ "${1:-}" == "--clean" && $# == 1 ]]; then
    "${COMPOSE[@]}" down --volumes --remove-orphans
    exit 0
fi
if [[ $# != 0 ]]; then
    printf 'Usage: %s [--clean]\n' "$0" >&2
    exit 1
fi

for command in docker java lhctl; do
    if ! command -v "$command" >/dev/null 2>&1; then
        printf 'Required command not found: %s\n' "$command" >&2
        exit 1
    fi
done
docker compose version >/dev/null
docker info >/dev/null

# Keep host-side registration and CLI checks on the local, plaintext listener.
export LHC_API_HOST=localhost LHC_API_PORT=2023 LHC_API_PROTOCOL=PLAINTEXT

"${COMPOSE[@]}" up --detach --wait --wait-timeout 300 littlehorse ollama
"${COMPOSE[@]}" exec -T ollama ollama pull "$OLLAMA_MODEL"
"${COMPOSE[@]}" up --detach agent

# The connector owns the TaskDef; wait for it before registering the WfSpec.
task_registered=false
for ((attempt = 0; attempt < 60; attempt++)); do
    if lhctl --configFile "$SCRIPT_DIR/littlehorse.config" get taskDef text-to-text-agent >/dev/null 2>&1; then
        task_registered=true
        break
    fi
    sleep 5
done
if [[ "$task_registered" != true ]]; then
    "${COMPOSE[@]}" logs agent >&2
    printf 'The connector did not register text-to-text-agent within five minutes.\n' >&2
    exit 1
fi

"$REPO_ROOT/gradlew" -p "$SCRIPT_DIR" run
printf '\nSetup complete. Dashboard: http://localhost:8080\n'
printf 'Use the explicit lhctl --configFile commands documented in README.md.\n'
