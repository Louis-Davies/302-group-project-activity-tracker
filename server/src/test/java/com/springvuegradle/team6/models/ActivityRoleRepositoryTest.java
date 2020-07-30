package com.springvuegradle.team6.models;

import com.springvuegradle.team6.controllers.TestDataGenerator;
import com.springvuegradle.team6.models.entities.*;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ActivityRoleRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@DataJpaTest
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class ActivityRoleRepositoryTest {

  @Autowired private TagRepository tagRepository;

  @Autowired private ActivityRepository activityRepository;

  @Autowired private ProfileRepository profileRepository;

  @Autowired private ActivityRoleRepository activityRoleRepository;

  private Activity activity;

  private Profile profile;

  @BeforeEach
  void setup() {
    // tagRepository.deleteAll();
    // activityRepository.deleteAll();

    Set<Email> emails = new HashSet<>();
    Email email = new Email("johnydoe1@gmail.com");
    emails.add(email);
    profile = new Profile();
    profile.setFirstname("John");
    profile.setLastname("Doe1");
    profile.setEmails(emails);
    profile.setDob("2010-01-01");
    profile.setPassword("Password1");
    profile.setGender("male");
    profile = profileRepository.save(profile);

    activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    activityRepository.save(activity);
  }

  void addRole(Profile profile, Activity activity, ActivityRoleType roleType) {
    ActivityRole role = new ActivityRole();
    role.setProfile(profile);
    role.setActivityRoleType(roleType);
    role.setActivity(activity);
    activityRoleRepository.save(role);
  }

  @Test
  void testGetCreator() {
    addRole(profile, activity, ActivityRoleType.Creator);
    List<ActivityRole> roles =
        activityRoleRepository.findByActivityRoleType(ActivityRoleType.Creator);
    org.junit.jupiter.api.Assertions.assertEquals(
        "Run at Hagley Park", roles.get(0).getActivity().getActivityName());
  }

  @Test
  void testActivityRolesByProfileId() {
    addRole(profile, activity, ActivityRoleType.Participant);
    List<ActivityRole> roles =
        activityRoleRepository.findByActivity_IdAndProfile_Id(activity.getId(), profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(1, roles.size());
  }

  @Test
  void testNoActivityRolesForAParticularProfile() {
    List<ActivityRole> roles =
        activityRoleRepository.findByActivity_IdAndProfile_Id(activity.getId(), profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(0, roles.size());
  }

  @Test
  void testMultipleActivityRolesForAParticularProfile() {
    // Profiles should not have more than one role in an activity, however just testing the function
    addRole(profile, activity, ActivityRoleType.Participant);
    addRole(profile, activity, ActivityRoleType.Creator);
    List<ActivityRole> roles =
        activityRoleRepository.findByActivity_IdAndProfile_Id(activity.getId(), profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(2, roles.size());
  }
}
