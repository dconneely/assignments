package com.davidconneely.car.controller;

import com.davidconneely.car.entity.Car;
import com.davidconneely.car.repository.CarRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/cars")
public class CarController {
    private final CarRepository repository;

    public CarController(CarRepository repository) {
        this.repository = repository;
    }

    private static <T> Stream<T> iterableToStream(Iterable<T> iter) {
        return StreamSupport.stream(iter.spliterator(), false);
    }

    private static <T> ResponseEntity<Iterable<T>> responseOf(Iterable<T> iter) {
        return iter.iterator().hasNext() ? ResponseEntity.ok(iter) : ResponseEntity.notFound().build();
    }

    @GetMapping({"", "/"})
    @ResponseBody
    public ResponseEntity<Iterable<Car>> getCars(@RequestParam(value = "model", required = false) String model) {
        Iterable<Car> cars = repository.findAll();
        if (model != null && !model.isBlank()) {
            // case-insensitive substring match (using US English casing rules):
            String lcModel = model.trim().toLowerCase(Locale.US);
            cars = iterableToStream(cars)
                    .filter(car -> car.model().toLowerCase(Locale.US).contains(lcModel))
                    .toList();
        }
        return responseOf(cars);
    }

    @GetMapping({"/{id}", "/{id}/"})
    @ResponseBody
    public ResponseEntity<Car> getCar(@PathVariable(value = "id") long id) {
        return ResponseEntity.of(repository.findById(id));
    }
}
