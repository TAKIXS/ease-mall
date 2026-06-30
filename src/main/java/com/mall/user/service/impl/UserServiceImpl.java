package com.mall.user.service.impl;

import com.mall.auth.entity.User;
import com.mall.auth.mapper.UserMapper;
import com.mall.common.exception.BusinessException;
import com.mall.user.dto.UserProfileDTO;
import com.mall.user.service.UserService;
import com.mall.user.vo.UserProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public UserProfileVO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        return toVO(user);
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, UserProfileDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");

        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());
        if (dto.getGender() != null) user.setGender(dto.getGender());

        userMapper.updateById(user);
    }

    private UserProfileVO toVO(User user) {
        return UserProfileVO.builder()
                .id(user.getId()).username(user.getUsername())
                .nickname(user.getNickname()).phone(user.getPhone())
                .email(user.getEmail()).avatar(user.getAvatar())
                .gender(user.getGender()).status(user.getStatus())
                .createTime(user.getCreateTime()).build();
    }
}
