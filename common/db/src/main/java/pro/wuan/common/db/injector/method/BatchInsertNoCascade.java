package pro.wuan.common.db.injector.method;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import lombok.NoArgsConstructor;

import java.util.function.Predicate;

/**
 * @program: tellhowcloud
 * @description: 批量插入，未实现级联消息分发
 * @author: HawkWang
 * @create: 2021-08-26 11:00
 **/
@NoArgsConstructor
public class BatchInsertNoCascade extends InsertBatchSomeColumn {
    public BatchInsertNoCascade(Predicate<TableFieldInfo> predicate) {
        super(predicate);
    }

}
