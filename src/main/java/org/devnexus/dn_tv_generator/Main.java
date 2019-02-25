package org.devnexus.dn_tv_generator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class Main {

    
    private static final String DEVNEXUS_LOGO = "/dn_logo.png";
    private static final int PADDING_TOP = 27;
    private static final int PADDING_LEFT = 48;
    private static final int BACKGROUND_COLOR = 0x000;

    private static final BufferedImage logo;

    private static Fonts fonts = Fonts.getInstance();
    
    static {
        try {
            logo = ImageIO.read(Main.class.getResourceAsStream(DEVNEXUS_LOGO));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String args[]) throws Exception {

        SessionMapper sessionMapper = SessionMapper.getMapper();

        for (DateRoom dateTrack : sessionMapper.map.keySet()) {
            List<Session> sessions = sessionMapper.getDayOneSessionForRoom(dateTrack.sessionTrack);
            
            BufferedImage image = new BufferedImage(1080, 1920, BufferedImage.TYPE_INT_RGB);
            Graphics imageGfx = image.getGraphics();

            fillBackGround(imageGfx);
            drawLogo(imageGfx);
            drawDate(imageGfx, dateTrack);
            drawRoomInfo(imageGfx, dateTrack);
            drawSchedules(imageGfx, dateTrack, sessions);

            final String roomName = dateTrack.sessionTrack.roomName;
            final String dateName = "Wednesday";
            

            write(dateName, roomName, image);

        }

    }

    private static void drawRoomInfo(Graphics imageGfx,
        DateRoom dateTrack) {
        final String roomName = dateTrack.sessionTrack.roomName;
        final String trackName = dateTrack.sessionTrack.trackName;
    }

    private static void drawSchedules(Graphics imageGfx,
        DateRoom dateTrack, List<Session> sessions) {
        final Color roomColor = dateTrack.sessionTrack.getColor();
        final String trackName = dateTrack.sessionTrack.trackName;


        if (sessions.size() < 2) {
            return;
        }
        
        int width = 1040;
        int height = 200;
        int offset_x = 20;
        int offset_y = 300;

        for (Session session : sessions) {

            DateFormat format = new SimpleDateFormat("hh:mm");
            BufferedImage tile = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            Graphics2D tileGraphics = (Graphics2D) tile.getGraphics();
            tileGraphics.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            tileGraphics.setColor(roomColor);
            tileGraphics.fill3DRect(0, 0, width, height, true);

            tileGraphics.setColor(Color.WHITE);
            tileGraphics.setFont(fonts.font);
            tileGraphics.drawString(format.format(session.fromTime), 20, 50);

            tileGraphics.setFont(fonts.fontSmall);
            String sessionTitle = session.title;

            List<String> textList = StringUtils.wrap(sessionTitle, tileGraphics.getFontMetrics(), width - 80);
            if (textList.size() > 3) {
                textList = textList.subList(0, 3);
                String lastLine = textList.get(2);
                lastLine = lastLine.subSequence(0, lastLine.length() - 3).toString() + "...";
                textList.remove(2);
                textList.add(lastLine);
            }

            tileGraphics.setFont(fonts.fontSmall);
            for (int i = 0; i < textList.size(); i++) {
                tileGraphics.drawString(textList.get(i), 20, 110 + 40 * i);
            }

            tileGraphics.setFont(fonts.fontTiny);

            imageGfx.drawImage(tile, offset_x, offset_y, null);
            offset_x += width + 20;
            if (offset_x > 1800) {
                offset_x = 20;
                offset_y = offset_y + height + PADDING_TOP;
            }
        }


    }

    private static void drawDate(Graphics imageGfx, DateRoom dateTrack) {

        ((Graphics2D) imageGfx).setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        Date date = dateTrack.sessionTime;
        DateFormat format = new SimpleDateFormat("EEEE MMMM d");
        String dateString = format.format(date);
        int width = 1080;
        int height = 100;
        int offset_x = 20;
        int offset_y = 20;
        
        BufferedImage tile = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D tileGraphics = (Graphics2D) tile.getGraphics();
        tileGraphics.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        imageGfx.setColor(Color.WHITE);

        imageGfx.setFont(fonts.fontHuge);

        imageGfx.drawString(dateString, (1080 - (int) imageGfx.getFontMetrics().getStringBounds(dateString, imageGfx).getWidth()) / 2, 260);



    }

    private static Color asColor(int color) {
        return new Color(color, true);
    }

    private static void drawLogo(Graphics imageGfx) throws IOException {

        imageGfx.setColor(asColor(Color.TRANSLUCENT));
        imageGfx.drawImage(logo, PADDING_LEFT, PADDING_TOP, null);

    }

    private static void fillBackGround(Graphics imageGfx) {
        imageGfx.setColor(asColor(BACKGROUND_COLOR));
        imageGfx.fillRect(0, 0, 1080, 1920);
    }

    private static void write(String dateName, String roomName, RenderedImage image) {

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(new File("/home/summerspittman/Pictures/DevNexus/" + roomName + dateName + ".jpg"))) {
            Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
            ImageWriter writer;
            writer = iter.next();
            ImageWriteParam iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(0.99f);
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), iwp);
            writer.dispose();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

}
