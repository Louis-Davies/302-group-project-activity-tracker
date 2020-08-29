package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

public class LocationUpdateRequest {

//  @NotEmpty(message = "latitude cannot be empty")
  @JsonProperty("latitude")
  @Range(min=-90, max=90, message = "latitude must be between -90 and 90")
  @NotNull
  public double latitude;

  @JsonProperty("longitude")
  @Range(min=-180, max=180, message = "longitude must be between -180 and 180")
//  @NotEmpty(message = "longitude cannot be empty")
  public double longitude;

}
