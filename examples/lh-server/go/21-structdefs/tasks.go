package main

import "fmt"

func DescribeCar(car Car) string {
	return fmt.Sprintf("You drive a %s %s", car.Make, car.Model)
}
