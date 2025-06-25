package com.campus.trading.dto;

import java.util.List;

/**
 * 分页响应数据传输对象
 *
 * @param <T> 数据类型
 */
public class PageResponseDTO<T> {

    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页码
     */
    private int pageNum;

    /**
     * 每页大小
     */
    private int pageSize;

    /**
     * 总页数
     */
    private int pages;

    public PageResponseDTO() {
    }

    public PageResponseDTO(List<T> list, long total, int pageNum, int pageSize, int pages) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pages;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
} 