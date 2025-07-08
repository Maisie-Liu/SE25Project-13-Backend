package com.campus.trading.repository;

import com.campus.trading.entity.UserView;
import com.campus.trading.entity.User;
import com.campus.trading.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserViewRepository extends JpaRepository<UserView, Long> {
    List<UserView> findByUser(User user);
    List<UserView> findByItem(Item item);
    List<UserView> findByUserAndItem(User user, Item item);
} 