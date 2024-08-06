package pro.wuan.common.core.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pro.wuan.common.core.annotation.sysenum.EnumType;

/**
 * 全局公共常量
 */
public interface CommonConstant {
    /**
     * token请求头名称
     */
    String TOKEN_HEADER = "access-token";
    /**
     * 删除标记
     */
    String DEL_FLAG = "is_del";

    /**
     * 超级管理员用户名
     */
    String ADMIN_USER_NAME = "admin";

    /**
     * 可用状态
     */
     static final String FILE_STATUS_AVAILABLE = "1";
    /**
     * 公共日期格式
     */
    String MONTH_FORMAT = "yyyy-MM";
    String DATE_FORMAT = "yyyy-MM-dd";
    String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    String DATETIME_FORMAT2 = "yyyy-MM-dd HH:mm:ss.SSS";
    String SIMPLE_MONTH_FORMAT = "yyyyMM";
    String SIMPLE_DATE_FORMAT = "yyyyMMdd";
    String SIMPLE_DATETIME_FORMAT = "yyyyMMddHHmmss";
    String DEF_USER_PASSWORD = "123456";
    String LOCK_KEY_PREFIX = "LOCK_KEY:";


    /**
     * 日志链路追踪id信息头
     */
    String TRACE_ID_HEADER = "x-traceId-header";
    /**
     * 日志链路追踪id日志标志
     */
    String LOG_TRACE_ID = "traceId";
    /**
     * 负载均衡策略-版本号 信息头
     */
    String Z_L_T_VERSION = "z-l-t-version";
    /**
     * 注册中心元数据 版本号
     */
    String METADATA_VERSION = "version";

    /**
     * 默认父级id -1L
     */
    Long DEFAULT_PARENT_ID = -1L;

    //分隔符
    String SPILT_CHARACTER = ";";

    /**
     * 默认父级code -1
     */
    String DEFAULT_PARENT_CODE = "-1";

    /**
     * 用户详细信息缓存key值前缀
     */
    String JEDIS_USER_DETAILS_PREFIX = "loginName_";

    /**
     * 用户权限信息缓存key值前缀
     */
    String JEDIS_USER_AUTH_PREFIX = "auth_";

    /**
     * 单位管理员角色id
     */
    Long ROLE_ID_UNIT_ADMIN = 1L;

    /**
     * 树形根结点id
     */
    Long TREE_ROOT_ID = 0L;

    /**
     * 树形根结点名称
     */
    String TREE_ROOT_NAME = "顶级结点";

    /**
     * 空格
     */
    String STRING_SPACE = " ";

    /**
     * 空字符
     */
    String STRING_BLANK = "";

    /**
     * 逗号分隔符
     */
    String SEPARATOR_COMMA = ",";

    /**
     * 左小括号
     */
    String SYMBOL_LEFT_BRACKET = "(";

    /**
     * 右小括号
     */
    String SYMBOL_RIGHT_BRACKET = ")";

    /**
     * 左大括号
     */
    String SYMBOL_LEFT_BRACE = "{";

    /**
     * 右大括号
     */
    String SYMBOL_RIGHT_BRACE = "}";

    /**
     * 默认排序值
     */
    Integer DEFAULT_SORT = 0;

    /**
     * 默认id -1L
     */
    Long DEFAULT_ID = -1L;

    /**
     * 数据权限过滤系统参数
     */
    String DATA_FILTER_SYSTEM_PARAMS_USER_DETAILS = "userDetails"; // 用户详情
    String DATA_FILTER_SYSTEM_PARAMS_USER_ID = "userId"; // 用户id

    /**
     * 数据字典KEY
     */
    String DICT_KEY_ORGAN_BUSINESS_TYPE = "cloud_organBusinessType";  // 机构业务类型


    /**
     * 启用状态
     */
    @EnumType(name = "useStatus")
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum USE_STATUS {
        USE(1, "启用"),
        NONUSE(0, "停用");

        private final Integer value;
        private final String name;
    }

    /**
     * 用户类型
     */
    @EnumType(name = "userType")
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum USER_TYPE {
        SUPER_ADMIN("superAdmin", "超级管理员"),
        UNIT_ADMIN("unitAdmin", "单位管理员"),
        COMMON_USER("commonUser", "普通用户");

        private final String value;
        private final String name;
    }

    /**
     * 性别
     */
    @EnumType(name = "sex")
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum SEX {
        MAN("1", "男"),
        WOMAN("2", "女");

        private final String value;
        private final String name;
    }

    /**
     * 消息推送类型
     *
     * @author lucky
     */
    @Getter
    enum MESSAGE_PUSH_TYPE {
        WEBSOCKET("websocket", "WEB"),
        EMAIL("email", "邮件");

        private final String value;
        private final String name;

        private MESSAGE_PUSH_TYPE(String value, String name) {
            this.value = value;
            this.name = name;
        }
    }

    /**
     * 应用类型
     */
    @EnumType(name = "appType")
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum APP_TYPE {
        WEB("web", "网页应用"),
        APP("app", "手机应用"),
        API("api", "API应用");

        private final String value;
        private final String name;
    }

    /**
     * 组织机构类型
     */
    @EnumType(name = "organType")
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum ORGAN_TYPE {
        UNIT("unit", "单位"),
        DEPT("dept", "部门");

        private final String value;
        private final String name;
    }

    /**
     * 岗位类型
     */
    @EnumType(name = "postType")
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum POST_TYPE {
        DILIGENT("diligent", "工勤岗位"),
        TECHNOLOGY("technology", "专技岗位"),
        MANAGE("manage", "管理岗位");

        private final String value;
        private final String name;
    }

    /**
     * 元素类型
     */
    @Getter
    enum ELEMENT_TYPE {
        UNIT("unit", "单位"),
        DEPT("dept", "部门"),
        POST("post", "岗位"),
        USER("user", "用户"),
        ROLE("role", "角色");

        private final String value;
        private final String name;

        private ELEMENT_TYPE(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public static ELEMENT_TYPE parse(String value) {
            for (ELEMENT_TYPE elementType : ELEMENT_TYPE.values()) {
                if (elementType.getValue().equals(value)) {
                    return elementType;
                }
            }
            throw new RuntimeException("解析失败，不支持的元素类型值：" + value);
        }
    }

    /**
     * 角色类型
     */
    @Getter
    enum ROLE_TYPE {
        SYSTEM("system", "系统角色"),
        BUSINESS("business", "业务角色");

        private final String value;
        private final String name;

        private ROLE_TYPE(String value, String name) {
            this.value = value;
            this.name = name;
        }
    }

    /**
     * 业务资源类型
     */
    @Getter
    enum BUSINESS_RESOURCE_TYPE {
        CATALOG("catalog", "目录"),
        MENU("menu", "菜单"),
        FUNCTION("function", "功能");

        private final String value;
        private final String name;

        private BUSINESS_RESOURCE_TYPE(String value, String name) {
            this.value = value;
            this.name = name;
        }
    }

    /**
     * 权限类型
     */
    @Getter
    enum PRIVILEGE_TYPE {
        MENU("menu", "菜单"),
        FUNCTION("function", "功能"),
        LIST("list", "列表"),
        DATA("data", "数据");

        private final String value;
        private final String name;

        private PRIVILEGE_TYPE(String value, String name) {
            this.value = value;
            this.name = name;
        }
    }

    /**
     * 打开方式（菜单）
     */
    @EnumType(name = "openMode")
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum OPEN_MODE {
        SELF("_self", "当前页"),
        BLANK("_blank", "空白页");

        private final String value;
        private final String name;
    }

    /**
     * 数据方案类型
     */
    @Getter
    enum DATA_SCHEME_TYPE {
        TEMPLATE("template", "模板"),
        CUSTOM("custom", "自定义");

        private final String value;
        private final String name;

        private DATA_SCHEME_TYPE(String value, String name) {
            this.value = value;
            this.name = name;
        }
    }

    /**
     * 数据方案字段类型
     */
    @EnumType(name = "schemeColumnType")
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum SCHEME_COLUMN_TYPE {
        STRING("String", "String"),
        INTEGER("Integer", "Integer"),
        BOOLEAN("Boolean", "Boolean"),
        DOUBLE("Double", "Double");

        private final String value;
        private final String name;
    }

    /**
     * 数据方案字段连接符
     */
    @EnumType(name = "columnConnector")
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum COLUMN_CONNECTOR {
        AND("and", "and"),
        OR("or", "or");

        private final String value;
        private final String name;
    }

    /**
     * 数据方案字段条件内容
     */
    @EnumType(name = "columnConditionContent")
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum COLUMN_CONDITION_CONTENT {
        ANY_TEXT("anyText", "任意文本"),
        CURRENT_USER("@currentUserId", "当前用户id"),
        CURRENT_UNIT("@currentUnitId", "当前用户单位id"),
        CURRENT_DEPT("@currentDeptId", "当前用户部门id");

        private final String value;
        private final String name;
    }

    /**
     * 数据方案字段条件符号
     */
    @EnumType(name = "columnConditionSymbol")
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum COLUMN_CONDITION_SYMBOL {
        EQ("=", "等于"),
        NE("!=", "不等于"),
        GT(">", "大于"),
        GE(">=", "大于等于"),
        LT("<", "小于"),
        LE("<=", "小于等于");

        private final String value;
        private final String name;
    }

    /**
     * 显示状态
     */
    @EnumType(name = "showStatus")
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum SHOW_STATUS {
        SHOW(1, "显示"),
        HIDE(0, "隐藏");

        private final Integer value;
        private final String name;
    }

}
