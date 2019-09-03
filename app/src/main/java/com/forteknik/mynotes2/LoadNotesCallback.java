package com.forteknik.mynotes2;



import com.forteknik.mynotes2.entity.NoteModel;

import java.util.ArrayList;

public interface LoadNotesCallback {
    void preExecute();

    void postExecute(ArrayList<NoteModel> noteModels);

}
