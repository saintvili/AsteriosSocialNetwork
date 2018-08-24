package ru.asterios.open.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.pkmmte.view.CircularImageView;

import java.util.List;

import github.ankushsachdeva.emojicon.EmojiconTextView;
import ru.asterios.open.ProfileActivity;
import ru.asterios.open.R;
import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;
import ru.asterios.open.model.Chat;

public class DialogListAdapter extends BaseAdapter implements Constants {

	private Activity activity;
	private LayoutInflater inflater;
	private List<Chat> chatList;

    ImageLoader imageLoader = App.getInstance().getImageLoader();

	public DialogListAdapter(Activity activity, List<Chat> chatList) {

		this.activity = activity;
		this.chatList = chatList;
	}

	@Override
	public int getCount() {

		return chatList.size();
	}

	@Override
	public Object getItem(int location) {

		return chatList.get(location);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}
	
	static class ViewHolder {

        public TextView chatOpponentFullname;
        public EmojiconTextView chatLastMessage;
        public TextView chatLastMessageAgo;
		public TextView chatNewMessagesCount;
		public CircularImageView chatOpponent;
	        
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;

		if (inflater == null) {

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

		if (convertView == null) {
			
			convertView = inflater.inflate(R.layout.chat_list_row, null);
			
			viewHolder = new ViewHolder();

            viewHolder.chatOpponent = (CircularImageView) convertView.findViewById(R.id.chatOpponent);
            viewHolder.chatOpponentFullname = (TextView) convertView.findViewById(R.id.chatOpponentFullname);
			viewHolder.chatLastMessage = (EmojiconTextView) convertView.findViewById(R.id.chatLastMessage);
            viewHolder.chatLastMessageAgo = (TextView) convertView.findViewById(R.id.chatLastMessageAgo);
			viewHolder.chatNewMessagesCount = (TextView) convertView.findViewById(R.id.chatNewMessagesCount);

            convertView.setTag(viewHolder);

            viewHolder.chatOpponent.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int getPosition = (Integer) v.getTag();

                    Chat chat = chatList.get(getPosition);

                    Intent intent = new Intent(activity, ProfileActivity.class);
                    intent.putExtra("profileId", chat.getWithUserId());
                    activity.startActivity(intent);
                }
            });

		} else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }

        viewHolder.chatOpponentFullname.setTag(position);
        viewHolder.chatLastMessage.setTag(position);
        viewHolder.chatLastMessageAgo.setTag(position);
		viewHolder.chatNewMessagesCount.setTag(position);
        viewHolder.chatOpponent.setTag(position);
        viewHolder.chatOpponent.setTag(R.id.notifyAuthor, viewHolder);
		
		final Chat chat = chatList.get(position);

        viewHolder.chatOpponentFullname.setText(chat.getWithUserFullname());

        if (chat.getWithUserPhotoUrl().length() > 0) {

            imageLoader.get(chat.getWithUserPhotoUrl(), ImageLoader.getImageListener(viewHolder.chatOpponent, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

        } else {

            viewHolder.chatOpponent.setImageResource(R.drawable.profile_default_photo);
        }

        if (chat.getWithUserVerify() != 1) {

            viewHolder.chatOpponentFullname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        } else {

            viewHolder.chatOpponentFullname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.profile_verify_icon, 0);
        }

		if (chat.getLastMessage().length() != 0) {

			viewHolder.chatLastMessage.setText(chat.getLastMessage().replaceAll("<br>", " "));

		} else {

			viewHolder.chatLastMessage.setText(activity.getString(R.string.label_last_message_image));
		}

        if (chat.getLastMessageAgo().length() != 0) {

            viewHolder.chatLastMessageAgo.setText(chat.getLastMessageAgo());

        } else {

            viewHolder.chatLastMessageAgo.setText("");
        }

        if (chat.getNewMessagesCount() == 0) {

            viewHolder.chatNewMessagesCount.setVisibility(View.GONE);
            viewHolder.chatNewMessagesCount.setText(Integer.toString(chat.getNewMessagesCount()));

        } else {

            viewHolder.chatNewMessagesCount.setVisibility(View.VISIBLE);
            viewHolder.chatNewMessagesCount.setText(Integer.toString(chat.getNewMessagesCount()));
        }

		return convertView;
	}
}