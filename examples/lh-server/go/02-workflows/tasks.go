package main

import "fmt"

func Greet(name string) string {
	result := "Hello there, " + name + "!"
	fmt.Println(result)
	return result
}
