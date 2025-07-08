package com.campus.trading.repository;

import com.campus.trading.entity.ItemDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemESRepository extends ElasticsearchRepository<ItemDocument, Long> {
    List<ItemDocument> findByNameContainingOrDescriptionContaining(String name, String description);
    // 可扩展更多自定义查询
} 