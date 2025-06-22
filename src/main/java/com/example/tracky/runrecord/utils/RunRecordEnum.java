package com.example.tracky.runrecord.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class RunRecordEnum {

    @Getter
    @RequiredArgsConstructor
    public static enum RunPlaceEnum {
        ROAD("도로"),
        TRACK("트랙"),
        TRAIL("산길");

        private final String name;
    }

}
