import java.util.ArrayList;

/**
 * Created by William on 12/20/15.
 */
public class Chord {

    private ArrayList<ParallelNote> notes;

    public Chord() {
        notes = new ArrayList<ParallelNote>();
    }

    public Chord(ArrayList<ParallelNote> notes) {
        this.notes = notes;
    }

    public Chord(ParallelNote note1) {
        notes = new ArrayList<ParallelNote>();
        notes.add(note1);
    }

    public Chord(ParallelNote note1, ParallelNote note2) {
        notes = new ArrayList<ParallelNote>();
        this.notes.add(note1);
        this.notes.add(note2);
    }

    public Chord(ParallelNote note1, ParallelNote note2, ParallelNote note3) {
        notes = new ArrayList<ParallelNote>();
        this.notes.add(note1);
        this.notes.add(note2);
        this.notes.add(note3);
    }

    public void addNote(ParallelNote note) {
        this.notes.add(note);
    }


    public void addNote(ParallelNote note1, ParallelNote note2) {
        this.notes.add(note1);
        this.notes.add(note2);
    }


    public void addNote(ParallelNote note1, ParallelNote note2, ParallelNote note3) {
        this.notes.add(note1);
        this.notes.add(note2);
        this.notes.add(note3);
    }

    public void addNote(ArrayList<ParallelNote> notes) {
        for (int i = 0; i < notes.size(); i++) {
            // adds new note if not a duplicate
            if (!this.notes.contains(notes.get(i))) {
                this.notes.add(notes.get(i));
            }
        }
    }

    /**
     * reorders the chord from low to high notes
     * @param chord the chord to be reordered
     * @return the reordered chord
     */
    public Chord lowToHigh(Chord chord) {
        int lowestNote = 0;
        Chord lowToHigh = new Chord();
        ArrayList<ParallelNote> dummyArray = chord.getNotes();
        for (ParallelNote note: dummyArray) {
            lowestNote = Math.min(lowestNote, note.getNoteNumber());
        }
        while (dummyArray.size() > 0) {
            for (int i = 0; i < dummyArray.size(); i++) {
                if (dummyArray.get(i).getNoteNumber() == lowestNote) {
                    lowToHigh.addNote(dummyArray.get(i));
                    dummyArray.remove(i);
                    for (ParallelNote note : dummyArray) {
                        lowestNote = Math.min(lowestNote, note.getNoteNumber());
                    }
                    break;
                }
            }
        }
        return lowToHigh;
    }

    /**
     * reorders the chord from high to low notes
     * @param chord the chord to be reordered
     * @return the reordered chord
     */
    public Chord highToLow(Chord chord) {
        ArrayList<ParallelNote> dummyArray = lowToHigh(chord).getNotes();
        Chord highToLow = new Chord();
        for (int i = dummyArray.size() - 1; i >= 0; i--) {
            highToLow.addNote(dummyArray.get(i));
        }
        return highToLow;
    }


    /**
     * Takes a chord and returns the chord with the root as the base
     * @pre the chord has exactly three notes
     * @param chord
     * @return the chord in root
     */
    public Chord toRoot(Chord chord) {
        if (chord.getNotes().size() != 3) {
            System.out.println("Currently only implemented for Chords of 3 notes in major or minor modes");
            return chord;
        }
        int delta1 = 0;
        int delta2 = 0;
        int delta3 = 0;
        delta1 = chord.getNotes().get(1).getNoteNumber() - chord.getNotes().get(0).getNoteNumber();
        delta2 = chord.getNotes().get(2).getNoteNumber() - chord.getNotes().get(1).getNoteNumber();
        delta3 = chord.getNotes().get(2).getNoteNumber() - chord.getNotes().get(0).getNoteNumber();
        delta1 = delta1 % 12;
        delta2 = delta2 % 12;
        delta3 = delta3 % 12;
        // TODO finish root logic
        if (delta1 == 4 && delta2 == )
    }

    public ArrayList<ParallelNote> getNotes() {
        return notes;
    }
}
