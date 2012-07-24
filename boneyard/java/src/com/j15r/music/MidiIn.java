package com.j15r.music;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiDevice.Info;

public class MidiIn {

  public interface Listener {

    void onMidiKeyDown(int note);

    void onMidiKeyUp(int note);
  }

  private MidiDevice device;
  private Listener listener;

  public MidiIn(Listener listener) {
    try {
      this.listener = listener;

      Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
      device = MidiSystem.getMidiDevice(midiDeviceInfo[0]);
      device.open();
      device.getTransmitter().setReceiver(new Receiver() {
        public void send(MidiMessage message, long timeStamp) {
          onMidiMessage(message);
        }

        public void close() {
        }
      });
    } catch (MidiUnavailableException e) {
      throw new RuntimeException(e);
    }
  }

  private void onMidiMessage(MidiMessage message) {
    if (message instanceof ShortMessage) {
      ShortMessage shortMessage = (ShortMessage) message;
      if (Utilities.isNote(shortMessage)) {
        int note = shortMessage.getData1();
        if (Utilities.isNoteOn(shortMessage)) {
          listener.onMidiKeyDown(note);
        } else {
          listener.onMidiKeyUp(note);
        }
      }
    }
  }

  public void close() {
    device.close();
  }
}