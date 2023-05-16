package pro.wuan.core.department;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pro.wuan.core.department.elasticsearch.ElasticDepartmentRepository;

@RestController
@RequiredArgsConstructor
public class DepartmentController {


    private final DepartmentRepository departmentRepository;
    private final ElasticDepartmentRepository elasticDepartmentRepository;


    @PostMapping("/savePerson")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Department> savePerson(@RequestBody Department person) {
//        Department savedEntity = elasticsearchOperations.save(person);
        departmentRepository.save(person);
        JSONObject department = JSON.parseObject(JSONObject.toJSONString(person, JSONWriter.Feature.UseSingleQuotes), JSONObject.class, JSONReader.Feature.AllowUnQuotedFieldNames);
        pro.wuan.core.department.elasticsearch.Department department1 = JSONObject.parseObject(department.toJSONString(), pro.wuan.core.department.elasticsearch.Department.class, JSONReader.Feature.AllowUnQuotedFieldNames);
        elasticDepartmentRepository.save(department1);
        return ResponseEntity.ok(person);
    }

    @GetMapping("/person/{name}")
    public ResponseEntity<?> findById(@PathVariable("name") String name) {

//        Criteria criteria = new Criteria("name").is(name);
//        CriteriaQueryBuilder queryBuilder = new CriteriaQueryBuilder(criteria);
//        SearchHits<Department> searchHits = elasticsearchOperations.search(queryBuilder.build(), Department.class);
        return ResponseEntity.ok(elasticDepartmentRepository.findDepartmentByName(name));
    }

}
