# Agent Connector Quickstart

Run the [LittleHorse Agent Connector](https://github.com/littlehorse-enterprises/lh-agent-connector) as a prebuilt task worker. This ports its text-to-text example, including a restricted Yahoo Finance MCP tool, without building or running the connector from source.

```text
input (STR) -> text-to-text-agent -> output (STR)
```

The [website walkthrough](https://littlehorse.io/docs/getting-started/agent-connector) covers the same quickstart.

## Prerequisites

- Docker with Docker Compose v2 and a running Docker daemon.
- Java 21 and `lhctl` 1.3.0 or newer. See the [system setup guide](https://littlehorse.io/docs/getting-started/quickstart#system-setup) for installation instructions.
- Internet access for images, Gradle dependencies, the model download, and the third-party MCP endpoint.
- Enough Docker memory and disk space for LittleHorse, the Java connector, and `qwen3:1.7b`. Its model download is approximately 1.4 GB; inference requires additional memory. CPU-only inference can be slow.
- Host ports `2023` and `8080` must be free. Stop any earlier standalone LittleHorse container first.

No host Ollama installation or paid model API key is needed. Ollama runs inside Compose on CPU by default, with no host port exposed. These instructions target a local plaintext server; use a shell without cloud-specific `LHC_*` authentication settings.

## Setup

From the Developer Hub repository root:

```bash
./examples/agent-connector/00-quickstart/setup.sh
```

The script starts LittleHorse Server, Dashboard, and Kafka using `ghcr.io/littlehorse-enterprises/littlehorse/lh-standalone:1.3.0`, starts Ollama, and pulls `qwen3:1.7b`. It then starts `ghcr.io/littlehorse-enterprises/lh-agent-connector:1.3.0`, waits for the connector's `text-to-text-agent` TaskDef, and runs the Java SDK 1.3.0 registration program to install `text-to-text-example`. All services remain running in the background. Nothing compiles the connector, and the script does not start a WfRun.

The setup can be rerun. Ollama models are cached in a Compose volume. The Ollama image uses `latest`; the LittleHorse and connector images and Java SDK are pinned to `1.3.0`.

### Agent Configuration

[`compose.yaml`](./compose.yaml) preserves the source example's TEXT input/output, finance system message, four-minute model-call timeout, and `max-retries=1` (one model-call attempt). The workflow allows five minutes for its agent task. This is a total task budget, not a guarantee: multiple model/tool calls or slow CPU inference can exceed it.

The connector uses Ollama's OpenAI-compatible API at `http://ollama:11434/v1`, with the dummy key `ollama`. It reaches LittleHorse at `littlehorse:2024`, whose INTERNAL listener advertises an address reachable from containers. Host-side registration and `lhctl` use `localhost:2023`.

The MCP endpoint is `https://gateway.mcpservers.org/yahoo-finance/mcp`. Only the server-side `get_quote` tool is included, appearing to the model as `yahoo_get_quote`. Filtering happens before the client prefix is added. The endpoint is a third-party service, not an officially supported Yahoo Finance MCP server; its availability and data are outside LittleHorse's control. Its catalog loads lazily, so successful setup does not prove that a subsequent tool call will succeed.

The default `qwen3:1.7b` reduces the model download size, but its tool selection and instruction following have not been verified for this scenario. To try the larger `qwen3:4b` model (approximately 2.5 GB), select it for both provisioning and connector configuration with the same variable:

```bash
OLLAMA_MODEL=qwen3:4b ./examples/agent-connector/00-quickstart/setup.sh
```

## Run The Workflow

Use the supplied local CLI configuration explicitly. This avoids any existing cloud configuration and does not overwrite `~/.config/littlehorse.config`. All commands below run from the repository root:

```bash
lhctl --configFile examples/agent-connector/00-quickstart/littlehorse.config \
  run text-to-text-example input \
  "What is Apple's latest available stock price and how does it compare with its previous close?"
```

The agent can call `yahoo_get_quote`, then store a natural-language comparison in `output`. Use the WfRun ID printed by `lhctl run`:

```bash
lhctl --configFile examples/agent-connector/00-quickstart/littlehorse.config \
  get wfRun <wf-run-id>
lhctl --configFile examples/agent-connector/00-quickstart/littlehorse.config \
  get variable <wf-run-id> 0 output
```

You can also inspect the run at [http://localhost:8080](http://localhost:8080). The response is model-generated, not a fixed expected string.

### A Deliberately Excluded Tool

The source example also demonstrates a request requiring a tool outside the allowlist:

```bash
lhctl --configFile examples/agent-connector/00-quickstart/littlehorse.config \
  run text-to-text-example input \
  "Retrieve Apple's company profile and latest full-time employee count from Yahoo Finance."
```

The agent cannot call `quote_summary`. Its system message asks it to explain the unavailable capability rather than invent data. A completed response is expected to describe the limitation, but model compliance is not guaranteed.

## Inspect The Code

- [`AgentWorkflow.java`](./src/main/java/io/littlehorse/examples/AgentWorkflow.java) only builds and registers the workflow graph; it does not run inference or start a worker.
- [`setup.sh`](./setup.sh) provisions services and registers the workflow after the connector registers its TaskDef.
- [`compose.yaml`](./compose.yaml) configures the prebuilt connector as the sole agent worker, Ollama, and LittleHorse.

To register the workflow again without provisioning services:

```bash
export LHC_API_HOST=localhost LHC_API_PORT=2023 LHC_API_PROTOCOL=PLAINTEXT
./gradlew -p examples/agent-connector/00-quickstart run
```

## Troubleshooting

Inspect service logs from the repository root:

```bash
docker compose -p lh-agent-quickstart \
  -f examples/agent-connector/00-quickstart/compose.yaml logs agent
```

If setup cannot find the TaskDef, check connector startup logs. If a run fails, inspect its task error in the Dashboard: check the MCP endpoint's availability and Docker resource limits. A five-minute timeout can indicate slow model inference or multiple tool/model calls, not necessarily a networking failure. Configure a larger task timeout in `AgentWorkflow.java` if needed, then rerun registration.

## Cleanup

```bash
./examples/agent-connector/00-quickstart/setup.sh --clean
```

This stops and removes only this quickstart's Compose services and volumes, deleting local workflow definitions, run history, and downloaded Ollama models. It leaves other Docker projects and any host Ollama service untouched.
