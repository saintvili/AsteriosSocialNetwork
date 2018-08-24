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
import ru.asterios.open.ProfileActivity;
import ru.asterios.open.R;
import ru.asterios.open.ViewItemActivity;
import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;
import ru.asterios.open.model.Item;
import ru.asterios.open.util.CustomRequest;
import ru.asterios.open.util.ItemInterface;
import ru.asterios.open.util.TagClick;
import ru.asterios.open.util.TagSelectingTextview;
import ru.asterios.open.view.ResizableImageView;

public class GroupListAdapter extends BaseAdapter implements Constants, TagClick {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Item> itemsList;

    private ItemInterface responder;


    TagSelectingTextview mTagSelectingTextview;

    public static int hashTagHyperLinkEnabled = 1;
    public static int hashTagHyperLinkDisabled = 0;

    ImageLoader imageLoader = App.getInstance().getImageLoader();

    public GroupListAdapter(Activity activity, List<Item> itemsList, ItemInterface responder) {

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
        public EmojiconTextView itemPost;
        public TextView itemTimeAgo;
        public TextView itemLikesCount;
        public TextView itemCommentsCount;
        public TextView itemRePostsCount;
        public ImageView itemLike;
        public ImageView itemRePost;
        public ImageView itemComment;
        public ImageView itemAction;
        public CircularImageView itemAuthorPhoto;

        public LinearLayout rePostContainer;
        public ResizableImageView rePostItemImg;
        public CircularImageView rePostItemAuthorPhoto;
        public TextView rePostItemAuthor;
        public TextView rePostItemUsername;
        public EmojiconTextView rePostItemPost;
        public TextView rePostItemTimeAgo;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (inflater == null) {

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.group_item_list_row, null);

            viewHolder = new ViewHolder();

            viewHolder.itemImg = (ResizableImageView) convertView.findViewById(R.id.itemImg);
            viewHolder.itemAuthor = (TextView) convertView.findViewById(R.id.itemAuthor);
            viewHolder.itemPost = (EmojiconTextView) convertView.findViewById(R.id.itemPost);
            viewHolder.itemTimeAgo = (TextView) convertView.findViewById(R.id.itemTimeAgo);
            viewHolder.itemLikesCount = (TextView) convertView.findViewById(R.id.itemLikesCount);
            viewHolder.itemRePostsCount = (TextView) convertView.findViewById(R.id.itemRePostsCount);
            viewHolder.itemCommentsCount = (TextView) convertView.findViewById(R.id.itemCommentsCount);
            viewHolder.itemLike = (ImageView) convertView.findViewById(R.id.itemLike);
            viewHolder.itemRePost = (ImageView) convertView.findViewById(R.id.itemRePost);
            viewHolder.itemComment = (ImageView) convertView.findViewById(R.id.itemComment);
            viewHolder.itemAction = (ImageView) convertView.findViewById(R.id.itemAction);
            viewHolder.itemAuthorPhoto = (CircularImageView) convertView.findViewById(R.id.itemAuthorPhoto);

            viewHolder.rePostContainer = (LinearLayout) convertView.findViewById(R.id.rePostContainer);
            viewHolder.rePostItemAuthorPhoto = (CircularImageView) convertView.findViewById(R.id.rePostItemAuthorPhoto);
            viewHolder.rePostItemImg = (ResizableImageView) convertView.findViewById(R.id.rePostItemImg);
            viewHolder.rePostItemAuthor = (TextView) convertView.findViewById(R.id.rePostItemAuthor);
            viewHolder.rePostItemUsername = (TextView) convertView.findViewById(R.id.rePostItemUsername);
            viewHolder.rePostItemPost = (EmojiconTextView) convertView.findViewById(R.id.rePostItemPost);
            viewHolder.rePostItemTimeAgo = (TextView) convertView.findViewById(R.id.rePostItemTimeAgo);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }

        viewHolder.itemImg.setTag(position);
        viewHolder.itemAuthor.setTag(position);
        viewHolder.itemPost.setTag(position);
        viewHolder.itemTimeAgo.setTag(position);
        viewHolder.itemLikesCount.setTag(position);
        viewHolder.itemRePostsCount.setTag(position);
        viewHolder.itemCommentsCount.setTag(position);
        viewHolder.itemLike.setTag(position);
        viewHolder.itemRePost.setTag(position);
        viewHolder.itemComment.setTag(position);
        viewHolder.itemAction.setTag(position);
        viewHolder.itemAuthorPhoto.setTag(position);

        viewHolder.rePostContainer.setTag(position);
        viewHolder.rePostItemImg.setTag(position);
        viewHolder.rePostItemAuthorPhoto.setTag(position);
        viewHolder.rePostItemAuthor.setTag(position);
        viewHolder.rePostItemUsername.setTag(position);
        viewHolder.rePostItemPost.setTag(position);
        viewHolder.rePostItemTimeAgo.setTag(position);

        final Item item = itemsList.get(position);

        if (item.getRePostId() != 0 && item.getRePostRemoveAt() == 0) {

            viewHolder.rePostContainer.setVisibility(View.VISIBLE);

            viewHolder.rePostItemTimeAgo.setText(item.getRePostTimeAgo());
            viewHolder.rePostItemTimeAgo.setVisibility(View.VISIBLE);

            viewHolder.rePostItemAuthor.setText(item.getRePostFromUserFullname());
            viewHolder.rePostItemUsername.setText("@" + item.getRePostFromUserUsername());

            if (item.getRePostFromUserVerify() == 1) {

                viewHolder.rePostItemAuthor.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.profile_verify_icon, 0);

            } else {

                viewHolder.rePostItemAuthor.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            if (item.getRePostFromUserPhotoUrl().length() != 0) {

                viewHolder.rePostItemAuthorPhoto.setVisibility(View.VISIBLE);

                imageLoader.get(item.getRePostFromUserPhotoUrl(), ImageLoader.getImageListener(viewHolder.rePostItemAuthorPhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

            } else {

                viewHolder.rePostItemAuthorPhoto.setVisibility(View.VISIBLE);
                viewHolder.rePostItemAuthorPhoto.setImageResource(R.drawable.profile_default_photo);
            }

            viewHolder.rePostItemAuthorPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(activity, ViewItemActivity.class);
                    intent.putExtra("itemId", item.getRePostId());
                    activity.startActivity(intent);
                }
            });

            viewHolder.rePostItemAuthor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(activity, ViewItemActivity.class);
                    intent.putExtra("itemId", item.getRePostId());
                    activity.startActivity(intent);
                }
            });

            viewHolder.rePostItemUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(activity, ViewItemActivity.class);
                    intent.putExtra("itemId", item.getRePostId());
                    activity.startActivity(intent);
                }
            });

            if (item.getRePostPost().length() > 0) {

                viewHolder.rePostItemPost.setText(item.getRePostPost().replaceAll("<br>", "\n"));

                viewHolder.rePostItemPost.setVisibility(View.VISIBLE);

                viewHolder.rePostItemPost.setMovementMethod(LinkMovementMethod.getInstance());

                String textHtml = item.getRePostPost();

                viewHolder.rePostItemPost.setText(mTagSelectingTextview.addClickablePart(Html.fromHtml(textHtml).toString(), this, hashTagHyperLinkDisabled, HASHTAGS_COLOR), TextView.BufferType.SPANNABLE);

            } else {

                viewHolder.rePostItemPost.setVisibility(View.GONE);
            }

            if (item.getRePostImgUrl().length() > 0) {

                imageLoader.get(item.getRePostImgUrl(), ImageLoader.getImageListener(viewHolder.rePostItemImg, R.drawable.img_loading, R.drawable.img_loading));
                viewHolder.rePostItemImg.setVisibility(View.VISIBLE);

                viewHolder.rePostItemImg.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(activity, PhotoViewActivity.class);
                        i.putExtra("imgUrl", item.getRePostImgUrl());
                        activity.startActivity(i);
                    }
                });

            } else {

                viewHolder.rePostItemImg.setVisibility(View.GONE);
            }

        } else {

            viewHolder.rePostContainer.setVisibility(View.GONE);
        }


        viewHolder.itemAuthor.setText(item.getFromUserFullname());

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

        viewHolder.itemAuthorPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (item.getFromUserId() != item.getGroupId()) {

                    Intent intent = new Intent(activity, ProfileActivity.class);
                    intent.putExtra("profileId", item.getFromUserId());
                    activity.startActivity(intent);
                }
            }
        });

        viewHolder.itemAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (item.getFromUserId() != item.getGroupId()) {

                    Intent intent = new Intent(activity, ProfileActivity.class);
                    intent.putExtra("profileId", item.getFromUserId());
                    activity.startActivity(intent);
                }
            }
        });

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

        viewHolder.itemRePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (App.getInstance().getId() == item.getFromUserId()) {

                    Toast.makeText(activity, activity.getString(R.string.msg_post_has_shared_error), Toast.LENGTH_SHORT).show();

                } else {

                    if (!item.isMyRePost()) {

                        final int getPosition = (Integer) view.getTag();

                        responder.repost(getPosition);

                    } else {

                        Toast.makeText(activity, activity.getString(R.string.msg_post_has_been_already_shared), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if (item.isMyRePost()) {

            viewHolder.itemRePost.setImageResource(R.drawable.repost_active);

        } else {

            viewHolder.itemRePost.setImageResource(R.drawable.repost);
        }

        if (item.getRePostsCount() > 0) {

            viewHolder.itemRePostsCount.setText(Integer.toString(item.getRePostsCount()));
            viewHolder.itemRePostsCount.setVisibility(View.VISIBLE);

        } else {

            viewHolder.itemRePostsCount.setText(Integer.toString(item.getRePostsCount()));
            viewHolder.itemRePostsCount.setVisibility(View.GONE);
        }

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


        if (item.getPost().length() > 0) {

            viewHolder.itemPost.setText(item.getPost().replaceAll("<br>", "\n"));

            viewHolder.itemPost.setVisibility(View.VISIBLE);

            viewHolder.itemPost.setMovementMethod(LinkMovementMethod.getInstance());

            String textHtml = item.getPost();

            viewHolder.itemPost.setText(mTagSelectingTextview.addClickablePart(Html.fromHtml(textHtml).toString(), this, hashTagHyperLinkDisabled, HASHTAGS_COLOR), TextView.BufferType.SPANNABLE);

        } else {

            viewHolder.itemPost.setVisibility(View.GONE);
        }

        if (item.getImgUrl().length() > 0) {

            imageLoader.get(item.getImgUrl(), ImageLoader.getImageListener(viewHolder.itemImg, R.drawable.img_loading, R.drawable.img_loading));
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

    @Override
    public void clickedTag(CharSequence tag) {
        // TODO Auto-generated method stub

        Intent i = new Intent(activity, HashtagsActivity.class);
        i.putExtra("hashtag", tag);
        activity.startActivity(i);
    }
}