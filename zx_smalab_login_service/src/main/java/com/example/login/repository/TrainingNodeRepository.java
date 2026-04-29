package com.example.login.repository;

import com.example.login.entity.TrainingNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 实训节点数据访问层
 */
@Repository
public interface TrainingNodeRepository extends JpaRepository<TrainingNode, Long> {

    List<TrainingNode> findByTrainingIdAndIsDeletedOrderBySortOrder(Long trainingId, Integer isDeleted);

    List<TrainingNode> findByTrainingIdAndParentIdAndIsDeleted(Long trainingId, Long parentId, Integer isDeleted);
}
