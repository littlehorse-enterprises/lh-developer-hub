# Saddle Quickstart

In this quickstart, we'll build the simplest possible workflow that touches multiple Saddle concepts:

* `SaddleJob`s, which deploy pre-built Task Workers
* The _Workflow Builder_, a low-code GUI to build `WfSpec`s
* A `Streamlet`, which is a strongly-typed Kafka topic to handle events
* A `WebhookSource`, which receives webhooks and posts them to a `Streamlet`
* A `WorkflowTrigger`, which starts `WfRun`s from `Streamlet `events

Our simple demo involves building a one-task workflow with the Workflow Builder that sends a slack message, and triggering it from a webhook.

## Step 1: Create the Slack Task Worker

We need to register a `TaskDef` and start a worker for a `TaskDef` called **`send-slack`**. We can either:

1. Use the pre-built SaddleBag which deploys a no-code Slack task worker, OR
2. Run a mock task worker locally, **useful if you don't have access to the Slack API**.

### Option 1: SaddleBag

A Saddle Bag is a prebuilt task worker, and a Saddle Job deploys it. Use this option if you have a Slack _Bot Token_ that you can use to send messages to a channel.

#### Create Slack Token

To get a Slack bot token, open [Slack API Apps](https://api.slack.com/apps) and click **Create New App**. Choose **From scratch**, name the app, and select your Slack workspace. Open **OAuth & Permissions**, add `chat:write` under **Scopes > Bot Token Scopes**, then click **Install to Workspace** and approve the requested permission. Copy the **Bot User OAuth Token**, which starts with `xoxb-`.

Invite the app to the channel where you want to post messages by running `/invite @your-app` in Slack. Otherwise, Slack may reject the request with `channel_not_found`.

#### Create Saddle Secret

Go to **Configure** -> **Secrets**, and add a secret called `slack-token`, and paste in your Slack token as the value.

#### Deploy SaddleBag

In Saddle, expand **Deploy** in the left sidebar and select **Saddle Jobs**. Click **Add Saddle Job** in the top-right corner.

Enter a name and an optional description, then select `saddle-bag-slack` as the bag. Under **Configuration**, change the Slack token value from **Not set** to **Secret** and select the secret from the previous step.

![Saddle Job configured with the Slack Saddle Bag](saddle-job-configuration.png)

Click **Create Job** to start the worker and register the `send-slack` TaskDef.

### Option 2: Run the local mock worker

In this option, for users who don't have slack permissions, we write a mock Task Worker that runs locally and just prints out the message instead of using the Slack API.

#### Get Kernel Credentials

We first need to create a `Kernel Client` in order to access the LittleHorse Server.

In the left sidebar on Saddle, expand **Configure** and select **Clients**. On the Clients page, click **Add Client** in the top-right corner.

![Saddle Add Client configuration form](client-configuration.png)

Enter a name and an optional description, select **Kernel Client**, and click **Create Client**. From the repository root, copy the environment template:

```bash
cp examples/saddle/00-quickstart/.env.example examples/saddle/00-quickstart/.env
```

Save the generated LittleHorse configuration in `.env`, replacing every placeholder.

**NOTE: If you copy .env into `~/.config/littlehorse.config`, you can use this configuration to access the LH Server via `lhctl`**

#### Start the Worker

From the repository root, run the following command:

```bash
./gradlew :examples:saddle:java:00-quickstart:run
```

### Verify TaskDef in LH Dashboard

Navigate to the Dashboard: Click on **Orchestrate** in the sidebar, and then click on the **Dashboard** tile to go to the LH Server dashboard. If you've tried the open-source LittleHorse Server, this should be a familiar screen!

Go to the `TaskDef` page. You should see a `send-slack` TaskDef, regardless of whether you ran the worker locally or used the `SaddleBag` approach.

## Step 2: Creating a WfSpec in Saddle

Let's now create a `WfSpec` that accepts a channel and message, and sends the message to the specified channel.

In the left sidebar, expand **Orchestrate** and select **Workflows**. Click **Add Workflow** in the top-right corner, enter `quickstart-wf` as the workflow name, and create the workflow. Saddle will open the new WfSpec in the Workflow Builder.

Select **Variables** at the top of the Workflow Builder. Add two variables with the `STR` type: `channel` and `message`.

![Workflow Builder variables configured with channel and message STR values](workflow-builder-variables.png)

Drag a **Task** node from the left sidebar onto the canvas. Select `send-slack`.

Set the task's `channel` parameter to the `channel` variable and its `message` parameter to the `message` variable.

Next, drag an **Exit** node onto the canvas to the right of the task. Connect the nodes in this order: **Start**, your task, **Exit**.

![Workflow Builder using the Slack option with a send-slack task connected between Start and Exit](workflow-builder.png)

Click **Deploy WfSpec**, then click **Register Workflow**. If you go to the LittleHorse dashboard, you should be able to test the workflow by clicking **Execute** from the `WfSpec` page.

### Step 4: Creating a Webhook

In Saddle, a `Webhook` receives a JSON request and publishes it to a `Streamlet`. We'll receive a JSON request that contains two fields: `message` and `channel`.

In the left sidebar, expand **Connect** and select **Webhooks**. Click **Add Webhook** in the top-right corner.

Enter `quickstart-webhook` as the name and `/quickstart` as the path. Under **Body Schema**, select **JSON Body** and paste the following schema:

```json
{
  "$id": "https://example.com/schemas/quickstart-webhook.json",
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "properties": {
    "channel": {
      "type": "string"
    },
    "message": {
      "type": "string"
    }
  },
  "required": [
    "channel",
    "message"
  ],
  "additionalProperties": false
}
```

![Saddle webhook configured with the quickstart path and JSON body schema](webhook-configuration.png)

Validate the schema, then click **Create Webhook**. Saddle creates an associated streamlet named `wh.quickstart-webhook` for the incoming requests.

### Step 5: Creating a Workflow Trigger

The Workflow Trigger starts the WfSpec whenever the webhook publishes a request to its streamlet.

In the left sidebar, expand **Stream** and select **Workflow Triggers**. Click **Add Trigger** in the top-right corner, then configure these fields:

- **Name:** `quickstart-trigger`
- **Type:** **Workflow Spec**
- **Workflow:** `quickstart-wf`
- **Major Version:** `0.0`
- **Streamlet:** `wh.quickstart-webhook`

![Saddle workflow trigger configured with the quickstart workflow and webhook streamlet](workflow-trigger-configuration.png)

Leave the `wfRunId` mapping empty so the server generates an ID for each run. Map the WfSpec variables to the webhook body:

- `message`: `$.value.body.message`
- `channel`: `$.value.body.channel`

![Saddle workflow trigger mapping webhook fields to the message and channel WfSpec variables](workflow-trigger-variable-mapping.png)

Click **Create Trigger** to activate the integration.

### Step 6: Testing the Workflow

Open the `quickstart-webhook` details page and copy its generated listener URL. Send a `POST` request with the Slack channel and message that the workflow expects:

```bash
curl --request POST '<WEBHOOK_LISTENER_URL>' \
  --header 'Content-Type: application/json' \
  --data '{
    "channel": "<CHANNEL>",
    "message": "Hello from Saddle!"
  }'
```

Replace `<WEBHOOK_LISTENER_URL>` with the complete URL shown by Saddle. For the Slack option, replace `<CHANNEL>` with the ID of a channel that the Slack app can access. For the DIY option, use any descriptive channel value.

The webhook publishes the request to `wh.quickstart-webhook`, and the Workflow Trigger starts a new `quickstart-wf` run. Confirm that the WfRun completes successfully in Saddle. The Slack option sends the message to the selected channel; the DIY option prints it in the terminal running the Java application.
