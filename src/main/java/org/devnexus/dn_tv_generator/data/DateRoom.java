package org.devnexus.dn_tv_generator.data;

import java.util.Date;
import java.util.Objects;

public class DateRoom {

  public final Date sessionTime;
  public final Track sessionTrack;

  public DateRoom(Date sessionTime, Track sessionTrack) {
    this.sessionTime = sessionTime;
    this.sessionTrack = sessionTrack;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DateRoom dateRoom = (DateRoom) o;
    return Objects.equals(sessionTime, dateRoom.sessionTime) &&
        Objects.equals(sessionTrack, dateRoom.sessionTrack);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sessionTime, sessionTrack);
  }
}
