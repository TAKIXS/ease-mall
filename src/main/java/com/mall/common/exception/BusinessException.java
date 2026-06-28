package com.mall.common.exception;

import com.mall.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 * 业务逻辑出现问题时抛出此异常，由 GlobalExceptionHandler 统一捕获处理
 *
 * 使用示例：
 * <pre>
 *   if (user == null) {
 *       throw new BusinessException("用户不存在");
 *   }
 *   if (stock < quantity) {
 *       throw new BusinessException(ResultCode.ERROR, "库存不足");
 *   }
 * </pre>
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 状态码，默认 500 */
    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.ERROR;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
