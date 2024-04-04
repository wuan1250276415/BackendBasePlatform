package pro.wuan.core.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

    // 在此处添加通用的方法，例如逻辑删除、根据租户id查询等

}
