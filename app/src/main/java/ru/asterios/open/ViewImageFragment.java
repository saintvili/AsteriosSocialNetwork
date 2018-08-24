package ru.asterios.open;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import ru.asterios.open.dialogs.MyPhotoActionDialog;
import ru.asterios.open.dialogs.PhotoActionDialog;
import ru.asterios.open.dialogs.PhotoDeleteDialog;
import ru.asterios.open.dialogs.PhotoReportDialog;
import ru.asterios.open.model.Comment;
import ru.asterios.open.model.Image;
import ru.asterios.open.util.Api;
import ru.asterios.open.util.CommentInterface;
import ru.asterios.open.util.CustomRequest;
import ru.asterios.open.view.ResizableImageView;


public class ViewImageFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener, CommentInterface {

    private ProgressDialog pDialog;

    SwipeRefreshLayout mContentContainer;
    RelativeLayout mErrorScreen, mLoadingScreen, mEmptyScreen;
    LinearLayout mContentScreen, mCommentFormContainer, mItemLocationContainer;

    EmojiconEditText mCommentText;

    ListView listView;
    Button mRetryBtn;

    View mListViewHeader;

    ImageView mItemAction, mItemLike, mItemComment, mEmojiBtn, mSendComment;
    TextView mItemCity, mItemCountry, mItemAuthor, mItemUsername, mItemMode, mItemText, mItemTimeAgo, mItemLikesCount, mItemCommentsCount;
    ResizableImageView mItemImg;
    CircularImageView mItemAuthorPhoto;

    LinearLayout mItemHeader, mItemContent, mItemFooter;

    ImageLoader imageLoader = App.getInstance().getImageLoader();


    private ArrayList<Comment> commentsList;

    private CommentListAdapter itemAdapter;

    Image item;

    long itemId = 0, replyToUserId = 0;
    int arrayLength = 0;
    String commentText;

    private Boolean loading = false;
    private Boolean restore = false;
    private Boolean preload = false;

    EmojiconsPopup popup;

    public ViewImageFragment() {
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

        View rootView = inflater.inflate(R.layout.fragment_view_image, container, false);

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

        mListViewHeader = getActivity().getLayoutInflater().inflate(R.layout.image_list_row, null);

        listView.addHeaderView(mListViewHeader);

        listView.setAdapter(itemAdapter);

        mItemAuthorPhoto = (CircularImageView) mListViewHeader.findViewById(R.id.itemAuthorPhoto);
        mItemAction = (ImageView) mListViewHeader.findViewById(R.id.itemAction);
        mItemLike = (ImageView) mListViewHeader.findViewById(R.id.itemLike);
        mItemComment = (ImageView) mListViewHeader.findViewById(R.id.itemComment);

        mItemAuthor = (TextView) mListViewHeader.findViewById(R.id.itemAuthor);
        mItemUsername = (TextView) mListViewHeader.findViewById(R.id.itemUsername);
        mItemMode = (TextView) mListViewHeader.findViewById(R.id.itemMode);
        mItemText = (TextView) mListViewHeader.findViewById(R.id.itemText);
        mItemTimeAgo = (TextView) mListViewHeader.findViewById(R.id.itemTimeAgo);
        mItemLikesCount = (TextView) mListViewHeader.findViewById(R.id.itemLikesCount);
        mItemCommentsCount = (TextView) mListViewHeader.findViewById(R.id.itemCommentsCount);
        mItemCity = (TextView) mListViewHeader.findViewById(R.id.itemCity);
        mItemCountry = (TextView) mListViewHeader.findViewById(R.id.itemCountry);
        mItemLocationContainer = (LinearLayout) mListViewHeader.findViewById(R.id.locationContainer);
        mItemImg = (ResizableImageView) mListViewHeader.findViewById(R.id.itemImg);

        mItemHeader = (LinearLayout) mListViewHeader.findViewById(R.id.itemHeader);
        mItemContent = (LinearLayout) mListViewHeader.findViewById(R.id.itemContent);
        mItemFooter = (LinearLayout) mListViewHeader.findViewById(R.id.itemFooter);

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

    public String getItemModeText(int postMode) {

        switch (postMode) {

            case 0: {

                return getString(R.string.label_image_for_public);
            }

            default: {

                return getString(R.string.label_image_for_friends);
            }
        }
    }

    public void updateItem() {

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
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

        mItemMode.setText(getItemModeText(item.getAccessMode()));

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

                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("profileId", item.getFromUserId());
                startActivity(intent);
            }
        });

        mItemAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("profileId", item.getFromUserId());
                startActivity(intent);
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

                    CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_IMAGE_LIKE, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    if (!isAdded() || getActivity() == null) {

                                        Log.e("ERROR", "ViewImageFragment Not Added to Activity");

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

                                Log.e("ERROR", "ViewImageFragment Not Added to Activity");

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

                Intent intent = new Intent(getActivity(), LikersActivity.class);
                intent.putExtra("itemId", item.getId());
                intent.putExtra("itemType", ITEM_TYPE_IMAGE);
                startActivity(intent);
            }
        });


        mItemTimeAgo.setText(item.getTimeAgo());
        mItemTimeAgo.setVisibility(View.VISIBLE);

        if (item.getComment().length() > 0) {

            mItemText.setText(item.getComment().replaceAll("<br>", "\n"));

            mItemText.setVisibility(View.VISIBLE);

        } else {

            mItemText.setVisibility(View.GONE);
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

    public void getItem() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_IMAGE_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "ViewImageFragment Not Added to Activity");

                            return;
                        }

                        try {

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                commentsList.clear();

                                itemId = response.getInt("itemId");

                                if (response.has("items")) {

                                    JSONArray itemsArray = response.getJSONArray("items");

                                    arrayLength = itemsArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < itemsArray.length(); i++) {

                                            JSONObject itemObj = (JSONObject) itemsArray.get(i);

                                            item = new Image(itemObj);

                                            updateItem();
                                        }
                                    }
                                }

                                if (response.has("comments") && item.getFromUserAllowPhotosComments() == 1) {

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

                    Log.e("ERROR", "ViewImageFragment Not Added to Activity");

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

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_IMAGE_COMMENTS_NEW, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (!isAdded() || getActivity() == null) {

                                Log.e("ERROR", "ViewImageFragment Not Added to Activity");

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

                        Log.e("ERROR", "ViewImageFragment Not Added to Activity");

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

    public void onPhotoDelete(final int position) {

        Api api = new Api(getActivity());

        api.photoDelete(item.getId());

        getActivity().finish();
    }

    public void onPhotoReport(int position, int reasonId) {

        if (App.getInstance().isConnected()) {

            Api api = new Api(getActivity());

            api.photoReport(item.getId(), reasonId);

        } else {

            Toast.makeText(getActivity(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void remove(int position) {

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        PhotoDeleteDialog alert = new PhotoDeleteDialog();

        Bundle b = new Bundle();
        b.putInt("position", 0);

        alert.setArguments(b);
        alert.show(fm, "alert_dialog_photo_delete");
    }

    public void report(int position) {

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        PhotoReportDialog alert = new PhotoReportDialog();

        Bundle b  = new Bundle();
        b.putInt("position", position);
        b.putInt("reason", 0);

        alert.setArguments(b);
        alert.show(fm, "alert_dialog_photo_report");
    }

    public void action(int position) {

        if (item.getFromUserId() == App.getInstance().getId()) {

            /** Getting the fragment manager */
            android.app.FragmentManager fm = getActivity().getFragmentManager();

            /** Instantiating the DialogFragment class */
            MyPhotoActionDialog alert = new MyPhotoActionDialog();

            /** Creating a bundle object to store the selected item's index */
            Bundle b  = new Bundle();

            /** Storing the selected item's index in the bundle object */
            b.putInt("position", position);

            /** Setting the bundle object to the dialog fragment object */
            alert.setArguments(b);

            /** Creating the dialog fragment object, which will in turn open the alert dialog window */

            alert.show(fm, "alert_my_post_action");

        } else {

            /** Getting the fragment manager */
            android.app.FragmentManager fm = getActivity().getFragmentManager();

            /** Instantiating the DialogFragment class */
            PhotoActionDialog alert = new PhotoActionDialog();

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

    public void loadingComplete() {

        itemAdapter.notifyDataSetChanged();

        if (listView.getAdapter().getCount() == 0) {

            showEmptyScreen();

        } else {

            showContentScreen();
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

        if (item.getFromUserAllowPhotosComments() == COMMENTS_DISABLED) {

            mCommentFormContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

        if (item.getFromUserAllowPhotosComments() == COMMENTS_ENABLED) {

            final Comment comment = commentsList.get(position);

            replyToUserId = comment.getFromUserId();

            mCommentText.setText("@" + comment.getFromUserUsername() + ", ");
            mCommentText.setSelection(mCommentText.getText().length());

            mCommentText.requestFocus();

        } else {

            Toast.makeText(getActivity(), getString(R.string.msg_comments_disabled), Toast.LENGTH_SHORT).show();
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

        api.imagesCommentDelete(comment.getId());
    }
}