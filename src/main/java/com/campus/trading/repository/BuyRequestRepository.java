package com.campus.trading.repository;

import com.campus.trading.entity.BuyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BuyRequestRepository extends JpaRepository<BuyRequest, Long>, JpaSpecificationExecutor<BuyRequest> {
    // 可扩展自定义查询
} 