package ru.asterios.open.adapter;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import github.ankushsachdeva.emojicon.EmojiconTextView;
import ru.asterios.open.GroupActivity;
import ru.asterios.open.HashtagsActivity;
import ru.asterios.open.PhotoViewActivity;
import ru.asterios.open.ProfileActivity;
import ru.asterios.open.R;
import ru.asterios.open.ViewItemActivity;
import ru.asterios.open.ViewYouTubeVideoActivity;
import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;
import ru.asterios.open.model.Item;
import ru.asterios.open.util.CustomRequest;
import ru.asterios.open.util.TagClick;
import ru.asterios.open.util.TagSelectingTextview;
import ru.asterios.open.view.ResizableImageView;


public class AdvancedItemListAdapter extends RecyclerView.Adapter<AdvancedItemListAdapter.ViewHolder> implements Constants, TagClick {

    private List<Item> items = new ArrayList<>();

    private Context context;

    TagSelectingTextview mTagSelectingTextview;

    public static int hashTagHyperLinkDisabled = 0;

    public static final String HASHTAGS_COLOR = "#5BCFF2";

    ImageLoader imageLoader = App.getInstance().getImageLoader();

    private OnItemMenuButtonClickListener onItemMenuButtonClickListener;

    public interface OnItemMenuButtonClickListener {

        void onItemClick(View view, Item obj, int actionId, int position);
    }

    public void setOnMoreButtonClickListener(final OnItemMenuButtonClickListener onItemMenuButtonClickListener) {

        this.onItemMenuButtonClickListener = onItemMenuButtonClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircularImageView mItemAuthorPhoto, mItemAuthorIcon;
        public TextView mItemAuthor, mItemAuthorUsername;
        public ImageView mItemAuthorOnlineIcon, mItemPlayVideo;
        public ImageView mItemMenuButton;
        public ResizableImageView mItemImg;
        public ImageView mItemLikeImg, mItemCommentImg, mItemRepostImg;
        public TextView mItemLikesCount, mItemCommentsCount, mItemRepostsCount;
        public EmojiconTextView mItemDescription;
        public TextView mItemTimeAgo;
        public ProgressBar mProgressBar;
        public MaterialRippleLayout mItemLikeButton, mItemCommentButton, mItemRepostButton;

        public LinearLayout mLinkContainer;
        public ImageView mLinkImage;
        public TextView mLinkTitle;
        public TextView mLinkDescription;

        public CardView mAdCard;
        public NativeExpressAdView mAdView;

        public Button mSpotlightMoreBtn;
        public RecyclerView mSpotlightRecyclerView;


        public LinearLayout mCardRepostContainer;

        public CircularImageView mReAuthorPhoto, mReAuthorIcon;
        public TextView mReAuthor, mReAuthorUsername;
        public ImageView mRePlayVideo;
        public ResizableImageView mReImg;
        public EmojiconTextView mReDescription;
        public TextView mReTimeAgo;
        public ProgressBar mReProgressBar;

        public LinearLayout mReLinkContainer, mReMessageContainer, mReHeaderContainer, mReBodyContainer;
        public ImageView mReLinkImage;
        public TextView mReLinkTitle;
        public TextView mReLinkDescription;

        public ViewHolder(View v, int itemType) {

            super(v);

            if (itemType == 0) {

                mItemAuthorPhoto = (CircularImageView) v.findViewById(R.id.itemAuthorPhoto);
                mItemAuthorIcon = (CircularImageView) v.findViewById(R.id.itemAuthorIcon);

                mItemAuthor = (TextView) v.findViewById(R.id.itemAuthor);
                mItemAuthorOnlineIcon = (ImageView) v.findViewById(R.id.itemAuthorOnlineIcon);
                mItemAuthorUsername = (TextView) v.findViewById(R.id.itemAuthorUsername);

                mItemImg = (ResizableImageView) v.findViewById(R.id.itemImg);
                mItemDescription = (EmojiconTextView) v.findViewById(R.id.itemDescription);

                mItemPlayVideo = (ImageView) v.findViewById(R.id.itemPlayVideo);

                mItemMenuButton = (ImageView) v.findViewById(R.id.itemMenuButton);
                mItemLikeImg = (ImageView) v.findViewById(R.id.itemLikeImg);
                mItemCommentImg = (ImageView) v.findViewById(R.id.itemCommentImg);
                mItemRepostImg = (ImageView) v.findViewById(R.id.itemRepostImg);
                mItemTimeAgo = (TextView) v.findViewById(R.id.itemTimeAgo);

                mItemLikesCount = (TextView) v.findViewById(R.id.itemLikesCount);
                mItemCommentsCount = (TextView) v.findViewById(R.id.itemCommentsCount);
                mItemRepostsCount = (TextView) v.findViewById(R.id.itemRepostsCount);

                mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);

                mItemLikeButton = (MaterialRippleLayout) v.findViewById(R.id.itemLikeButton);
                mItemCommentButton = (MaterialRippleLayout) v.findViewById(R.id.itemCommentButton);
                mItemRepostButton = (MaterialRippleLayout) v.findViewById(R.id.itemRepostButton);

                mLinkContainer = (LinearLayout) v.findViewById(R.id.linkContainer);
                mLinkTitle = (TextView) v.findViewById(R.id.linkTitle);
                mLinkDescription = (TextView) v.findViewById(R.id.linkDescription);
                mLinkImage = (ImageView) v.findViewById(R.id.linkImage);

                // Repost

                mReHeaderContainer = (LinearLayout) v.findViewById(R.id.reHeaderContainer);
                mReMessageContainer = (LinearLayout) v.findViewById(R.id.reMessageContainer);
                mReBodyContainer = (LinearLayout) v.findViewById(R.id.reBodyContainer);
                mCardRepostContainer = (LinearLayout) v.findViewById(R.id.cardRepostContainer);

                mReAuthorPhoto = (CircularImageView) v.findViewById(R.id.reAuthorPhoto);
                mReAuthorIcon = (CircularImageView) v.findViewById(R.id.reAuthorIcon);

                mReAuthor = (TextView) v.findViewById(R.id.reAuthor);
                mReAuthorUsername = (TextView) v.findViewById(R.id.reAuthorUsername);

                mReProgressBar = (ProgressBar) v.findViewById(R.id.reProgressBar);
                mRePlayVideo = (ImageView) v.findViewById(R.id.rePlayVideo);

                mReImg = (ResizableImageView) v.findViewById(R.id.reImg);
                mReDescription = (EmojiconTextView) v.findViewById(R.id.reDescription);
                mReTimeAgo = (TextView) v.findViewById(R.id.reTimeAgo);

                mReLinkContainer = (LinearLayout) v.findViewById(R.id.reLinkContainer);
                mReLinkTitle = (TextView) v.findViewById(R.id.reLinkTitle);
                mReLinkDescription = (TextView) v.findViewById(R.id.reLinkDescription);
                mReLinkImage = (ImageView) v.findViewById(R.id.reLinkImage);

            } else if (itemType == 1) {

                mAdCard = (CardView) v.findViewById(R.id.adCard);

                mAdView = (NativeExpressAdView) v.findViewById(R.id.adView);
            }
        }

    }

    public AdvancedItemListAdapter(Context ctx, List<Item> items) {

        this.context = ctx;
        this.items = items;

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }

        mTagSelectingTextview = new TagSelectingTextview();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_row, parent, false);

            return new ViewHolder(v, viewType);

        } else if (viewType == 1) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_item, parent, false);

            return new ViewHolder(v, viewType);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final Item p = items.get(position);

        if (p.getAd() == 0) {

            onBindItem(holder, position);

        } else {

            AdRequest request = new AdRequest.Builder()
                    .addTestDevice("F1F999894A4018F06D1074F74D7EDFC6")
                    .build();
            holder.mAdView.loadAd(request);

            holder.mAdCard.setVisibility(View.VISIBLE);
        }
    }

    public void onBindItem(ViewHolder holder, final int position) {

        final Item p = items.get(position);

        holder.mLinkContainer.setVisibility(View.GONE);

        holder.mItemPlayVideo.setVisibility(View.GONE);
        holder.mProgressBar.setVisibility(View.GONE);

        holder.mItemAuthorPhoto.setVisibility(View.VISIBLE);

        holder.mItemAuthorPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (p.getGroupId() == 0) {

                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("profileId", p.getFromUserId());
                    context.startActivity(intent);

                } else {

                    Intent intent = new Intent(context, GroupActivity.class);
                    intent.putExtra("groupId", p.getGroupId());
                    context.startActivity(intent);
                }
            }
        });

        if (p.getFromUserPhotoUrl().length() != 0) {

            imageLoader.get(p.getFromUserPhotoUrl(), ImageLoader.getImageListener(holder.mItemAuthorPhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

        } else {

            holder.mItemAuthorPhoto.setVisibility(View.VISIBLE);
            holder.mItemAuthorPhoto.setImageResource(R.drawable.profile_default_photo);
        }

        if (p.getFromUserVerify() == 1) {

            holder.mItemAuthorIcon.setVisibility(View.VISIBLE);

        } else {

            holder.mItemAuthorIcon.setVisibility(View.GONE);
        }

        holder.mItemAuthor.setVisibility(View.VISIBLE);
        holder.mItemAuthor.setText(p.getFromUserFullname());

        holder.mItemAuthor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (p.getGroupId() == 0) {

                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("profileId", p.getFromUserId());
                    context.startActivity(intent);

                } else {

                    Intent intent = new Intent(context, GroupActivity.class);
                    intent.putExtra("groupId", p.getGroupId());
                    context.startActivity(intent);
                }
            }
        });

        holder.mItemAuthorUsername.setVisibility(View.VISIBLE);
        holder.mItemAuthorUsername.setText("@" + p.getFromUserUsername());

        holder.mItemAuthorOnlineIcon.setVisibility(View.GONE);

//        if (p.getFromUserOnline() && p.getFromUserAllowShowOnline() == ENABLED) {
//
//            holder.mItemAuthorOnlineIcon.setVisibility(View.VISIBLE);
//
//        } else {
//
//            holder.mItemAuthorOnlineIcon.setVisibility(View.GONE);
//        }

        if (p.getImgUrl().length() != 0){

            holder.mItemImg.setVisibility(View.VISIBLE);
            holder.mProgressBar.setVisibility(View.VISIBLE);

            final ProgressBar progressView = holder.mProgressBar;
            final ImageView imageView = holder.mItemImg;

            Picasso.with(context)
                    .load(p.getImgUrl())
                    .into(holder.mItemImg, new Callback() {

                        @Override
                        public void onSuccess() {

                            progressView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                            progressView.setVisibility(View.GONE);
                            imageView.setImageResource(R.drawable.img_loading_error);
                        }
                    });

        } else {

            if (p.getVideoUrl() != null && p.getVideoUrl().length() != 0) {

                holder.mItemImg.setVisibility(View.VISIBLE);
                holder.mProgressBar.setVisibility(View.VISIBLE);

                final ImageView imageView = holder.mItemImg;
                final ProgressBar progressView = holder.mProgressBar;
                final ImageView playButtonView = holder.mItemPlayVideo;

                Picasso.with(context)
                        .load(p.getPreviewVideoImgUrl())
                        .into(holder.mItemImg, new Callback() {

                            @Override
                            public void onSuccess() {

                                progressView.setVisibility(View.GONE);
                                playButtonView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {

                                progressView.setVisibility(View.GONE);
                                playButtonView.setVisibility(View.VISIBLE);
                                imageView.setImageResource(R.drawable.img_loading_error);
                            }
                        });

            } else if (p.getYouTubeVideoUrl() != null && p.getYouTubeVideoUrl().length() != 0) {

                holder.mItemImg.setVisibility(View.VISIBLE);
                holder.mProgressBar.setVisibility(View.VISIBLE);

                final ProgressBar progressView = holder.mProgressBar;
                final ImageView playButtonView = holder.mItemPlayVideo;

                Picasso.with(context)
                        .load(p.getYouTubeVideoImg())
                        .into(holder.mItemImg, new Callback() {

                            @Override
                            public void onSuccess() {

                                progressView.setVisibility(View.GONE);
                                playButtonView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {
                                // TODO Auto-generated method stub

                            }
                        });

            } else {

                holder.mItemImg.setVisibility(View.GONE);
            }
        }

        holder.mItemImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (p.getVideoUrl().length() != 0) {

                    viewItem(p);

                } else {

                    if (p.getImgUrl() != null && p.getImgUrl().length() > 0) {

                        Intent i = new Intent(context, PhotoViewActivity.class);
                        i.putExtra("imgUrl", p.getImgUrl());
                        context.startActivity(i);

                    } else {

                        watchYoutubeVideo(p.getYouTubeVideoCode());
                    }
                }
            }
        });

        holder.mItemPlayVideo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (p.getVideoUrl().length() != 0) {

                    viewItem(p);

                } else {

                    watchYoutubeVideo(p.getYouTubeVideoCode());
                }
            }
        });

        if (p.getPost().length() != 0) {

            holder.mItemDescription.setVisibility(View.VISIBLE);
            holder.mItemDescription.setText(p.getPost().replaceAll("<br>", "\n"));

            holder.mItemDescription.setMovementMethod(LinkMovementMethod.getInstance());

            String textHtml = p.getPost();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                holder.mItemDescription.setText(mTagSelectingTextview.addClickablePart(Html.fromHtml(textHtml, Html.FROM_HTML_MODE_LEGACY).toString(), this, hashTagHyperLinkDisabled, HASHTAGS_COLOR), TextView.BufferType.SPANNABLE);

            } else {

                holder.mItemDescription.setText(mTagSelectingTextview.addClickablePart(Html.fromHtml(textHtml).toString(), this, hashTagHyperLinkDisabled, HASHTAGS_COLOR), TextView.BufferType.SPANNABLE);
            }

            holder.mItemDescription.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("msg", p.getPost().replaceAll("<br>", "\n"));
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(context, context.getString(R.string.msg_copied_to_clipboard), Toast.LENGTH_SHORT).show();

                    return false;
                }
            });

        } else {

            holder.mItemDescription.setVisibility(View.GONE);
        }

        holder.mItemTimeAgo.setVisibility(View.VISIBLE);
        holder.mItemTimeAgo.setText(p.getTimeAgo());


        holder.mItemMenuButton.setVisibility(View.VISIBLE);
        holder.mItemMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                onItemMenuButtonClick(view, p, position);
            }
        });

        if (p.getCommentsCount() > 0) {

            holder.mItemCommentsCount.setVisibility(View.VISIBLE);
            holder.mItemCommentsCount.setText(Integer.toString(p.getCommentsCount()));

        } else {

            holder.mItemCommentsCount.setVisibility(View.GONE);
        }

        if (p.getLikesCount() > 0) {

            holder.mItemLikesCount.setVisibility(View.VISIBLE);
            holder.mItemLikesCount.setText(Integer.toString(p.getLikesCount()));

        } else {

            holder.mItemLikesCount.setVisibility(View.GONE);
        }

        if (p.getRePostsCount() > 0) {

            holder.mItemRepostsCount.setVisibility(View.VISIBLE);
            holder.mItemRepostsCount.setText(Integer.toString(p.getRePostsCount()));

        } else {

            holder.mItemRepostsCount.setVisibility(View.GONE);
        }

        if (p.isMyLike()) {

            holder.mItemLikeImg.setImageResource(R.drawable.perk_active);

        } else {

            holder.mItemLikeImg.setImageResource(R.drawable.perk);
        }

        holder.mItemLikeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (p.isMyLike()) {

                    p.setMyLike(false);
                    p.setLikesCount(p.getLikesCount() - 1);

                } else {

                    p.setMyLike(true);
                    p.setLikesCount(p.getLikesCount() + 1);
                }

                CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ITEMS_LIKE, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    if (!response.getBoolean("error")) {

                                        p.setLikesCount(response.getInt("likesCount"));
                                        p.setMyLike(response.getBoolean("myLike"));
                                    }

                                } catch (JSONException e) {

                                    e.printStackTrace();

                                } finally {

                                    notifyDataSetChanged();

                                    Log.e("Item.Like", response.toString());
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("Item.Like", error.toString());
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("accountId", Long.toString(App.getInstance().getId()));
                        params.put("accessToken", App.getInstance().getAccessToken());
                        params.put("itemId", Long.toString(p.getId()));

                        return params;
                    }
                };

                App.getInstance().addToRequestQueue(jsonReq);

                notifyDataSetChanged();
            }
        });

        holder.mItemCommentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                viewItem(p);
            }
        });

        holder.mItemRepostButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                onItemMenuButtonClickListener.onItemClick(view, p,  R.id.action_repost, position);
            }
        });

        if (p.getUrlPreviewLink() != null && p.getUrlPreviewLink().length() > 0) {

            holder.mLinkContainer.setVisibility(View.VISIBLE);

            holder.mLinkContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!p.getUrlPreviewLink().startsWith("https://") && !p.getUrlPreviewLink().startsWith("http://")){

                        p.setUrlPreviewLink("http://" + p.getUrlPreviewLink());
                    }

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(p.getUrlPreviewLink()));
                    context.startActivity(i);
                }
            });

            if (p.getUrlPreviewImage() != null && p.getUrlPreviewImage().length() != 0) {

                imageLoader.get(p.getUrlPreviewImage(), ImageLoader.getImageListener(holder.mLinkImage, R.drawable.img_link, R.drawable.img_link));

            } else {

                holder.mLinkImage.setImageResource(R.drawable.img_link);
            }

            if (p.getUrlPreviewTitle() != null && p.getUrlPreviewTitle().length() != 0) {

                holder.mLinkTitle.setText(p.getUrlPreviewTitle());

            } else {

                holder.mLinkTitle.setText("Link");
            }

            if (p.getUrlPreviewDescription() != null && p.getUrlPreviewDescription().length() != 0) {

                holder.mLinkDescription.setText(p.getUrlPreviewDescription());

            } else {

                holder.mLinkDescription.setText("Link");
            }
        }



        // Repost

        if (p.getRePostId() != 0) {

            holder.mCardRepostContainer.setVisibility(View.VISIBLE);

            if (p.getRePostRemoveAt() == 0) {

                // original post available

                holder.mReMessageContainer.setVisibility(View.GONE);
                holder.mReLinkContainer.setVisibility(View.GONE);

                holder.mRePlayVideo.setVisibility(View.GONE);
                holder.mReProgressBar.setVisibility(View.GONE);

                holder.mReAuthorPhoto.setVisibility(View.VISIBLE);

                holder.mReAuthorPhoto.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(context, ViewItemActivity.class);
                        intent.putExtra("itemId", p.getRePostId());
                        context.startActivity(intent);
                    }
                });

                if (p.getRePostFromUserPhotoUrl().length() != 0) {

                    imageLoader.get(p.getRePostFromUserPhotoUrl(), ImageLoader.getImageListener(holder.mReAuthorPhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

                } else {

                    holder.mReAuthorPhoto.setVisibility(View.VISIBLE);
                    holder.mReAuthorPhoto.setImageResource(R.drawable.profile_default_photo);
                }

                if (p.getRePostFromUserVerify() == 1) {

                    holder.mReAuthorIcon.setVisibility(View.VISIBLE);

                } else {

                    holder.mReAuthorIcon.setVisibility(View.GONE);
                }

                holder.mReAuthor.setVisibility(View.VISIBLE);
                holder.mReAuthor.setText(p.getRePostFromUserFullname());

                holder.mReAuthor.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(context, ViewItemActivity.class);
                        intent.putExtra("itemId", p.getRePostId());
                        context.startActivity(intent);
                    }
                });

                holder.mReAuthorUsername.setVisibility(View.VISIBLE);
                holder.mReAuthorUsername.setText("@" + p.getRePostFromUserUsername());

                if (p.getRePostImgUrl().length() != 0) {

                    holder.mReImg.setVisibility(View.VISIBLE);
                    holder.mReProgressBar.setVisibility(View.VISIBLE);

                    final ProgressBar reProgressView = holder.mReProgressBar;
                    final ImageView reImageView = holder.mReImg;

                    Picasso.with(context)
                            .load(p.getRePostImgUrl())
                            .into(holder.mReImg, new Callback() {

                                @Override
                                public void onSuccess() {

                                    reProgressView.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {

                                    reProgressView.setVisibility(View.GONE);
                                    reImageView.setImageResource(R.drawable.img_loading_error);
                                }
                            });

                } else {

                    if (p.getReVideoUrl() != null && p.getReVideoUrl().length() != 0) {

                        holder.mReImg.setVisibility(View.VISIBLE);
                        holder.mReProgressBar.setVisibility(View.VISIBLE);

                        final ImageView reImageView = holder.mReImg;
                        final ProgressBar reProgressView = holder.mReProgressBar;
                        final ImageView rePlayButtonView = holder.mRePlayVideo;

                        Picasso.with(context)
                                .load(p.getReVideoUrl())
                                .into(holder.mReImg, new Callback() {

                                    @Override
                                    public void onSuccess() {

                                        reProgressView.setVisibility(View.GONE);
                                        rePlayButtonView.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onError() {

                                        reProgressView.setVisibility(View.GONE);
                                        rePlayButtonView.setVisibility(View.VISIBLE);
                                        reImageView.setImageResource(R.drawable.img_loading_error);
                                    }
                                });

                    } else if (p.getReYouTubeVideoUrl() != null && p.getReYouTubeVideoUrl().length() != 0) {

                        holder.mReImg.setVisibility(View.VISIBLE);
                        holder.mReProgressBar.setVisibility(View.VISIBLE);

                        final ProgressBar reProgressView = holder.mReProgressBar;
                        final ImageView rePlayButtonView = holder.mRePlayVideo;

                        Picasso.with(context)
                                .load(p.getReYouTubeVideoImg())
                                .into(holder.mReImg, new Callback() {

                                    @Override
                                    public void onSuccess() {

                                        reProgressView.setVisibility(View.GONE);
                                        rePlayButtonView.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        // TODO Auto-generated method stub

                                    }
                                });

                    } else {

                        holder.mReImg.setVisibility(View.GONE);
                    }
                }

                holder.mReImg.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (p.getReVideoUrl().length() != 0) {

                            viewItem(p);

                        } else {

                            if (p.getRePostImgUrl() != null && p.getRePostImgUrl().length() > 0) {

                                Intent i = new Intent(context, PhotoViewActivity.class);
                                i.putExtra("imgUrl", p.getRePostImgUrl());
                                context.startActivity(i);

                            } else {

                                watchYoutubeVideo(p.getReYouTubeVideoCode());
                            }
                        }
                    }
                });

                holder.mRePlayVideo.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (p.getReVideoUrl().length() != 0) {

                            viewItem(p);

                        } else {

                            watchYoutubeVideo(p.getReYouTubeVideoCode());
                        }
                    }
                });

                if (p.getRePostPost().length() != 0) {

                    holder.mReDescription.setVisibility(View.VISIBLE);
                    holder.mReDescription.setText(p.getRePostPost().replaceAll("<br>", "\n"));

                    holder.mReDescription.setMovementMethod(LinkMovementMethod.getInstance());

                    String textHtml = p.getRePostPost();

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                        holder.mReDescription.setText(mTagSelectingTextview.addClickablePart(Html.fromHtml(textHtml, Html.FROM_HTML_MODE_LEGACY).toString(), this, hashTagHyperLinkDisabled, HASHTAGS_COLOR), TextView.BufferType.SPANNABLE);

                    } else {

                        holder.mReDescription.setText(mTagSelectingTextview.addClickablePart(Html.fromHtml(textHtml).toString(), this, hashTagHyperLinkDisabled, HASHTAGS_COLOR), TextView.BufferType.SPANNABLE);
                    }

                } else {

                    holder.mReDescription.setVisibility(View.GONE);
                }

                holder.mReTimeAgo.setVisibility(View.VISIBLE);
                holder.mReTimeAgo.setText(p.getRePostTimeAgo());


                if (p.getReUrlPreviewLink() != null && p.getReUrlPreviewLink().length() > 0) {

                    holder.mReLinkContainer.setVisibility(View.VISIBLE);

                    holder.mReLinkContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!p.getReUrlPreviewLink().startsWith("https://") && !p.getReUrlPreviewLink().startsWith("http://")){

                                p.setReUrlPreviewLink("http://" + p.getReUrlPreviewLink());
                            }

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(p.getReUrlPreviewLink()));
                            context.startActivity(i);
                        }
                    });

                    if (p.getReUrlPreviewImage() != null && p.getReUrlPreviewImage().length() != 0) {

                        imageLoader.get(p.getReUrlPreviewImage(), ImageLoader.getImageListener(holder.mReLinkImage, R.drawable.img_link, R.drawable.img_link));

                    } else {

                        holder.mReLinkImage.setImageResource(R.drawable.img_link);
                    }

                    if (p.getReUrlPreviewTitle() != null && p.getReUrlPreviewTitle().length() != 0) {

                        holder.mReLinkTitle.setText(p.getReUrlPreviewTitle());

                    } else {

                        holder.mReLinkTitle.setText("Link");
                    }

                    if (p.getReUrlPreviewDescription() != null && p.getReUrlPreviewDescription().length() != 0) {

                        holder.mReLinkDescription.setText(p.getReUrlPreviewDescription());

                    } else {

                        holder.mReLinkDescription.setText("Link");
                    }
                }


            } else {

                // original post has deleted
                // show message

                holder.mReMessageContainer.setVisibility(View.VISIBLE);

                holder.mReHeaderContainer.setVisibility(View.GONE);
                holder.mReBodyContainer.setVisibility(View.GONE);
            }

        } else {

            // not repost
            // hide repost container

            holder.mCardRepostContainer.setVisibility(View.GONE);
        }
    }

    private void onItemMenuButtonClick(final View view, final Item post, final int position){

        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                onItemMenuButtonClickListener.onItemClick(view, post, item.getItemId(), position);

                return true;
            }
        });

        if (post.getFromUserId() == App.getInstance().getId()) {

            popupMenu.inflate(R.menu.menu_my_item_popup);

        } else {

            popupMenu.inflate(R.menu.menu_item_popup);

            // hide repost menu item

            popupMenu.getMenu().findItem(R.id.action_repost).setVisible(false);
        }

        popupMenu.show();
    }

    public void viewItem(Item item) {

        Intent intent = new Intent(context, ViewItemActivity.class);
        intent.putExtra("itemId", item.getId());
        context.startActivity(intent);
    }

    public void watchYoutubeVideo(String id) {

        if (YOUTUBE_API_KEY.length() > 5) {

            Intent i = new Intent(context, ViewYouTubeVideoActivity.class);
            i.putExtra("videoCode", id);
            context.startActivity(i);

        } else {

            try {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
                context.startActivity(intent);

            } catch (ActivityNotFoundException ex) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id));
                context.startActivity(intent);
            }
        }
    }

    @Override
    public void clickedTag(CharSequence tag) {

        Intent i = new Intent(context, HashtagsActivity.class);
        i.putExtra("hashtag", tag);
        context.startActivity(i);
    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        final Item p = items.get(position);

        if (p.getAd() == 0) {

            return 0;

        } else {

            return 1;
        }
    }
}