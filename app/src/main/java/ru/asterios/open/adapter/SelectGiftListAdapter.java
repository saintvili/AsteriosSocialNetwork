package ru.asterios.open.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import ru.asterios.open.R;
import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;


public class SelectGiftListAdapter extends BaseAdapter implements Constants {

	private Activity activity;
	private LayoutInflater inflater;
	private List<BaseGift> itemsList;;

    ImageLoader imageLoader = App.getInstance().getImageLoader();

	public SelectGiftListAdapter(Activity activity, List<BaseGift> itemsList) {

		this.activity = activity;
		this.itemsList = itemsList;
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
        public TextView itemCost;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;

		if (inflater == null) {

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

		if (convertView == null) {
			
			convertView = inflater.inflate(R.layout.base_gift_list_row, null);
			
			viewHolder = new ViewHolder();

            viewHolder.itemImg = (ImageView) convertView.findViewById(R.id.itemImg);
            viewHolder.itemCost = (TextView) convertView.findViewById(R.id.itemCost);

            convertView.setTag(viewHolder);

		} else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }

        viewHolder.itemImg.setTag(position);
        viewHolder.itemCost.setTag(position);
		
		final BaseGift item = itemsList.get(position);

        viewHolder.itemCost.setText(Integer.toString(item.getCost()) + " (" + activity.getString(R.string.label_credits) + ")");

        imageLoader.get(item.getImgUrl(), ImageLoader.getImageListener(viewHolder.itemImg, R.drawable.app_logo, R.drawable.app_logo));

		return convertView;
	}
}