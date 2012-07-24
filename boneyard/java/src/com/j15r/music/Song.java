package com.j15r.music;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 * 
 */
public class Song {

  /**
   * 
   */
  public static class Instant {
    private final long tick;

    private List<Note> notes = new ArrayList<Note>();
    private float bpm;

    public Instant(long tick) {
      this.tick = tick;
    }

    public float getBpm() {
      return bpm;
    }

    public List<Note> getNotes() {
      return notes;
    }

    public long getTick() {
      return tick;
    }

    public boolean hasStartingNotes() {
      for (Note note : notes) {
        if (note.getTick() == tick) {
          return true;
        }
      }
      return false;
    }
  }

  /**
   * 
   */
  public static class Note implements Comparable<Note> {
    private final int channel;
    private final int note;
    private final long tick;
    private final float velocity;

    private long duration;
    private boolean removeMe;

    private Note(int note, long tick, int channel, float velocity,
        boolean beginning) {
      this.note = note;
      this.tick = tick;
      this.channel = channel;
      this.velocity = velocity;
    }

    public int compareTo(Note other) {
      return (int) (tick - other.tick);
    }

    public int getChannel() {
      return channel;
    }

    public long getDuration() {
      return duration;
    }

    public int getNote() {
      return note;
    }

    public long getTick() {
      return tick;
    }

    public float getVelocity() {
      return velocity;
    }
  }

  private Sequence sequence;
  private ArrayList<Instant> instants;

  /**
   * Gets the instance at a given position.
   */
  public Instant getInstant(int position) {
    assert (position < instants.size());
    return instants.get(position);
  }

  /**
   * Gets the maximum possible position value (i.e. the end of the sequence).
   */
  public int getMaxPosition() {
    return instants.size();
  }

  /**
   * Opens a new MIDI stream, clearing any existing one and starting at the
   * beginning of the sequence.
   */
  public void open(InputStream midiStream) throws IOException {
    try {
      sequence = MidiSystem.getSequence(midiStream);
      processMessages();
    } catch (InvalidMidiDataException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Processes all the sequences events into a collection of Instances.
   */
  private void processMessages() {
    // Flatten all the tracks into a single list of events.
    Track[] tracks = sequence.getTracks();
    int[] trackPositions = new int[tracks.length];
    ArrayList<MidiEvent> events = new ArrayList<MidiEvent>();

    boolean done = false;
    while (!done) {
      done = true;

      for (int i = 0; i < tracks.length; ++i) {
        if (trackPositions[i] >= tracks[i].size()) {
          continue;
        }
        done = false;
        events.add(tracks[i].get(trackPositions[i]));
        ++trackPositions[i];
      }

      if (done) {
        break;
      }
    }

    // Sort the events (they might come out slightly out of whack because
    // of the way we iterate over the tracks).
    Collections.sort(events, new Comparator<MidiEvent>() {
      public int compare(MidiEvent e0, MidiEvent e1) {
        return (int) (e0.getTick() - e1.getTick());
      }
    });

    // Group the events into 'instants'.
    instants = new ArrayList<Instant>();
    Note[] notes = new Note[127];

    instants.add(new Instant(0));
    long lastTick = 0;
    float currentBpm = 60;

    for (MidiEvent event : events) {
      MidiMessage message = event.getMessage();

      // Time for a new instant?
      if (event.getTick() > lastTick) {
        // Add notes to the instant, and clear finished notes from the notes
        // array.
        Instant instant = instants.get(instants.size() - 1);
        instant.bpm = currentBpm;
        for (int i = 0; i < notes.length; ++i) {
          if (notes[i] != null) {
            instant.notes.add(notes[i]);
            if (notes[i].removeMe) {
              notes[i] = null;
            }
          }
        }

        // On to a new instant.
        lastTick = event.getTick();
        instants.add(new Instant(lastTick));
      }

      // All notes are 'short messages' in midi-speak.
      if (message instanceof ShortMessage) {
        ShortMessage shortMessage = (ShortMessage) message;

        // We only care about key on/off events.
        if (Utilities.isNote(shortMessage)) {
          int note = shortMessage.getData1();
          if (Utilities.isNoteOn(shortMessage)) {
            notes[note] = new Note(note, event.getTick(),
                shortMessage.getChannel(), Utilities.getVelocity(shortMessage),
                true);
          } else {
            // Note off: Calculate its duration and mark it for removal.
            long curTick = event.getTick();
            if (notes[note] != null) {
              notes[note].duration = curTick - notes[note].getTick();
              notes[note].removeMe = true;
            }
          }
        }
      } else if (message instanceof MetaMessage) {
        // Watch for tempo messages.
        MetaMessage metaMessage = (MetaMessage) message;
        if (metaMessage.getType() == 0x51) {
          currentBpm = Utilities.getTempo(metaMessage);
        }
      }
    }
  }
}