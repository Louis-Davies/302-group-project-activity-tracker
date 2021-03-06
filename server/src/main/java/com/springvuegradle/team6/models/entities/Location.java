package com.springvuegradle.team6.models.entities;

import java.io.Serializable;
import javax.persistence.*;

import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Latitude;
import org.hibernate.search.annotations.Longitude;
import org.hibernate.search.annotations.Spatial;
import org.hibernate.search.spatial.Coordinates;
import org.hibernate.validator.constraints.Range;

/**
 * Location is a base class for defining a location in the world. It has three members: locationID:
 * The stored generated id for the database name: The associated name of the location lat & lon: The
 * geo coordinates of the feature This class can be used to define a feature point. Right now it is
 * used to define a users location but it can be extended to be used for instance for vertices in
 * event routes.
 */
@Indexed
@Spatial
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Location implements Serializable, Coordinates {

  @Id
  @GeneratedValue
  private int id;

  @Latitude
  @Range(min=-90, max=90)
  private Double latitude = 0d;


  @Longitude
  @Range(min=-180, max=180)
  private Double longitude = 0d;

  private String name;

  public Location() {
  }

  /**
   * Sets a location using a specific latitude and longitude
   *
   * @param latitude  Global coordinate latitude
   * @param longitude Global coordinate longitude
   */
  public Location(double latitude, double longitude) {
    setLatitude(latitude);
    setLongitude(longitude);
  }

  public Double getLatitude() {
    return latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }
}
