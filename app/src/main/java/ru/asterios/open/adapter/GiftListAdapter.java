package ru.asterios.open.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.pkmmte.view.CircularImageView;

import java.util.List;

import github.ankushsachdeva.emojicon.EmojiconTextView;
import ru.asterios.open.ProfileActivity;
import ru.asterios.open.R;
import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;
import ru.asterios.open.model.Gift;
import ru.asterios.open.util.GiftInterface;


public class GiftListAdapter extends BaseAdapter implements Constants {

	private Activity activity;
	private LayoutInflater inflater;
	private List<Gift> itemsList;

    private GiftInterface responder;

    ImageLoader imageLoader = App.getInstance().getImageLoader();

	public GiftListAdapter(Activity activity, List<Gift> itemsList, GiftInterface responder) {

		this.activity = activity;
		this.itemsList = itemsList;
        this.responder = responder;
	}

	@Override
	public int getCount() {

		return itemsList.size();
	}

	@Override
	public Object getItem(int location) {

		return itemsList.get(location);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}
	
	static class ViewHolder {

        public ImageView itemImg;
        public TextView itemAuthor;
        public TextView itemUsername;
        public EmojiconTextView itemMessage;
        public TextView itemTimeAgo;
        public ImageView itemAction;
        public CircularImageView itemAuthorPhoto;
	        
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;

		if (inflater == null) {

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

		if (convertView == null) {
			
			convertView = inflater.inflate(R.layout.gift_list_row, null);
			
			viewHolder = new ViewHolder();

            viewHolder.itemImg = (ImageView) convertView.findViewById(R.id.itemImg);
            viewHolder.itemAuthor = (TextView) convertView.findViewById(R.id.itemAuthor);
            viewHolder.itemUsername = (TextView) convertView.findViewById(R.id.itemUsername);
            viewHolder.itemMessage = (EmojiconTextView) convertView.findViewById(R.id.itemMessage);
            viewHolder.itemTimeAgo = (TextView) convertView.findViewById(R.id.itemTimeAgo);
            viewHolder.itemAction = (ImageView) convertView.findViewById(R.id.itemAction);
            viewHolder.itemAuthorPhoto = (CircularImageView) convertView.findViewById(R.id.itemAuthorPhoto);

            convertView.setTag(viewHolder);

		} else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }

        viewHolder.itemImg.setTag(position);
        viewHolder.itemAuthor.setTag(position);
        viewHolder.itemUsername.setTag(position);
        viewHolder.itemMessage.setTag(position);
        viewHolder.itemTimeAgo.setTag(position);
        viewHolder.itemAction.setTag(position);
        viewHolder.itemAuthorPhoto.setTag(position);
		
		final Gift item = itemsList.get(position);


        viewHolder.itemAuthor.setText(item.getGiftFromUserFullname());
        viewHolder.itemUsername.setText("@" + item.getGiftFromUserUsername());

        if (item.getGiftFromUserVerify() == 1) {

            viewHolder.itemAuthor.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.profile_verify_icon, 0);

        } else {

            viewHolder.itemAuthor.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        if (item.getGiftFromUserPhotoUrl().length() != 0) {

            viewHolder.itemAuthorPhoto.setVisibility(View.VISIBLE);

            imageLoader.get(item.getGiftFromUserPhotoUrl(), ImageLoader.getImageListener(viewHolder.itemAuthorPhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

        } else {

            viewHolder.itemAuthorPhoto.setVisibility(View.VISIBLE);
            viewHolder.itemAuthorPhoto.setImageResource(R.drawable.profile_default_photo);
        }

        viewHolder.itemAuthorPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int getPosition = (Integer) v.getTag();

                Gift gift = itemsList.get(getPosition);

                Intent intent = new Intent(activity, ProfileActivity.class);
                intent.putExtra("profileId", gift.getGiftFromUserId());
                activity.startActivity(intent);
            }
        });

        viewHolder.itemAuthor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int getPosition = (Integer) v.getTag();

                Gift gift = itemsList.get(getPosition);

                Intent intent = new Intent(activity, ProfileActivity.class);
                intent.putExtra("profileId", gift.getGiftFromUserId());
                activity.startActivity(intent);
            }
        });

        if (item.getGiftToUserId() == App.getInstance().getId()) {

            viewHolder.itemAction.setImageResource(R.drawable.ic_action_remove);

            viewHolder.itemAction.setVisibility(View.VISIBLE);

            viewHolder.itemAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final int getPosition = (Integer) view.getTag();

                    responder.action(getPosition);
                }
            });

        } else {

            viewHolder.itemAction.setVisibility(View.GONE);
        }

        viewHolder.itemTimeAgo.setText(item.getTimeAgo());
        viewHolder.itemTimeAgo.setVisibility(View.VISIBLE);

        if (item.getMessage().length() > 0) {

            viewHolder.itemMessage.setText(item.getMessage().replaceAll("<br>", "\n"));

            viewHolder.itemMessage.setVisibility(View.VISIBLE);

        } else {

            viewHolder.itemMessage.setVisibility(View.GONE);
        }

        imageLoader.get(item.getImgUrl(), ImageLoader.getImageListener(viewHolder.itemImg, R.drawable.img_loading, R.drawable.img_loading));
        viewHolder.itemImg.setVisibility(View.VISIBLE);

		return convertView;
	}
}