package com.j15r.music;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class PianoWeenie {

  private class Listeners implements WindowListener, KeyListener,
      MidiIn.Listener {

    public void keyPressed(KeyEvent e) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_RIGHT:
          moveToNextRealInstant(true);
          break;
        case KeyEvent.VK_LEFT:
          moveToNextRealInstant(false);
          break;
        case KeyEvent.VK_HOME:
          setPosition(startPosition);
          break;
        case KeyEvent.VK_END:
          setPosition(endPosition);
          break;
        case KeyEvent.VK_DOWN:
          setStartPosition(position);
          break;
        case KeyEvent.VK_UP:
          setEndPosition(position);
          break;
        case KeyEvent.VK_ESCAPE:
          setStartPosition(0);
          setEndPosition(song.getMaxPosition());
          break;
        case KeyEvent.VK_0:
          autoPlayNone();
          break;
        case KeyEvent.VK_1:
          autoPlayLeft();
          break;
        case KeyEvent.VK_2:
          autoPlayRight();
          break;
        case KeyEvent.VK_3:
          autoPlayBoth();
          break;
      }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void onMidiKeyDown(int note) {
      keys[note] = true;
      checkKeyMatch();
    }

    public void onMidiKeyUp(int note) {
      keys[note] = false;
      checkKeyMatch();
    }

    public void onSongPositionChanged(int position) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
      midiIn.close();
      synth.close();
      System.exit(0);
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }
  }

  public static void main(String[] args) throws Throwable {
    new PianoWeenie();
  }

  private final boolean[] keys = new boolean[127];

  private JFrame frame = new JFrame("Piano Weenie");
  private Listeners listeners = new Listeners();

  private Song song = new Song();
  private int position;
  private int startPosition, endPosition;
  private boolean[] autoPlayChannels = new boolean[2];

  private List<SongDisplay> displays = new ArrayList<SongDisplay>();
  private SynthOut synth = new SynthOut(song);
  private MidiIn midiIn = new MidiIn(listeners);

  private Timer nextNoteTimer = new Timer();
  private TimerTask nextNoteTask;

  public PianoWeenie() throws IOException {
    KeyboardDisplay keyboard = new KeyboardDisplay();
    PianoRollDisplay pianoRoll = new PianoRollDisplay();
    CircleDisplay leftCircle = new CircleDisplay(0);
    CircleDisplay rightCircle = new CircleDisplay(1);

    addDisplay(keyboard);
    addDisplay(pianoRoll);
    addDisplay(leftCircle);
    addDisplay(rightCircle);

    keyboard.addKeyListener(listeners);

    InputStream stream = getClass().getClassLoader().getResourceAsStream(
        "com/j15r/music/midi/hollyivy.mid");
    song.open(stream);

    startPosition = 0;
    endPosition = song.getMaxPosition();

    frame.setBackground(Color.WHITE);
    frame.addWindowListener(listeners);

    JPanel topPanel = new JPanel(new GridLayout(1, 2));
    topPanel.add(leftCircle);
    topPanel.add(rightCircle);

    JPanel panel = new JPanel(new BorderLayout(4, 4));
    panel.add(topPanel, BorderLayout.NORTH);
    panel.add(keyboard, BorderLayout.SOUTH);
    panel.add(pianoRoll, BorderLayout.CENTER);
    frame.setContentPane(panel);

    frame.setSize(1200, 600);
    frame.setVisible(true);

    clearKeys();
    updateDisplays();
    checkKeyMatch();
  }

  private void addDisplay(SongDisplay display) {
    displays.add(display);
  }

  private void autoPlay() {
    synth.play(song, position, autoPlayChannels);
  }

  private void checkKeyMatch() {
    // Determine whether the midi keys are down for all the notes begun in this
    // instant.
    Song.Instant instant = song.getInstant(position);
    for (Song.Note note : instant.getNotes()) {
      // Ignore auto-play channels.
      if (autoPlayChannels[note.getChannel()]) {
        continue;
      }

      // Only test notes starting in this instant.
      if (note.getTick() == instant.getTick()) {
        if (!keys[note.getNote()]) {
          // Missing a key. Not time to advance yet.
          return;
        }
      }
    }

    // All keys are pressed. Advance.
    moveToNextRealInstant(true);
  }

  private void clearKeys() {
    Arrays.fill(keys, false);
  }

  /**
   * Gets the current song position.
   * 
   * @return the song position
   */
  private int getPosition() {
    return position;
  }

  private void moveToNextRealInstant(boolean forward) {
    // Cancel any outstanding auto-play notes.
    if (nextNoteTask != null) {
      nextNoteTask.cancel();
    }

    // Skip all instants with no new notes.
    while (true) {
      if (forward) {
        setPosition(getPosition() + 1);
      } else {
        setPosition(getPosition() - 1);
      }

      if (song.getInstant(position).hasStartingNotes()) {
        break;
      }
    }

    // If the current instant has new notes in non-auto-play channels, move
    // along normally.
    Song.Instant instant = song.getInstant(position);
    for (Song.Note note : instant.getNotes()) {
      if (note.getTick() == instant.getTick()) {
        if (!autoPlayChannels[note.getChannel()]) {
          return;
        }
      }
    }

    // The instant only has new auto-play notes. Schedule it to be auto-played
    // later.
    nextNoteTask = new TimerTask() {
      @Override
      public void run() {
        nextNoteTask = null;
        autoPlay();
        moveToNextRealInstant(true);
      }
    };

    Song.Instant prevInstant = song.getInstant(position - 1);
    nextNoteTimer.schedule(nextNoteTask, instant.getTick()
        - prevInstant.getTick());
  }

  /**
   * Sets the current song position (in 'instants').
   * 
   * @param pos the new song position
   */
  private void setPosition(int pos) {
    if (pos < startPosition) {
      pos = startPosition;
    } else if (pos >= endPosition) {
      pos = startPosition;
    }

    position = pos;

    clearKeys();
    updateDisplays();
    autoPlay();
  }

  private void updateDisplays() {
    for (SongDisplay display : displays) {
      display.display(song, position);
    }
  }

  private void setStartPosition(int pos) {
    startPosition = pos;
    if (startPosition >= endPosition) {
      endPosition = song.getMaxPosition();
    }
  }

  private void setEndPosition(int pos) {
    endPosition = pos;
    if (endPosition <= startPosition) {
      startPosition = 0;
    }
  }

  private void autoPlayNone() {
    autoPlayChannels[0] = false;
    autoPlayChannels[1] = false;
  }

  private void autoPlayLeft() {
    autoPlayChannels[0] = true;
    autoPlayChannels[1] = false;
    checkKeyMatch();
  }

  private void autoPlayRight() {
    autoPlayChannels[0] = false;
    autoPlayChannels[1] = true;
    checkKeyMatch();
  }

  private void autoPlayBoth() {
    autoPlayChannels[0] = true;
    autoPlayChannels[1] = true;
  }
}
