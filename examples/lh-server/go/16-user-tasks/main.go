package main

import (
	"context"
	"log"

	"github.com/littlehorse-enterprises/lh-developer-hub/examples/lh-server/go/internal/examplesupport"
	"github.com/littlehorse-enterprises/littlehorse/sdk-go/lhproto"
	"github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"
)

func main() {
	config := littlehorse.NewConfigFromEnv()
	client, err := config.GetGrpcClient()
	if err != nil {
		log.Fatal(err)
	}
	_, err = (*client).PutUserTaskDef(context.Background(), &lhproto.PutUserTaskDefRequest{
		Name: "report-favorite-player",
		Fields: []*lhproto.UserTaskField{
			{Name: "favoriteTeam", DisplayName: "Favorite Team", Type: lhproto.VariableType_STR, Required: true},
			{Name: "favoritePlayerNumber", DisplayName: "Favorite Player Number", Type: lhproto.VariableType_INT, Required: true},
		},
	})
	if err != nil {
		log.Fatal(err)
	}

	worker := examplesupport.NewWorker(config, reportFavoritePlayer, "report-favorite-player")
	workflow := littlehorse.NewWorkflow(wfLogic, "favorite-player-demo")
	examplesupport.Start(config, []*littlehorse.LHTaskWorker{worker}, workflow)
}
