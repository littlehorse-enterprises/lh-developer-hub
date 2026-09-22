package main

import "fmt"

const TaskDefName string = "greet"

func Greet(name string) string {
	result := "Hello there, " + name + "!"
	fmt.Println(result)
	return result
}
