package com.j15r.music;

import com.j15r.music.Song.Instant;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

public class SynthOut {

  private final Synthesizer synth;
  private final Song song;
  private int curPosition = -1;

  public SynthOut(Song song) {
    try {
      this.song = song;

      synth = MidiSystem.getSynthesizer();
      synth.open();
    } catch (MidiUnavailableException e) {
      throw new RuntimeException(e);
    }
  }

  public void close() {
    synth.close();
  }

  public void play(Song song, int position, boolean[] channels) {
    try {
      if (curPosition >= 0) {
        Instant instant = song.getInstant(curPosition);
        for (Song.Note note : instant.getNotes()) {
          if (!channels[note.getChannel()]) {
            continue;
          }
  
          ShortMessage message = new ShortMessage();
          byte index = (byte) note.getNote();
          if (note.getTick() == instant.getTick()) {
            byte velocity = (byte) (note.getVelocity() * 127);
            message.setMessage(ShortMessage.NOTE_ON, 0, index, velocity);
            synth.getReceiver().send(message, instant.getTick());
          } else if (note.getTick() + note.getDuration() == instant.getTick()) {
            message.setMessage(ShortMessage.NOTE_OFF, 0, index, 0);
            synth.getReceiver().send(message, instant.getTick());
          }
        }
      }
  
      curPosition = position;
    } catch (InvalidMidiDataException e) {
      throw new RuntimeException(e);
    } catch (MidiUnavailableException e) {
      throw new RuntimeException(e);
    }
  }
}
