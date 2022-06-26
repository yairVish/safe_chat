package com.proj.safe_chat.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.proj.safe_chat.R;
import com.proj.safe_chat.roomsql.Message;
import com.proj.safe_chat.roomsql.NoteViewModel;
import com.proj.safe_chat.tools.KeysJsonI;
import com.proj.safe_chat.tools.MySocket;
import com.proj.safe_chat.tools.MySocketSingleton;
import com.proj.safe_chat.tools.User;

import java.nio.ByteBuffer;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.ViewHolder> implements KeysJsonI {
    private Context context;
    private List<User> users;
    private OnItemClickListener mListener;
    private NoteViewModel noteViewModel;
    private String myUid;
    private MySocket mySocket;

    //ממשק האחראי לקבל האם אחד הitems נלחץ
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    //הגדרת הממשק
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    //בנאי המחלקה
    public AdapterUsers(Context context, List<User> users, String myUid){
        this.context = context;
        this.users = users;
        this.myUid=myUid;
        this.mySocket = MySocketSingleton.getMySocket();
        noteViewModel= ViewModelProviders.of((FragmentActivity) context).get(NoteViewModel.class);
    }
    //נקרא כאשר יש ViewHolder חדש
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_participants,parent,false);
        return new AdapterUsers.ViewHolder(view, mListener);
    }

    //נקרא עבור כל Item
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position){
        byte[] image_bytes = users.get(position).getProfileImage();
        if(image_bytes!=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(image_bytes , 0, image_bytes.length);
            holder.profileImage.setImageBitmap(bitmap);
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mySocket.getProfileImage(users.get(position).getUnique_id(), new MySocket.OnReceiveImage() {
                    @Override
                    public void OnReceive(Bitmap bitmap, String uid) {
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.profileImage.setImageBitmap(bitmap);
                                int bytes = bitmap.getByteCount();
                                ByteBuffer buffer = ByteBuffer.allocate(bytes);
                                bitmap.copyPixelsToBuffer(buffer);

                                byte[] array = buffer.array();
                                for(User user : users){
                                    if(user.getUnique_id().equals(uid))
                                        user.setProfileImage(array);
                                        break;
                                }
                            }
                        });
                    }
                });
            }
        });thread.start();


        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    setImageDialog(((BitmapDrawable) holder.profileImage.getDrawable()).getBitmap());
                }catch (Exception e){
                    setImageDialog(null);
                }
            }
        });

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

    //יוצר את הפופאפ שמציג את תמונת הפרופיל
    private void setImageDialog(Bitmap bitmap) {
        Dialog dialog;
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        final View mView = LayoutInflater.from(context).inflate(R.layout.resize_image_dialog, null);
        mBuilder.setView(mView);
        ImageView ivPhoto = mView.findViewById(R.id.ivPhoto);
        if(bitmap!=null) {
            Drawable d = new BitmapDrawable(context.getResources(), bitmap);
            ivPhoto.setImageDrawable(d);
        }else{
            ivPhoto.setImageDrawable(((Activity)context).getResources().getDrawable(R.drawable.account_circle_big));
        }
        dialog = mBuilder.create();
        dialog.show();
    }

    //מחזיר משתמש על פי id
    public User getUserById(String uid,int position){
        for(User user : users){
            if(user.getUnique_id().equals(uid)){
                return user;
            }
        }
        return users.get(position);
    }

    //מקבל את כמות הitems שצריך ליצור
    @Override
    public int getItemCount() {
        return users.size();
    }

    //מחזיר את מיקום הitem
    @Override
    public long getItemId(int position) {
        return position;
    }

    //מחזיק את האלמנטים הדרושים לitem
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textName,textEmail,badgeText;
        public CircleImageView profileImage;
        public LinearLayout badgeBack;
        public ViewHolder(View itemView, OnItemClickListener listener){
            super(itemView);
            textName=itemView.findViewById(R.id.username);
            textEmail=itemView.findViewById(R.id.email);
            badgeText=itemView.findViewById(R.id.badge_text);
            badgeBack=itemView.findViewById(R.id.badge_back);
            profileImage = itemView.findViewById(R.id.profile_image);
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
