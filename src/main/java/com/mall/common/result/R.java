package com.mall.common.result;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 统一返回结果类
 * 项目中所有 Controller 接口都用此类包装返回数据
 *
 * 使用示例：
 * <pre>
 *   // 成功返回数据
 *   return R.ok(userList);
 *   // 失败返回错误信息
 *   return R.fail("用户名已存在");
 *   // 构建复杂响应
 *   return R.ok().message("操作成功").data(user);
 * </pre>
 *
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<T> {

    /** 状态码，200 表示成功，其他表示失败 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 返回数据 */
    private T data;

    // ==================== 成功响应 ====================

    public static <T> R<T> ok() {
        return new R<>(ResultCode.SUCCESS, "操作成功", null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(ResultCode.SUCCESS, "操作成功", data);
    }

    public static <T> R<T> ok(String message, T data) {
        return new R<>(ResultCode.SUCCESS, message, data);
    }

    // ==================== 失败响应 ====================

    public static <T> R<T> fail() {
        return new R<>(ResultCode.ERROR, "操作失败", null);
    }

    public static <T> R<T> fail(String message) {
        return new R<>(ResultCode.ERROR, message, null);
    }

    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, message, null);
    }

    // ==================== 链式构建 ====================

    public R<T> code(int code) {
        this.code = code;
        return this;
    }

    public R<T> message(String message) {
        this.message = message;
        return this;
    }

    public R<T> data(T data) {
        this.data = data;
        return this;
    }

    // ==================== 便捷判断 ====================

    /** 是否请求成功 */
    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS;
    }
}
