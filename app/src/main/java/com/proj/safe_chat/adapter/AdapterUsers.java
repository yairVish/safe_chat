package com.proj.safe_chat.adapter;

import android.annotation.SuppressLint;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.proj.safe_chat.MainActivity;
import com.proj.safe_chat.R;
import com.proj.safe_chat.roomsql.Message;
import com.proj.safe_chat.roomsql.NoteViewModel;
import com.proj.safe_chat.tools.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.ViewHolder>{
    private Context context;
    private List<User> users;
    private OnItemClickListener mListener;
    private NoteViewModel noteViewModel;
    private String myUid;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    public AdapterUsers(Context context, List<User> users, String myUid){
        this.context = context;
        this.users = users;
        this.myUid=myUid;
        noteViewModel= ViewModelProviders.of((FragmentActivity) context).get(NoteViewModel.class);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_participants,parent,false);
        return new AdapterUsers.ViewHolder(view, mListener);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position){
        noteViewModel.getAllMessagesNotShow(users.get(position).getUnique_id())
                .observe((LifecycleOwner) context, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages){
                Log.d("TAG", "onChanged getAllMessagesNotShow: "+position);

                if(messages.size()>=1){
                    String sn=String.valueOf(messages.size());
                    if(messages.size()>=99){
                        sn="+99";
                    }
                    holder.badgeText.setText(sn);
                    holder.badgeBack.setVisibility(View.VISIBLE);
                }else{
                    holder.badgeBack.setVisibility(View.GONE);
                }
            }
        });
        holder.textName.setText(users.get(position).getName());
        noteViewModel.getLastMessage(myUid, users.get(position).getUnique_id())
                .observe((LifecycleOwner) context, new Observer<Message>() {
            @Override
            public void onChanged(Message message) {
                if (message != null) {
                    Log.d("TAG", "onChanged: getLastMessage - "+users.get(position).getName());
                    /*Log.d("TAG", "message:mLastPosition: " + position);
                    Log.d("TAG", "message.getUid(): " + message.getToId());
                    Log.d("TAG", "message.getBody(): " + message.getBody());
                    Log.d("TAG", "message.getFrom(): " + message.getFromId());
                    Log.d("TAG", "message.isShow(): " + message.isShow());*/
                    String msg = message.getBody().trim();
                    String msgOut = message.getBody().trim();
                    if (msg.length() > 30) {
                        msgOut = msg.substring(0, 20) + "...";
                    }
                    if (myUid.equals(message.getFromId().trim())) {
                        holder.textEmail.setText("You: " + msgOut);
                        Log.d("TAG", "onChanged: msgOut - "+msgOut);

                    } else {
                        holder.textEmail.setText(getUserById(message.getFromId()
                                .trim(), position).getName() + ": " + msgOut);
                        Log.d("TAG", "onChanged: msgOut2 - "+msgOut);
                    }
                } else {
                    holder.textEmail.setText(users.get(position).getEmail());
                }
            }
        });
    }

    public User getUserById(String uid,int position){
        for(User user : users){
            if(user.getUnique_id().equals(uid)){
                return user;
            }
        }
        return users.get(position);
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
