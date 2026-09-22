from littlehorse.lh_struct import lh_struct_def


@lh_struct_def(name="car", description="A car.")
class Car:
    make: str
    model: str
    year: int


async def describe_car(car: Car) -> str:
    return f"You drive a {car.make} {car.model}"
