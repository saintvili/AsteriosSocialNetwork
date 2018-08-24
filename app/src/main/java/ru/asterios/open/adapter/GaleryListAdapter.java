package ru.asterios.open.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.pkmmte.view.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import github.ankushsachdeva.emojicon.EmojiconTextView;
import ru.asterios.open.HashtagsActivity;
import ru.asterios.open.LikesActivity;
import ru.asterios.open.PhotoViewActivity;
import ru.asterios.open.R;
import ru.asterios.open.ViewItemActivity;
import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;
import ru.asterios.open.model.Photo;
import ru.asterios.open.util.CustomRequest;
import ru.asterios.open.util.PhotoInterface;
import ru.asterios.open.util.TagClick;
import ru.asterios.open.util.TagSelectingTextview;
import ru.asterios.open.view.ResizableImageView;

public class GaleryListAdapter extends BaseAdapter implements Constants, TagClick {

	private Activity activity;
	private LayoutInflater inflater;
	private List<Photo> itemsList;

    private PhotoInterface responder;


    TagSelectingTextview mTagSelectingTextview;

    public static int hashTagHyperLinkEnabled = 1;
    public static int hashTagHyperLinkDisabled = 0;

    ImageLoader imageLoader = App.getInstance().getImageLoader();

	public GaleryListAdapter(Activity activity, List<Photo> itemsList, PhotoInterface responder) {

		this.activity = activity;
		this.itemsList = itemsList;
        this.responder = responder;

        mTagSelectingTextview = new TagSelectingTextview();
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

        public ResizableImageView itemImg;
        public TextView itemAuthor;
        public TextView itemUsername;
        public EmojiconTextView itemPost;
        public TextView itemTimeAgo;
        public TextView itemMode;
        public TextView itemLikesCount;
        public TextView itemCommentsCount;
        public ImageView itemLike;
        public ImageView itemComment;
        public ImageView itemAction;
        public CircularImageView itemAuthorPhoto;
        public TextView itemCity;
        public TextView itemCountry;
        public LinearLayout locationContainer;
        public LinearLayout footerContainer;
	        
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;

		if (inflater == null) {

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

		if (convertView == null) {
			
			convertView = inflater.inflate(R.layout.galery_list_row, null);
			
			viewHolder = new ViewHolder();

            viewHolder.itemImg = (ResizableImageView) convertView.findViewById(R.id.itemImg);
            viewHolder.itemAuthor = (TextView) convertView.findViewById(R.id.itemAuthor);
            viewHolder.itemUsername = (TextView) convertView.findViewById(R.id.itemUsername);
            viewHolder.itemPost = (EmojiconTextView) convertView.findViewById(R.id.itemPost);
            viewHolder.itemTimeAgo = (TextView) convertView.findViewById(R.id.itemTimeAgo);
            viewHolder.itemMode = (TextView) convertView.findViewById(R.id.itemMode);
            viewHolder.itemLikesCount = (TextView) convertView.findViewById(R.id.itemLikesCount);
            viewHolder.itemCommentsCount = (TextView) convertView.findViewById(R.id.itemCommentsCount);
            viewHolder.itemLike = (ImageView) convertView.findViewById(R.id.itemLike);
            viewHolder.itemComment = (ImageView) convertView.findViewById(R.id.itemComment);
            viewHolder.itemAction = (ImageView) convertView.findViewById(R.id.itemAction);
            viewHolder.itemAuthorPhoto = (CircularImageView) convertView.findViewById(R.id.itemAuthorPhoto);
            viewHolder.itemCity = (TextView) convertView.findViewById(R.id.itemCity);
            viewHolder.itemCountry = (TextView) convertView.findViewById(R.id.itemCountry);
            viewHolder.locationContainer = (LinearLayout) convertView.findViewById(R.id.locationContainer);
            viewHolder.footerContainer = (LinearLayout) convertView.findViewById(R.id.postFooter);

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
        viewHolder.itemPost.setTag(position);
        viewHolder.itemTimeAgo.setTag(position);
        viewHolder.itemMode.setTag(position);
        viewHolder.itemLikesCount.setTag(position);
        viewHolder.itemCommentsCount.setTag(position);
        viewHolder.itemLike.setTag(position);
        viewHolder.itemComment.setTag(position);
        viewHolder.itemAction.setTag(position);
        viewHolder.itemAuthorPhoto.setTag(position);
        viewHolder.itemCity.setTag(position);
        viewHolder.itemCountry.setTag(position);
        viewHolder.locationContainer.setTag(position);
        viewHolder.footerContainer.setTag(position);

        viewHolder.footerContainer.setVisibility(View.GONE);
		
		final Photo item = itemsList.get(position);

        if ((item.getCity() != null && item.getCity().length() > 0) || (item.getCountry() != null && item.getCountry().length() > 0)) {

            if (item.getCity() != null && item.getCity().length() > 0) {

                viewHolder.itemCity.setText(item.getCity());
                viewHolder.itemCity.setVisibility(View.VISIBLE);

            } else {

                viewHolder.itemCity.setVisibility(View.GONE);
            }

            if (item.getCountry() != null && item.getCountry().length() > 0) {

                viewHolder.itemCountry.setText(item.getCountry());
                viewHolder.itemCountry.setVisibility(View.VISIBLE);

            } else {

                viewHolder.itemCountry.setVisibility(View.GONE);
            }

            viewHolder.locationContainer.setVisibility(View.VISIBLE);

        } else {

            viewHolder.locationContainer.setVisibility(View.GONE);
        }

        viewHolder.itemAuthor.setText(item.getFromUserFullname());
        viewHolder.itemUsername.setText("@" + item.getFromUserUsername());

        if (item.getFromUserVerify() == 1) {

            viewHolder.itemAuthor.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.profile_verify_icon, 0);

        } else {

            viewHolder.itemAuthor.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        if (item.getFromUserPhotoUrl().length() != 0) {

            viewHolder.itemAuthorPhoto.setVisibility(View.VISIBLE);

            imageLoader.get(item.getFromUserPhotoUrl(), ImageLoader.getImageListener(viewHolder.itemAuthorPhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

        } else {

            viewHolder.itemAuthorPhoto.setVisibility(View.VISIBLE);
            viewHolder.itemAuthorPhoto.setImageResource(R.drawable.profile_default_photo);
        }

        viewHolder.itemAction.setImageResource(R.drawable.ic_action_collapse);

        viewHolder.itemAction.setVisibility(View.VISIBLE);

        viewHolder.itemAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final int getPosition = (Integer) view.getTag();

                responder.action(getPosition);
            }
        });

        viewHolder.itemLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final int getPosition = (Integer) view.getTag();

                if (App.getInstance().isConnected()) {

                    CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ITEMS_LIKE, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {

                                        if (!response.getBoolean("error")) {

                                            item.setLikesCount(response.getInt("likesCount"));
                                            item.setMyLike(response.getBoolean("myLike"));
                                        }

                                    } catch (JSONException e) {

                                        e.printStackTrace();

                                    } finally {

                                        notifyDataSetChanged();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.error_data_loading), Toast.LENGTH_LONG).show();
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("accountId", Long.toString(App.getInstance().getId()));
                            params.put("accessToken", App.getInstance().getAccessToken());
                            params.put("itemId", Long.toString(item.getId()));

                            return params;
                        }
                    };

                    App.getInstance().addToRequestQueue(jsonReq);

                } else {

                    Toast.makeText(activity.getApplicationContext(), activity.getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (item.isMyLike()) {

            viewHolder.itemLike.setImageResource(R.drawable.perk_active);

        } else {

            viewHolder.itemLike.setImageResource(R.drawable.perk);
        }

        viewHolder.itemComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, ViewItemActivity.class);
                intent.putExtra("itemId", item.getId());
                activity.startActivity(intent);
            }
        });

        viewHolder.itemCommentsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, ViewItemActivity.class);
                intent.putExtra("itemId", item.getId());
                activity.startActivity(intent);
            }
        });

        if (item.getLikesCount() > 0) {

            viewHolder.itemLikesCount.setText(Integer.toString(item.getLikesCount()));
            viewHolder.itemLikesCount.setVisibility(View.VISIBLE);

        } else {

            viewHolder.itemLikesCount.setText(Integer.toString(item.getLikesCount()));
            viewHolder.itemLikesCount.setVisibility(View.GONE);
        }

        if (item.getCommentsCount() > 0) {

            viewHolder.itemCommentsCount.setText(Integer.toString(item.getCommentsCount()));
            viewHolder.itemCommentsCount.setVisibility(View.VISIBLE);

        } else {

            viewHolder.itemCommentsCount.setText(Integer.toString(item.getCommentsCount()));
            viewHolder.itemCommentsCount.setVisibility(View.GONE);
        }

        viewHolder.itemLikesCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, LikesActivity.class);
                intent.putExtra("itemId", item.getId());
                activity.startActivity(intent);
            }
        });

        viewHolder.itemTimeAgo.setText(item.getTimeAgo());
        viewHolder.itemTimeAgo.setVisibility(View.VISIBLE);

        viewHolder.itemMode.setText(getPostModeText(item.getAccessMode()));
        viewHolder.itemMode.setVisibility(View.VISIBLE);


        if (item.getComment().length() > 0) {

            viewHolder.itemPost.setText(item.getComment().replaceAll("<br>", "\n"));

            viewHolder.itemPost.setVisibility(View.VISIBLE);

            viewHolder.itemPost.setMovementMethod(LinkMovementMethod.getInstance());

            String textHtml = item.getComment();

            viewHolder.itemPost.setText(mTagSelectingTextview.addClickablePart(Html.fromHtml(textHtml).toString(), this, hashTagHyperLinkDisabled, HASHTAGS_COLOR), TextView.BufferType.SPANNABLE);

        } else {

            viewHolder.itemPost.setVisibility(View.GONE);
        }

        if (item.getPreviewImgUrl().length() > 0) {

            imageLoader.get(item.getPreviewImgUrl(), ImageLoader.getImageListener(viewHolder.itemImg, R.drawable.img_loading, R.drawable.img_loading));
            viewHolder.itemImg.setVisibility(View.VISIBLE);

            viewHolder.itemImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent i = new Intent(activity, PhotoViewActivity.class);
                    i.putExtra("imgUrl", item.getImgUrl());
                    activity.startActivity(i);
                }
            });

        } else {

            viewHolder.itemImg.setVisibility(View.GONE);
        }

		return convertView;
	}

    public String getPostModeText(int postMode) {

        switch (postMode) {

            case 0: {

                return activity.getString(R.string.label_post_to_public);
            }

            default: {

                return activity.getString(R.string.label_post_to_followers);
            }
        }
    }

    @Override
    public void clickedTag(CharSequence tag) {
        // TODO Auto-generated method stub

        Intent i = new Intent(activity, HashtagsActivity.class);
        i.putExtra("hashtag", tag);
        activity.startActivity(i);
    }
}