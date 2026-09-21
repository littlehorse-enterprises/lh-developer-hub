package main

import "github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"

const CarStructDefName = "car"
const CarStructDefDescription = "A car."

type Car struct {
	Make  string `json:"make"`
	Model string `json:"model"`
	Year  int    `json:"year"`
}

func (Car) LHStructDef() littlehorse.LHStructDefInfo {
	return littlehorse.LHStructDefInfo{
		Name:        CarStructDefName,
		Description: CarStructDefDescription,
	}
}
