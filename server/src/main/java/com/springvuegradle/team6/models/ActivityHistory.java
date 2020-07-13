package com.springvuegradle.team6.models;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 *
 */
@Entity
public class ActivityHistory {

    // For testing purposes only
    public ActivityHistory() {
        this.activity = null;
        this.timeDate = null;
        this.message = null;
    }

    /**
     * Each activity history instance has its own unique id
     */
    @Id
    @GeneratedValue
    @Column(name = "activity_history_id")
    private Integer id;

    /**
     * The activity this history instance is associated with
     */
    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    /**
     * The time at which this change was made
     */
    @Column(name = "time_date", columnDefinition = "datetime default NOW()")
    private LocalDateTime timeDate;

    /**
     * A message detailing the changes made generated by the controller and
     * used in the feed
     */
    private String message;

    //==========GETTERS==========

    public Integer getId() {
        return id;
    }

    public Activity getActivity() {
        return activity;
    }

    public LocalDateTime getTimeDate() {
        return timeDate;
    }

    public String getMessage() {
        return message;
    }

    //==========SETTERS==========

    public void setId(Integer id) {
        this.id = id;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setTimeDate(LocalDateTime timeDate) {
        this.timeDate = timeDate;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
