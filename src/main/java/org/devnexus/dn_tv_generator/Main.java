package org.devnexus.dn_tv_generator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
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
import org.devnexus.dn_tv_generator.data.DateRoom;
import org.devnexus.dn_tv_generator.data.Session;
import org.devnexus.dn_tv_generator.data.SessionMapper;
import org.devnexus.dn_tv_generator.render.Fonts;

public class Main {

    
    private static final String DEVNEXUS_LOGO = "/dn_logo.png";
    private static final int PADDING_TOP = 27;
    private static final int PADDING_LEFT = 48;
    private static final int BACKGROUND_COLOR = 0x000;

    private static final BufferedImage logo;
    private static final SimpleDateFormat dayNameFormatter = new SimpleDateFormat("EEEE");
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
            final String dateName = dayNameFormatter.format(dateTrack.sessionTime);
            List<Session> sessions;
            if (dateName.equals("Thursday")) {
                sessions= sessionMapper.getDayOneSessionForRoom(dateTrack.sessionTrack);
            } else {
                sessions= sessionMapper.getDayTwoSessionForRoom(dateTrack.sessionTrack);
            }
            
            BufferedImage image = new BufferedImage(1080, 1920, BufferedImage.TYPE_INT_RGB);
            Graphics imageGfx = image.getGraphics();

            fillBackGround(imageGfx);
            drawLogo(imageGfx);
            drawDate(imageGfx, dateTrack);
            drawRoomInfo(imageGfx, dateTrack);
            drawSchedules(imageGfx, dateTrack, sessions);

            
            
            final String roomName = dateTrack.sessionTrack.roomName;
            
            

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
        
        int width = 1050;
        int height = 200;
        int offset_x = 15;
        int offset_y = 300;

        for (Session session : sessions) {

            DateFormat timeFormat = new SimpleDateFormat("kk:mm");
            BufferedImage tile = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            Graphics2D tileGraphics = (Graphics2D) tile.getGraphics();
            
            String time = timeFormat.format(session.fromTime);
            Rectangle2D timeBounds = imageGfx.getFontMetrics()
                .getStringBounds(time, tileGraphics);
            
            tileGraphics.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            tileGraphics.setColor(roomColor);
            tileGraphics.fill3DRect(0, 0, width, height, true);

            tileGraphics.setColor(Color.WHITE);
            tileGraphics.setFont(fonts.font);
            tileGraphics.drawString(time, 20, height/2 + (int)(timeBounds.getHeight())/4);

            String sessionTitle = session.title;

            


            List<String> textList = StringUtils.wrap(sessionTitle, tileGraphics.getFontMetrics(),
                (int) (width - 40 - (timeBounds.getWidth())));
            if (textList.size() > 3) {
                textList = textList.subList(0, 3);
                String lastLine = textList.get(2);
                lastLine = lastLine.subSequence(0, lastLine.length() - 3).toString() + "...";
                textList.remove(2);
                textList.add(lastLine);
            }
            
            Rectangle2D titleBounds = imageGfx.getFontMetrics()
                .getStringBounds(sessionTitle, tileGraphics);
            
            int lineHeight = (int) titleBounds.getHeight();

            if (textList.size() == 1) {
                tileGraphics.drawString(sessionTitle, 40 + (int)timeBounds.getWidth(), height/2 + (lineHeight)/4);    
            } else if (textList.size() == 2) {
                tileGraphics.drawString(textList.get(0), 40 + (int)timeBounds.getWidth(), height/2 + 20 - (lineHeight)/3);
                tileGraphics.drawString(textList.get(1), 40 + (int)timeBounds.getWidth(), height/2 + 20 + (lineHeight)/3);
            } else {
                tileGraphics.drawString(textList.get(0), 40 + (int)timeBounds.getWidth(), height/2 - 15 - (lineHeight)/4);
                tileGraphics.drawString(textList.get(1), 40 + (int)timeBounds.getWidth(), height/2 - 5 + (lineHeight)/4);
                tileGraphics.drawString(textList.get(2), 40 + (int)timeBounds.getWidth(), height/2 + 25 +  (lineHeight)/2);
            }
            
            
            

            imageGfx.drawImage(tile, offset_x, offset_y, null);
            offset_y = offset_y + height + PADDING_TOP;
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

        
        BufferedImage tile = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D tileGraphics = (Graphics2D) tile.getGraphics();
        tileGraphics.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        imageGfx.setColor(Color.WHITE);

        imageGfx.setFont(fonts.fontHuge);

        imageGfx.drawString(dateString, (1080 - (int) imageGfx.getFontMetrics().getStringBounds(dateString, imageGfx).getWidth()) / 2, 220);



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
