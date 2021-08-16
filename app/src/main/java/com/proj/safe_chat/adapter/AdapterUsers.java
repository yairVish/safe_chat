package com.proj.safe_chat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.proj.safe_chat.R;
import com.proj.safe_chat.tools.User;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.ViewHolder>{
    private Context context;
    private List<User> users;
    private OnItemClickListener mListener;


    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    public AdapterUsers(Context context, List<User> users){
        this.context = context;
        this.users = users;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_participants,parent,false);
        return new AdapterUsers.ViewHolder(view, mListener);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position){
        holder.textName.setText(users.get(position).getName());
        holder.textEmail.setText(users.get(position).getEmail());
    }
    @Override
    public int getItemCount() {
        return users.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textName,textEmail,badgeText;
        public LinearLayout badgeBack;
        public ViewHolder(View itemView, OnItemClickListener listener){
            super(itemView);
            textName=itemView.findViewById(R.id.username);
            textEmail=itemView.findViewById(R.id.email);
            badgeText=itemView.findViewById(R.id.badge_text);
            badgeBack=itemView.findViewById(R.id.badge_back);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
