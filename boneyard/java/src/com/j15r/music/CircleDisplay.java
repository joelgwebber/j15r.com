package com.j15r.music;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

public class CircleDisplay extends JComponent implements SongDisplay {

  private Song song;
  private int position;
  private final int channel;

  public CircleDisplay(int channel) {
    this.channel = channel;
  }

  public void display(Song song, int position) {
    this.song = song;
    this.position = position;
    repaint();
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(128, 128);
  }

  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    paint((Graphics2D) g, getWidth(), getHeight());
  }

  private void paint(Graphics2D g, int width, int height) {
    int size = (width > height) ? height : width;
    int r = size / 2 - 4;

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g.setBackground(Color.WHITE);
    g.clearRect(0, 0, size, size);

    g.setColor(Color.BLACK);
    g.drawOval(4, 4, size - 8, size - 8);

    if (song == null) {
      return;
    }

    g.setComposite(AlphaComposite.SrcOver);
    Song.Instant instant = song.getInstant(position);
    for (Song.Note note : instant.getNotes()) {
      // Only display notes from the requested channel.
      if (note.getChannel() != channel) {
        continue;
      }

      if (note.getDuration() > 0) {
        int i = note.getNote() % 12;
        double a = (2.0 * PI / 12.0) * i;
        int x = (int) (r * cos(a));
        int y = (int) (r * sin(a));
  
        double alpha = 1.0 - (double) (instant.getTick() - note.getTick())
            / (double) note.getDuration();
        g.setBackground(new Color(0, 0, 0, (int) (255 * alpha)));
        g.setColor(new Color(0, 0, 0, (int) (255 * alpha)));
        g.fillOval(r + x, r + y, 8, 8);
      }
    }
  }
}
