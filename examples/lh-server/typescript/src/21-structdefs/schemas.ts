import { lhStruct } from "littlehorse-client";
import { z } from "zod";

export const Car = lhStruct(
  "car",
  z.object({
    make: z.string(),
    model: z.string(),
    year: z.number().int(),
  }),
);

export type Car = z.infer<typeof Car>;

export const Address = lhStruct(
  "address",
  z.object({ street: z.string(), city: z.string() }),
);

export const Person = lhStruct(
  "person",
  z.object({
    firstName: z.string(),
    lastName: z.string(),
    homeAddress: Address.nullable(),
  }),
);