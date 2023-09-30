package com.vitrum.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;


public class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateCourse() {
        CourseRequest courseRequest = new CourseRequest("Math", "Advanced math course");
        UserDetails userDetails = mock(UserDetails.class);
        CourseResponse expectedResponse = new CourseResponse();

        when(courseService.createCourse(courseRequest, userDetails)).thenReturn(expectedResponse);

        ResponseEntity<?> responseEntity = courseController.createCourse(userDetails, courseRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    public void testEnrollUserToCourse() {
        Long courseId = 1L;
        Long userId = 2L;

        ResponseEntity<?> responseEntity = courseController.enrollUserToCourse(courseId, userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("User enrolled in the course successfully.", responseEntity.getBody());
    }

    @Test
    public void testGetAllCourses() {
        List<CourseResponse> expectedResponse = new ArrayList<>();
        when(courseService.getAllCourses()).thenReturn(expectedResponse);

        List<CourseResponse> response = courseController.getAllCourses();

        assertEquals(expectedResponse, response);
    }

    @Test
    public void testGetCourseByNameFound() {
        String courseName = "Math";
        CourseResponse expectedResponse = new CourseResponse();

        when(courseService.getCourseResponseByName(courseName)).thenReturn(expectedResponse);

        ResponseEntity<CourseResponse> responseEntity = courseController.getCourseByName(courseName);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    public void testGetCourseByNameNotFound() {
        String courseName = "NonExistentCourse";

        when(courseService.getCourseResponseByName(courseName)).thenReturn(null);

        ResponseEntity<CourseResponse> responseEntity = courseController.getCourseByName(courseName);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void testDeleteById() {
        Long courseId = 1L;

        ResponseEntity<?> responseEntity = courseController.deleteById(courseId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Deleted", responseEntity.getBody());
    }
}
