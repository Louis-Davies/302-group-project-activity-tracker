package com.springvuegradle.team6.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityRole;
import com.springvuegradle.team6.models.entities.ActivityRoleType;
import com.springvuegradle.team6.models.entities.ActivityType;
import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.Location;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.entities.Tag;
import com.springvuegradle.team6.models.entities.VisibilityType;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ActivityRoleRepository;
import com.springvuegradle.team6.models.repositories.LocationRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.TagRepository;
import com.springvuegradle.team6.services.ExternalAPI.GoogleAPIServiceMocking;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SearchActivityControllerTest {

  @Autowired private ProfileRepository profileRepository;

  @Autowired private ActivityRepository activityRepository;

  @Autowired private TagRepository tagRepository;

  @Autowired private ActivityRoleRepository activityRoleRepository;

  @Autowired private LocationRepository locationRepository;

  @Autowired private MockMvc mvc;

  @Autowired private GoogleAPIServiceMocking googleAPIService;

  private int id;

  private MockHttpSession session;

  private Profile profile;

  @AfterAll
  void tearDown(@Autowired DataSource dataSource) throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("tearDown.sql"));
    }
  }

  @BeforeAll
  void setup() throws Exception {
    session = new MockHttpSession();

    Set<Email> emails = new HashSet<>();
    Email email = new Email("poly@pocket.com");
    email.setPrimary(true);
    emails.add(email);
    profile = new Profile();
    profile.setFirstname("Poly");
    profile.setLastname("Pocket");
    profile.setEmails(emails);
    profile.setDob("2010-01-01");
    profile.setPassword("Password1");
    profile.setGender("female");
    profile = profileRepository.save(profile);
    String login_url = "/login/";
    mvc.perform(
            post(login_url)
                .content(
                    "{\n"
                        + "\t\"email\" : \"poly@pocket.com\",\n"
                        + "\t\"password\": \"Password1\"\n"
                        + "}")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Set<Email> emails2 = new HashSet<>();
    Email email2 = new Email("johnydoe99@gmail.com");
    email.setPrimary(true);
    emails.add(email);
    Profile profile2 = new Profile();
    profile2.setFirstname("John");
    profile2.setLastname("Doe");
    profile2.setEmails(emails2);
    profile2.setDob("2010-01-01");
    profile2.setPassword("Password1");
    profile2.setGender("male");
    profileRepository.save(profile2);

    Tag running = new Tag("running");
    tagRepository.save(running);

    Tag canterbury = new Tag("canterbury");
    tagRepository.save(canterbury);

    Tag walking = new Tag("walking");
    tagRepository.save(walking);

    Activity activity = new Activity();
    activity.setActivityName("Kaikoura Coast Track race");
    activity.setDescription("A big and nice race on a lovely peninsula");
    Set<ActivityType> activityTypes = new HashSet<>();
    activityTypes.add(ActivityType.Run);
    activity.setActivityTypes(activityTypes);
    activity.setContinuous(true);
    activity.setVisibilityType(VisibilityType.Public);
    activity.setProfile(profile);
    Set<Tag> tags = new HashSet<>();
    tags.add(running);
    activity.setTags(tags);
    // Kaikoura
    Location location = new Location(-42.3994929, 173.6800878);
    locationRepository.save(location);
    activity.setLocation(location);
    activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setActivityName("Walking at Hagley Park");
    Set<ActivityType> activityTypes1 = new HashSet<>();
    activityTypes1.add(ActivityType.Walk);
    activity1.setActivityTypes(activityTypes1);
    activity1.setContinuous(false);
    activity1.setVisibilityType(VisibilityType.Public);
    activity1.setStartTime(
        LocalDateTime.parse(
            "2020-04-01T15:50:41+1300", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
    activity1.setEndTime(
        LocalDateTime.parse(
            "2020-04-01T15:50:41+1300", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
    activity1.setProfile(profile);
    Set<Tag> tags1 = new HashSet<>();
    tags1.add(walking);
    activity1.setTags(tags1);
    // 290 Blenheim Road
    Location location1 = new Location(-43.5383822, 172.5843095);
    locationRepository.save(location1);
    activity1.setLocation(location1);
    activityRepository.save(activity1);

    Activity activity2 = new Activity();
    activity2.setActivityName("Running at Hagley Park");
    Set<ActivityType> activityTypes2 = new HashSet<>();
    activityTypes2.add(ActivityType.Run);
    activity2.setActivityTypes(activityTypes2);
    activity2.setContinuous(false);
    activity2.setVisibilityType(VisibilityType.Public);
    activity2.setStartTime(
        LocalDateTime.parse(
            "2020-04-03T15:50:41+1300", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
    activity2.setEndTime(
        LocalDateTime.parse(
            "2020-04-07T15:50:41+1300", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
    activity2.setProfile(profile);
    Set<Tag> tags2 = new HashSet<>();
    tags2.add(running);
    activity2.setTags(tags2);
    // 116 Blenheim Road
    Location location2 = new Location(-43.53702, 172.6006563);
    locationRepository.save(location2);
    activity2.setLocation(location2);
    activityRepository.save(activity2);

    Activity activity3 = new Activity();
    activity3.setActivityName("Canterbury Triathlon");
    Set<ActivityType> activityTypes3 = new HashSet<>();
    activityTypes3.add(ActivityType.Run);
    activityTypes3.add(ActivityType.Bike);
    activityTypes3.add(ActivityType.Swim);
    activity3.setActivityTypes(activityTypes3);
    activity3.setContinuous(false);
    activity3.setVisibilityType(VisibilityType.Public);
    activity3.setStartTime(
        LocalDateTime.parse(
            "2020-04-05T15:50:41+1300", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
    activity3.setEndTime(
        LocalDateTime.parse(
            "2020-04-07T16:50:41+1300", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
    activity3.setProfile(profile);
    Set<Tag> tags3 = new HashSet<>();
    tags3.add(running);
    tags3.add(canterbury);
    activity3.setTags(tags3);
    // 216 Blenheim Road
    Location location3 = new Location(-43.5376602, 172.5912131);
    locationRepository.save(location3);
    activity3.setLocation(location3);
    activityRepository.save(activity3);

    Activity activity4 = new Activity();
    activity4.setActivityName("Private activity");
    activity4.setContinuous(true);
    activity4.setVisibilityType(VisibilityType.Private);
    activity4.setProfile(profile2);
    activityRepository.save(activity4);

    Activity activity5 = new Activity();
    activity5.setActivityName("Restricted activity");
    activity5.setContinuous(true);
    activity5.setVisibilityType(VisibilityType.Restricted);
    activity5.setProfile(profile2);
    activityRepository.save(activity5);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity5);
    activityRole.setActivityRoleType(ActivityRoleType.Access);
    activityRole.setProfile(profile);
    activityRoleRepository.save(activityRole);

    // Setup api mocking of google api to prevent error on calls.
    googleAPIService.mockReverseGeocode("controllers/46BalgaySt_OK.json");
  }

  @Test
  void getActivitiesCountReturnOneActivity() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities/count?name=race").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    org.junit.jupiter.api.Assertions.assertEquals(1, Integer.parseInt(response));
  }

  @Test
  void getActivitiesCountReturnTwoActivity() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities/count?name=Hagley").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    org.junit.jupiter.api.Assertions.assertEquals(2, Integer.parseInt(response));
  }

  @Test
  void getActivitiesByNameReturnOneActivity() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?name=Kaikoura%20Coast%20Track%20rac")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void getActivitiesByPartialNameReturnOneActivity() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?name=race").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void getActivitiesByOneLetterReturnThreeActivities() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?name=r").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(3, arr.length());
  }

  @Test
  void getActivitiesByTwoLettersReturnOneActivity() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?name=ru").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void getActivitiesByTwoLettersReturnTwoActivity() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?name=Ha").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());
  }

  @Test
  void getActivitiesByPartialNameReturnTwoActivities() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?name=Hagley").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());
  }

  @Test
  void getActivitiesByPartialNameResultsSizeMatchesCountReturnTwoActivities() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?name=Hagley").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());
    String response2 =
        mvc.perform(MockMvcRequestBuilders.get("/activities/count?name=Hagley").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    Integer count = Integer.parseInt(response2);
    org.junit.jupiter.api.Assertions.assertEquals(2, count);
  }

  @Test
  void getActivitiesByActivityTypesWithOrReturnFourActivities() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?types=run%20walk&types-method=or")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(4, arr.length());
  }

  @Test
  void getActivitiesByActivityTypesWithOrResultsSizeMatchesCountReturnFourActivities()
      throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?types=run%20walk&types-method=or")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(4, arr.length());

    String response2 =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities/count?types=run%20walk&types-method=or")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    Integer count = Integer.parseInt(response2);
    org.junit.jupiter.api.Assertions.assertEquals(4, count);
  }

  @Test
  void getActivitiesWithOffsetReturnTwoResults() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?types=run%20walk&types-method=or&offset=2")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());
  }

  @Test
  void getActivitiesWithLimitReturnThreeResults() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?types=run%20walk&types-method=or&limit=3")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(3, arr.length());
  }

  @Test
  void getActivitiesWithOffsetThreeReturnOneResults() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?types=run%20walk&types-method=or&offset=3")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void getActivitiesWithLimitWithOffsetReturnTwoResultsCountReturnsFour() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities?types=run%20walk&types-method=or&limit=2&offset=1")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());

    String response2 =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities/count?types=run%20walk&types-method=or")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    Integer count = Integer.parseInt(response2);
    org.junit.jupiter.api.Assertions.assertEquals(4, count);
  }

  @Test
  void getActivitiesWithLimitWithOffsetReturnOneResults() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities?types=run%20walk&types-method=or&limit=1&offset=1")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void getActivitiesByActivityTypesWithAndReturnOneActivity() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?types=run%20swim%20bike&types-method=and")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void getActivitiesByHashtagsWithOrReturnFourActivities() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities?hashtags=running%20walking&hashtags-method=or")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(4, arr.length());
  }

  @Test
  void getActivitiesByHashtagsWithAndReturnOneActivity() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities?hashtags=running%20canterbury&hashtags-method=and")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void getActivitiesWithContinuousReturnTwoActivity() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?time=continuous").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());
  }

  @Test
  void getActivitiesWithDurationReturnThreeActivity() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?time=duration").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(3, arr.length());
  }

  @Test
  void getActivitiesWithDurationWithStartDateReturnTwoActivity() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?time=duration&start-date=2020-04-03")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());
  }

  @Test
  void getActivitiesWithDurationWithEndDateReturnTwoActivity() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?time=duration&start-date=2020-04-03")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());
  }

  @Test
  void getActivitiesWithDurationWithStartDateWithEndDateReturnOneActivity() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities?time=duration&start-date=2020-04-01&end-date=2020-04-02")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void getActivitiesWithDurationWithStartDateWithEndDateReturnNoActivities() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities?time=duration&start-date=2030-04-01&end-date=2030-04-02")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(0, arr.length());
  }

  @Test
  void getActivitiesCanNotViewPrivateActivitiesReturnNoActivities() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?name=private").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(0, arr.length());
  }

  @Test
  void getActivitiesCanViewRestrictedActivitiesWithAccessReturnOneActivities() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?name=restricted").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN"})
  void getActivitiesAdminCanViewAllActivitiesReturnThreeActivities() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?time=continuous").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(3, arr.length());
  }

  @Test
  void getActivitiesSearchWithMultipleOptionsReturnOneResult() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?name=hagley&types=walk").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void getActivitiesByLocationReturnOneResult() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?lon=173.6800878&lat=-42.3994929&radius=1")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void getActivitiesByLocationReturnMultipleResults() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?lon=172.5912131&lat=-43.5376602&radius=2")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(3, arr.length());
  }

  @Test
  void getActivitiesByLocationReturnMultipleResultsInCorrectOrder() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities?lon=172.5912131&lat=-43.5376602&radius=2") // 216 Blenheim Road
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(3, arr.length());
    org.junit.jupiter.api.Assertions.assertEquals(
        -43.5376602, arr.getJSONObject(0).getJSONObject("location").getDouble("latitude"));
    org.junit.jupiter.api.Assertions.assertEquals(
        172.5912131, arr.getJSONObject(0).getJSONObject("location").getDouble("longitude"));
    org.junit.jupiter.api.Assertions.assertEquals(
        -43.5383822, arr.getJSONObject(1).getJSONObject("location").getDouble("latitude"));
    org.junit.jupiter.api.Assertions.assertEquals(
        172.5843095, arr.getJSONObject(1).getJSONObject("location").getDouble("longitude"));
    org.junit.jupiter.api.Assertions.assertEquals(
        -43.53702, arr.getJSONObject(2).getJSONObject("location").getDouble("latitude"));
    org.junit.jupiter.api.Assertions.assertEquals(
        172.6006563, arr.getJSONObject(2).getJSONObject("location").getDouble("longitude"));
  }

  @Test
  void getActivitiesByLocationNoRadiusUsesDefaultRadiusReturnOneResult() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?lon=173.6800878&lat=-42.3994929")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void getActivitiesByLocationInvalidRadiusOverMaxReturnBadRequest() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?lon=173.6800878&lat=-42.3994929&radius=201")
                    .session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationInvalidRadiusOnMaxReturnOk() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?lon=173.6800878&lat=-42.3994929&radius=200")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationInvalidRadiusUnderMinReturnBadRequest() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?lon=173.6800878&lat=-42.3994929&radius=0")
                    .session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationInvalidRadiusOnMinReturnOk() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?lon=173.6800878&lat=-42.3994929&radius=1")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationInvalidRadiusIsDecimalReturnBadRequest() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?lon=173.6800878&lat=-42.3994929&radius=1.2")
                    .session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationWithLatitudeNoLongitudeReturnBadRequest() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?lat=0").session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationWithLongitudeNoLatitudeReturnBadRequest() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?lon=0").session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationInvalidLongitudeOverMaxReturnBadRequest() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?lon=181&lat=0&radius=1").session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationInvalidLongitudeUnderMinReturnBadRequest() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?lon=-181&lat=0&radius=1").session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationInvalidLongitudeOnMaxReturnOk() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?lon=180&lat=0&radius=1").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationInvalidLongitudeOnMinReturnOk() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?lon=-180&lat=0&radius=1").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationInvalidLatitudeOverMaxReturnBadRequest() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?lon=0&lat=91&radius=1").session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationInvalidLatitudeUnderMinReturnBadRequest() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?lon=0&lat=-91&radius=1").session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationInvalidLatitudeOnMaxReturnOk() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?lon=0&lat=90&radius=1").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationInvalidLatitudeOnMinReturnOk() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?lon=0&lat=-90&radius=1").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByHashtagsInvalidMethodReturnBadRequest() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities?hashtags=running%20walking&hashtags-method=not")
                    .session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByActivityTypesInvalidMethodReturnBadRequest() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?types=run%20walk&types-method=not")
                    .session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesOffsetUnderMinReturnBadRequest() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?name=run&offset=-1").session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesOffsetOnMinReturnOk() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?name=run&offset=0").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesLimitUnderMinReturnBadRequest() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?name=run&limit=-1").session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesLimitOnMinReturnOk() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?name=run&limit=0").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByTimeInvalidReturnBadRequest() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?time=nothing").session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByDurationStartDateInvalidFormatReturnBadRequest() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?time=duration&start-date=20201010")
                    .session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByDurationEndDateInvalidFormatReturnBadRequest() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?time=duration&end-date=20201010")
                    .session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByDurationStartDateInvalidDateReturnBadRequest() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?time=duration&start-date=2020-20-10")
                    .session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByDurationEndDateInvalidDateReturnBadRequest() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?time=duration&end-date=2020-20-10")
                    .session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitySortByEarliestStartDateReturnInCorrectOrder() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?time=duration&sort=EARLIEST_START_DATE")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(3, arr.length());
    LocalDateTime startDate =
        LocalDateTime.parse(
            arr.getJSONObject(0).getString("startTime"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    LocalDateTime startDate1 =
        LocalDateTime.parse(
            arr.getJSONObject(1).getString("startTime"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    LocalDateTime startDate2 =
        LocalDateTime.parse(
            arr.getJSONObject(2).getString("startTime"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    org.junit.jupiter.api.Assertions.assertTrue(
        startDate.isBefore(startDate1) || startDate.isEqual(startDate1));
    org.junit.jupiter.api.Assertions.assertTrue(
        startDate1.isBefore(startDate2) || startDate1.isEqual(startDate2));
  }

  @Test
  void getActivitySortByLatestStartDateReturnInCorrectOrder() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?time=duration&sort=latest_start_date")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(3, arr.length());
    LocalDateTime startDate =
        LocalDateTime.parse(
            arr.getJSONObject(0).getString("startTime"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    LocalDateTime startDate1 =
        LocalDateTime.parse(
            arr.getJSONObject(1).getString("startTime"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    LocalDateTime startDate2 =
        LocalDateTime.parse(
            arr.getJSONObject(2).getString("startTime"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    org.junit.jupiter.api.Assertions.assertTrue(
        startDate.isAfter(startDate1) || startDate.isEqual(startDate1));
    org.junit.jupiter.api.Assertions.assertTrue(
        startDate1.isAfter(startDate2) || startDate1.isEqual(startDate2));
  }

  @Test
  void getActivitySortByEarliestEndDateReturnInCorrectOrder() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?time=duration&sort=EARLIEST_end_DATE")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(3, arr.length());
    LocalDateTime endDate =
        LocalDateTime.parse(
            arr.getJSONObject(0).getString("endTime"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    LocalDateTime endDate1 =
        LocalDateTime.parse(
            arr.getJSONObject(1).getString("endTime"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    LocalDateTime endDate2 =
        LocalDateTime.parse(
            arr.getJSONObject(2).getString("endTime"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    org.junit.jupiter.api.Assertions.assertTrue(
        endDate.isBefore(endDate1) || endDate.isEqual(endDate1));
    org.junit.jupiter.api.Assertions.assertTrue(
        endDate1.isBefore(endDate2) || endDate1.isEqual(endDate2));
  }

  @Test
  void getActivitySortByLatestEndDateReturnInCorrectOrder() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?time=duration&sort=latest_end_date")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(3, arr.length());
    LocalDateTime endDate =
        LocalDateTime.parse(
            arr.getJSONObject(0).getString("endTime"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    LocalDateTime endDate1 =
        LocalDateTime.parse(
            arr.getJSONObject(1).getString("endTime"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    LocalDateTime endDate2 =
        LocalDateTime.parse(
            arr.getJSONObject(2).getString("endTime"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    org.junit.jupiter.api.Assertions.assertTrue(
        endDate.isAfter(endDate1) || endDate.isEqual(endDate1));
    org.junit.jupiter.api.Assertions.assertTrue(
        endDate1.isAfter(endDate2) || endDate1.isEqual(endDate2));
  }

  @Test
  void getActivitiesSortByClosestLocationReturnInCorrectOrder() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities?lon=172.5912131&lat=-43.5376602&radius=2&sort=closest_location") // 216 Blenheim Road
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(3, arr.length());
    org.junit.jupiter.api.Assertions.assertEquals(
        -43.5376602, arr.getJSONObject(0).getJSONObject("location").getDouble("latitude"));
    org.junit.jupiter.api.Assertions.assertEquals(
        172.5912131, arr.getJSONObject(0).getJSONObject("location").getDouble("longitude"));
    org.junit.jupiter.api.Assertions.assertEquals(
        -43.5383822, arr.getJSONObject(1).getJSONObject("location").getDouble("latitude"));
    org.junit.jupiter.api.Assertions.assertEquals(
        172.5843095, arr.getJSONObject(1).getJSONObject("location").getDouble("longitude"));
    org.junit.jupiter.api.Assertions.assertEquals(
        -43.53702, arr.getJSONObject(2).getJSONObject("location").getDouble("latitude"));
    org.junit.jupiter.api.Assertions.assertEquals(
        172.6006563, arr.getJSONObject(2).getJSONObject("location").getDouble("longitude"));
  }

  @Test
  void getActivitiesSortByFurthestLocationReturnInCorrectOrder() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities?lon=172.5912131&lat=-43.5376602&radius=2&sort=furthest_location") // 216 Blenheim Road
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(3, arr.length());
    org.junit.jupiter.api.Assertions.assertEquals(
        -43.5376602, arr.getJSONObject(2).getJSONObject("location").getDouble("latitude"));
    org.junit.jupiter.api.Assertions.assertEquals(
        172.5912131, arr.getJSONObject(2).getJSONObject("location").getDouble("longitude"));
    org.junit.jupiter.api.Assertions.assertEquals(
        -43.5383822, arr.getJSONObject(1).getJSONObject("location").getDouble("latitude"));
    org.junit.jupiter.api.Assertions.assertEquals(
        172.5843095, arr.getJSONObject(1).getJSONObject("location").getDouble("longitude"));
    org.junit.jupiter.api.Assertions.assertEquals(
        -43.53702, arr.getJSONObject(0).getJSONObject("location").getDouble("latitude"));
    org.junit.jupiter.api.Assertions.assertEquals(
        172.6006563, arr.getJSONObject(0).getJSONObject("location").getDouble("longitude"));
  }

  @Test
  void getActivityInvalidSortReturnBadRequest() throws Exception {
    String mvcResponse =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities?time=duration&sort=some_sort_that_does_not_exist")
                    .session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
    Assertions.assertEquals("Sort method provided does not exist", mvcResponse);
  }

  @Test
  void getActivityLocationSortWithoutLongitudeReturnBadRequest() throws Exception {
    String mvcResponse =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?lon=90&radius=10&sort=furthest_location")
                    .session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
    Assertions.assertEquals(
        "Cannot sort by location when no original location is provided", mvcResponse);
  }

  @Test
  void getActivityLocationSortWithoutLatitudeReturnBadRequest() throws Exception {
    String mvcResponse =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?lat=90&radius=10&sort=furthest_location")
                    .session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
    Assertions.assertEquals(
        "Cannot sort by location when no original location is provided", mvcResponse);
  }

  @Test
  void getActivityNoParametersReturnInCreationDateOrderReturnStatusOk() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(5, arr.length());
    org.junit.jupiter.api.Assertions.assertEquals(
        "Restricted activity", arr.getJSONObject(0).getString("activityName"));
    org.junit.jupiter.api.Assertions.assertEquals(
        "Kaikoura Coast Track race", arr.getJSONObject(arr.length() - 1).getString("activityName"));
  }
}
