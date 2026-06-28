package com.mall.common.result;

/**
 * 常用状态码常量
 *
 * 遵循 HTTP 状态码语义：
 *   2xx — 成功
 *   4xx — 客户端错误（参数校验失败、未授权等）
 *   5xx — 服务端错误（业务异常、系统异常）
 */
public final class ResultCode {

    private ResultCode() {}  // 工具类不允许实例化

    /** 成功 */
    public static final int SUCCESS = 200;

    /** 通用错误 */
    public static final int ERROR = 500;

    /** 未认证（未登录或 Token 过期） */
    public static final int UNAUTHORIZED = 401;

    /** 无权限（角色不满足要求） */
    public static final int FORBIDDEN = 403;

    /** 资源不存在 */
    public static final int NOT_FOUND = 404;

    /** 参数校验失败 */
    public static final int PARAM_ERROR = 400;
}
