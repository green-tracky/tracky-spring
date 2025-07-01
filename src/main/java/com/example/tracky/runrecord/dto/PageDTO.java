package com.example.tracky.runrecord.dto;

import lombok.Data;

import java.util.List;

public class PageDTO {
    @Data
    public class PagedRecentRunsDTO {
        private List<RecentRunsDTO> content; // 현재 페이지의 러닝 기록 리스트
        private int page;                    // 현재 페이지 번호 (1부터 시작)
        private int size;                    // 페이지 당 개수
        private long totalElements;         // 전체 러닝 기록 수
        private int totalPages;             // 전체 페이지 수

        public PagedRecentRunsDTO(List<RecentRunsDTO> content, int page, int size, long totalElements, int totalPages) {
            this.content = content;
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }
    }
}
