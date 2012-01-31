package com.j15r.music;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class KeyboardDisplay extends JComponent implements SongDisplay {

  private static final Rectangle[] keyRects = new Rectangle[] {
      new Rectangle(1, 132, 31, 70), new Rectangle(22, 50, 14, 70),
      new Rectangle(34, 132, 31, 70), new Rectangle(64, 50, 14, 70),
      new Rectangle(68, 132, 31, 70), new Rectangle(102, 132, 31, 70),
      new Rectangle(122, 50, 14, 70), new Rectangle(135, 132, 31, 70),
      new Rectangle(161, 50, 14, 70), new Rectangle(169, 132, 31, 70),
      new Rectangle(200, 50, 14, 70), new Rectangle(203, 132, 31, 70),};
  private static final int CHUNK_WIDTH = 235;
  private static final int CHUNK_HEIGHT = 209;
  private static final Color SUSTAIN_COLOR = Color.GRAY;

  static Rectangle keyRect(int index, int width, int height) {
    // MIDI notes start an octave below the piano.
    index -= 12;
    assert (index >= 0);

    int octave = index / 12;
    int note = index % 12;

    int xScale = (width / 8), yScale = height;
    int xOffset = octave * xScale;
    Rectangle r = new Rectangle(keyRects[note]);

    r.x = r.x * xScale / CHUNK_WIDTH;
    r.width = r.width * xScale / CHUNK_WIDTH;
    r.y = r.y * yScale / CHUNK_HEIGHT;
    r.height = r.height * yScale / CHUNK_HEIGHT;

    r.x += xOffset;
    return r;
  }

  private final Image chunk;
  private Song song;
  private int position;

  public KeyboardDisplay() throws IOException {
    URL imageUrl = getClass().getClassLoader().getResource(
        "com/j15r/music/keyboard_chunk.jpg");
    chunk = ImageIO.read(imageUrl);
    setBackground(Color.WHITE);
    this.setFocusable(true);
  }

  public void display(Song song, int position) {
    this.song = song;
    this.position = position;
    repaint();
  }

  public Dimension getPreferredSize() {
    return new Dimension(0, 256);
  }

  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    paint((Graphics2D) g, getWidth(), getHeight());
  }

  private void paint(Graphics2D g, int width, int height) {
    g.setClip(null);

    int x = 0, step = width / 8;
    for (int i = 0; i < 8; ++i) {
      g.drawImage(chunk, x, 0, step, height, null);
      x += step;
    }

    if (song == null) {
      return;
    }

    Song.Instant instant = song.getInstant(position);
    for (Song.Note note : instant.getNotes()) {
      boolean beginning = note.getTick() == instant.getTick();
      // Don't display the last moment of a note.
      if (!beginning
          && (instant.getTick() == (note.getTick() + note.getDuration()))) {
        continue;
      }

      Color color = beginning ? Constants.CHANNEL_COLORS[note.getChannel()]
          : SUSTAIN_COLOR;
      g.setPaint(color);
      g.setPaintMode();

      Rectangle r = keyRect(note.getNote(), getWidth(), getHeight());
      g.fill(r);
    }
  }
}
