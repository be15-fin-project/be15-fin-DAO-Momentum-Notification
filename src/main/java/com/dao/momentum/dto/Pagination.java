package com.dao.momentum.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Pagination {
    private final int currentPage;
    private final int totalPage;
    private final long totalItems;
}