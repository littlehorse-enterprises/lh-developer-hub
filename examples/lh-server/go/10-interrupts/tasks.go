package main

import "fmt"

func startCollection() string {
	fmt.Println("Starting collection of underpants")
	return ""
}

func shipItem(underPantOwner string) string {
	result := fmt.Sprintln("Successfully collected underpants from ", underPantOwner)
	fmt.Println(result)
	return result
}

func profit(underpants *[]string) string {
	result := fmt.Sprintf("Collect %d underpants", len(*underpants))
	fmt.Println(result)
	return result
}
