package com.davidconneely.car.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.davidconneely.car.entity.Car;
import com.davidconneely.car.repository.CarRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

final class CarControllerTest {
  @Test
  void testGetAll() {
    CarRepository mockRepo = mock(CarRepository.class);
    when(mockRepo.findAll())
        .thenReturn(
            List.of(
                new Car(1L, "manufacturer1", "model1"),
                new Car(2L, "manufacturer2", "model2"),
                new Car(3L, "manufacturer3", "model3")));
    CarController controller = new CarController(mockRepo);
    Collection<Car> cars = controller.getCars(null, null);
    assertEquals(3, cars.size());
  }

  @Test
  void testGetAllModel() {
    CarRepository mockRepo = mock(CarRepository.class);
    when(mockRepo.findAll())
        .thenReturn(
            List.of(
                new Car(1L, "manufacturer1", "model1"),
                new Car(2L, "manufacturer2", "model2"),
                new Car(3L, "manufacturer3", "model3")));
    CarController controller = new CarController(mockRepo);
    Collection<Car> cars = controller.getCars(null, "Model3");
    assertEquals(1, cars.size());
  }

  @Test
  void testGetAllEmpty() {
    CarRepository mockRepo = mock(CarRepository.class);
    when(mockRepo.findAll())
        .thenReturn(
            List.of(
                new Car(1L, "manufacturer1", "model1"),
                new Car(2L, "manufacturer2", "model2"),
                new Car(3L, "manufacturer3", "model3")));
    CarController controller = new CarController(mockRepo);
    Collection<Car> cars = controller.getCars(null, "model99");
    assertTrue(cars.isEmpty());
  }

  @Test
  void testGetOneExists() {
    CarRepository mockRepo = mock(CarRepository.class);
    when(mockRepo.findById(1L)).thenReturn(Optional.of(new Car(1L, "manufacturer1", "model1")));
    CarController controller = new CarController(mockRepo);
    ResponseEntity<Car> response = controller.getCar(1L);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1L, response.getBody().id());
  }

  @Test
  void testGetOneNotExists() {
    CarRepository mockRepo = mock(CarRepository.class);
    when(mockRepo.findById(99L)).thenReturn(Optional.empty());
    CarController controller = new CarController(mockRepo);
    ResponseEntity<Car> response = controller.getCar(99L);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
  }

  @Test
  void testGetAllByManufacturer() {
    CarRepository mockRepo = mock(CarRepository.class);
    when(mockRepo.findAll())
        .thenReturn(
            List.of(
                new Car(1L, "Ford", "Focus"),
                new Car(2L, "Ford", "Fiesta"),
                new Car(3L, "Honda", "Civic")));
    CarController controller = new CarController(mockRepo);
    Collection<Car> cars = controller.getCars("Ford", null);
    assertEquals(2, cars.size());
  }

  @Test
  void testGetAllByManufacturerAndModel() {
    CarRepository mockRepo = mock(CarRepository.class);
    when(mockRepo.findAll())
        .thenReturn(
            List.of(
                new Car(1L, "Ford", "Focus"),
                new Car(2L, "Ford", "Fiesta"),
                new Car(3L, "Honda", "Civic")));
    CarController controller = new CarController(mockRepo);
    Collection<Car> cars = controller.getCars("Ford", "Focus");
    assertEquals(1, cars.size());
    assertEquals("Focus", cars.iterator().next().model());
  }

  @Test
  void testGetAllCaseInsensitive() {
    CarRepository mockRepo = mock(CarRepository.class);
    when(mockRepo.findAll()).thenReturn(List.of(new Car(1L, "Ford", "Focus")));
    CarController controller = new CarController(mockRepo);
    Collection<Car> cars = controller.getCars("FORD", "FOCUS");
    assertEquals(1, cars.size());
  }

  @Test
  void testGetAllTrimsWhitespace() {
    CarRepository mockRepo = mock(CarRepository.class);
    when(mockRepo.findAll()).thenReturn(List.of(new Car(1L, "Ford", "Focus")));
    CarController controller = new CarController(mockRepo);
    Collection<Car> cars = controller.getCars("  Ford  ", "  Focus  ");
    assertEquals(1, cars.size());
  }

  @Test
  void testGetAllEmptyRepository() {
    CarRepository mockRepo = mock(CarRepository.class);
    when(mockRepo.findAll()).thenReturn(List.of());
    CarController controller = new CarController(mockRepo);
    Collection<Car> cars = controller.getCars(null, null);
    assertTrue(cars.isEmpty());
  }
}
