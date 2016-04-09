
import javax.sound.midi.MidiChannel;
import java.util.concurrent.RecursiveAction;
import java.io.File;
import java.io.IOException;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

/**
 * Created by William on 11/14/15.
 */
public class ParallelNote extends Thread {

    private int noteNumber;
    private int noteVolume;
    private int noteDuration;
    private MidiChannel channel;

    public ParallelNote(int noteNumber, int noteVolume, int noteDuration, MidiChannel channel) {
        this.noteNumber = noteNumber;
        this.noteVolume = noteVolume;
        this.noteDuration = noteDuration;
        this.channel = channel;
    }

    @Override
    public void run() {
        System.out.println("Compute worked: " + Note.getNoteName(this.getNoteNumber()));
        channel.noteOn(noteNumber, noteVolume);
        try
        {
            Thread.sleep(noteDuration);
        }
        catch (InterruptedException e)
        {
        }
        channel.noteOff(noteNumber);
    }

    public ParallelNote recycleParallelNote() {
        ParallelNote newNote = new ParallelNote(this.getNoteNumber(),
                this.getnoteVolume(), this.getNoteDuration(),
                this.getChannel());
        System.out.println("Recycle worked: " + Note.getNoteName(this.getNoteNumber()));
        return newNote;
    }

    public int getNoteNumber() {
        return noteNumber;
    }

    public int getnoteVolume() {
        return noteVolume;
    }

    public int getNoteDuration() {
        return noteDuration;
    }

    public MidiChannel getChannel() {
        return channel;
    }

}
