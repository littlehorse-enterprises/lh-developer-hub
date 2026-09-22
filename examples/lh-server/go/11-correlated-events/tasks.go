package main

import "fmt"

func ProcessSignedDocument(documentID, signerName string) string {
	result := fmt.Sprintf("Document %s was signed by %s. Processing complete.", documentID, signerName)
	fmt.Println(result)
	return result
}
