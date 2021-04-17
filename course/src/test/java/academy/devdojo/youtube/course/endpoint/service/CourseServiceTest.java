package academy.devdojo.youtube.course.endpoint.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import academy.devdojo.youtube.core.model.Course;
import academy.devdojo.youtube.core.repository.CourseRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CourseServiceTest {
	
	@Mock
	private CourseRepository repository;
	
	@InjectMocks
	private CourseService service;
	
	private Pageable pageable;
	private Page<Course> coursePage;
	
	@BeforeEach
	public void setUp() {
		pageable = PageRequest.of(1, 1);
		Course course = Course.builder()
				.id(1L)
				.title("test")
				.build();
		List<Course> courses = Arrays.asList(course);
		coursePage = new PageImpl<Course>(courses);
	}
	
	@Test
	public void givenPageable_whenListingCourses_ThenShouldReturnCoursesTest() {
		when(repository.findAll(pageable)).thenReturn(coursePage);
		
		Iterable<Course> methodReturn = service.list(pageable);
		
		assertEquals(coursePage, methodReturn);
		verify(repository).findAll(pageable);
		verifyNoMoreInteractions(repository);
	}
}
