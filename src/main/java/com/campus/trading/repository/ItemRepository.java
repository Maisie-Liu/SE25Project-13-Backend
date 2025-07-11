package com.campus.trading.repository;

import com.campus.trading.entity.Item;
import com.campus.trading.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 物品数据访问接口
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    /**
     * 根据用户查询物品列表
     *
     * @param user     用户
     * @param pageable 分页参数
     * @return 物品分页列表
     */
    Page<Item> findByUser(User user, Pageable pageable);

    /**
     * 根据状态查询物品列表
     *
     * @param status   状态
     * @param pageable 分页参数
     * @return 物品分页列表
     */
    Page<Item> findByStatus(Integer status, Pageable pageable);

    /**
     * 根据分类ID查询物品列表
     *
     * @param categoryId 分类ID
     * @param pageable   分页参数
     * @return 物品分页列表
     */
    Page<Item> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * 根据名称模糊查询物品列表
     *
     * @param name     名称
     * @param pageable 分页参数
     * @return 物品分页列表
     */
    Page<Item> findByNameContaining(String name, Pageable pageable);

    /**
     * 根据价格区间查询物品列表
     *
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param pageable 分页参数
     * @return 物品分页列表
     */
    Page<Item> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * 根据热度排序查询物品列表
     *
     * @param pageable 分页参数
     * @return 物品分页列表
     */
    Page<Item> findAllByOrderByPopularityDesc(Pageable pageable);

    /**
     * 根据用户和状态查询物品列表
     *
     * @param user     用户
     * @param status   状态
     * @param pageable 分页参数
     * @return 物品分页列表
     */
    Page<Item> findByUserAndStatus(User user, Integer status, Pageable pageable);

    /**
     * 根据关键字搜索物品
     *
     * @param keyword  关键字
     * @param pageable 分页参数
     * @return 物品分页列表
     */
    @Query("SELECT i FROM Item i WHERE i.name LIKE %:keyword% OR i.description LIKE %:keyword%")
    Page<Item> searchByKeyword(String keyword, Pageable pageable);

    /**
     * 根据状态和热度排序查询物品列表
     *
     * @param status   状态
     * @param pageable 分页参数
     * @return 物品分页列表
     */
    Page<Item> findByStatusOrderByPopularityDesc(Integer status, Pageable pageable);

    /**
     * 统计指定状态的商品数量
     *
     * @param status 商品状态
     * @return 商品数量
     */
    long countByStatus(Integer status);

    /**
     * 查询上架且有库存的商品
     */
    Page<Item> findByStatusAndStockGreaterThan(Integer status, Integer stock, Pageable pageable);

    /**
     * 查询分类下上架且有库存的商品
     */
    Page<Item> findByCategoryIdAndStatusAndStockGreaterThan(Long categoryId, Integer status, Integer stock, Pageable pageable);

    // 按收藏量降序分页查询上架且有库存的商品
    @Query(value = "SELECT i.* FROM t_item i LEFT JOIN t_favorite f ON i.id = f.item_id WHERE i.status = 1 AND i.stock > 0 GROUP BY i.id ORDER BY COUNT(f.id) DESC, i.create_time DESC",
           countQuery = "SELECT COUNT(DISTINCT i.id) FROM t_item i WHERE i.status = 1 AND i.stock > 0",
           nativeQuery = true)
    Page<Item> findAllOrderByFavoriteCountDesc(Pageable pageable);
    
    /**
     * 查询用户发布的所有商品（按创建时间倒序）
     */
    List<Item> findByUserOrderByCreateTimeDesc(User user);

    /**
     * 根据分类ID列表查询物品列表，并根据状态和热度排序
     *
     * @param categoryIds 分类ID列表
     * @param status      状态
     * @param pageable    分页参数
     * @return 物品分页列表
     */
    Page<Item> findByCategoryIdInAndStatusOrderByPopularityDesc(List<Long> categoryIds, Integer status, Pageable pageable);
} 