package cn.iocoder.yudao.module.leaf.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * Leaf 错误码枚举类
 */
public interface ErrorCodeConstants {

    // ========== 红包相关错误码 ==========
    ErrorCode RED_PACKET_NOT_FOUND = new ErrorCode(100001, "红包不存在");
    ErrorCode RED_PACKET_NOT_ACTIVE = new ErrorCode(100002, "红包未激活或已结束");
    ErrorCode RED_PACKET_EXPIRED = new ErrorCode(100003, "红包已过期");
    ErrorCode RED_PACKET_EMPTY = new ErrorCode(100004, "红包已被抢光");
    ErrorCode RED_PACKET_ALREADY_GRABBED = new ErrorCode(100005, "您已经抢过该红包");
    ErrorCode RED_PACKET_GRAB_LOCK_FAILED = new ErrorCode(100006, "获取红包锁失败，请稍后再试");
    ErrorCode RED_PACKET_GRAB_LOCK_INTERRUPTED = new ErrorCode(100007, "抢红包过程被中断");
    ErrorCode RED_PACKET_SHARD_GRAB_FAILED = new ErrorCode(100008, "抢红包分片失败");
    ErrorCode RED_PACKET_INTERNAL_ERROR = new ErrorCode(100009, "红包系统内部错误");
    ErrorCode RED_PACKET_TOKEN_INVALID = new ErrorCode(100010, "红包令牌无效");
    ErrorCode RED_PACKET_TOKEN_EXPIRED = new ErrorCode(100011, "红包令牌已过期");
    ErrorCode RED_PACKET_TOKEN_SIGNATURE_ERROR = new ErrorCode(100012, "红包令牌签名错误");
    ErrorCode RED_PACKET_SHARD_GENERATION_FAILED = new ErrorCode(100013, "红包分片生成失败");
    ErrorCode RED_PACKET_INVALID_PARAMETERS = new ErrorCode(100014, "红包参数无效");
}