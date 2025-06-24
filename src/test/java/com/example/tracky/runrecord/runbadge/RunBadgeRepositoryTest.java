package com.example.tracky.runrecord.runbadge;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.tracky.runrecord.runbadge.RunBadge;
import com.example.tracky.runrecord.runbadge.RunBadgeRepository;

@SpringBootTest
public class RunBadgeRepositoryTest {

    @Autowired
	private RunBadgeRepository runBadgeRepository;

	@Test
	void findAll_test() {
		List<RunBadge> runBadges = runBadgeRepository.findAll();
		for (RunBadge runBadge : runBadges) {
			System.out.println(runBadge.getName());
		}
	}
    
}

