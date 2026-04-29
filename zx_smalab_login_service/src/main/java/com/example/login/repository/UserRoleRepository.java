package com.example.login.repository;

import com.example.login.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户角色数据访问层
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUserIdAndIsDeleted(Long userId, Integer isDeleted);

    Optional<UserRole> findByUserIdAndRoleIdAndIsDeleted(Long userId, Long roleId, Integer isDeleted);
}
