#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
VERSION='0.13.1'
ARTIFACT="lh-kafka-connect-${VERSION}"
ARCHIVE="${SCRIPT_DIR}/${ARTIFACT}.zip"
CONNECTORS_DIR="${SCRIPT_DIR}/connectors"
KAFKA_IMAGE='apache/kafka:4.0.0'

docker run --pull always --name lh-standalone --rm -d \
    -p 2023:2023 -p 8080:8080 -p 9092:9092 \
    ghcr.io/littlehorse-enterprises/littlehorse/lh-standalone:1.2.1

curl -fL \
    "https://github.com/littlehorse-enterprises/lh-kafka-connect/releases/download/v${VERSION}/${ARTIFACT}.zip" \
    -o "${ARCHIVE}"
mkdir -p "${CONNECTORS_DIR}/${ARTIFACT}"
unzip -q -o "${ARCHIVE}" -d "${CONNECTORS_DIR}/${ARTIFACT}"

for attempt in $(seq 1 60); do
    if docker run --rm --net=host "${KAFKA_IMAGE}" \
        /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list >/dev/null 2>&1; then
        break
    fi
    if [[ "${attempt}" == 60 ]]; then
        echo 'Kafka did not become ready within 120 seconds.' >&2
        exit 1
    fi
    sleep 2
done

docker run --rm --net=host "${KAFKA_IMAGE}" \
    /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 \
    --create --if-not-exists --topic new-customers

docker run --name kafka-connect -d --rm --net=host \
    -v "${SCRIPT_DIR}:/lh:ro" \
    "${KAFKA_IMAGE}" \
    /opt/kafka/bin/connect-standalone.sh \
    /lh/connect-standalone-config.properties /lh/wfrun-sink-connector-config.properties
