package org.devnexus.dn_tv_generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SessionMapper {

  private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
  private static SessionMapper instance;
  public final TrackMetadataMapper trackMetadataMapper = TrackMetadataMapper.getMapper();

  public final Map<DateRoom, Session> map = new HashMap<>();

  private final static Date DAY_TWO_MIDNIGHT;

  static {
    try {
      DAY_TWO_MIDNIGHT = format.parse("2019-03-08T00:00:00");
    } catch (ParseException e) {
      throw  new RuntimeException(e);
    }
  }

  private SessionMapper(JsonObject scheduleJson) {
    JsonArray daysArray = scheduleJson
        .get("schedule").getAsJsonObject()
        .get("conference").getAsJsonObject()
        .get("days").getAsJsonArray();

    for (int i = 0; i < daysArray.size(); i++) {
      JsonObject day = daysArray.get(i).getAsJsonObject();
      JsonObject rooms = day.get("rooms").getAsJsonObject();
      rooms.entrySet().forEach(roomScheduleEntry -> {
        try {
          String roomName = roomScheduleEntry.getKey();
          Track track = trackMetadataMapper.mappings.get(roomName);
          JsonArray sessionsArray = roomScheduleEntry.getValue().getAsJsonArray();
          for (int j = 0; j < sessionsArray.size(); j++) {
            JsonObject roomObject = sessionsArray.get(j).getAsJsonObject();
            String startDatePart = roomObject.get("date").getAsString().split("-05:00")[0];

            Date startDate = format.parse(startDatePart);
            DateRoom dateRoom = new DateRoom(startDate, track);
            String title = roomObject.get("title").getAsString();
            Session session = new Session(startDate, title);
            map.put(dateRoom, session);  
          }
          
          
        } catch (ParseException e) {
          throw new RuntimeException(e);
        }

      });
    }


  }

  public synchronized static SessionMapper getMapper() {
    if (instance == null) {
      JsonParser gson = new JsonParser();
      JsonObject object = gson
          .parse(new InputStreamReader(TrackMetadataMapper.class.getResourceAsStream("/schedule.json")))
          .getAsJsonObject();
      instance = new SessionMapper(object);
    }
    return instance;
  }
  
  public List<Session> getDayOneSessionForRoom(Track track) {
    List<Session> sessions = 
    map.keySet().stream()
        .filter(entry -> entry.sessionTime.before(DAY_TWO_MIDNIGHT))
        .filter(entry -> entry.sessionTrack.equals(track))
        .map(entry -> map.get(entry))
        .collect(Collectors.toList())
        ;
    sessions.sort(Session::compareTo);
    return sessions;
  }
  
  public List<Session> getDayTwoSessionForRoom(Track track) {
    List<Session> sessions =
        map.keySet().stream()
            .filter(entry -> entry.sessionTime.after(DAY_TWO_MIDNIGHT))
            .map(entry -> map.get(entry))
            .collect(Collectors.toList())
        ;
    sessions.sort(Session::compareTo);
    return sessions;
  }
  

}
