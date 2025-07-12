package com.example.tracky.community.leaderboard;

import lombok.Data;

import java.util.List;


public class LeaderBoardsResponse {

    /**
     * 친구끼리 LeaderBoard DTO
     */
    @Data
    public static class LeaderBoardDTO {
        private MyRankingDTO myRanking;
        private List<RankingListDTO> rankingList;

        public LeaderBoardDTO(MyRankingDTO myRanking, List<RankingListDTO> rankingList) {
            this.myRanking = myRanking;
            this.rankingList = rankingList;
        }
    }

    /**
     * 챌린지에 참여한 사람들의 ChallengeLeadeBoardDTO
     */
    @Data
    public static class ChallengeLeaderBoardDTO {
        private List<RankingListDTO> rankingList;

        public ChallengeLeaderBoardDTO(List<RankingListDTO> rankingList) {
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
