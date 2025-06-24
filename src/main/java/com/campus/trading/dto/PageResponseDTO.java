package com.campus.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页响应数据传输对象
 *
 * @param <T> 数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 从Spring Data的Page对象构建分页响应对象
     *
     * @param page Spring Data分页对象
     * @param <T>  数据类型
     * @return 分页响应对象
     */
    public static <T> PageResponseDTO<T> from(Page<T> page) {
        return PageResponseDTO.<T>builder()
                .pageNum(page.getNumber() + 1)
                .pageSize(page.getSize())
                .total(page.getTotalElements())
                .pages(page.getTotalPages())
                .list(page.getContent())
                .build();
    }
} 