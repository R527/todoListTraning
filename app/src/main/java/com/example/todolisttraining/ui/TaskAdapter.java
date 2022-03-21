package com.example.todolisttraining.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolisttraining.R;
import com.example.todolisttraining.db.TaskEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * リストの表示データとリストUIをひもつける
 *
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>{
    private static final String TAG = "TaskAdapter";

    private DeleteTaskListener mListener;
    private List<TaskEntity> mData;

    public TaskAdapter() {
        mData = new ArrayList<TaskEntity>();
    }


    //ViewHolderとはタスク１とデリートボタンなどをまとめている一つのアイテムを指す
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        private final Button deleteTaskButton;
        private final ImageView importantIcon;

        public ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.task_text);
            deleteTaskButton = (Button) v.findViewById(R.id.delete_task_button);
            importantIcon = (ImageView) v.findViewById(R.id.important_icon);
        }

        public TextView getTextView() {
            return textView;
        }

        public Button getDeleteTaskButton() {
            return deleteTaskButton;
        }

        public ImageView getImportantIcon() {
            return importantIcon;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);

        return new ViewHolder(v);
    }

    //アイテム追加、削除時更新する
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "Element " + position + " set.");

        holder.getTextView().setText(mData.get(position).getText());
        if(mData.get(position).isImportant){
            holder.getImportantIcon().setVisibility(View.VISIBLE);
        }else{
            holder.getImportantIcon().setVisibility(View.GONE);
        }

        holder.getDeleteTaskButton().setTag(position);
        holder.getDeleteTaskButton().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Element " + v.getTag() + " deleted.");
                if(mListener != null) {
                    mListener.onClickDeleteTask((int)v.getTag());
                    //notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<TaskEntity> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void setDeleteTaskListener(DeleteTaskListener listener) {
        mListener = listener;
    }
}
