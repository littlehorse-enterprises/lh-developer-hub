package io.littlehorse.docs.usertasks;

import io.littlehorse.sdk.usertask.annotations.UserTaskField;

public class FavoritePlayerForm {

    @UserTaskField(displayName = "Favorite Team", required = true)
    public String favoriteTeam;

    @UserTaskField(displayName = "Favorite Player's Number", required = true)
    public int favoritePlayerNumber;
}