package com.mall.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.dto.AdminLoginDTO;
import com.mall.admin.vo.AdminLoginVO;
import com.mall.admin.vo.DashboardVO;
import com.mall.auth.entity.User;

public interface AdminService {

    /** 管理员登录 */
    AdminLoginVO login(AdminLoginDTO dto);

    /** 数据看板 */
    DashboardVO dashboard();

    /** 用户列表 */
    Page<User> listUsers(int page, int size);

    /** 启用/禁用用户 */
    void toggleUserStatus(Long userId);
}
