package ru.asterios.open.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.pkmmte.view.CircularImageView;

import java.util.List;

import ru.asterios.open.R;
import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;
import ru.asterios.open.model.Group;


public class CommunityListAdapter extends BaseAdapter {

	private Activity activity;
	private LayoutInflater inflater;
	private List<Group> itemList;

	ImageLoader imageLoader = App.getInstance().getImageLoader();

	public CommunityListAdapter(Activity activity, List<Group> itemList) {

		this.activity = activity;
		this.itemList = itemList;
	}

	@Override
	public int getCount() {

		return itemList.size();
	}

	@Override
	public Object getItem(int location) {

		return itemList.get(location);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}
	
	static class ViewHolder {

        public TextView groupFollowers;
		public TextView groupFullname;
		public CircularImageView groupPhoto;
	        
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;

		if (inflater == null) {

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

		if (convertView == null) {
			
			convertView = inflater.inflate(R.layout.group_list_row, null);
			
			Group u = itemList.get(position);
			
			viewHolder = new ViewHolder();
			
			viewHolder.groupPhoto = (CircularImageView) convertView.findViewById(R.id.groupPhoto);
			viewHolder.groupFullname = (TextView) convertView.findViewById(R.id.groupFullname);
            viewHolder.groupFollowers = (TextView) convertView.findViewById(R.id.groupFollowers);

			convertView.setTag(viewHolder);

		} else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.groupFullname.setTag(position);
		viewHolder.groupPhoto.setTag(position);
		viewHolder.groupFollowers.setTag(position);
		
		final Group u = itemList.get(position);

        viewHolder.groupFullname.setText(u.getFullname());

        viewHolder.groupFollowers.setText(activity.getString(R.string.label_followers) + ": " + Integer.toString(u.getFollowersCount()));
		
        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }

        if (!u.isVerify()) {

            viewHolder.groupFullname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        } else {

            viewHolder.groupFullname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.profile_verify_icon, 0);
        }

        if (u.getLowPhotoUrl() != null && u.getLowPhotoUrl().length() > 0 && u.getState() == Constants.ACCOUNT_STATE_ENABLED) {

            imageLoader.get(u.getLowPhotoUrl(), ImageLoader.getImageListener(viewHolder.groupPhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

        } else {

            viewHolder.groupPhoto.setImageResource(R.drawable.profile_default_photo);
        }

		return convertView;
	}
}