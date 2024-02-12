package com.davidconneely.car;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class CarController {
    private final CarRepository repository;

    public CarController(CarRepository repository) {
        this.repository = repository;
    }

    @GetMapping({"/cars", "/cars/"})
    @ResponseBody
    public ResponseEntity<List<Car>> getCars(@RequestParam(value = "model", required = false) String model) {
        List<Car> cars = model == null ? repository.findAll() : repository.findAll().stream()
                .filter(car -> car.model().regionMatches(true, 0, model, 0, model.length()))
                .collect(Collectors.toList());
        return cars.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(cars);
    }
}
