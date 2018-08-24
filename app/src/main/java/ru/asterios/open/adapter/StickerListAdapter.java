package ru.asterios.open.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import ru.asterios.open.R;
import ru.asterios.open.model.Sticker;


public class StickerListAdapter extends RecyclerView.Adapter<StickerListAdapter.MyViewHolder> {

	private Context mContext;
	private List<Sticker> itemList;

	private OnItemClickListener mOnItemClickListener;

	public class MyViewHolder extends RecyclerView.ViewHolder {

		public ImageView mStickerImg;
		public MaterialRippleLayout mParent;
		public ProgressBar mProgressBar;

		public MyViewHolder(View view) {

			super(view);

			mParent = (MaterialRippleLayout) view.findViewById(R.id.parent);

			mStickerImg = (ImageView) view.findViewById(R.id.stickerImg);
			mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		}
	}


	public StickerListAdapter(Context mContext, List<Sticker> itemList) {

		this.mContext = mContext;
		this.itemList = itemList;
	}

	public interface OnItemClickListener {

		void onItemClick(View view, Sticker obj, int position);
	}

	public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {

		this.mOnItemClickListener = mItemClickListener;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_thumbnail, parent, false);


		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Sticker item = itemList.get(position);

		holder.mProgressBar.setVisibility(View.VISIBLE);
		holder.mStickerImg.setVisibility(View.GONE);

		if (item.getImgUrl() != null && item.getImgUrl().length() > 0) {

			final ImageView img = holder.mStickerImg;
			final ProgressBar progressView = holder.mProgressBar;

			Picasso.with(mContext)
					.load(item.getImgUrl())
					.into(holder.mStickerImg, new Callback() {

						@Override
						public void onSuccess() {

							progressView.setVisibility(View.GONE);
							img.setVisibility(View.VISIBLE);
						}

						@Override
						public void onError() {

							progressView.setVisibility(View.GONE);
							img.setVisibility(View.VISIBLE);
							img.setImageResource(R.drawable.sticker_default);
						}
					});

		} else {

			holder.mProgressBar.setVisibility(View.GONE);
			holder.mStickerImg.setVisibility(View.VISIBLE);

			holder.mStickerImg.setImageResource(R.drawable.sticker_default);
		}

		holder.mParent.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				if (mOnItemClickListener != null) {

					mOnItemClickListener.onItemClick(view, item, position);
				}
			}
		});
	}

	@Override
	public int getItemCount() {

		return itemList.size();
	}
}