package com.example.tracky.runrecords;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RunRecordsService {

    private final RunRecordsRepository runRecordsRepository;

}