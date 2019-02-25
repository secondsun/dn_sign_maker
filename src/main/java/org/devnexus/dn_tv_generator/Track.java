package org.devnexus.dn_tv_generator;

import java.awt.Color;

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
  
}
