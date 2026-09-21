package main

import "fmt"

func Greet(name string) string {
	result := "Hello, " + name + "!"
	fmt.Println(result)
	return result
}
