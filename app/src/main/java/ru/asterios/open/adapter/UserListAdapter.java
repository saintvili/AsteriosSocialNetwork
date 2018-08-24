package ru.asterios.open.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import ru.asterios.open.R;
import ru.asterios.open.model.Profile;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {

	private Context mContext;
	private List<Profile> itemList;

	public class MyViewHolder extends RecyclerView.ViewHolder {

		public TextView title, status;
		public ImageView thumbnail;

		public MyViewHolder(View view) {

			super(view);

			title = (TextView) view.findViewById(R.id.title);
			status = (TextView) view.findViewById(R.id.status);
			thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
		}
	}


	public UserListAdapter(Context mContext, List<Profile> itemList) {

		this.mContext = mContext;
		this.itemList = itemList;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_grid_row, parent, false);


		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, int position) {

		Profile u = itemList.get(position);
		holder.title.setText(u.getFullname());

		if (u.isOnline()) {

			holder.status.setText(mContext.getString(R.string.label_online));

		} else {

			holder.status.setText(mContext.getString(R.string.label_offline));
		}

        if (!u.isVerify()) {

            holder.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        } else {

            holder.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.profile_verify_icon, 0);
        }

		// loading album cover using Glide library
		Glide.with(mContext).load(u.getNormalPhotoUrl()).into(holder.thumbnail);
	}

	@Override
	public int getItemCount() {

		return itemList.size();
	}
}