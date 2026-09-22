using LittleHorse.Sdk.UserTask;
using LittleHorse.Sdk.Worker;

namespace UserTasksExample;

public class FavoritePlayerForm
{
    [UserTaskField(DisplayName = "Favorite Team", Required = true)]
    public string FavoriteTeam = string.Empty;

    [UserTaskField(DisplayName = "Favorite Player's Number", Required = true)]
    public int FavoritePlayerNumber;
}

public class PlayerReporter
{
    [LHTaskMethod("report-favorite-player")]
    public Task<string> ReportFavoritePlayer(string user, string team, int player)
    {
        string result = $"{user}'s favorite player is # {player} on the {team} team!";
        Console.WriteLine(result);
        return Task.FromResult(result);
    }
}