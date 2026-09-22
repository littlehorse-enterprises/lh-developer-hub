package main

import (
	"errors"
	"fmt"
)

type User struct {
	Email string `json:"email"`
	Title string `json:"title"`
	Age   int    `json:"age"`
}

func FetchUser(userId string) (*User, error) {
	switch userId {
	case "obiwan":
		return &User{"obiwan@jedi.temple", "Master Kenobi", 37}, nil
	case "anakin":
		return &User{"anakin@jedi.temple", "Padawan Skywalker (not Master)", 22}, nil
	default:
		return nil, errors.New("user-not-found: Could not find specified user")
	}
}

func SendEmail(toAdress, message string) string {
	result := fmt.Sprintf("sent email %s to %s", message, toAdress)
	fmt.Println(result)
	return result
}
