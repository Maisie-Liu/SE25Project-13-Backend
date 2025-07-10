package com.campus.trading.repository;

import com.campus.trading.entity.Favorite;
import com.campus.trading.entity.Item;
import com.campus.trading.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    /**
     * 根据用户ID查询用户的所有收藏
     * @param user 用户
     * @param pageable 分页参数
     * @return 收藏分页列表
     */
    Page<Favorite> findByUser(User user, Pageable pageable);
    
    /**
     * 根据用户和物品查询收藏
     * @param user 用户
     * @param item 物品
     * @return 收藏记录
     */
    Optional<Favorite> findByUserAndItem(User user, Item item);
    
    /**
     * 删除指定用户对指定物品的收藏
     * @param user 用户
     * @param item 物品
     */
    void deleteByUserAndItem(User user, Item item);
    
    /**
     * 检查用户是否收藏了指定物品
     * @param user 用户
     * @param item 物品
     * @return 是否收藏
     */
    boolean existsByUserAndItem(User user, Item item);

    // 统计某个物品被收藏的数量
    long countByItem(Item item);

    // 统计所有物品的收藏量，返回物品id和收藏数
    @org.springframework.data.jpa.repository.Query("SELECT f.item.id, COUNT(f.id) FROM Favorite f GROUP BY f.item.id")
    java.util.List<Object[]> countFavoritesGroupByItem();
} 