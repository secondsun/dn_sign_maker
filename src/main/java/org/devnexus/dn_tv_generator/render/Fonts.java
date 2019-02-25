package org.devnexus.dn_tv_generator.render;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import org.devnexus.dn_tv_generator.Main;

public final class Fonts {

  private static final String ROBOTO = "/RobotoCondensed-Regular.ttf";
  private static Fonts instance;

  public final Font fontHuge;
  public final Font font;
  public final Font fontSmall;
  public final Font fontTiny;

  private Fonts() {

    try {

      Font fontHuge = Font
          .createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("/Roboto-Bold.ttf"));
      this.fontHuge = fontHuge.deriveFont(96F);

      Font font = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream(ROBOTO));
      this.font = font.deriveFont(48F);

      Font fontSmall = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream(ROBOTO));
      this.fontSmall = fontSmall.deriveFont(36F);

      this.fontTiny = fontSmall.deriveFont(24F);
    } catch (FontFormatException | IOException e) {
      throw new RuntimeException(e);
    }


  }
  
  public synchronized static Fonts getInstance() {
    if (instance == null) {
      instance = new Fonts();
    }
    return instance;
  }

}
