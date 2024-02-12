package com.davidconneely.car;

import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Repository
public class CarRepository {
    List<Car> cars = Collections.unmodifiableList(Arrays.asList(
            new Car(1, "Avensis"),
            new Car(2, "Aygo"),
            new Car(3, "C-HR"),
            new Car(4, "RAV4"),
            new Car(5, "Yaris")
    ));
    List<Car> findAll() {
        return cars;
    }
}
