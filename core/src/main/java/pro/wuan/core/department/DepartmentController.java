/**
 * This class provides the controller for Department operations.
 * It uses Spring Boot's @RestController annotation to indicate that it's a RESTful controller.
 * It uses Lombok's @RequiredArgsConstructor annotation to generate a constructor with required fields.
 */
package pro.wuan.core.department;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DepartmentController {

    /**
     * An instance of DepartmentRepository to handle Department-related operations.
     */
    private final DepartmentRepository departmentRepository;

    /**
     * This method saves a Department entity.
     * It uses Spring Boot's @PostMapping annotation to handle POST requests.
     * It uses Spring Boot's @Transactional annotation to ensure the operation is transactional.
     *
     * @param department the Department entity to save
     * @return a ResponseEntity containing the saved Department entity
     */
    @PostMapping("/saveDepartment")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Department> savePerson(@RequestBody Department department) {
        departmentRepository.save(department);
        return ResponseEntity.ok(department);
    }

    /**
     * This method finds a Department entity by name.
     * It uses Spring Boot's @GetMapping annotation to handle GET requests.
     *
     * @param name the name of the Department entity to find
     * @return a ResponseEntity containing the found Department entity
     */
    @GetMapping("/department/{name}")
    public ResponseEntity<List<Department>> findByName(@PathVariable("name") String name) {
        Department department = new Department();
        department.setName(name);

        ExampleMatcher matcher = ExampleMatcher.matching()
            .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains()); // 设置name字段为模糊匹配

        Example<Department> example = Example.of(department, matcher);

        return ResponseEntity.ok(departmentRepository.findAll(example));
    }

}