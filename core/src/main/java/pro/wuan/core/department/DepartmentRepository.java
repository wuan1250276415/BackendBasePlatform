package pro.wuan.core.department;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pro.wuan.core.base.repository.BaseRepository;
import pro.wuan.feignapi.userapi.entity.Department;

@EnableJpaRepositories
public interface DepartmentRepository extends BaseRepository<Department, Integer> {
}
