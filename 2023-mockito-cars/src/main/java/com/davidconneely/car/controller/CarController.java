package com.davidconneely.car.controller;

import com.davidconneely.car.entity.Car;
import com.davidconneely.car.repository.CarRepository;
import java.util.Collection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
public class CarController {
  private final CarRepository repository;

  public CarController(CarRepository repository) {
    this.repository = repository;
  }

  private static boolean containsIgnoreCase(String str, String search) {
    return search.isEmpty() || str.toLowerCase().contains(search.toLowerCase());
  }

  @GetMapping
  public Collection<Car> getCars(
      @RequestParam(required = false) String manufacturer,
      @RequestParam(required = false) String model) {
    String mfr = manufacturer != null ? manufacturer.trim() : "";
    String mdl = model != null ? model.trim() : "";
    return repository.findAll().stream()
        .filter(car -> containsIgnoreCase(car.manufacturer(), mfr))
        .filter(car -> containsIgnoreCase(car.model(), mdl))
        .toList();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Car> getCar(@PathVariable long id) {
    return ResponseEntity.of(repository.findById(id));
  }
}
