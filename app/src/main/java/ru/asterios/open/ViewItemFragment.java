package ru.asterios.open;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.pkmmte.view.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import github.ankushsachdeva.emojicon.EditTextImeBackListener;
import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;
import ru.asterios.open.adapter.CommentListAdapter;
import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;
import ru.asterios.open.dialogs.CommentActionDialog;
import ru.asterios.open.dialogs.CommentDeleteDialog;
import ru.asterios.open.dialogs.CommentUserActionDialog;
import ru.asterios.open.dialogs.MyCommentActionDialog;
import ru.asterios.open.dialogs.MyGroupPostActionDialog;
import ru.asterios.open.dialogs.MyPostActionDialog;
import ru.asterios.open.dialogs.PostActionDialog;
import ru.asterios.open.dialogs.PostDeleteDialog;
import ru.asterios.open.dialogs.PostReportDialog;
import ru.asterios.open.dialogs.PostShareDialog;
import ru.asterios.open.model.Comment;
import ru.asterios.open.model.Item;
import ru.asterios.open.util.Api;
import ru.asterios.open.util.CommentInterface;
import ru.asterios.open.util.CustomRequest;
import ru.asterios.open.util.TagClick;
import ru.asterios.open.util.TagSelectingTextview;
import ru.asterios.open.view.ResizableImageView;

public class ViewItemFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener, CommentInterface, TagClick {

    private ProgressDialog pDialog;

    SwipeRefreshLayout mContentContainer;
    RelativeLayout mErrorScreen, mLoadingScreen, mEmptyScreen;
    LinearLayout mContentScreen, mCommentFormContainer, mItemLocationContainer;

    EmojiconEditText mCommentText;

    ListView listView;
    Button mRetryBtn;

    View mListViewHeader;

    ImageView mItemAction, mItemLike, mItemRePost, mItemComment, mEmojiBtn, mSendComment;
    TextView mItemCity, mItemCountry, mItemAuthor, mItemUsername, mItemMode, mItemPost, mItemTimeAgo, mItemLikesCount, mItemCommentsCount, mItemRePostsCount, mRePostItemAuthor, mRePostItemUsername, mRePostItemTimeAgo, mRePostItemPost;
    ImageView mRePostItemImg;
    CircularImageView mItemAuthorPhoto, mRePostItemAuthorPhoto;
    LinearLayout mRePostContainer;

    ImageView mItemImg;

    LinearLayout mLinkContainer;
    ImageView mLinkImage;
    TextView mLinkTitle;
    TextView mLinkDescription;

    public RelativeLayout youTubeVideoContainer;
    public ResizableImageView youTubeImg;
    public ImageView youTubePlayImg;

    public RelativeLayout videoContainer;
    public ImageView videoImg;
    public ImageView videoPlayImg;

    ImageLoader imageLoader = App.getInstance().getImageLoader();

    TagSelectingTextview mTagSelectingTextview;

    public static int hashTagHyperLinkDisabled = 0;


    private ArrayList<Comment> commentsList;

    private CommentListAdapter itemAdapter;

    Item item;

    long itemId = 0, replyToUserId = 0;
    int arrayLength = 0;
    String commentText;

    private Boolean loading = false;
    private Boolean restore = false;
    private Boolean preload = false;

    EmojiconsPopup popup;

    public ViewItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        initpDialog();

        Intent i = getActivity().getIntent();

        itemId = i.getLongExtra("itemId", 0);

        commentsList = new ArrayList<Comment>();
        itemAdapter = new CommentListAdapter(getActivity(), commentsList, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_view_item, container, false);

        popup = new EmojiconsPopup(rootView, getActivity());

        popup.setSizeForSoftKeyboard();

        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {

                mCommentText.append(emojicon.getEmoji());
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {

                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mCommentText.dispatchKeyEvent(event);
            }
        });

        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {

                setIconEmojiKeyboard();
            }
        });

        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {

                if (popup.isShowing())

                    popup.dismiss();
            }
        });

        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {

                mCommentText.append(emojicon.getEmoji());
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {

                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mCommentText.dispatchKeyEvent(event);
            }
        });

        if (savedInstanceState != null) {

            restore = savedInstanceState.getBoolean("restore");
            loading = savedInstanceState.getBoolean("loading");
            preload = savedInstanceState.getBoolean("preload");

            replyToUserId = savedInstanceState.getLong("replyToUserId");

        } else {

            restore = false;
            loading = false;
            preload = false;

            replyToUserId = 0;
        }

        if (loading) {

            showpDialog();
        }

        mEmptyScreen = (RelativeLayout) rootView.findViewById(R.id.emptyScreen);
        mErrorScreen = (RelativeLayout) rootView.findViewById(R.id.errorScreen);
        mLoadingScreen = (RelativeLayout) rootView.findViewById(R.id.loadingScreen);
        mContentContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.contentContainer);
        mContentContainer.setOnRefreshListener(this);

        mContentScreen = (LinearLayout) rootView.findViewById(R.id.contentScreen);
        mCommentFormContainer = (LinearLayout) rootView.findViewById(R.id.commentFormContainer);

        mCommentText = (EmojiconEditText) rootView.findViewById(R.id.commentText);
        mSendComment = (ImageView) rootView.findViewById(R.id.sendCommentImg);
        mEmojiBtn = (ImageView) rootView.findViewById(R.id.emojiBtn);

        mSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                send();
            }
        });

        mRetryBtn = (Button) rootView.findViewById(R.id.retryBtn);

        mRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (App.getInstance().isConnected()) {

                    showLoadingScreen();

                    getItem();
                }
            }
        });

        listView = (ListView) rootView.findViewById(R.id.listView);

        mListViewHeader = getActivity().getLayoutInflater().inflate(R.layout.stream_list_row, null);

        listView.addHeaderView(mListViewHeader);

        listView.setAdapter(itemAdapter);

        youTubeImg = (ResizableImageView) mListViewHeader.findViewById(R.id.youTubeImg);
        youTubePlayImg = (ImageView) mListViewHeader.findViewById(R.id.youTubePlayImg);
        youTubeVideoContainer = (RelativeLayout) mListViewHeader.findViewById(R.id.youTubeVideoContainer);

        videoImg = (ImageView) mListViewHeader.findViewById(R.id.videoImg);
        videoPlayImg = (ImageView) mListViewHeader.findViewById(R.id.videoPlayImg);
        videoContainer = (RelativeLayout) mListViewHeader.findViewById(R.id.videoContainer);

        mItemAuthorPhoto = (CircularImageView) mListViewHeader.findViewById(R.id.itemAuthorPhoto);
        mItemAction = (ImageView) mListViewHeader.findViewById(R.id.itemAction);
        mItemLike = (ImageView) mListViewHeader.findViewById(R.id.itemLike);
        mItemRePost = (ImageView) mListViewHeader.findViewById(R.id.itemRePost);
        mItemComment = (ImageView) mListViewHeader.findViewById(R.id.itemComment);

        mItemAuthor = (TextView) mListViewHeader.findViewById(R.id.itemAuthor);
        mItemUsername = (TextView) mListViewHeader.findViewById(R.id.itemUsername);
        mItemMode = (TextView) mListViewHeader.findViewById(R.id.itemMode);
        mItemPost = (TextView) mListViewHeader.findViewById(R.id.itemPost);
        mItemTimeAgo = (TextView) mListViewHeader.findViewById(R.id.itemTimeAgo);
        mItemLikesCount = (TextView) mListViewHeader.findViewById(R.id.itemLikesCount);
        mItemRePostsCount = (TextView) mListViewHeader.findViewById(R.id.itemRePostsCount);
        mItemCommentsCount = (TextView) mListViewHeader.findViewById(R.id.itemCommentsCount);
        mItemCity = (TextView) mListViewHeader.findViewById(R.id.itemCity);
        mItemCountry = (TextView) mListViewHeader.findViewById(R.id.itemCountry);
        mItemLocationContainer = (LinearLayout) mListViewHeader.findViewById(R.id.locationContainer);
        mItemImg = (ImageView) mListViewHeader.findViewById(R.id.itemImg);

        mLinkContainer = (LinearLayout) mListViewHeader.findViewById(R.id.linkContainer);
        mLinkTitle = (TextView) mListViewHeader.findViewById(R.id.linkTitle);
        mLinkDescription = (TextView) mListViewHeader.findViewById(R.id.linkDescription);
        mLinkImage = (ImageView) mListViewHeader.findViewById(R.id.linkImage);

        mRePostContainer = (LinearLayout) mListViewHeader.findViewById(R.id.rePostContainer);
        mRePostItemAuthor = (TextView) mListViewHeader.findViewById(R.id.rePostItemAuthor);
        mRePostItemUsername = (TextView) mListViewHeader.findViewById(R.id.rePostItemUsername);
        mRePostItemTimeAgo = (TextView) mListViewHeader.findViewById(R.id.rePostItemTimeAgo);
        mRePostItemPost = (TextView) mListViewHeader.findViewById(R.id.rePostItemPost);
        mRePostItemAuthorPhoto = (CircularImageView) mListViewHeader.findViewById(R.id.rePostItemAuthorPhoto);
        mRePostItemImg = (ImageView) mListViewHeader.findViewById(R.id.rePostItemImg);

        mTagSelectingTextview = new TagSelectingTextview();

        if (!EMOJI_KEYBOARD) {

            mEmojiBtn.setVisibility(View.GONE);
        }

        mEmojiBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!popup.isShowing()) {

                    if (popup.isKeyBoardOpen()){

                        popup.showAtBottom();
                        setIconSoftKeyboard();

                    } else {

                        mCommentText.setFocusableInTouchMode(true);
                        mCommentText.requestFocus();
                        popup.showAtBottomPending();

                        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mCommentText, InputMethodManager.SHOW_IMPLICIT);

                        setIconSoftKeyboard();
                    }

                } else {

                    popup.dismiss();
                }
            }
        });

        EditTextImeBackListener er = new EditTextImeBackListener() {

            @Override
            public void onImeBack(EmojiconEditText ctrl, String text) {

                hideEmojiKeyboard();
            }
        };

        mCommentText.setOnEditTextImeBackListener(er);

        if (!restore) {

            if (App.getInstance().isConnected()) {

                showLoadingScreen();
                getItem();

            } else {

                showErrorScreen();
            }

        } else {

            if (App.getInstance().isConnected()) {

                if (!preload) {

                    loadingComplete();
                    updateItem();

                } else {

                    showLoadingScreen();
                }

            } else {

                showErrorScreen();
            }
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    public void hideEmojiKeyboard() {

        popup.dismiss();
    }

    public void setIconEmojiKeyboard() {

        mEmojiBtn.setBackgroundResource(R.drawable.ic_emoji);
    }

    public void setIconSoftKeyboard() {

        mEmojiBtn.setBackgroundResource(R.drawable.ic_keyboard);
    }

    public void onDestroyView() {

        super.onDestroyView();

        hidepDialog();
    }

    protected void initpDialog() {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putBoolean("loading", loading);
        outState.putBoolean("preload", preload);

        outState.putLong("replyToUserId", replyToUserId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ITEM_EDIT && resultCode == getActivity().RESULT_OK) {

            item.setPost(data.getStringExtra("post"));
            item.setImgUrl(data.getStringExtra("imgUrl"));

            updateItem();

        } else if (requestCode == ITEM_REPOST && resultCode == getActivity().RESULT_OK) {

            item.setMyRePost(true);
            item.setRePostsCount(item.getRePostsCount() + 1);

            updateItem();
        }
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            mContentContainer.setRefreshing(true);
            getItem();

        } else {

            mContentContainer.setRefreshing(false);
        }
    }

    public String getPostModeText(int postMode) {

        switch (postMode) {

            case 0: {

                return getString(R.string.label_post_to_public);
            }

            default: {

                return getString(R.string.label_post_to_followers);
            }
        }
    }

    public void updateItem() {

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }

        if (item.getUrlPreviewLink().length() != 0) {

            mLinkContainer.setVisibility(View.VISIBLE);

            if (item.getUrlPreviewImage().length() != 0) {

                imageLoader.get(item.getUrlPreviewImage(), ImageLoader.getImageListener(mLinkImage, R.drawable.img_link, R.drawable.img_link));

            } else {

                mLinkImage.setImageResource(R.drawable.img_link);
            }

            if (item.getUrlPreviewTitle().length() != 0) {

                mLinkTitle.setText(item.getUrlPreviewTitle());

            } else {

                mLinkTitle.setText("Link");
            }

            if (item.getUrlPreviewDescription().length() != 0) {

                mLinkDescription.setVisibility(View.VISIBLE);
                mLinkDescription.setText(item.getUrlPreviewDescription());

            } else {

                mLinkDescription.setVisibility(View.GONE);
            }

            mLinkContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!item.getUrlPreviewLink().startsWith("https://") && !item.getUrlPreviewLink().startsWith("http://")){

                        item.setUrlPreviewLink("http://" + item.getUrlPreviewLink());
                    }

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(item.getUrlPreviewLink()));
                    startActivity(i);
                }
            });

        } else {

            mLinkContainer.setVisibility(View.GONE);
        }

        if (item.getYouTubeVideoImg().length() != 0) {

            youTubeVideoContainer.setVisibility(View.VISIBLE);

            imageLoader.get(item.getYouTubeVideoImg(), ImageLoader.getImageListener(youTubeImg, R.drawable.img_loading, R.drawable.img_loading));

            youTubeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    watchYoutubeVideo(item.getYouTubeVideoCode());
                }
            });

            youTubePlayImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    watchYoutubeVideo(item.getYouTubeVideoCode());
                }
            });

        } else {

            youTubeVideoContainer.setVisibility(View.GONE);
        }

        if (item.getPreviewVideoImgUrl().length() != 0) {

            videoContainer.setVisibility(View.VISIBLE);

            imageLoader.get(item.getPreviewVideoImgUrl(), ImageLoader.getImageListener(videoImg, R.drawable.img_loading, R.drawable.img_loading));

            videoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getActivity(), VideoViewActivity.class);
                    i.putExtra("videoUrl", item.getVideoUrl());
                    startActivity(i);
                }
            });

            videoPlayImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getActivity(), VideoViewActivity.class);
                    i.putExtra("videoUrl", item.getVideoUrl());
                    startActivity(i);
                }
            });

        } else {

            videoContainer.setVisibility(View.GONE);
        }

        if (item.getRePostId() != 0 && item.getRePostRemoveAt() == 0) {

            mRePostContainer.setVisibility(View.VISIBLE);

            mRePostItemTimeAgo.setText(item.getRePostTimeAgo());
            mRePostItemTimeAgo.setVisibility(View.VISIBLE);

            mRePostItemAuthor.setText(item.getRePostFromUserFullname());
            mRePostItemUsername.setText("@" + item.getRePostFromUserUsername());

            if (item.getRePostFromUserVerify() == 1) {

                mRePostItemAuthor.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.profile_verify_icon, 0);

            } else {

                mRePostItemAuthor.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            if (item.getRePostFromUserPhotoUrl().length() != 0) {

                mRePostItemAuthorPhoto.setVisibility(View.VISIBLE);

                imageLoader.get(item.getRePostFromUserPhotoUrl(), ImageLoader.getImageListener(mRePostItemAuthorPhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

            } else {

                mRePostItemAuthorPhoto.setVisibility(View.VISIBLE);
                mRePostItemAuthorPhoto.setImageResource(R.drawable.profile_default_photo);
            }

            mRePostItemAuthorPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("profileId", item.getRePostFromUserId());
                    getActivity().startActivity(intent);
                }
            });

            mRePostItemAuthor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("profileId", item.getRePostFromUserId());
                    getActivity().startActivity(intent);
                }
            });

            mRePostItemUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("profileId", item.getRePostFromUserId());
                    getActivity().startActivity(intent);
                }
            });

            if (item.getRePostPost().length() > 0) {

                mRePostItemPost.setText(item.getRePostPost().replaceAll("<br>", "\n"));

                mRePostItemPost.setVisibility(View.VISIBLE);

                mRePostItemPost.setMovementMethod(LinkMovementMethod.getInstance());

                String textHtml = item.getRePostPost();

                mRePostItemPost.setText(mTagSelectingTextview.addClickablePart(Html.fromHtml(textHtml).toString(), this, hashTagHyperLinkDisabled, HASHTAGS_COLOR), TextView.BufferType.SPANNABLE);

            } else {

                mRePostItemPost.setVisibility(View.GONE);
            }

            if (item.getRePostImgUrl().length() > 0) {

                imageLoader.get(item.getRePostImgUrl(), ImageLoader.getImageListener(mRePostItemImg, R.drawable.img_loading, R.drawable.img_loading));
                mRePostItemImg.setVisibility(View.VISIBLE);

                mRePostItemImg.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(getActivity(), PhotoViewActivity.class);
                        i.putExtra("imgUrl", item.getRePostImgUrl());
                        getActivity().startActivity(i);
                    }
                });

            } else {

                mRePostItemImg.setVisibility(View.GONE);
            }

        } else {

            mRePostContainer.setVisibility(View.GONE);
        }

        if ((item.getCity() != null && item.getCity().length() > 0) || (item.getCountry() != null && item.getCountry().length() > 0)) {

            if (item.getCity() != null && item.getCity().length() > 0) {

                mItemCity.setText(item.getCity());
                mItemCity.setVisibility(View.VISIBLE);

            } else {

                mItemCity.setVisibility(View.GONE);
            }

            if (item.getCountry() != null && item.getCountry().length() > 0) {

                mItemCountry.setText(item.getCountry());
                mItemCountry.setVisibility(View.VISIBLE);

            } else {

                mItemCountry.setVisibility(View.GONE);
            }

            mItemLocationContainer.setVisibility(View.VISIBLE);

        } else {

            mItemLocationContainer.setVisibility(View.GONE);
        }

        mItemComment.setVisibility(View.GONE);
        mItemCommentsCount.setVisibility(View.GONE);

        mItemAuthor.setText(item.getFromUserFullname());
        mItemUsername.setText("@" + item.getFromUserUsername());

        mItemMode.setText(getPostModeText(item.getAccessMode()));

        if (item.getFromUserVerify() == 1) {

            mItemAuthor.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.profile_verify_icon, 0);

        } else {

            mItemAuthor.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        if (item.getFromUserPhotoUrl().length() != 0) {

            mItemAuthorPhoto.setVisibility(View.VISIBLE);

            imageLoader.get(item.getFromUserPhotoUrl(), ImageLoader.getImageListener(mItemAuthorPhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

        } else {

            mItemAuthorPhoto.setVisibility(View.VISIBLE);
            mItemAuthorPhoto.setImageResource(R.drawable.profile_default_photo);
        }

        mItemAuthorPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (item.getGroupId() == 0) {

                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("profileId", item.getFromUserId());
                    startActivity(intent);

                } else {

                    Intent intent = new Intent(getActivity(), GroupActivity.class);
                    intent.putExtra("groupId", item.getGroupId());
                    startActivity(intent);
                }
            }
        });

        mItemAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (item.getGroupId() == 0) {

                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("profileId", item.getFromUserId());
                    startActivity(intent);

                } else {

                    Intent intent = new Intent(getActivity(), GroupActivity.class);
                    intent.putExtra("groupId", item.getGroupId());
                    startActivity(intent);
                }
            }
        });

        if (item.getFromUserId() == App.getInstance().getId()) {

            itemAdapter.setMyPost(true);

        } else {

            itemAdapter.setMyPost(false);
        }

        mItemAction.setImageResource(R.drawable.ic_action_collapse);

        mItemAction.setVisibility(View.VISIBLE);

        mItemAction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                action(0);
            }
        });

        mItemLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (App.getInstance().isConnected()) {

                    CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ITEMS_LIKE, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    if (!isAdded() || getActivity() == null) {

                                        Log.e("ERROR", "ViewItemFragment Not Added to Activity");

                                        return;
                                    }

                                    try {

                                        if (!response.getBoolean("error")) {

                                            item.setLikesCount(response.getInt("likesCount"));
                                            item.setMyLike(response.getBoolean("myLike"));
                                        }

                                    } catch (JSONException e) {

                                        e.printStackTrace();

                                    } finally {

                                        updateItem();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            if (!isAdded() || getActivity() == null) {

                                Log.e("ERROR", "ViewItemFragment Not Added to Activity");

                                return;
                            }

                            Toast.makeText(getActivity(), getString(R.string.error_data_loading), Toast.LENGTH_LONG).show();
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

                    Toast.makeText(getActivity(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (item.isMyLike()) {

            mItemLike.setImageResource(R.drawable.perk_active);

        } else {

            mItemLike.setImageResource(R.drawable.perk);
        }

        if (item.getLikesCount() > 0) {

            mItemLikesCount.setText(Integer.toString(item.getLikesCount()));
            mItemLikesCount.setVisibility(View.VISIBLE);

        } else {

            mItemLikesCount.setText(Integer.toString(item.getLikesCount()));
            mItemLikesCount.setVisibility(View.GONE);
        }

        mItemLikesCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), LikesActivity.class);
                intent.putExtra("itemId", item.getId());
                startActivity(intent);
            }
        });

        if (item.isMyRePost()) {

            mItemRePost.setImageResource(R.drawable.repost_active);

        } else {

            mItemRePost.setImageResource(R.drawable.repost);
        }

        if (item.getRePostsCount() > 0) {

            mItemRePostsCount.setText(Integer.toString(item.getRePostsCount()));
            mItemRePostsCount.setVisibility(View.VISIBLE);

        } else {

            mItemRePostsCount.setText(Integer.toString(item.getRePostsCount()));
            mItemRePostsCount.setVisibility(View.GONE);
        }

        mItemRePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (App.getInstance().getId() == item.getFromUserId()) {

                    Toast.makeText(getActivity(), getString(R.string.msg_post_has_shared_error), Toast.LENGTH_SHORT).show();

                } else {

                    if (!item.isMyRePost()) {

                        repost(0);

                    } else {

                        Toast.makeText(getActivity(), getString(R.string.msg_post_has_been_already_shared), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mItemTimeAgo.setText(item.getTimeAgo());
        mItemTimeAgo.setVisibility(View.VISIBLE);

        if (item.getPost().length() > 0) {

            mItemPost.setText(item.getPost().replaceAll("<br>", "\n"));

            mItemPost.setVisibility(View.VISIBLE);

            mItemPost.setMovementMethod(LinkMovementMethod.getInstance());

            String textHtml = item.getPost();

            mItemPost.setText(mTagSelectingTextview.addClickablePart(Html.fromHtml(textHtml).toString(), this, hashTagHyperLinkDisabled, HASHTAGS_COLOR), TextView.BufferType.SPANNABLE);

        } else {

            mItemPost.setVisibility(View.GONE);
        }

        if (item.getImgUrl().length() > 0) {

            imageLoader.get(item.getImgUrl(), ImageLoader.getImageListener(mItemImg, R.drawable.img_loading, R.drawable.img_loading));
            mItemImg.setVisibility(View.VISIBLE);

            mItemImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getActivity(), PhotoViewActivity.class);
                    i.putExtra("imgUrl", item.getImgUrl());
                    startActivity(i);
                }
            });

        } else {

            mItemImg.setVisibility(View.GONE);
        }
    }

    @Override
    public void clickedTag(CharSequence tag) {
        // TODO Auto-generated method stub

        Intent i = new Intent(getActivity(), HashtagsActivity.class);
        i.putExtra("hashtag", tag);
        startActivity(i);
    }

    public void getItem() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ITEM_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "ViewItemFragment Not Added to Activity");

                            return;
                        }

                        try {

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

//                                Toast.makeText(ViewItemActivity.this, response.toString(), Toast.LENGTH_SHORT).show();

                                commentsList.clear();

                                itemId = response.getInt("itemId");

                                if (response.has("items")) {

                                    JSONArray itemsArray = response.getJSONArray("items");

                                    arrayLength = itemsArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < itemsArray.length(); i++) {

                                            JSONObject itemObj = (JSONObject) itemsArray.get(i);

                                            item = new Item(itemObj);

                                            updateItem();
                                        }
                                    }
                                }

                                if (response.has("comments")) {

                                    JSONObject commentsObj = response.getJSONObject("comments");

                                    if (commentsObj.has("comments")) {

                                        JSONArray commentsArray = commentsObj.getJSONArray("comments");

                                        arrayLength = commentsArray.length();

                                        if (arrayLength > 0) {

                                            for (int i = commentsArray.length() - 1; i > -1 ; i--) {

                                                JSONObject itemObj = (JSONObject) commentsArray.get(i);

                                                Comment comment = new Comment(itemObj);

                                                commentsList.add(comment);
                                            }
                                        }
                                    }
                                }

                                loadingComplete();

                            } else {

                                showErrorScreen();
                            }

                        } catch (JSONException e) {

                            showErrorScreen();

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "ViewItemFragment Not Added to Activity");

                    return;
                }

                showErrorScreen();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("itemId", Long.toString(itemId));
                params.put("language", "en");

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void send() {

        commentText = mCommentText.getText().toString();
        commentText = commentText.trim();

        if (App.getInstance().isConnected() && App.getInstance().getId() != 0 && commentText.length() > 0) {

            loading = true;

            showpDialog();

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_COMMENTS_NEW, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (!isAdded() || getActivity() == null) {

                                Log.e("ERROR", "ViewItemFragment Not Added to Activity");

                                return;
                            }

                            try {

                                if (!response.getBoolean("error")) {

                                    if (response.has("comment")) {

                                        JSONObject commentObj = (JSONObject) response.getJSONObject("comment");

                                        Comment comment = new Comment(commentObj);

                                        commentsList.add(comment);

                                        itemAdapter.notifyDataSetChanged();

                                        listView.setSelection(itemAdapter.getCount() - 1);

                                        mCommentText.setText("");
                                        replyToUserId = 0;
                                    }

                                    Toast.makeText(getActivity(), getString(R.string.msg_comment_has_been_added), Toast.LENGTH_SHORT).show();

                                }

                            } catch (JSONException e) {

                                e.printStackTrace();

                            } finally {

                                loading = false;

                                hidepDialog();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (!isAdded() || getActivity() == null) {

                        Log.e("ERROR", "ViewItemFragment Not Added to Activity");

                        return;
                    }

                    loading = false;

                    hidepDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accountId", Long.toString(App.getInstance().getId()));
                    params.put("accessToken", App.getInstance().getAccessToken());

                    params.put("itemId", Long.toString(item.getId()));
                    params.put("commentText", commentText);

                    params.put("replyToUserId", Long.toString(replyToUserId));

                    return params;
                }
            };

            int socketTimeout = 0;//0 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

            jsonReq.setRetryPolicy(policy);

            App.getInstance().addToRequestQueue(jsonReq);
        }
    }

    public void onPostDelete(final int position) {

        Api api = new Api(getActivity());

        api.postDelete(item.getId());

        getActivity().finish();
    }

    public void onPostReport(int position, int reasonId) {

        if (App.getInstance().isConnected()) {

            Api api = new Api(getActivity());

            api.postReport(item.getId(), reasonId);

        } else {

            Toast.makeText(getActivity(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void onPostEdit(final int position) {

        Intent i = new Intent(getActivity(), EditItemActivity.class);
        i.putExtra("position", position);
        i.putExtra("postId", item.getId());
        i.putExtra("post", item.getPost());
        i.putExtra("imgUrl", item.getImgUrl());
        startActivityForResult(i, ITEM_EDIT);
    }

    public void onPostShare(final int position) {

        Api api = new Api(getActivity());
        api.postShare(item);
    }

    public void onPostCopyLink(final int position) {

        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("post url", item.getLink());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getActivity(), getText(R.string.msg_post_link_copied), Toast.LENGTH_SHORT).show();
    }

    public void onPostRemove(int position) {

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        PostDeleteDialog alert = new PostDeleteDialog();

        Bundle b = new Bundle();
        b.putInt("position", 0);

        alert.setArguments(b);
        alert.show(fm, "alert_dialog_post_delete");
    }

    public void report(int position) {

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        PostReportDialog alert = new PostReportDialog();

        Bundle b  = new Bundle();
        b.putInt("position", position);
        b.putInt("reason", 0);

        alert.setArguments(b);
        alert.show(fm, "alert_dialog_post_report");
    }

    public void action(int position) {

        if (item.getFromUserId() == App.getInstance().getId()) {

            /** Getting the fragment manager */
            android.app.FragmentManager fm = getActivity().getFragmentManager();

            /** Instantiating the DialogFragment class */
            MyPostActionDialog alert = new MyPostActionDialog();

            /** Creating a bundle object to store the selected item's index */
            Bundle b  = new Bundle();

            /** Storing the selected item's index in the bundle object */
            b.putInt("position", position);

            /** Setting the bundle object to the dialog fragment object */
            alert.setArguments(b);

            /** Creating the dialog fragment object, which will in turn open the alert dialog window */

            alert.show(fm, "alert_my_post_action");

        } else {

            if (item.getGroupId() != 0 && item.getGroupAuthor() == App.getInstance().getId()) {

                /** Getting the fragment manager */
                android.app.FragmentManager fm = getActivity().getFragmentManager();

                /** Instantiating the DialogFragment class */
                MyGroupPostActionDialog alert = new MyGroupPostActionDialog();

                /** Creating a bundle object to store the selected item's index */
                Bundle b  = new Bundle();

                /** Storing the selected item's index in the bundle object */
                b.putInt("position", position);

                /** Setting the bundle object to the dialog fragment object */
                alert.setArguments(b);

                /** Creating the dialog fragment object, which will in turn open the alert dialog window */

                alert.show(fm, "alert_group_my_post_action");

            } else {

                /** Getting the fragment manager */
                android.app.FragmentManager fm = getActivity().getFragmentManager();

                /** Instantiating the DialogFragment class */
                PostActionDialog alert = new PostActionDialog();

                /** Creating a bundle object to store the selected item's index */
                Bundle b  = new Bundle();

                /** Storing the selected item's index in the bundle object */
                b.putInt("position", position);

                /** Setting the bundle object to the dialog fragment object */
                alert.setArguments(b);

                /** Creating the dialog fragment object, which will in turn open the alert dialog window */

                alert.show(fm, "alert_post_action");
            }
        }
    }

    public void repost(int position) {

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        PostShareDialog alert = new PostShareDialog();

        Bundle b = new Bundle();

        b.putInt("position", position);

        alert.setArguments(b);

        alert.show(fm, "alert_repost_action");
    }

    public void onPostRePost(final int position) {

        long rePostId = item.getId();

        if (item.getRePostId() != 0) {

            rePostId = item.getRePostId();
        }

        Intent i = new Intent(getActivity(), RePostItemActivity.class);
        i.putExtra("position", position);
        i.putExtra("rePostId", rePostId);
        startActivityForResult(i, ITEM_REPOST);
    }

    public void loadingComplete() {

        itemAdapter.notifyDataSetChanged();

        if (listView.getAdapter().getCount() == 0) {

            showEmptyScreen();

        } else {

            if (isAdded()) {

                showContentScreen();
            }
        }

        if (mContentContainer.isRefreshing()) {

            mContentContainer.setRefreshing(false);
        }
    }

    public void showLoadingScreen() {

        preload = true;

        mContentScreen.setVisibility(View.GONE);
        mErrorScreen.setVisibility(View.GONE);
        mEmptyScreen.setVisibility(View.GONE);

        mLoadingScreen.setVisibility(View.VISIBLE);
    }

    public void showEmptyScreen() {

        mContentScreen.setVisibility(View.GONE);
        mLoadingScreen.setVisibility(View.GONE);
        mErrorScreen.setVisibility(View.GONE);

        mEmptyScreen.setVisibility(View.VISIBLE);
    }

    public void showErrorScreen() {

        mContentScreen.setVisibility(View.GONE);
        mLoadingScreen.setVisibility(View.GONE);
        mEmptyScreen.setVisibility(View.GONE);

        mErrorScreen.setVisibility(View.VISIBLE);
    }

    public void showContentScreen() {

        preload = false;

        mLoadingScreen.setVisibility(View.GONE);
        mErrorScreen.setVisibility(View.GONE);
        mEmptyScreen.setVisibility(View.GONE);

        mContentScreen.setVisibility(View.VISIBLE);

        if (item.getGroupId() == 0){

            if (item.getAllowComments() == COMMENTS_DISABLED) {

                mCommentFormContainer.setVisibility(View.GONE);
            }

        } else {

            if (item.getGroupAllowComments() == 0) {

                mCommentFormContainer.setVisibility(View.GONE);
            }
        }
    }

    public void like(int position, JSONObject data) {


    }

    public void commentAction(int position) {

        final Comment comment = commentsList.get(position);

        if (comment.getFromUserId() != App.getInstance().getId() && item.getFromUserId() != App.getInstance().getId()) {

            /** Getting the fragment manager */
            android.app.FragmentManager fm = getActivity().getFragmentManager();

            /** Instantiating the DialogFragment class */
            CommentActionDialog alert = new CommentActionDialog();

            /** Creating a bundle object to store the selected item's index */
            Bundle b = new Bundle();

            /** Storing the selected item's index in the bundle object */
            b.putInt("position", position);

            /** Setting the bundle object to the dialog fragment object */
            alert.setArguments(b);

            /** Creating the dialog fragment object, which will in turn open the alert dialog window */

            alert.show(fm, "alert_dialog_comment_action");

        } else if (comment.getFromUserId() != App.getInstance().getId() && item.getFromUserId() == App.getInstance().getId()) {

            /** Getting the fragment manager */
            android.app.FragmentManager fm = getActivity().getFragmentManager();

            /** Instantiating the DialogFragment class */
            CommentUserActionDialog alert = new CommentUserActionDialog();

            /** Creating a bundle object to store the selected item's index */
            Bundle b = new Bundle();

            /** Storing the selected item's index in the bundle object */
            b.putInt("position", position);

            /** Setting the bundle object to the dialog fragment object */
            alert.setArguments(b);

            /** Creating the dialog fragment object, which will in turn open the alert dialog window */

            alert.show(fm, "alert_dialog_comment_user_action");

        } else {

            /** Getting the fragment manager */
            android.app.FragmentManager fm = getActivity().getFragmentManager();

            /** Instantiating the DialogFragment class */
            MyCommentActionDialog alert = new MyCommentActionDialog();

            /** Creating a bundle object to store the selected item's index */
            Bundle b  = new Bundle();

            /** Storing the selected item's index in the bundle object */
            b.putInt("position", position);

            /** Setting the bundle object to the dialog fragment object */
            alert.setArguments(b);

            /** Creating the dialog fragment object, which will in turn open the alert dialog window */

            alert.show(fm, "alert_dialog_my_comment_action");
        }
    }

    public void onCommentReply(final int position) {

        if (item.getGroupId() == 0) {

            if (item.getAllowComments() == COMMENTS_ENABLED) {

                final Comment comment = commentsList.get(position);

                replyToUserId = comment.getFromUserId();

                mCommentText.setText("@" + comment.getFromUserUsername() + ", ");
                mCommentText.setSelection(mCommentText.getText().length());

                mCommentText.requestFocus();

            } else {

                Toast.makeText(getActivity(), getString(R.string.msg_comments_disabled), Toast.LENGTH_SHORT).show();
            }

        } else {

            if (item.getGroupAllowComments() == 1) {

                final Comment comment = commentsList.get(position);

                replyToUserId = comment.getFromUserId();

                mCommentText.setText("@" + comment.getFromUserUsername() + ", ");
                mCommentText.setSelection(mCommentText.getText().length());

                mCommentText.requestFocus();

            } else {

                Toast.makeText(getActivity(), getString(R.string.group_comments_disabled), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onCommentRemove(int position) {

        /** Getting the fragment manager */
        android.app.FragmentManager fm = getActivity().getFragmentManager();

        /** Instantiating the DialogFragment class */
        CommentDeleteDialog alert = new CommentDeleteDialog();

        /** Creating a bundle object to store the selected item's index */
        Bundle b  = new Bundle();

        /** Storing the selected item's index in the bundle object */
        b.putInt("position", position);

        /** Setting the bundle object to the dialog fragment object */
        alert.setArguments(b);

        /** Creating the dialog fragment object, which will in turn open the alert dialog window */

        alert.show(fm, "alert_dialog_comment_delete");
    }

    public void onCommentDelete(final int position) {

        final Comment comment = commentsList.get(position);

        commentsList.remove(position);
        itemAdapter.notifyDataSetChanged();

        Api api = new Api(getActivity());

        api.commentDelete(comment.getId());
    }

    public void watchYoutubeVideo(String id) {

        if (YOUTUBE_API_KEY.length() > 5) {

            Intent i = new Intent(getActivity(), ViewYouTubeVideoActivity.class);
            i.putExtra("videoCode", id);
            startActivity(i);

        } else {

            try {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
                startActivity(intent);

            } catch (ActivityNotFoundException ex) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id));
                startActivity(intent);
            }
        }
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}