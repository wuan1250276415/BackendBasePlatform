package pro.wuan.common.core.model;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class PageResult<T> extends Result<T> implements IPage<T> {

    private static final long serialVersionUID = 8545996863226528798L;
    private List<T> records;
    private long total;
    private long size;
    private long current;
    private List<OrderItem> orders;
    private boolean optimizeCountSql;
    private boolean isSearchCount;
    private boolean hitCount;

    public PageResult() {
        this.records = Collections.emptyList();
        this.total = 0L;
        this.size = 10L;
        this.current = 1L;
        this.orders = new ArrayList();
        this.optimizeCountSql = true;
        this.isSearchCount = true;
        this.hitCount = false;
    }

    public PageResult(long current, long size) {
        this(current, size, 0L);
    }

    public PageResult(long current, long size, long total) {
        this(current, size, total, true);
    }

    public PageResult(long current, long size, boolean isSearchCount) {
        this(current, size, 0L, isSearchCount);
    }

    public PageResult(long current, long size, long total, boolean isSearchCount) {
        this.records = Collections.emptyList();
        this.total = 0L;
        this.size = 10L;
        this.current = 1L;
        this.orders = new ArrayList();
        this.optimizeCountSql = true;
        this.isSearchCount = true;
        this.hitCount = false;
        if (current > 1L) {
            this.current = current;
        }

        this.size = size;
        this.total = total;
        this.isSearchCount = isSearchCount;
    }

    public boolean hasPrevious() {
        return this.current > 1L;
    }

    public boolean hasNext() {
        return this.current < this.getPages();
    }

    public List<T> getRecords() {
        return this.records;
    }

    public PageResult<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    public long getTotal() {
        return this.total;
    }

    public PageResult<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    public long getSize() {
        return this.size;
    }

    public PageResult<T> setSize(long size) {
        this.size = size;
        return this;
    }

    public long getCurrent() {
        return this.current;
    }

    public PageResult<T> setCurrent(long current) {
        this.current = current;
        return this;
    }

    /** @deprecated */
    @Deprecated
    @Nullable
    public String[] ascs() {
        return CollectionUtils.isNotEmpty(this.orders) ? this.mapOrderToArray(OrderItem::isAsc) : null;
    }

    private String[] mapOrderToArray(Predicate<OrderItem> filter) {
        List<String> columns = new ArrayList(this.orders.size());
        this.orders.forEach((i) -> {
            if (filter.test(i)) {
                columns.add(i.getColumn());
            }

        });
        return (String[])columns.toArray(new String[0]);
    }

    private void removeOrder(Predicate<OrderItem> filter) {
        for(int i = this.orders.size() - 1; i >= 0; --i) {
            if (filter.test(this.orders.get(i))) {
                this.orders.remove(i);
            }
        }

    }

    public PageResult<T> addOrder(OrderItem... items) {
        this.orders.addAll(Arrays.asList(items));
        return this;
    }

    public PageResult<T> addOrder(List<OrderItem> items) {
        this.orders.addAll(items);
        return this;
    }

    /** @deprecated */
    @Deprecated
    public PageResult<T> setAscs(List<String> ascs) {
        return CollectionUtils.isNotEmpty(ascs) ? this.setAsc((String[])ascs.toArray(new String[0])) : this;
    }

    /** @deprecated */
    @Deprecated
    public PageResult<T> setAsc(String... ascs) {
        this.removeOrder(OrderItem::isAsc);
        String[] var2 = ascs;
        int var3 = ascs.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String s = var2[var4];
            this.addOrder(OrderItem.asc(s));
        }

        return this;
    }

    /** @deprecated */
    @Deprecated
    public String[] descs() {
        return this.mapOrderToArray((i) -> {
            return !i.isAsc();
        });
    }

    /** @deprecated */
    @Deprecated
    public PageResult<T> setDescs(List<String> descs) {
        if (CollectionUtils.isNotEmpty(descs)) {
            this.removeOrder((item) -> {
                return !item.isAsc();
            });
            Iterator var2 = descs.iterator();

            while(var2.hasNext()) {
                String s = (String)var2.next();
                this.addOrder(OrderItem.desc(s));
            }
        }

        return this;
    }

    /** @deprecated */
    @Deprecated
    public PageResult<T> setDesc(String... descs) {
        this.setDescs(Arrays.asList(descs));
        return this;
    }

    public List<OrderItem> orders() {
        return this.getOrders();
    }

    public List<OrderItem> getOrders() {
        return this.orders;
    }

    public void setOrders(List<OrderItem> orders) {
        this.orders = orders;
    }

    public boolean optimizeCountSql() {
        return this.optimizeCountSql;
    }

    public boolean isSearchCount() {
        return this.total < 0L ? false : this.isSearchCount;
    }

    public PageResult<T> setSearchCount(boolean isSearchCount) {
        this.isSearchCount = isSearchCount;
        return this;
    }

    public PageResult<T> setOptimizeCountSql(boolean optimizeCountSql) {
        this.optimizeCountSql = optimizeCountSql;
        return this;
    }

    public void hitCount(boolean hit) {
        this.hitCount = hit;
    }

    public boolean isHitCount() {
        return this.hitCount;
    }


}
