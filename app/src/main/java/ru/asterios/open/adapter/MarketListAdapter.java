package ru.asterios.open.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import ru.asterios.open.R;
import ru.asterios.open.model.MarketItem;
import ru.asterios.open.util.Helper;


public class MarketListAdapter extends RecyclerView.Adapter<MarketListAdapter.MyViewHolder> {

	private Context mContext;
	private List<MarketItem> itemList;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {

        void onItemClick(View view, MarketItem obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {

        this.mOnItemClickListener = mItemClickListener;
    }

	public class MyViewHolder extends RecyclerView.ViewHolder {

		public TextView mItemTitle, mItemPrice;
		public ImageView mItemImg;
		public MaterialRippleLayout mParent;
		public ProgressBar mProgressBar;

		public MyViewHolder(View view) {

			super(view);

			mParent = (MaterialRippleLayout) view.findViewById(R.id.parent);

			mItemImg = (ImageView) view.findViewById(R.id.itemImg);
			mItemTitle = (TextView) view.findViewById(R.id.itemTitle);
			mItemPrice = (TextView) view.findViewById(R.id.itemPrice);
			mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		}
	}


	public MarketListAdapter(Context mContext, List<MarketItem> itemList) {

		this.mContext = mContext;
		this.itemList = itemList;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_thumbnail, parent, false);


		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, final int position) {

		final MarketItem item = itemList.get(position);

        holder.mProgressBar.setVisibility(View.VISIBLE);
        holder.mItemImg.setVisibility(View.GONE);

        if (item.getImgUrl() != null && item.getImgUrl().length() > 0) {

            final ImageView img = holder.mItemImg;
            final ProgressBar progressView = holder.mProgressBar;

            Picasso.with(mContext)
                    .load(item.getImgUrl())
                    .into(holder.mItemImg, new Callback() {

                        @Override
                        public void onSuccess() {

                            progressView.setVisibility(View.GONE);
                            img.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {

                            progressView.setVisibility(View.GONE);
                            img.setVisibility(View.VISIBLE);
                            img.setImageResource(R.drawable.profile_default_photo);
                        }
                    });

        } else {

            holder.mProgressBar.setVisibility(View.GONE);
            holder.mItemImg.setVisibility(View.VISIBLE);

			holder.mItemImg.setImageResource(R.drawable.profile_default_photo);
		}

        holder.mItemTitle.setText(item.getTitle());

        Helper helper = new Helper();

        holder.mItemPrice.setText(mContext.getString(R.string.label_currency) + helper.getFormatedAmount(item.getPrice()));

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