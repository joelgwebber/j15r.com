package com.j15r.music;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;

public class PianoRollDisplay extends JComponent implements SongDisplay {

  private Song song;
  private int position;

  public void display(Song song, int position) {
    this.song = song;
    this.position = position;
    repaint();
  }

  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    paint((Graphics2D) g, getWidth(), getHeight());
  }

  private float getDefaultBpm() {
    return song.getInstant(0).getBpm();
  }

  private Point getNoteBounds(int note) {
    Rectangle keyRect = KeyboardDisplay.keyRect(note, getWidth(), getHeight());
    return new Point(keyRect.x, keyRect.width);
  }

  private int getTickPos(long tick) {
    long baseTick = song.getInstant(position).getTick();
    return (int) (getHeight() - ((tick - baseTick) * 25 / getDefaultBpm()));
  }

  private void paint(Graphics2D g, int width, int height) {
    if (song == null) {
      return;
    }

    Set<Song.Note> notes = new HashSet<Song.Note>();

    int curPosition = position;
    Song.Instant instant = song.getInstant(curPosition);

    g.setColor(Color.GRAY);
    while (true) {
      boolean notesFound = false;
      for (Song.Note note : instant.getNotes()) {
        notes.add(note);
        if (note.getTick() == instant.getTick()) {
          notesFound = true;
        }
      }

      if (notesFound) {
        int y = getTickPos(instant.getTick());
        if (y < 0) {
          break;
        }

        g.drawLine(0, y, width, y);
      }

      ++curPosition;
      if (curPosition >= song.getMaxPosition()) {
        break;
      }
      instant = song.getInstant(curPosition);
    }

    for (Song.Note note : notes) {
      Point notePos = getNoteBounds(note.getNote());
      int yStart = getTickPos(note.getTick() + note.getDuration());
      int yEnd = getTickPos(note.getTick());

      Color color = Constants.CHANNEL_COLORS[note.getChannel()];
      g.setColor(color);
      g.setPaint(new GradientPaint(notePos.x, yStart, new Color(0, 0, 0, 0),
          notePos.x, yEnd, color));
      g.fillRect(notePos.x, yStart, notePos.y, yEnd - yStart);
    }
  }
}
