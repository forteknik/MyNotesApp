package com.forteknik.mynotes2.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.forteknik.mynotes2.CostumeOnItemClickListener;
import com.forteknik.mynotes2.NoteAddUpdateActivity;
import com.forteknik.mynotes2.R;
import com.forteknik.mynotes2.entity.NoteModel;


import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private ArrayList<NoteModel> listNotes = new ArrayList<>();
    private Activity activity;


    public NoteAdapter(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<NoteModel> getListNotes() {
        return listNotes;
    }

    @NonNull
    @Override
    public NoteAdapter.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);

        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.NoteViewHolder holder, int position) {
        holder.tvTitle.setText(listNotes.get(position).getTitle());
        holder.tvDate.setText(listNotes.get(position).getDate());
        holder.tvDescription.setText(listNotes.get(position).getDescription());

        holder.cvNote.setOnClickListener(new CostumeOnItemClickListener(position, new CostumeOnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent(activity, NoteAddUpdateActivity.class);
                intent.putExtra(NoteAddUpdateActivity.EXTRA_POSITION, position);
                intent.putExtra(NoteAddUpdateActivity.EXTRA_NOTE, listNotes.get(position));
                activity.startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_UPDATE);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return listNotes.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvDescription, tvDate;
        final CardView cvNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvTitle = itemView.findViewById(R.id.tv_title);

            cvNote = itemView.findViewById(R.id.cv_note);
        }
    }


    public void setListNote(ArrayList<NoteModel> listNotes) {
        if (listNotes.size() > 0) {
            this.listNotes.clear();
        }
        this.listNotes.addAll(listNotes);
        notifyDataSetChanged();

    }

    public void addItem(NoteModel noteModel) {
        this.listNotes.add(noteModel);
        notifyItemInserted(listNotes.size() - 1);
    }

    public void updateItem(int position, NoteModel noteModel) {
        this.listNotes.set(position, noteModel);
        notifyItemChanged(position, noteModel);

    }

    public void deleteItem(int position) {
        this.listNotes.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listNotes.size());
    }
}
