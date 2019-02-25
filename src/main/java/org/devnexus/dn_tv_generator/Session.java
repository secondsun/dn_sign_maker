package org.devnexus.dn_tv_generator;

import java.util.Date;

public class Session implements Comparable<Session> {

  public final Date fromTime;
  public final String title;

  public Session(Date fromTime, String title) {
    this.fromTime = fromTime;
    this.title = title;
    
  }

  @Override
  public int compareTo(Session o) {
    return fromTime.compareTo(o.fromTime);
  }
}
