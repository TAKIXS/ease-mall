package com.mall.user.service;

import com.mall.user.dto.UserProfileDTO;
import com.mall.user.vo.UserProfileVO;

public interface UserService {
    /** 获取个人信息 */
    UserProfileVO getProfile(Long userId);
    /** 更新个人信息 */
    void updateProfile(Long userId, UserProfileDTO dto);
}
