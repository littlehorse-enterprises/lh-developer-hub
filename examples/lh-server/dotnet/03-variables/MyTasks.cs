using LittleHorse.Sdk.Exceptions;
using LittleHorse.Sdk.Worker;

namespace VariablesExample;

public class User(string email, string title, int age)
{
    public string Email = email;
    public string Title = title;
    public int Age = age;
}

public class MyTasks
{
    [LHTaskMethod("fetch-user")]
    public User FetchUser(string userId)
    {
        return userId switch
        {
            "obiwan" => new User("obiwan@jedi.temple", "Master Kenobi", 37),
            "anakin" => new User("anakin@jedi.temple", "Padawan Skywalker (not Master)", 22),
            _ => throw new LHTaskException("user-not-found", "Could not find specified user")
        };
    }

    [LHTaskMethod("send-email")]
    public string SendEmail(string toAddress, string message)
    {
        string result = $"sent email {message} to address {toAddress}";
        Console.WriteLine(result);
        return result;
    }
}