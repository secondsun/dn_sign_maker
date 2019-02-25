package org.devnexus.dn_tv_generator.data;

import java.awt.Color;
import java.util.Objects;

public class Track {

  public final String trackName, color, roomName;

  public Track(String trackName, String color, String roomName) {
    this.trackName = trackName;
    this.roomName = roomName;
    this.color = color;
  }
  
  public Color getColor() {
    return Color.decode(color);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Track track = (Track) o;
    return Objects.equals(trackName, track.trackName) &&
        Objects.equals(color, track.color) &&
        Objects.equals(roomName, track.roomName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(trackName, color, roomName);
  }
}
