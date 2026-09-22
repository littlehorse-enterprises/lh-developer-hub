async def process_signed_document(document_id: str, signer_name: str) -> str:
    result = f"Document {document_id} was signed by {signer_name}. Processing complete."
    print(result)
    return result
