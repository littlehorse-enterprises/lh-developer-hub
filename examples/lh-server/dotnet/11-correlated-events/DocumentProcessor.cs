using LittleHorse.Sdk.Worker;

namespace CorrelatedEventsExample;

public class DocumentProcessor
{
    [LHTaskMethod("processSignedDocument")]
    public string ProcessSignedDocument(string documentId, string signerName)
    {
        string result = $"Document {documentId} was signed by {signerName}. Processing complete.";
        Console.WriteLine(result);
        return result;
    }
}