package com.mall.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.auth.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper — 操作 user 表
 *
 * 继承 BaseMapper<User> 后，免费获得以下方法：
 *   insert(user)     → 插入一条记录
 *   deleteById(id)   → 根据ID删除
 *   updateById(user) → 根据ID更新
 *   selectById(id)   → 根据ID查询
 *   selectList(...)  → 条件查询多条
 *   selectPage(...)  → 分页查询
 *
 * 复杂查询可以在接口里写方法 + XML，但目前不需要。
 */
@Mapper  // 这个注解告诉 Spring：这是一个 MyBatis 的 Mapper，帮我管理它
public interface UserMapper extends BaseMapper<User> {
    // BaseMapper 已经提供了所有基础方法，这里暂时不需要写任何代码
    // 后续需要复杂查询时再加方法
}
