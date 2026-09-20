#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

docker rm -f kafka-connect >/dev/null 2>&1 || true
docker rm -f lh-standalone >/dev/null 2>&1 || true
rm -rf -- "${SCRIPT_DIR:?}/connectors"
rm -f -- "${SCRIPT_DIR}/lh-kafka-connect-0.13.1.zip"
