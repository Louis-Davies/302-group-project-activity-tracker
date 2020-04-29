package com.springvuegradle.team6.controllers;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.springvuegradle.team6.models.*;
import com.springvuegradle.team6.requests.CreateActivityRequest;
import com.springvuegradle.team6.requests.EditActivityTypeRequest;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(
    origins = "http://localhost:9500",
    allowCredentials = "true",
    allowedHeaders = "://",
    methods = {
      RequestMethod.GET,
      RequestMethod.POST,
      RequestMethod.DELETE,
      RequestMethod.PUT,
      RequestMethod.PATCH
    })
@RestController
@RequestMapping("")
public class ActivityController {
  private final ProfileRepository profileRepository;
  private final ActivityRepository activityRepository;

  ActivityController(ProfileRepository profileRep, ActivityRepository activityRepository) {
    this.profileRepository = profileRep;
    this.activityRepository = activityRepository;
  }

  /**
   * Check if user is authorised to edit activity type
   *
   * @param requestId userid in endpoint
   * @param session http session
   * @return ResponseEntity or null
   */
  private ResponseEntity<String> checkAuthorised(Integer requestId, HttpSession session) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    if (!(id.toString().equals(requestId.toString()))) {
      return new ResponseEntity<>("You can only edit you're own profile", HttpStatus.UNAUTHORIZED);
    }

    if (!profileRepository.existsById(requestId)) {
      return new ResponseEntity<>("No such user", HttpStatus.NOT_FOUND);
    }
    return null;
  }

  /**
   * Update/replace list of user activity types
   *
   * @param profileId user id to be updated
   * @param request EditActivityTypeRequest
   * @param session HTTPSession
   * @return unauthorised response or 201/400 HttpStatus Response
   */
  @PutMapping("/profiles/{profileId}/activity-types")
  public ResponseEntity<String> updateActivityTypes(
      @PathVariable Integer profileId,
      @Valid @RequestBody EditActivityTypeRequest request,
      HttpSession session) {
    Optional<Profile> profile = profileRepository.findById(profileId);
    if (profile.isPresent()) {
      Profile user = profile.get();

      ResponseEntity<String> authorisedResponse = this.checkAuthorised(profileId, session);
      if (authorisedResponse != null) {
        return authorisedResponse;
      }

      user.setActivityTypes(request.getActivities());
      profileRepository.save(user);
    }
    return ResponseEntity.ok("User activity types updated");
  }
  /**
   * Get all activity types
   *
   * @return 200 response with headers
   */
  @GetMapping("/profiles/activity-types")
  public ResponseEntity getActivityTypes() {
    List<String> activityList = new ArrayList();
    for (ActivityType myVar : ActivityType.values()) {
      activityList.add(myVar.toString());
    }
    return ResponseEntity.ok(activityList);
  }

  /**
   * Post Request to create an activity for the given profile based on the request
   * @param profileId The id of the profile where the activity is created for
   * @param request The request with values to create the activity
   * @param session The session of the currently logged in user
   * @return The response code and message
   */
  @PostMapping("/profiles/{profileId}/activities")
  public ResponseEntity<String> createActivity(
      @PathVariable Integer profileId,
      @Valid @RequestBody CreateActivityRequest request,
      HttpSession session) {
    ResponseEntity<String> checkAuthorisedResponse = checkAuthorised(profileId, session);
    if (checkAuthorisedResponse != null) {
      return checkAuthorisedResponse;
    }
    Activity activity = new Activity(request, profileId);
    if (!activity.isContinuous()) {
      if (activity.getStartTime() == null) {
        return new ResponseEntity<>(
            "Start date/time cannot be empty for a non continuous activity",
            HttpStatus.BAD_REQUEST);
      }
      if (activity.getEndTime() == null) {
        return new ResponseEntity<>(
            "End date/time cannot be empty for a non continuous activity", HttpStatus.BAD_REQUEST);
      }

      // Stand and End date/time validations
      LocalDateTime startDateTime;
      LocalDateTime endDateTime;
      try {
          startDateTime =
              LocalDateTime.parse(activity.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        if (startDateTime.isBefore(LocalDateTime.now())) {
          return new ResponseEntity(
              "Start date/time cannot be before the current time", HttpStatus.BAD_REQUEST);
        }
      } catch (DateTimeParseException e) {
        System.out.println(e);
        return new ResponseEntity(
            "Start date/time must be in correct format of yyyy-MM-dd'T'HH:mm:ssZ",
            HttpStatus.BAD_REQUEST);
      }

      try {
          endDateTime = LocalDateTime.parse(activity.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        if (endDateTime.isBefore(LocalDateTime.now())) {
          return new ResponseEntity(
              "End date/time cannot be before the current time", HttpStatus.BAD_REQUEST);
        }
      } catch (DateTimeParseException e) {
        return new ResponseEntity(
            "End date/time must be in correct format of yyyy-MM-dd'T'HH:mm:ssZ",
            HttpStatus.BAD_REQUEST);
      }

      if (startDateTime.isAfter(endDateTime)) {
        return new ResponseEntity(
            "Start date/time cannot be after End date/time", HttpStatus.BAD_REQUEST);
      }
    }
    activityRepository.save(activity);
    return new ResponseEntity(activity.getId(), HttpStatus.CREATED);
  }
}