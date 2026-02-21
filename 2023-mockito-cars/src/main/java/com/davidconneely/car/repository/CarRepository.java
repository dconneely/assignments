package com.davidconneely.car.repository;

import com.davidconneely.car.entity.Car;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CarRepository {
  private final Map<Long, Car> cars =
      Map.of(
          1L, new Car(1L, "Ford", "Focus"),
          2L, new Car(2L, "Honda", "Civic"),
          3L, new Car(3L, "Kia", "Ceed"),
          4L, new Car(4L, "SEAT", "Leon"),
          5L, new Car(5L, "Volkswagen", "Golf"));

  public Collection<Car> findAll() {
    return cars.values();
  }

  public Optional<Car> findById(Long id) {
    return Optional.ofNullable(cars.get(id));
  }
}
