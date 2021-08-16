package com.proj.safe_chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proj.safe_chat.R;
import com.proj.safe_chat.roomsql.Message;

import java.text.SimpleDateFormat;
import java.util.List;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.ViewHolder>{

    private Context context;
    private String myId;
    private List<Message> messages;
    private List<Boolean> bDates;
    public AdapterMessage(Context context,List<Message>messages, String myId){
        this.context=context;
        this.myId=myId;
        this.messages=messages;
    }

    @Override
    public AdapterMessage.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==-1){
            View view = LayoutInflater.from(context).inflate(R.layout.other_chat_item,parent,false);
            return new ViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.my_chat_item,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMessage.ViewHolder holder, int position) {
        holder.message.setText(messages.get(position).getBody().trim());
        SimpleDateFormat formatClock = new SimpleDateFormat("HH:mm");
        SimpleDateFormat formatDate = new SimpleDateFormat("MMM dd EE");
        final String strClock = formatClock.format(messages.get(position).getTime());
        final String strDate = formatDate.format(messages.get(position).getTime());
        //   Log.d("TAG", "strDate: "+strDate);
        //   Log.d("TAG", "strClock: "+strClock);
        holder.clock.setText(strClock);
        if(bDates.get(position)){
            holder.titleDate.setText(strDate);
            holder.titleDate.setVisibility(View.VISIBLE);
        }else{
            holder.titleDate.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (myId.equals(messages.get(position).getFromId().trim())) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setBDates(List<Boolean>bDates){
        this.bDates=bDates;
    }
    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView message;
        public TextView clock,titleDate;
        public ViewHolder(View itemView){
            super(itemView);
            message=itemView.findViewById(R.id.show_message);
            clock=itemView.findViewById(R.id.clock);
            titleDate=itemView.findViewById(R.id.title_date);
        }
    }
}
