package com.j15r.music;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.ShortMessage;

public class Utilities {

  /**
   * Gets the tempo associated with a tempo message (0x51), in beats per minute.
   */
  public static float getTempo(MetaMessage metaMessage) {
    byte[] data = metaMessage.getData();

    // tempo in microseconds per beat
    float tempo = ((data[0] & 0xFF) << 16) | ((data[1] & 0xFF) << 8)
        | (data[2] & 0xFF);

    if (tempo <= 0) {
      tempo = 0.1f;
    }
    return 60000000.0f / tempo;
  }

  /**
   * Gets the velocity of the given message (assuming it's a note on/off).
   */
  public static float getVelocity(ShortMessage message) {
    return (float) message.getData2() / 127;
  }

  /**
   * Determines whether the given message is a note on/off.
   */
  public static boolean isNote(ShortMessage message) {
    int cmd = message.getCommand();
    if ((cmd == ShortMessage.NOTE_ON) || (cmd == ShortMessage.NOTE_OFF)) {
      return true;
    }
    return false;
  }

  /**
   * Determines whether the given message is a 'note on'.
   */
  public static boolean isNoteOn(ShortMessage message) {
    return ((message.getCommand() == ShortMessage.NOTE_ON) && (getVelocity(message) > 0));
  }
}
