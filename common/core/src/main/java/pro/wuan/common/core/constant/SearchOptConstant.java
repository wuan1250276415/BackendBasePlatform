package pro.wuan.common.core.constant;

/**
 * 查询类型定义
 * @author ivan
 * @blame
 * @since 2019/11/18
 */
public class SearchOptConstant {
    //相等
    public static final String SEARCH_EQUAL = "eq";

    //小于
    public static final String SEARCH_LESS_THEN = "lt";

    //小于等于
    public static final String SEARCH_LESS_OR_EQUAL = "lte";

    //大于
    public static final String SEARCH_GREATER_THEN = "gt";

    //大于等于
    public static final String SEARCH_GREATER_THEN_OR_EQUAL = "gte";

    //之间
    public static final String SEARCH_BETWEEN = "between";

    //in
    public static final String SEARCH_IN = "in";

    //左右全模糊匹配
    public static final String SEARCH_LIKE_BOTH = "like";

    //左边模糊匹配
    public static final String SEARCH_LIKE_LEFT = "llike";

    //右边模糊匹配
    public static final String SEARCH_LIKE_RIGHT = "rlike";

    //不为空
    public static final String SEARCH_IS_NOT_NULL = "notNull";

    //为空
    public static final String SEARCH_IS_NULL = "null";

    //一个值对应多字段左右模糊匹配
    public static final String SEARCH_LIKE_MULTILIKE = "multiLike";

    //年月日时分秒的between，前后包含
    public static final String SERACH_DATETIME_BETWEEN = "datetimeBetween";

}
