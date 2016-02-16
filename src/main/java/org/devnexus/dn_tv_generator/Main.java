package org.devnexus.dn_tv_generator;

import com.google.common.collect.HashMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;

public class Main {

    private static final String ROBOTO = "/RobotoCondensed-Regular.ttf";
    private static final String ROBOTO_THIN = "/Roboto-Thin.ttf";
    private static final String DEVNEXUS_LOGO = "/dn_logo.png";
    private static final int PADDING_TOP = 27;
    private static final int PADDING_LEFT = 48;
    private static final int BACKGROUND_COLOR = 0xfffefefe;
    private static final String SCHEDULE = "/schedule.json";
    private static final Date DAY_1 = new Date(1455580800000l);
    private static final Date DAY_2 = new Date(1455667200000l);

    public static void main(String args[]) throws Exception {

        HashMultimap<String, JsonObject> roomSessions = HashMultimap.<String, JsonObject>create(30, 30);
        Map<String, Integer> roomColors = new HashMap<String, Integer>(30);

        Font fontHuge = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("/Roboto-Bold.ttf"));
        fontHuge = fontHuge.deriveFont(96F);

        Font font = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream(ROBOTO));
        font = font.deriveFont(48F);

        Font fontSmall = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream(ROBOTO));
        fontSmall = fontSmall.deriveFont(36F);

        Font fontTiny = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream(ROBOTO_THIN));
        fontTiny = fontSmall.deriveFont(24F);

        String scheduleJSON = IOUtils.toString(Main.class.getResourceAsStream(SCHEDULE));
        JsonElement scheduleJson = new JsonParser().parse(scheduleJSON);

        JsonArray items = scheduleJson.getAsJsonObject().get("scheduleItems").getAsJsonArray();
        JsonObject item;
        for (int i = 0; i < items.size(); i++) {
            item = items.get(i).getAsJsonObject();
            JsonObject room = item.get("room").getAsJsonObject();
            String roomName = room.get("name").getAsString();
            Integer roomColor = Integer.parseInt(room.get("color").getAsString().replace("#", ""), 16);
            roomSessions.put(roomName, item);
            roomColors.put(roomName, roomColor);

        }

        for (String roomName : roomSessions.keySet()) {

            BufferedImage image = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
            Graphics imageGfx = image.getGraphics();

            fillBackGround(imageGfx);
            drawLogo(imageGfx);

            int roomColor = roomColors.get(roomName);
            ArrayList<JsonObject> sessions = new ArrayList<JsonObject>(roomSessions.get(roomName));
            if (sessions.size() < 2) {
                continue;
            }

            if (sessions.get(1).get("presentation") == null || sessions.get(1).get("presentation").isJsonNull() || sessions.get(1).get("presentation").getAsJsonObject().get("track") == null) {
                continue;
            }

            String trackName = sessions.get(1).get("presentation").getAsJsonObject().get("track").getAsJsonObject().get("name").getAsString();

            ((Graphics2D) imageGfx).setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            imageGfx.setColor(Color.decode(roomColor + ""));

            imageGfx.setFont(fontHuge);
            imageGfx.drawString(trackName, 1920 - 80 - (int) imageGfx.getFontMetrics().getStringBounds(trackName, imageGfx).getWidth(), PADDING_TOP + 80);

            Collections.sort(sessions, (JsonObject object, JsonObject another)
                    -> (int) (object.get("fromTime").getAsLong() - another.get("fromTime").getAsLong()));

            int width = 450;
            int height = 400;
            int offset_x = 20;
            int offset_y = 180;
            String dateName = "Wednesday";
            for (JsonObject session : sessions) {
                if (session.get("presentation") == null) {
                    continue;
                }
                DateFormat format = new SimpleDateFormat("hh:mm");
                BufferedImage tile = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                if (session.get("fromTime").getAsLong() < DAY_2.getTime()) {
                    continue;
                }
                String date = format.format(new Date(session.get("fromTime").getAsLong() + 5 * 1000 * 60 * 60));
                Graphics2D tileGraphics = (Graphics2D) tile.getGraphics();
                tileGraphics.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                tileGraphics.setColor(Color.decode(roomColor + ""));
                tileGraphics.fill3DRect(0, 0, width, height, true);

                tileGraphics.setColor(Color.WHITE);
                tileGraphics.setFont(font);
                tileGraphics.drawString(date, 20, 50);

                tileGraphics.setFont(fontSmall);
                List<String> textList = StringUtils.wrap(session.get("presentation").getAsJsonObject().get("title").getAsString(), tileGraphics.getFontMetrics(), width - 40);
                if (textList.size() > 3) {
                    textList = textList.subList(0, 3);
                    String lastLine = textList.get(2);
                    lastLine = lastLine.subSequence(0, lastLine.length() - 3).toString() + "...";
                    textList.remove(2);
                    textList.add(lastLine);
                }

                tileGraphics.setFont(fontSmall);
                for (int i = 0; i < textList.size(); i++) {
                    tileGraphics.drawString(textList.get(i), 20, 110 + 40 * i);
                }

                tileGraphics.setFont(fontTiny);
                int startSpeakers = 170 + (textList.size() - 1) * 30;

                JsonArray speakersArray = session.get("presentation").getAsJsonObject().get("speakers").getAsJsonArray();
                List<String> speakerNames = new ArrayList<>();
                for (int i = 0; i < speakersArray.size(); i++) {
                    JsonObject speaker = speakersArray.get(i).getAsJsonObject();
                    speakerNames.add(speaker.get("firstName").getAsString() + " " + speaker.get("lastName").getAsString());
                }

                tileGraphics.setFont(fontTiny);

                for (int i = 0; i < speakerNames.size(); i++) {
                    tileGraphics.drawString(speakerNames.get(i), 20, startSpeakers + i * 30);
                }

                imageGfx.drawImage(tile, offset_x, offset_y, null);
                offset_x += width + 20;
                if (offset_x > 1800) {
                    offset_x = 20;
                    offset_y = offset_y + height + PADDING_TOP;
                }
            }

            ImageIO.write(image, "jpg", new File("/tmp/" + roomName + dateName + ".jpg"));

        }

        for (String roomName : roomSessions.keySet()) {

            BufferedImage image = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
            Graphics imageGfx = image.getGraphics();

            fillBackGround(imageGfx);
            drawLogo(imageGfx);

            int roomColor = roomColors.get(roomName);
            ArrayList<JsonObject> sessions = new ArrayList<JsonObject>(roomSessions.get(roomName));
            if (sessions.size() < 2) {
                continue;
            }

            if (sessions.get(1).get("presentation") == null || sessions.get(1).get("presentation").isJsonNull() || sessions.get(1).get("presentation").getAsJsonObject().get("track") == null) {
                continue;
            }

            String trackName = sessions.get(1).get("presentation").getAsJsonObject().get("track").getAsJsonObject().get("name").getAsString();

            ((Graphics2D) imageGfx).setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            imageGfx.setColor(Color.decode(roomColor + ""));

            imageGfx.setFont(fontHuge);
            imageGfx.drawString(trackName, 1920 - 80 - (int) imageGfx.getFontMetrics().getStringBounds(trackName, imageGfx).getWidth(), PADDING_TOP + 80);

            Collections.sort(sessions, (JsonObject object, JsonObject another)
                    -> (int) (object.get("fromTime").getAsLong() - another.get("fromTime").getAsLong()));

            int width = 450;
            int height = 400;
            int offset_x = 20;
            int offset_y = 180;
            String dateName = "Tuesday";
            for (JsonObject session : sessions) {
                if (session.get("presentation") == null) {
                    continue;
                }
                DateFormat format = new SimpleDateFormat("hh:mm");
                BufferedImage tile = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                if (session.get("fromTime").getAsLong() > DAY_2.getTime()) {
                    continue;
                }
                String date = format.format(new Date(session.get("fromTime").getAsLong() + 5 * 1000 * 60 * 60));
                Graphics2D tileGraphics = (Graphics2D) tile.getGraphics();
                tileGraphics.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                tileGraphics.setColor(Color.decode(roomColor + ""));
                tileGraphics.fill3DRect(0, 0, width, height, true);

                tileGraphics.setColor(Color.WHITE);
                tileGraphics.setFont(font);
                tileGraphics.drawString(date, 20, 50);

                tileGraphics.setFont(fontSmall);
                List<String> textList = StringUtils.wrap(session.get("presentation").getAsJsonObject().get("title").getAsString(), tileGraphics.getFontMetrics(), width - 40);
                if (textList.size() > 3) {
                    textList = textList.subList(0, 3);
                    String lastLine = textList.get(2);
                    lastLine = lastLine.subSequence(0, lastLine.length() - 3).toString() + "...";
                    textList.remove(2);
                    textList.add(lastLine);
                }

                tileGraphics.setFont(fontSmall);
                for (int i = 0; i < textList.size(); i++) {
                    tileGraphics.drawString(textList.get(i), 20, 110 + 40 * i);
                }

                tileGraphics.setFont(fontTiny);
                int startSpeakers = 170 + (textList.size() - 1) * 30;

                JsonArray speakersArray = session.get("presentation").getAsJsonObject().get("speakers").getAsJsonArray();
                List<String> speakerNames = new ArrayList<>();
                for (int i = 0; i < speakersArray.size(); i++) {
                    JsonObject speaker = speakersArray.get(i).getAsJsonObject();
                    speakerNames.add(speaker.get("firstName").getAsString() + " " + speaker.get("lastName").getAsString());
                }

                tileGraphics.setFont(fontTiny);

                for (int i = 0; i < speakerNames.size(); i++) {
                    tileGraphics.drawString(speakerNames.get(i), 20, startSpeakers + i * 30);
                }

                imageGfx.drawImage(tile, offset_x, offset_y, null);
                offset_x += width + 20;
                if (offset_x > 1800) {
                    offset_x = 20;
                    offset_y = offset_y + height + PADDING_TOP;
                }
            }

            ImageIO.write(image, "jpg", new File("/tmp/" + roomName + dateName + ".jpg"));

        }

    }

    private static Color asColor(int color) {
        return new Color(color, true);
    }

    private static void drawLogo(Graphics imageGfx) throws IOException {
        BufferedImage logo = ImageIO.read(Main.class.getResourceAsStream(DEVNEXUS_LOGO));

        imageGfx.setColor(asColor(Color.TRANSLUCENT));
        imageGfx.drawImage(logo, PADDING_LEFT, PADDING_TOP, null);

    }

    private static void fillBackGround(Graphics imageGfx) {
        imageGfx.setColor(asColor(BACKGROUND_COLOR));
        imageGfx.fillRect(0, 0, 1920, 1080);
    }

}
