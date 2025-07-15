package com.campus.trading.repository;

import com.campus.trading.entity.BuyRequestComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyRequestCommentRepository extends JpaRepository<BuyRequestComment, Long> {
    Page<BuyRequestComment> findByBuyRequestId(Long buyRequestId, Pageable pageable);
    void deleteByBuyRequestId(Long buyRequestId);
} 