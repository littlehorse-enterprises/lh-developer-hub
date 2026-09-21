using LittleHorse.Sdk.Worker;

namespace ArraysExample;

public class ArrayWorker
{
    [LHTaskMethod("produce-array")]
    [LHType(masked: false, isLHArray: true)]
    public Task<long[]> ProduceArray() => Task.FromResult(new long[] { 1L, 2L, 3L });

    [LHTaskMethod("consume-array")]
    public Task<string> ConsumeArray([LHType(masked: false, isLHArray: true)] long[] arr)
    {
        return Task.FromResult($"Received {arr.Length} elements");
    }
}