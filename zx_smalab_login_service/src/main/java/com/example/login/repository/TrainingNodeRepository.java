package com.example.login.repository;

import com.example.login.entity.TrainingNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 实训节点数据访问层
 */
@Repository
public interface TrainingNodeRepository extends JpaRepository<TrainingNode, Long> {

    List<TrainingNode> findByTrainingIdAndIsDeletedOrderBySortOrder(Long trainingId, Integer isDeleted);

    List<TrainingNode> findByTrainingIdAndParentIdAndIsDeleted(Long trainingId, Long parentId, Integer isDeleted);

    @Modifying
    @Transactional
    @Query("DELETE FROM TrainingNode n WHERE n.trainingId = :trainingId")
    void deleteByTrainingId(Long trainingId);

    List<TrainingNode> findByTrainingIdAndIsDeleted(Long trainingId, Integer isDeleted);
}
