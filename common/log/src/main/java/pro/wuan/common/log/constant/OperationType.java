package pro.wuan.common.log.constant;

/**
 * @description 记录日志操作类型
 *
 */
public class OperationType {

	//新增类型
	public static final String OPERATION_ADD = "add";

	//删除类型
	public static final String OPERATION_DELETE = "delete";

	//更新类型
	public static final String OPERATION_UPDATE = "update";

	//查询类型
	public static final String OPERATION_QUERY = "query";

	//阅读类型
	public static final String OPERATION_VIEW = "view";

	//登录日志
	public static final String OPERATION_LOGIN = "login";

	//登出日志
	public static final String OPERATION_LOGOUT = "logout";

	//登出日志
	public static final String OPERATION_AUDIT = "audit";

	// 下载日志
    public static final String OPERATION_DOWNLOAD = "download";

    // 备份日志
    public static final String OPERATION_BACKUP = "backup";

	// 定时任务启用
	public static final String OPERATION_RESUME = "resume";

	// 定时任务暂停
	public static final String OPERATION_PAUSE = "pause";

	// 定时任务运行
	public static final String OPERATION_RUN = "run";

	// 异常日志
	public static final String OPERATION_EXCEPTION = "exception";

	//消息发送
	public static final String OPERATION_SEND = "send";
}
