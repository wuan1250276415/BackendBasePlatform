package pro.wuan.core.department;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DepartmentController {

//    private final DepartmentRepository departmentRepository;

    private final ElasticsearchOperations elasticsearchOperations;

//    @RequestMapping(value = "/save", method = RequestMethod.POST)
//    public ResponseEntity<Department> save(Department department) {
//        return ResponseEntity.ok(departmentRepository.save(department));
//    }


    @PostMapping("/savePerson")
    public Integer savePerson(@RequestBody Department person) {
        Department savedEntity = elasticsearchOperations.save(person);
        return savedEntity.getId();
    }

    @GetMapping("/person/{id}")
    public Department findById(@PathVariable("id") String id) {
        return elasticsearchOperations.get(id, Department.class);
    }

}
