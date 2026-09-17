import { toStructVariableValue } from "littlehorse-client";
import { config } from "../config.js";
import { Car } from "./schemas.js";

const inputCar: Car = { make: "Pontiac", model: "Aztek", year: 2005 };
const run = await config.getClient().runWf({
  wfSpecName: "quickstart",
  variables: { "input-car": toStructVariableValue(inputCar, Car) },
});
console.log(run);