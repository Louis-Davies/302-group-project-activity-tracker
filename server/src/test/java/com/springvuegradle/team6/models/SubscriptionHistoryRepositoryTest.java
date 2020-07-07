package com.springvuegradle.team6.models;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;


import java.util.*;

@SpringBootTest
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class SubscriptionHistoryRepositoryTest {

    @Autowired private SubscriptionHistoryRepository subscriptionHistoryRepository;
    @Autowired private ActivityRepository activityRepository;
    @Autowired private ProfileRepository profileRepository;

    @BeforeEach
    void setup() {
        subscriptionHistoryRepository.deleteAll();
    }

    /*
    @Test
    void singleSubscriptionFindByProfile() {
        Set<Email> emails = new HashSet<>();
        Email email = new Email("johnydoe99@gmail.com");
        email.setPrimary(true);
        emails.add(email);
        Profile profile = new Profile();
        profile.setFirstname("John");
        profile.setLastname("Doe");
        profile.setEmails(emails);
        profile.setDob("2010-01-01");
        profile.setPassword("Password1");
        profile.setGender("male");
        profile = profileRepository.save(profile);

        Activity activity = new Activity();
        activity.setProfile(profile);
        activity.setActivityName("Run at Hagley Park");
        activity.setContinuous(true);
        activity = activityRepository.save(activity);

        SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
        subscriptionHistory.setActivity(activity);
        subscriptionHistory.setProfile(profile);
        subscriptionHistory.setSubscribe(true);
        subscriptionHistory.setTimeDate(new Date());
        subscriptionHistory = subscriptionHistoryRepository.save(subscriptionHistory);

        Profile profile1 = profileRepository.findByEmailsContains(email);
        Set<SubscriptionHistory> subscriptionHistories = subscriptionHistoryRepository.findByProfile_id(profile1.getId());
        org.junit.jupiter.api.Assertions.assertEquals(1, subscriptionHistories.size());
    }

    @Test
    void singleSubscriptionFindByActivity() {
        Set<Email> emails = new HashSet<>();
        Email email = new Email("secondemail@gmail.com");
        email.setPrimary(true);
        emails.add(email);
        Profile profile = new Profile();
        profile.setFirstname("Gon");
        profile.setLastname("Freecss");
        profile.setEmails(emails);
        profile.setDob("2010-01-01");
        profile.setPassword("Password1");
        profile.setGender("male");
        profile = profileRepository.save(profile);

        Activity activity = new Activity();
        activity.setProfile(profile);
        activity.setActivityName("Fight at heavens arena");
        activity.setContinuous(true);
        activity = activityRepository.save(activity);

        SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
        subscriptionHistory.setActivity(activity);
        subscriptionHistory.setProfile(profile);
        subscriptionHistory.setSubscribe(true);
        subscriptionHistory.setTimeDate(new Date());
        subscriptionHistory = subscriptionHistoryRepository.save(subscriptionHistory);

        Profile profile1 = profileRepository.findByEmailsContains(email);
        Set<SubscriptionHistory> subscriptionHistories = subscriptionHistoryRepository
                .findByActivity_id(activityRepository.findByProfile_IdAndArchivedFalse(profile1.getId()).get(0).getId());
        org.junit.jupiter.api.Assertions.assertEquals(1, subscriptionHistories.size());
    }*/
}
