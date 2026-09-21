package io.littlehorse.docs.usertasks;

import io.littlehorse.sdk.worker.LHTaskMethod;

public class FavoritePlayerTasks {

    @LHTaskMethod("report-favorite-player")
    public String reportFavoritePlayer(String user, String team, int player) {
        String result = user + "'s favorite player is #" + player + " on the " + team + " team!";
        System.out.println(result);
        return result;
    }
}