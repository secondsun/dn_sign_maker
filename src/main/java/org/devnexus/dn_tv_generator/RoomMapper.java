package org.devnexus.dn_tv_generator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RoomMapper {

  private static RoomMapper instance;

  public Map<String, Room> mappings = new HashMap<>();

  private RoomMapper(JsonObject jsonMapping) {
    jsonMapping.entrySet().forEach(entry -> {
      JsonObject roomInfo = entry.getValue().getAsJsonObject();
      mappings.put(entry.getKey(),
          new Room(roomInfo.get("roomName").getAsString(), roomInfo.get("color").getAsString()));
    });
  }

  public synchronized static RoomMapper getMapper() {
    if (instance == null) {
      JsonParser gson = new JsonParser();
      JsonObject object = gson
          .parse(new InputStreamReader(RoomMapper.class.getResourceAsStream("/room_map.json")))
          .getAsJsonObject();
      instance = new RoomMapper(object);
    }
    return instance;
  }

}
