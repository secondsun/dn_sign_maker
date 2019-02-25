package org.devnexus.dn_tv_generator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class TrackMetadataMapper {

  private static TrackMetadataMapper instance;

  public final  Map<String, Track> mappings = new HashMap<>();

  private TrackMetadataMapper(JsonObject jsonMapping) {
    jsonMapping.entrySet().forEach(entry -> {
      JsonObject roomInfo = entry.getValue().getAsJsonObject();
      mappings.put(entry.getKey(),
          new Track(entry.getKey(), roomInfo.get("color").getAsString(), roomInfo.get("room").getAsString()));
    });
  }

  public synchronized static TrackMetadataMapper getMapper() {
    if (instance == null) {
      JsonParser gson = new JsonParser();
      JsonObject object = gson
          .parse(new InputStreamReader(TrackMetadataMapper.class.getResourceAsStream("/room_map.json")))
          .getAsJsonObject();
      instance = new TrackMetadataMapper(object);
    }
    return instance;
  }

}
