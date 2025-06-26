package com.example.tracky.runrecord.runsegment.runcoordinate;

import com.example.tracky._core.utils.DateTimeUtils;
import lombok.Data;

public class RunCoordinateResponse {

    /**
     * private Double lat;
     * <p>
     * private Double lon;
     * <p>
     * private Timestamp createdAt;
     */
    @Data
    public static class DTO {
        private Integer id;
        private Double lat;
        private Double lon;
        private String createdAt;

        public DTO(RunCoordinate runCoordinate) {
            this.id = runCoordinate.getId();
            this.lat = runCoordinate.getLat();
            this.lon = runCoordinate.getLon();
            this.createdAt = DateTimeUtils.toDateTimeString(runCoordinate.getCreatedAt());
        }

    }

}
