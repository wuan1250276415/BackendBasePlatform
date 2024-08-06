package pro.wuan.core.department;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pro.wuan.core.base.repository.BaseRepository;

@EnableJpaRepositories
public interface DepartmentRepository extends BaseRepository<Department, Integer> {
}
