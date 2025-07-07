package com.example.tracky.community.leaderboard;

import lombok.Data;

import java.util.List;


public class LeaderBoardsResponse {

    // 최종 응답을 감싸는 메인 DTO
    @Data
    public static class MainDTO {
        private MyRankingDTO myRanking;
        private List<RankingListDTO> rankingList;

        public MainDTO(MyRankingDTO myRanking, List<RankingListDTO> rankingList) {
            this.myRanking = myRanking;
            this.rankingList = rankingList;
        }
    }

    @Data
    public static class MyRankingDTO {
        private Integer totalDistanceMeters; // 총 거리. 미터 단위
        private Integer rank;

        public MyRankingDTO(Integer totalDistanceMeters, Integer rank) {
            this.totalDistanceMeters = totalDistanceMeters;
            this.rank = rank;
        }
    }

    @Data
    public static class RankingListDTO {
        private String profileUrl; // 프로필 이미지 주소
        private String username; // 유저 이름
        private Integer totalDistanceMeters; // 총 거리. 미터 단위
        private Integer rank;
        private Integer userId;

        public RankingListDTO(String profileUrl, String username, Integer totalDistanceMeters, Integer rank, Integer userId) {
            this.profileUrl = profileUrl;
            this.username = username;
            this.totalDistanceMeters = totalDistanceMeters;
            this.rank = rank;
            this.userId = userId;
        }
    }
}
