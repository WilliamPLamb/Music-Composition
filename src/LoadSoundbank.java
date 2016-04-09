/*
 *	LoadSoundbank.java
 *
 *	This file is part of jsresources.org
 */

/*
 * Copyright (c) 2003 by Matthias Pfisterer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Instrument;


/**	<titleabbrev>LoadSoundbank</titleabbrev>
 <title>Using custom Soundbanks</title>

 <formalpara><title>Purpose</title>
 <para>Loads a custom soundbank and uses its instruments. One note is
 played to verify successful loading.</para>
 </formalpara>

 <formalpara><title>Usage</title>
 <para>
 <cmdsynopsis><command>java LoadSoundbank</command>
 <arg choice="opt">
 <replaceable class="parameter">soundbank</replaceable>
 </arg>
 </cmdsynopsis>
 </para></formalpara>

 <formalpara><title>Parameters</title>
 <variablelist>
 <varlistentry>
 <term><replaceable class="parameter">soundbank</replaceable></term>
 <listitem><para>the filename of a custom soundbank to be loaded. If no
 soundbank is specified, the default soundbank of the synthesizer is used.
 If there is no default soundbank, no sound will be produced (without an
 error message).</para></listitem>
 </varlistentry>
 </variablelist>
 </formalpara>

 <formalpara><title>Bugs, limitations</title>
 <para>Using a custom soundbank even if no default soundbank is
 available only works with JDK 1.5.0 and later.</para>
 </formalpara>

 <formalpara><title>Source code</title>
 <para>
 <ulink url="LoadSoundbank.java.html">LoadSoundbank.java</ulink>
 </para>
 </formalpara>

 */
public class LoadSoundbank// extends JApplet
{
    private static final boolean DEBUG = true;
    private static int noteVelocity = 200;
    private static int noteDuration = 1000;
    private static final int CHORD_THREAD_NUMBER = 10;
    private final ExecutorService chordPool;
    private final ExecutorService melodyPool;

    public LoadSoundbank() {
        chordPool = Executors.newFixedThreadPool(CHORD_THREAD_NUMBER);
        melodyPool = Executors.newFixedThreadPool(1);
    }

    public ExecutorService getChordPool() {
        return chordPool;
    }

    public ExecutorService getMelodyPool() {
        return melodyPool;
    }

    public static void main(String[] args)//s init()
    {
        LoadSoundbank play = new LoadSoundbank();
//        int	nNoteNumber = 60;	// MIDI key number
//        int	nVelocity = 300;	// MIDI note on velocity

		/*
		 *	Time between note on and note off event in
		 *	milliseconds. Note that on most systems, the
		 *	best resolution you can expect are 10 ms.
		 */
//        int	nDuration = 2000;

        Soundbank soundbank = null;
//        if (args.length == 1)
//        {
//            File file = new File(args[0]);
//            try {
//                soundbank = MidiSystem.getSoundbank(file);
//            } catch (InvalidMidiDataException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (DEBUG) out("Soundbank: " + soundbank);
//        }
//        else if (args.length > 1)
//        {
//            printUsageAndExit();
//        }


		/*
		 *	We need a synthesizer to play the note on.
		 *	Here, we simply request the default
		 *	synthesizer.
		 */
        Synthesizer	synth = null;
        try {
            synth = MidiSystem.getSynthesizer();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
        if (DEBUG) out("Synthesizer: " + synth);

		/*
		 *	Of course, we have to open the synthesizer to
		 *	produce any sound for us.
		 */
        try {
            synth.open();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
        if (DEBUG) out("Defaut soundbank: " + synth.getDefaultSoundbank());

        if (soundbank != null)
        {
            out("soundbank supported: " + synth.isSoundbankSupported(soundbank));
            boolean bInstrumentsLoaded = synth.loadAllInstruments(soundbank);
            if (DEBUG) out("Instruments loaded: " + bInstrumentsLoaded);
        }
		/*
		 *	Turn the note on on MIDI channel 1.
		 *	(Index zero means MIDI channel 1)
		 */
        MidiChannel[]	channels = synth.getChannels();
        // Display all the available instruments and patches
        int instrumentCounter = 0;
        for (Instrument instrument : synth.getAvailableInstruments()) {
            System.out.println(instrumentCounter++ + ": " + instrument.getName() + ", Patch: " + instrument.getPatch().getProgram());
        }


        System.out.println("Number of Available Channels: " + channels.length);
        MidiChannel channel = channels[0];
        // Print the current channel's instrument's patch's program number
        System.out.println("Current instrument number: " + channel.getProgram());
        // Change instrument
        channel.programChange(0);
        // Verify the change
        System.out.println("New current instrument number: " + channel.getProgram());

        play.melody1(channel);
        play.getChordPool().shutdown();
        play.getMelodyPool().shutdown();
        // old example code
//        channels[0].noteOn(nNoteNumber, nVelocity);

		/*
		 *	Wait for the specified amount of time
		 *	(the duration of the note).
		 */
//        try
//        {
//            Thread.sleep(nDuration);
//        }
//        catch (InterruptedException e)
//        {
//        }

		/*
		 *	Turn the note off.
		 */
//        channels[0].noteOff(nNoteNumber);
    }

    private void melody1(MidiChannel channel) {
        noteDuration = 1000;
        ParallelNote a3 = new ParallelNote(Note.A3, noteVelocity, noteDuration, channel);
        ParallelNote b3 = new ParallelNote(Note.B3, noteVelocity, noteDuration, channel);
        ParallelNote c4 = new ParallelNote(Note.Cs4, noteVelocity, noteDuration, channel);
        ParallelNote d4 = new ParallelNote(Note.D4, noteVelocity, noteDuration, channel);
        ParallelNote e4 = new ParallelNote(Note.E4, noteVelocity, noteDuration, channel);
        ParallelNote f4 = new ParallelNote(Note.Fs4, noteVelocity, noteDuration, channel);
        ParallelNote g4 = new ParallelNote(Note.Gs4, noteVelocity, noteDuration, channel);
        ParallelNote a4 = new ParallelNote(Note.A4, noteVelocity, noteDuration, channel);
        ParallelNote e5 = new ParallelNote(Note.E5, noteVelocity, noteDuration, channel);
        ParallelNote f5 = new ParallelNote(Note.Fs5, noteVelocity, noteDuration, channel);
        ParallelNote g5 = new ParallelNote(Note.Gs5, noteVelocity, noteDuration, channel);
        noteDuration = 2000;
        ParallelNote a3l = new ParallelNote(Note.A3, noteVelocity, noteDuration, channel);
        ParallelNote b3l = new ParallelNote(Note.B3, noteVelocity, noteDuration, channel);
        ParallelNote c4l = new ParallelNote(Note.Cs4, noteVelocity, noteDuration, channel);

        //onlyChord(a4, c4, e4);
        //onlyChord(a4, d4, f4);
        //onlyChord(a4, c4, e4);
        //onlyChord(b3, g4, e4);
        onlyChord(a4, c4, e4);
        melody(e5);
        melody(f5);
        melody(g5);
        melody(f5);
        melody(e5);


    }

    private void melody2(MidiChannel channel) {
        noteDuration = 400;
        ParallelNote b3 = new ParallelNote(59, noteVelocity, noteDuration, channel);
        ParallelNote c4 = new ParallelNote(61, noteVelocity, noteDuration, channel);
        ParallelNote d4 = new ParallelNote(62, noteVelocity, noteDuration, channel);
        ParallelNote e4 = new ParallelNote(64, noteVelocity, noteDuration, channel);
        ParallelNote e4x2 = new ParallelNote(64, noteVelocity, noteDuration * 2, channel);
        ParallelNote f4 = new ParallelNote(65, noteVelocity, noteDuration, channel);
        ParallelNote g4 = new ParallelNote(67, noteVelocity, noteDuration, channel);
        ParallelNote a4 = new ParallelNote(69, noteVelocity, noteDuration, channel);
        ParallelNote a42 = new ParallelNote(69, noteVelocity, noteDuration / 2, channel);
        ParallelNote b42 = new ParallelNote(71, noteVelocity, noteDuration / 2, channel);
        ParallelNote c52 = new ParallelNote(72, noteVelocity, noteDuration / 2, channel);
        ParallelNote d52 = new ParallelNote(74, noteVelocity, noteDuration / 2, channel);
        ParallelNote e5x2b = new ParallelNote(76, noteVelocity, noteDuration * 2, channel);


        a4.run();
        e4x2.run();
        a4.run();
        a42.run();
        b42.run();
        c52.run();
        d52.run();
        e5x2b.run();
    }

    private static void printUsageAndExit()
    {
        out("LoadSoundbank: usage:");
        out("java LoadSoundbank [<soundbankfilename>]");
        System.exit(1);
    }


    private static void out(String strMessage)
    {
        System.out.println(strMessage);
    }

    private ParallelNote recycle(ParallelNote note) {
        try {
            note.join();
            ParallelNote newNote = note.recycleParallelNote();
            return newNote;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Plays a chord of three notes, and doesn't block melody notes
     * @pre all notes have the same duration
     * @param note1
     * @param note2
     * @param note3
     */
    private void chord(ParallelNote note1, ParallelNote note2, ParallelNote note3) {
        ParallelNote nullNote = new ParallelNote(note1.getNoteNumber(), 0,
                note1.getNoteDuration(), note1.getChannel());
        chordPool.execute(note1);
        chordPool.execute(note2);
        chordPool.execute(note3);
        for (int i = 3; i < CHORD_THREAD_NUMBER; i++) {
            chordPool.execute(nullNote);
        }
    }

    /**
     * Plays a chord of three notes, and blocks melody notes
     * @pre all notes have the same duration
     * @param note1
     * @param note2
     * @param note3
     */
    private void onlyChord(ParallelNote note1, ParallelNote note2, ParallelNote note3) {
        ParallelNote nullNote = new ParallelNote(note1.getNoteNumber(), 0,
                note1.getNoteDuration(), note1.getChannel());
        chordPool.execute(note1);
        chordPool.execute(note2);
        chordPool.execute(note3);
        for (int i = 3; i < CHORD_THREAD_NUMBER; i++) {
            chordPool.execute(nullNote);
        }
        melody(nullNote);
    }

    /**
     * Plays a chord of two notes, and doesn't block melody notes.
     * Third note is blocked for duration of the other notes.
     * @pre all notes have the same duration
     * @param note1
     * @param note2
     */
    private void chord(ParallelNote note1, ParallelNote note2) {
        ParallelNote nullNote = new ParallelNote(note1.getNoteNumber(), 0,
                note1.getNoteDuration(), note1.getChannel());
        chordPool.execute(note1);
        chordPool.execute(note2);
        for (int i = 2; i < CHORD_THREAD_NUMBER; i++) {
            chordPool.execute(nullNote);
        }
    }

    /**
     * Plays a chord of two notes, and blocks melody notes.
     * Third note is blocked for duration of the other notes.
     * @pre all notes have the same duration
     * @param note1
     * @param note2
     */
    private void onlyChord(ParallelNote note1, ParallelNote note2) {
        ParallelNote nullNote = new ParallelNote(note1.getNoteNumber(), 0,
                note1.getNoteDuration(), note1.getChannel());
        chordPool.execute(note1);
        chordPool.execute(note2);
        chordPool.execute(note1);
        for (int i = 2; i < CHORD_THREAD_NUMBER; i++) {
            chordPool.execute(nullNote);
        }
        melody(nullNote);
    }

    /**
     * Plays a single melody note and doesn't block chords
     * @param note
     */
    private void melody(ParallelNote note) {
        melodyPool.execute(note);
    }

    /**
     * Plays melody notes one after another from the ArrayList and doesn't block chords
     * @param notes the ordered ArrayList of notes to be played
     */
    private void melody(ArrayList<ParallelNote> notes) {
        for (ParallelNote note : notes) {
            melodyPool.execute(note);
        }
    }

    /**
     * Plays a single melody note and blocks chords
     * @param note
     */
    private void onlyMelody(ParallelNote note) {
        ParallelNote nullNote = new ParallelNote(note.getNoteNumber(), 0,
                note.getNoteDuration(), note.getChannel());
        melodyPool.execute(note);
        chord(nullNote, nullNote, nullNote);
    }

    /**
     * Plays melody notes one after another from the ArrayList and blocks chords
     * @param notes the ordered ArrayList of notes to be played
     */
    private void onlyMelody(ArrayList<ParallelNote> notes) {
        for (ParallelNote note : notes) {
            ParallelNote nullNote = new ParallelNote(note.getNoteNumber(), 0,
                    note.getNoteDuration(), note.getChannel());
            melodyPool.execute(note);
            chord(nullNote, nullNote, nullNote);
        }
    }

    /**
     * Plays a chord, and doesn't block melody notes
     * @pre all notes have the same duration
     * @pre chord size is less than CHORD_THREAD_NUMBER
     * @param chord
     */
    private void chord(Chord chord) {
        if (chord.getNotes().size() == 0) {
            return;
        }
        ParallelNote nullNote = new ParallelNote(chord.getNotes().get(0).getNoteNumber(), 0,
                chord.getNotes().get(0).getNoteDuration(), chord.getNotes().get(0).getChannel());
        for (ParallelNote note : chord.getNotes()) {
            chordPool.execute(note);
        }
        for (int i = chord.getNotes().size(); i < CHORD_THREAD_NUMBER; i++) {
            chordPool.execute(nullNote);
        }
    }

    /**
     * Plays a chord, and blocks melody notes
     * @pre all notes have the same duration
     * @pre chord size is less than CHORD_THREAD_NUMBER
     * @param chord
     */
    private void onlyChord(Chord chord) {
        if (chord.getNotes().size() == 0) {
            return;
        }
        ParallelNote nullNote = new ParallelNote(chord.getNotes().get(0).getNoteNumber(), 0,
                chord.getNotes().get(0).getNoteDuration(), chord.getNotes().get(0).getChannel());
        for (ParallelNote note : chord.getNotes()) {
            chordPool.execute(note);
        }
        for (int i = chord.getNotes().size(); i < CHORD_THREAD_NUMBER; i++) {
            chordPool.execute(nullNote);
        }
        melody(nullNote);
    }

    /**
     * Plays a chain of melody notes and doesn't block chords
     * @param chord
     */
    private void melody(Chord chord) {
        for (ParallelNote note : chord.getNotes()) {
            melodyPool.execute(note);
        }
    }

    /**
     * Plays a chain of melody notes and blocks chords
     * @param chord
     */
    private void onlyMelody(Chord chord) {
        if (chord.getNotes().size() == 0) {
            return;
        }
        ParallelNote nullNote = new ParallelNote(chord.getNotes().get(0).getNoteNumber(), 0,
                chord.getNotes().get(0).getNoteDuration(), chord.getNotes().get(0).getChannel());
        for (ParallelNote note : chord.getNotes()) {
            melodyPool.execute(note);
        }
        chord(nullNote, nullNote, nullNote);
    }


}



/*** LoadSoundbank.java ***/