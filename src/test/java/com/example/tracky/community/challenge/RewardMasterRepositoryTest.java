package com.example.tracky.community.challenge;

import com.example.tracky.community.challenge.domain.RewardMaster;
import com.example.tracky.community.challenge.enums.ChallengeTypeEnum;
import com.example.tracky.community.challenge.repository.RewardMasterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@Import(RewardMasterRepository.class)
@DataJpaTest
public class RewardMasterRepositoryTest {

    @Autowired
    private RewardMasterRepository challengeRepository;

    @Test
    public void findAllByType_test() {
        // given
        ChallengeTypeEnum challengeType = ChallengeTypeEnum.PRIVATE;

        // when
        List<RewardMaster> rewardMasters = challengeRepository.findAllByType(challengeType);

        // eye
        System.out.println("rewardMasters: " + rewardMasters.size());
    }
}
