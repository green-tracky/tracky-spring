package com.example.tracky.runrecord.dto;

import lombok.Data;

@Data
public class PageDTO {
    private Integer size;
    private Integer totalCount;
    private Integer totalPage;
    private Integer current;
    private Boolean isFirst;
    private Boolean isLast;

    public PageDTO(Integer totalCount, Integer current) {
        this.size = 3;
        this.totalCount = totalCount;
        this.totalPage = (int) Math.ceil((double) totalCount / size);
        this.current = (current != null && current > 0) ? current : 1;
        this.isFirst = current == 1;
        this.isLast = current >= totalPage;
    }
}
