package com.davidconneely.car.controller;

import com.davidconneely.car.entity.Car;
import com.davidconneely.car.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class CarControllerTest {
    private static <T> List<T> iterableToList(Iterable<T> iter) {
        return StreamSupport.stream(iter.spliterator(), false).toList();
    }

    @Test
    void testGetAll() {
        CarRepository mockRepo = mock(CarRepository.class);
        when(mockRepo.findAll()).thenReturn(List.of(
                new Car(1L, "model1"),
                new Car(2L, "model2"),
                new Car(3L, "model3")));
        CarController controller = new CarController(mockRepo);
        ResponseEntity<Iterable<Car>> response = controller.getCars(null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, iterableToList(response.getBody()).size());
    }

    @Test
    void testGetAllModel() {
        CarRepository mockRepo = mock(CarRepository.class);
        when(mockRepo.findAll()).thenReturn(List.of(
                new Car(1L, "model1"),
                new Car(2L, "model2"),
                new Car(3L, "model3")));
        CarController controller = new CarController(mockRepo);
        ResponseEntity<Iterable<Car>> response = controller.getCars("Model3");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, iterableToList(response.getBody()).size());
    }

    @Test
    void testGetAllEmpty() {
        CarRepository mockRepo = mock(CarRepository.class);
        when(mockRepo.findAll()).thenReturn(List.of(
                new Car(1L, "model1"),
                new Car(2L, "model2"),
                new Car(3L, "model3")));
        CarController controller = new CarController(mockRepo);
        ResponseEntity<Iterable<Car>> response = controller.getCars("model99");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetOneExists() {
        CarRepository mockRepo = mock(CarRepository.class);
        when(mockRepo.findById(1L)).thenReturn(Optional.of(new Car(1L, "model1")));
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
}
