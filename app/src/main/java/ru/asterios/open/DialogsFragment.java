package ru.asterios.open;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.asterios.open.adapter.DialogListAdapter;
import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;
import ru.asterios.open.model.Chat;
import ru.asterios.open.util.CustomRequest;

public class DialogsFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_LIST = "State Adapter Data";

    ListView mListView;
    TextView mMessage;
    ImageView mSplash;

    SwipeRefreshLayout mItemsContainer;

    private ArrayList<Chat> chatsList;
    private DialogListAdapter chatsAdapter;

    private int messageCreateAt = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean restore = false;

    public DialogsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

            chatsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            chatsAdapter = new DialogListAdapter(getActivity(), chatsList);

            restore = savedInstanceState.getBoolean("restore");
            messageCreateAt = savedInstanceState.getInt("messageCreateAt");

        } else {

            chatsList = new ArrayList<Chat>();
            chatsAdapter = new DialogListAdapter(getActivity(), chatsList);

            restore = false;
            messageCreateAt = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dialogs, container, false);

        mItemsContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);

        mMessage = (TextView) rootView.findViewById(R.id.message);
        mSplash = (ImageView) rootView.findViewById(R.id.splash);

        mListView = (ListView) rootView.findViewById(R.id.listView);
        mListView.setAdapter(chatsAdapter);

        if (chatsAdapter.getCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Chat chat = (Chat) adapterView.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("chatId", chat.getId());
                intent.putExtra("profileId", chat.getWithUserId());
                intent.putExtra("withProfile", chat.getWithUserFullname());

                intent.putExtra("with_android_fcm_regId", chat.get_android_fcm_regId());
                intent.putExtra("with_ios_fcm_regId", chat.get_iOS_fcm_regId());
                intent.putExtra("with_android_msg_fcm_regId", chat.get_android_msg_fcm_regId());
                intent.putExtra("with_ios_msg_fcm_regId", chat.get_iOS_msg_fcm_regId());

                intent.putExtra("with_user_username", chat.getWithUserUsername());
                intent.putExtra("with_user_fullname", chat.getWithUserFullname());
                intent.putExtra("with_user_photo_url", chat.getWithUserPhotoUrl());

                intent.putExtra("with_user_state", chat.getWithUserState());
                intent.putExtra("with_user_verified", chat.getWithUserVerify());

                intent.putExtra("blocked", chat.getBlocked());

                intent.putExtra("fromUserId", chat.getFromUserId());
                intent.putExtra("toUserId", chat.getToUserId());

                startActivityForResult(intent, VIEW_CHAT);

                chat.setNewMessagesCount(0);

                if (App.getInstance().getMessagesCount() > 0) {

                    App.getInstance().setMessagesCount(App.getInstance().getMessagesCount() - 1);
                }

                chatsAdapter.notifyDataSetChanged();
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;

                if ((lastInScreen == totalItemCount) && !(loadingMore) && (viewMore) && !(mItemsContainer.isRefreshing())) {

                    if (App.getInstance().isConnected()) {

                        loadingMore = true;

                        getConversations();
                    }
                }
            }
        });

        if (!restore) {

            showMessage(getText(R.string.msg_loading_2).toString());

            getConversations();
        }


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            messageCreateAt = 0;
            getConversations();

        } else {

            mItemsContainer.setRefreshing(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIEW_CHAT && resultCode == getActivity().RESULT_OK && null != data) {

            int pos = data.getIntExtra("position", 0);

            Toast.makeText(getActivity(), getString(R.string.msg_chat_has_been_removed), Toast.LENGTH_SHORT).show();

            chatsList.remove(pos);

            chatsAdapter.notifyDataSetChanged();

            if (chatsAdapter.getCount() == 0) {

                showMessage(getText(R.string.label_empty_list).toString());

            } else {

                hideMessage();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putInt("messageCreateAt", messageCreateAt);
        outState.putParcelableArrayList(STATE_LIST, chatsList);
    }

    public void getConversations() {

        mItemsContainer.setRefreshing(true);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_DIALOGS_NEW_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "DialogsFragment Not Added to Activity");

                            return;
                        }

                        try {

                            arrayLength = 0;

                            if (!loadingMore) {

                                chatsList.clear();
                            }

                            if (!response.getBoolean("error")) {

                                messageCreateAt = response.getInt("messageCreateAt");

                                if (response.has("chats")) {

                                    JSONArray chatsArray = response.getJSONArray("chats");

                                    arrayLength = chatsArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < chatsArray.length(); i++) {

                                            JSONObject chatObj = (JSONObject) chatsArray.get(i);

                                            Chat chat = new Chat(chatObj);

                                            chatsList.add(chat);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            loadingComplete();

                            e.printStackTrace();

                        } finally {

                            loadingComplete();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "DialogsFragment Not Added to Activity");

                    return;
                }

                loadingComplete();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("messageCreateAt", Integer.toString(messageCreateAt));
                params.put("language", "en");

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void loadingComplete() {

        if (arrayLength == LIST_ITEMS) {

            viewMore = true;

        } else {

            viewMore = false;
        }

        chatsAdapter.notifyDataSetChanged();

        if (chatsAdapter.getCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        loadingMore = false;
        mItemsContainer.setRefreshing(false);
    }

    public void showMessage(String message) {

        mMessage.setText(message);
        mMessage.setVisibility(View.VISIBLE);

        mSplash.setVisibility(View.VISIBLE);
    }

    public void hideMessage() {

        mMessage.setVisibility(View.GONE);

        mSplash.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}