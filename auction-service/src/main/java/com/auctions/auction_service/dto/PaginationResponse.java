package com.auctions.auction_service.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationResponse<T> {

    private List<T> content;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;

    private boolean last;
}
