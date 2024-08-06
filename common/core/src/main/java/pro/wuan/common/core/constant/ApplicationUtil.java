package pro.wuan.common.core.constant;


/**
 * 应用全局常量定义
 */
public class ApplicationUtil {

    /**
     * 用户性别
     */
    public static class OrgUserGender {
        /**
         * 男
         */
        public static final int MAN = 1;
        /**
         * 女
         */
        public static final int WOMAN = 2;
        /**
         * 未知
         */
        public static final int UNKNOW = 0;
    }

    /**
     * 用户类型
     */
    public static class OrgUserType {
        /**
         * 管理员
         */
        public static final int MANAGER = 1;
        /**
         * 普通员工
         */
        public static final int EMPLOYEE = 2;

    }

    /**
     * 用户官衔
     */
    public static class OrgUserOfficial {
        /**
         * 正职领导
         */
        public static final String LEADER = "1";
        /**
         * 副职领导
         */
        public static final String LEADERF = "2";
        /**
         * 其它
         */
        public static final String OTHER = "3";

    }


    /**
     * 机构关联数据类型
     */
    public static class OrgElementRefType {
        /**
         * 机构
         */
        public static final int ORGANIZATION = 1;
        /**
         * 部门
         */
        public static final int DEPARTMENT = 2;
        /**
         * 角色
         */
        public static final int ROLE = 8;
        /**
         * 人员
         */
        public static final int USER = 4;
        /**
         * 职级
         */
        public static final int RANK = 16;
        /**
         * 群组
         */
        public static final int GROUP = 32;

    }



    /**
     * 系统消息表类型
     */
    public static class SysMessageType {
        /**
         * 后台管理员发送
         */
        public static final int MANAGER_PUSH = 1;
        /**
         * 系统自动推送
         */
        public static final int SYSTEM_PUSH = 2;
    }

    /**
     * 删除类型
     */
    public static class DeletedType {
        /**
         * 已删除
         */
        public static final boolean DELETED = true;
        /**
         * 为删除
         */
        public static final boolean UN_DELETED = false;
    }

    /**
     * 是否
     */
    public static class YesOrNo {
        /**
         * 是
         */
        public static final String YES = "1";
        /**
         * 否
         */
        public static final String NO = "0";
    }

    public static class DefualtId {
        /**
         * 默认ID
         */
        public static final Long PARENTID = -1L;

    }

    /**
     * 默认密码
     */
    public static final String DEFAULT_PASSWORD = "888888";

    /**
     * 超级管理员登录名
     */
    public static final String SUPER_ADMIN = "SuperAdmin";

    /**
     * 南昌铁路运输法院管理员
     */
    public static final String NCTLYSFY = "nctlysfy";

    /**
     * 南昌铁路运输中级法院管理员
     */
    public static final String NCTLYSZJFY = "nctlyszjfy";

    /**
     * 单位管理员角色id
     */
    public static final Long UNIT_ADMIN_ROLE_ID = 1L;

    /**
     * 江西省高级人民法院代码
     */
    public static final Long PARENT_ID = 1700L;

    /**
     * 南昌铁路运输法院代码
     */
    public static final Long JC_PARENT_ID = 1814L;

    /**
     * 南昌铁路运输中级法院代码
     */
    public static final Long ZJ_PARENT_ID = 1813L;

    /**
     * 字典
     */
    public static class dicItem {
        /**
         * 提醒
         */
        public static final String TX = "TX";
        /**
         * 颜色
         */
        public static final String COLOR = "color";
        /**
         * 法院代码
         */
        public static final String FYDM = "FYDM";

        /**
         * 案件类型
         */
        public static final String AJLX = "AJLX";

        /**
         * 单位类型
         */
        public static final String UNITTYPE = "unitCaseType";

        /**
         * 地域
         */
        public static final String UNITTERR = "unitTerrFirst";

        /**
         * 地域
         */
        public static final String UNITSECOND = "unitTerrSecond";
        /**
         * 原告类型
         */
        public static final String YGTYPE = "ygType";
        /**
         * 被告类型
         */
        public static final String BGTYPE = "bgType";
        /**
         *
         */
        public static final String orgDeptCode = "orgDeptCode";

        /**
         * 行为种类
         */
        public static final String xwzlType = "xwzlType";
        /**
         * 案由一级
         */
        public static final String ayyjType = "ayyjType";
        /**
         * 月份
         */
        public static final String month = "month";
    }

    /**
     * 资源类型  '1'代表模块 '2'代表业务 '3'代表功能
     */
    public static class ResourceType {
        /**
         * 模块
         */
        public static final String TYPE_MODULE = "1";
        /**
         * 业务
         */
        public static final String CLIENT_BUSINESS = "2";
        /**
         * 功能
         */
        public static final String CLIENT_FUNCTION = "3";
    }

    /**
     * 资源所属范围  '1'代表后台管理资源 '2'代表客户端应用
     */
    public static class ResourceScope {
        /**
         * 后台管理系统
         */
        public static final String MANAGEMENT_SYSTEM = "1";
        /**
         * 客户端系统
         */
        public static final String CLIENT_SYSTEM = "2";
    }

    /**
     * 选择面板参数常量
     */
    public static class SelectPlaneParam {
        /**
         * 查询全部
         */
        public static final String TYPE_ALL = "all";
        /**
         * 查询本单位
         */
        public static final String TYPE_SELF = "self";
        /**
         * 查询本单位及下级单位
         */
        public static final String TYPE_NEXT = "next";
    }

    /**
     * 角色类型  '1'集团角色 '2'单位角色
     */
    public static class OrgRoleType {
        /**
         * 集团角色
         */
        public static final String ROLE_TYPE_GROUP = "1";
        /**
         * 单位角色
         */
        public static final String ROLE_TYPE_UNIT = "2";
    }

    /**
     * @Description: 内部或外部机构
     * @Author: ivanchen
     * @Time: 2020/10/21 4:08 下午
     */
    public static class OrgInsideOutside{
        // 内部机构
        public static final String ORG_INSIDE = "1";

        // 外部机构
        public static final String ORG_OUTSIDE = "2";
    }

    public static class OrgImValue{
        // url
        public static final String IM_URL = "-1";

        // fromip
        public static final String IM_FROM_IP = "-1";

        // status
        public static final String IM_STATUS = "-1";
    }

    /**
     * @Description: 授权类型
     * @Author: wbb
     */
    public static class LookDataType{
        // 本单位
        public static final String MY_ORG = "1";

        // 本单位及下属
        public static final String MY_ORG_SUB = "2";

        // 指定单位
        public static final String ZD_ORG = "3";

        // 指定单位及下属
        public static final String ZD_ORG_SUB = "4";
    }


}
