package com.campus.trading.repository;

import com.campus.trading.entity.Comment;
import com.campus.trading.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemAndParentIdIsNullOrderByCreateTimeDesc(Item item);
    List<Comment> findByParentIdOrderByCreateTimeAsc(Long parentId);
    List<Comment> findByItem(Item item);
} 