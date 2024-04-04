//package pro.wuan.core.service;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.flowable.engine.RuntimeService;
//import org.flowable.engine.TaskService;
//import org.flowable.task.api.Task;
//import org.springframework.stereotype.Service;
//import pro.wuan.core.person.Person;
//import pro.wuan.core.person.PersonRepository;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class MyService {
//
//    private final RuntimeService runtimeService;
//
//
//    private final TaskService taskService;
//
//    private final PersonRepository personRepository;
//
//    public void startProcess(String assignee) {
//
//        Person person = personRepository.findByUsername(assignee);
//
//        Map<String, Object> variables = new HashMap<>();
//        variables.put("person", person);
//        runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
//    }
//
//    public List<Task> getTasks(String assignee) {
//        return taskService.createTaskQuery().taskAssignee(assignee).list();
//    }
//
//    public void createDemoUsers() {
//        if (personRepository.findAll().size() == 0) {
//            personRepository.save(new Person("jbarrez", "Joram", "Barrez", new Date()));
//            personRepository.save(new Person("trademakers", "Tijs", "Rademakers", new Date()));
//        }
//    }
//
//}
