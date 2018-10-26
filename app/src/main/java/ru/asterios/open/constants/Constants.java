package ru.asterios.open.constants;

public interface Constants {

    // Attention! You can only change the values of the following constants:

    // YOUTUBE_API_KEY, EMOJI_KEYBOARD, WEB_SITE_AVAILABLE, GOOGLE_PAY_TEST_BUTTON, MY_AD_AFTER_ITEM_NUMBER,
    // APP_TEMP_FOLDER, VIDEO_FILE_MAX_SIZE, BILLING_KEY, WEB_SITE, CLIENT_ID, API_DOMAIN,
    // GHOST_MODE_COST, VERIFIED_BADGE_COST, DISABLE_ADS_COST
    // POST_CHARACTERS_LIMIT, HASHTAGS_COLOR

    // It is forbidden to change the value of constants, which are not indicated above !!!

    public static final Boolean EMOJI_KEYBOARD = true; // false = Do not display your own Emoji keyboard | true = allow display your own Emoji keyboard

    public static final Boolean WEB_SITE_AVAILABLE = true; // false = Do not show menu items (Open in browser, Copy profile link) in profile page | true = show menu items (Open in browser, Copy profile link) in profile page

    public static final Boolean GOOGLE_PAY_TEST_BUTTON = false; // false = Do not show google pay test button in section upgrades

    public static final int MY_AD_AFTER_ITEM_NUMBER = 0;  //After first item

    public static final String APP_TEMP_FOLDER = "AsteriosNetwork"; //directory for temporary storage of images from the camera

    public static final int VIDEO_FILE_MAX_SIZE = 7340035; //change this official topic view this  https://asterios.ws/developers_videosize


    public static final String YOUTUBE_API_KEY = "AIz1aSyAz8TvzJtYkvsNNaRsQz2Td9atmQZOyc32OTk";  //you youtube api key

   

    public static final String BILLING_KEY = "MIIBdfdfgfG9w0BAQEFQ8AMIIBCgK";

    public static final String WEB_SITE = "https://asterios.ws/";  //Don't change


    public static final String CLIENT_ID = "1743";  // default 1 and moree view this page https://asterios.ws/Develop_Help/post/38774

    public static final String API_DOMAIN = "https://newapi.asterios.ws/";  // url address to which the application sends requests

    public static final String API_FILE_EXTENSION = "";     // Attention! Do not change the value for this constant!
    public static final String API_VERSION = "v23";          // Attention! Do not change the value for this constant!

    // Attention! Do not change values for next constants!

    public static final String METHOD_NOTIFICATIONS_CLEAR = API_DOMAIN + "api/" + API_VERSION + "/method/notifications.clear" + API_FILE_EXTENSION;
    public static final String METHOD_GUESTS_CLEAR = API_DOMAIN + "api/" + API_VERSION + "/method/guests.clear" + API_FILE_EXTENSION;

    public static final String METHOD_GROUP_SEARCH_PRELOAD = API_DOMAIN + "api/" + API_VERSION + "/method/group.searchPreload" + API_FILE_EXTENSION;

    public static final String METHOD_ACCOUNT_PRIVACY = API_DOMAIN + "api/" + API_VERSION + "/method/account.privacy" + API_FILE_EXTENSION;

    public static final String METHOD_FRIENDS_REQUEST = API_DOMAIN + "api/" + API_VERSION + "/method/friends.sendRequest" + API_FILE_EXTENSION;
    public static final String METHOD_FRIENDS_ACCEPT = API_DOMAIN + "api/" + API_VERSION + "/method/friends.acceptRequest" + API_FILE_EXTENSION;
    public static final String METHOD_FRIENDS_REJECT = API_DOMAIN + "api/" + API_VERSION + "/method/friends.rejectRequest" + API_FILE_EXTENSION;
    public static final String METHOD_FRIENDS_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/friends.remove" + API_FILE_EXTENSION;
    public static final String METHOD_FRIENDS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/friends.get" + API_FILE_EXTENSION;

    public static final String METHOD_ACCOUNT_GET_SETTINGS = API_DOMAIN + "api/" + API_VERSION + "/method/account.getSettings" + API_FILE_EXTENSION;
    public static final String METHOD_DIALOGS_NEW_GET = API_DOMAIN + "api/" + API_VERSION + "/method/dialogs_new.get" + API_FILE_EXTENSION;
    public static final String METHOD_CHAT_UPDATE = API_DOMAIN + "api/" + API_VERSION + "/method/chat.update" + API_FILE_EXTENSION;

    public static final String METHOD_ACCOUNT_LOGIN = API_DOMAIN + "api/" + API_VERSION + "/method/account.signIn" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_SIGNUP = API_DOMAIN + "api/" + API_VERSION + "/method/account.signUp" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_AUTHORIZE = API_DOMAIN + "api/" + API_VERSION + "/method/account.authorize" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_SET_GCM_TOKEN = API_DOMAIN + "api/" + API_VERSION + "/method/account.setGcmToken" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_LOGINBYFACEBOOK = API_DOMAIN + "api/" + API_VERSION + "/method/account.signInByFacebook" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_RECOVERY = API_DOMAIN + "api/" + API_VERSION + "/method/account.recovery" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_SETPASSWORD = API_DOMAIN + "api/" + API_VERSION + "/method/account.setPassword" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_DEACTIVATE = API_DOMAIN + "api/" + API_VERSION + "/method/account.deactivate" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_SAVE_SETTINGS = API_DOMAIN + "api/" + API_VERSION + "/method/account.saveSettings" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_CONNECT_TO_FACEBOOK = API_DOMAIN + "api/" + API_VERSION + "/method/account.connectToFacebook" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_DISCONNECT_FROM_FACEBOOK = API_DOMAIN + "api/" + API_VERSION + "/method/account.disconnectFromFacebook" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_LOGOUT = API_DOMAIN + "api/" + API_VERSION + "/method/account.logOut" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_SET_ALLOW_COMMENTS = API_DOMAIN + "api/" + API_VERSION + "/method/account.setAllowComments" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_SET_ALLOW_MESSAGES = API_DOMAIN + "api/" + API_VERSION + "/method/account.setAllowMessages" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_SET_ALLOW_PHOTOS_COMMENTS = API_DOMAIN + "api/" + API_VERSION + "/method/account.setAllowPhotosComments" + API_FILE_EXTENSION;

    public static final String METHOD_GIFTS_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/gifts.remove" + API_FILE_EXTENSION;
    public static final String METHOD_GIFTS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/gifts.get" + API_FILE_EXTENSION;
    public static final String METHOD_GIFTS_SELECT = API_DOMAIN + "api/" + API_VERSION + "/method/gifts.select" + API_FILE_EXTENSION;
    public static final String METHOD_GIFTS_SEND = API_DOMAIN + "api/" + API_VERSION + "/method/gifts.send" + API_FILE_EXTENSION;

    public static final String METHOD_ACCOUNT_SET_GEO_LOCATION = API_DOMAIN + "api/" + API_VERSION + "/method/account.setGeoLocation" + API_FILE_EXTENSION;

    public static final String METHOD_PROFILE_PEOPLE_NEARBY_GET = API_DOMAIN + "api/" + API_VERSION + "/method/profile.getPeopleNearby" + API_FILE_EXTENSION;

    public static final String METHOD_ACCOUNT_ADD_FUNDS = API_DOMAIN + "api/" + API_VERSION + "/method/account.addFunds" + API_FILE_EXTENSION;

    public static final String METHOD_ACCOUNT_SET_GHOST_MODE = API_DOMAIN + "api/" + API_VERSION + "/method/account.setGhostMode" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_SET_VERIFIED_BADGE = API_DOMAIN + "api/" + API_VERSION + "/method/account.setVerifiedBadge" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_SET_DISABLE_ADS = API_DOMAIN + "api/" + API_VERSION + "/method/account.disableAds" + API_FILE_EXTENSION;

    public static final String METHOD_GUESTS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/guests.get" + API_FILE_EXTENSION;

    public static final String METHOD_SUPPORT_SEND_TICKET = API_DOMAIN + "api/" + API_VERSION + "/method/support.sendTicket" + API_FILE_EXTENSION;

    public static final String METHOD_SETTINGS_LIKES_GCM = API_DOMAIN + "api/" + API_VERSION + "/method/account.setAllowLikesGCM" + API_FILE_EXTENSION;
    public static final String METHOD_SETTINGS_COMMENTS_GCM = API_DOMAIN + "api/" + API_VERSION + "/method/account.setAllowCommentsGCM" + API_FILE_EXTENSION;
    public static final String METHOD_SETTINGS_FOLLOWERS_GCM = API_DOMAIN + "api/" + API_VERSION + "/method/account.setAllowFollowersGCM" + API_FILE_EXTENSION;
    public static final String METHOD_SETTINGS_MESSAGES_GCM = API_DOMAIN + "api/" + API_VERSION + "/method/account.setAllowMessagesGCM" + API_FILE_EXTENSION;
    public static final String METHOD_SETTINGS_GIFTS_GCM = API_DOMAIN + "api/" + API_VERSION + "/method/account.setAllowGiftsGCM" + API_FILE_EXTENSION;
    public static final String METHOD_SETTINGS_COMMENT_REPLY_GCM = API_DOMAIN + "api/" + API_VERSION + "/method/account.setAllowCommentReplyGCM" + API_FILE_EXTENSION;

    public static final String METHOD_PROFILE_GET = API_DOMAIN + "api/" + API_VERSION + "/method/profile.get" + API_FILE_EXTENSION;
    public static final String METHOD_PROFILE_FOLLOWINGS = API_DOMAIN + "api/" + API_VERSION + "/method/profile.followings" + API_FILE_EXTENSION;
    public static final String METHOD_PROFILE_FOLLOWERS = API_DOMAIN + "api/" + API_VERSION + "/method/profile.followers" + API_FILE_EXTENSION;
    public static final String METHOD_PROFILE_FOLLOW = API_DOMAIN + "api/" + API_VERSION + "/method/profile.follow" + API_FILE_EXTENSION;
    public static final String METHOD_PROFILE_REPORT = API_DOMAIN + "api/" + API_VERSION + "/method/profile.report" + API_FILE_EXTENSION;
    public static final String METHOD_PROFILE_UPLOADPHOTO = API_DOMAIN + "api/" + API_VERSION + "/method/profile.uploadPhoto" + API_FILE_EXTENSION;
    public static final String METHOD_PROFILE_UPLOADCOVER = API_DOMAIN + "api/" + API_VERSION + "/method/profile.uploadCover" + API_FILE_EXTENSION;
    public static final String METHOD_WALL_GET = API_DOMAIN + "api/" + API_VERSION + "/method/wall.get" + API_FILE_EXTENSION;

    public static final String METHOD_GROUP_CREATE = API_DOMAIN + "api/" + API_VERSION + "/method/group.create" + API_FILE_EXTENSION;
    public static final String METHOD_GROUP_SAVE_SETTINGS = API_DOMAIN + "api/" + API_VERSION + "/method/group.saveSettings" + API_FILE_EXTENSION;
    public static final String METHOD_GROUP_GET = API_DOMAIN + "api/" + API_VERSION + "/method/group.get" + API_FILE_EXTENSION;
    public static final String METHOD_GROUP_GET_MY_GROUPS = API_DOMAIN + "api/" + API_VERSION + "/method/group.getMyGroups" + API_FILE_EXTENSION;
    public static final String METHOD_GROUP_GET_MANAGED_GROUPS = API_DOMAIN + "api/" + API_VERSION + "/method/group.getManagedGroups" + API_FILE_EXTENSION;
    public static final String METHOD_GROUP_GET_FOLLOWERS = API_DOMAIN + "api/" + API_VERSION + "/method/group.getFollowers" + API_FILE_EXTENSION;
    public static final String METHOD_GROUP_FOLLOW = API_DOMAIN + "api/" + API_VERSION + "/method/group.follow" + API_FILE_EXTENSION;
    public static final String METHOD_GROUP_SEARCH = API_DOMAIN + "api/" + API_VERSION + "/method/group.search" + API_FILE_EXTENSION;
    public static final String METHOD_GROUP_UPLOADPHOTO = API_DOMAIN + "api/" + API_VERSION + "/method/group.uploadPhoto" + API_FILE_EXTENSION;
    public static final String METHOD_GROUP_GET_WALL = API_DOMAIN + "api/" + API_VERSION + "/method/group.getWall" + API_FILE_EXTENSION;

    public static final String METHOD_BLACKLIST_GET = API_DOMAIN + "api/" + API_VERSION + "/method/blacklist.get" + API_FILE_EXTENSION;
    public static final String METHOD_BLACKLIST_ADD = API_DOMAIN + "api/" + API_VERSION + "/method/blacklist.add" + API_FILE_EXTENSION;
    public static final String METHOD_BLACKLIST_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/blacklist.remove" + API_FILE_EXTENSION;

    public static final String METHOD_PHOTOS_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/photos.remove" + API_FILE_EXTENSION;
    public static final String METHOD_PHOTOS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/photos.get" + API_FILE_EXTENSION;
    public static final String METHOD_PHOTOS_NEW = API_DOMAIN + "api/" + API_VERSION + "/method/photos.new" + API_FILE_EXTENSION;
    public static final String METHOD_PHOTOS_REPORT = API_DOMAIN + "api/" + API_VERSION + "/method/photos.report" + API_FILE_EXTENSION;
    public static final String METHOD_PHOTOS_UPLOAD_IMG = API_DOMAIN + "api/" + API_VERSION + "/method/photos.uploadImg" + API_FILE_EXTENSION;

    public static final String METHOD_IMAGE_GET = API_DOMAIN + "api/" + API_VERSION + "/method/images.get" + API_FILE_EXTENSION;
    public static final String METHOD_IMAGE_COMMENTS_NEW = API_DOMAIN + "api/" + API_VERSION + "/method/images.comment" + API_FILE_EXTENSION;
    public static final String METHOD_IMAGE_COMMENTS_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/images.commentRemove" + API_FILE_EXTENSION;
    public static final String METHOD_IMAGE_LIKE = API_DOMAIN + "api/" + API_VERSION + "/method/images.like" + API_FILE_EXTENSION;
    public static final String METHOD_IMAGE_LIKES = API_DOMAIN + "api/" + API_VERSION + "/method/images.likes" + API_FILE_EXTENSION;

    public static final String METHOD_VIDEO_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/video.remove" + API_FILE_EXTENSION;
    public static final String METHOD_VIDEO_GET = API_DOMAIN + "api/" + API_VERSION + "/method/video.get" + API_FILE_EXTENSION;
    public static final String METHOD_VIDEO_NEW = API_DOMAIN + "api/" + API_VERSION + "/method/video.new" + API_FILE_EXTENSION;
    public static final String METHOD_VIDEO_REPORT = API_DOMAIN + "api/" + API_VERSION + "/method/video.report" + API_FILE_EXTENSION;
    public static final String METHOD_VIDEO_UPLOAD = API_DOMAIN + "api/" + API_VERSION + "/method/video.upload" + API_FILE_EXTENSION;
    public static final String METHOD_VIDEO_ITEM_GET = API_DOMAIN + "api/" + API_VERSION + "/method/videoItem.get" + API_FILE_EXTENSION;
    public static final String METHOD_VIDEO_COMMENTS_NEW = API_DOMAIN + "api/" + API_VERSION + "/method/video.comment" + API_FILE_EXTENSION;
    public static final String METHOD_VIDEO_COMMENTS_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/video.commentRemove" + API_FILE_EXTENSION;
    public static final String METHOD_VIDEO_LIKE = API_DOMAIN + "api/" + API_VERSION + "/method/video.like" + API_FILE_EXTENSION;
    public static final String METHOD_VIDEO_LIKES = API_DOMAIN + "api/" + API_VERSION + "/method/video.likes" + API_FILE_EXTENSION;

    public static final String METHOD_NOTIFICATIONS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/notifications.get" + API_FILE_EXTENSION;
    public static final String METHOD_HASHTAGS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/hashtags.get" + API_FILE_EXTENSION;
    public static final String METHOD_FEEDS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/feeds.get" + API_FILE_EXTENSION;
    public static final String METHOD_ITEM_GET = API_DOMAIN + "api/" + API_VERSION + "/method/item.get" + API_FILE_EXTENSION;
    public static final String METHOD_STREAM_GET = API_DOMAIN + "api/" + API_VERSION + "/method/stream.get" + API_FILE_EXTENSION;
    public static final String METHOD_POPULAR_GET = API_DOMAIN + "api/" + API_VERSION + "/method/popular.get" + API_FILE_EXTENSION;

    public static final String METHOD_APP_CHECKUSERNAME = API_DOMAIN + "api/" + API_VERSION + "/method/app.checkUsername" + API_FILE_EXTENSION;
    public static final String METHOD_APP_TERMS = API_DOMAIN + "api/" + API_VERSION + "/method/app.terms" + API_FILE_EXTENSION;
    public static final String METHOD_APP_THANKS = API_DOMAIN + "api/" + API_VERSION + "/method/app.thanks" + API_FILE_EXTENSION;
    public static final String METHOD_APP_SEARCH = API_DOMAIN + "api/" + API_VERSION + "/method/app.search" + API_FILE_EXTENSION;

    public static final String METHOD_APP_SEARCH_PRELOAD = API_DOMAIN + "api/" + API_VERSION + "/method/app.searchPreload" + API_FILE_EXTENSION;

    public static final String METHOD_ITEMS_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/items.remove" + API_FILE_EXTENSION;
    public static final String METHOD_ITEMS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/items.get" + API_FILE_EXTENSION;
    public static final String METHOD_ITEMS_UPLOAD_IMG = API_DOMAIN + "api/" + API_VERSION + "/method/items.uploadImg" + API_FILE_EXTENSION;
    public static final String METHOD_ITEMS_NEW = API_DOMAIN + "api/" + API_VERSION + "/method/items.new" + API_FILE_EXTENSION;
    public static final String METHOD_ITEMS_EDIT = API_DOMAIN + "api/" + API_VERSION + "/method/items.edit" + API_FILE_EXTENSION;
    public static final String METHOD_ITEMS_REPORT = API_DOMAIN + "api/" + API_VERSION + "/method/items.report" + API_FILE_EXTENSION;
    public static final String METHOD_ITEMS_LIKE = API_DOMAIN + "api/" + API_VERSION + "/method/items.like" + API_FILE_EXTENSION;
    public static final String METHOD_ITEMS_LIKES = API_DOMAIN + "api/" + API_VERSION + "/method/items.likes" + API_FILE_EXTENSION;

    public static final String METHOD_FAVORITES_GET = API_DOMAIN + "api/" + API_VERSION + "/method/favorites.get" + API_FILE_EXTENSION;

    public static final String METHOD_COMMENTS_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/comments.remove" + API_FILE_EXTENSION;
    public static final String METHOD_COMMENTS_NEW = API_DOMAIN + "api/" + API_VERSION + "/method/comments.new" + API_FILE_EXTENSION;

    public static final String METHOD_CHAT_GET = API_DOMAIN + "api/" + API_VERSION + "/method/chat.get" + API_FILE_EXTENSION;
    public static final String METHOD_CHAT_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/chat.remove" + API_FILE_EXTENSION;
    public static final String METHOD_CHAT_GET_PREVIOUS = API_DOMAIN + "api/" + API_VERSION + "/method/chat.getPrevious" + API_FILE_EXTENSION;
    public static final String METHOD_CHAT_GET_NEXT = API_DOMAIN + "api/" + API_VERSION + "/method/chat.getNext" + API_FILE_EXTENSION;
    public static final String METHOD_CONVERSATIONS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/conversations.get" + API_FILE_EXTENSION;

    public static final String METHOD_MSG_NEW = API_DOMAIN + "api/" + API_VERSION + "/method/msg.new" + API_FILE_EXTENSION;
    public static final String METHOD_MSG_UPLOAD_IMG = API_DOMAIN + "api/" + API_VERSION + "/method/msg.uploadImg" + API_FILE_EXTENSION;

    public static final String METHOD_REFERRALS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/referrals.get" + API_FILE_EXTENSION;

    public static final String METHOD_MARKET_SEARCH = API_DOMAIN + "api/" + API_VERSION + "/method/market.search" + API_FILE_EXTENSION;
    public static final String METHOD_MARKET_SEARCH_PRELOAD = API_DOMAIN + "api/" + API_VERSION + "/method/market.preload" + API_FILE_EXTENSION;
    public static final String METHOD_MARKET_NEW_ITEM = API_DOMAIN + "api/" + API_VERSION + "/method/market.newItem" + API_FILE_EXTENSION;
    public static final String METHOD_MARKET_REMOVE_ITEM = API_DOMAIN + "api/" + API_VERSION + "/method/market.removeItem" + API_FILE_EXTENSION;
    public static final String METHOD_MARKET_UPLOAD_IMG = API_DOMAIN + "api/" + API_VERSION + "/method/market.uploadImg" + API_FILE_EXTENSION;
    public static final String METHOD_MARKET_GET_MY_ITEMS = API_DOMAIN + "api/" + API_VERSION + "/method/market.getMyItems" + API_FILE_EXTENSION;

    // added for version 3.9

    public static final String METHOD_GET_STICKERS = API_DOMAIN + "api/" + API_VERSION + "/method/stickers.get" + API_FILE_EXTENSION;

    // added for version 4.1

    public static final String METHOD_CHAT_NOTIFY = API_DOMAIN + "api/" + API_VERSION + "/method/chat.notify" + API_FILE_EXTENSION;

    // added for version 4.3

    public static final String METHOD_ACCOUNT_SET_PRO_MODE = API_DOMAIN + "api/" + API_VERSION + "/method/account.setProMode" + API_FILE_EXTENSION;

    // Other Constants

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO = 1;                  //WRITE_EXTERNAL_STORAGE
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_COVER = 2;                  //WRITE_EXTERNAL_STORAGE
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 3;                               //ACCESS_COARSE_LOCATION
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_VIDEO_IMAGE = 4;            //WRITE_EXTERNAL_STORAGE

    public static final int GHOST_MODE_COST = 100;      //Cost in Credits
    public static final int VERIFIED_BADGE_COST = 150;  //Cost in Credits
    public static final int PRO_MODE_COST = 170;        //Cost in Credits
    public static final int DISABLE_ADS_COST = 200;     //Cost in Credits

    public static final int LIST_ITEMS = 20;

    public static final int POST_CHARACTERS_LIMIT = 1000;

    public static final int ENABLED = 1;
    public static final int DISABLED = 0;

    public static final int GCM_ENABLED = 1;
    public static final int GCM_DISABLED = 0;

    public static final int ADMOB_ENABLED = 1;
    public static final int ADMOB_DISABLED = 0;

    public static final int COMMENTS_ENABLED = 1;
    public static final int COMMENTS_DISABLED = 0;

    public static final int MESSAGES_ENABLED = 1;
    public static final int MESSAGES_DISABLED = 0;

    public static final int ERROR_SUCCESS = 0;

    public static final int SEX_UNKNOWN = 0;
    public static final int SEX_MALE = 1;
    public static final int SEX_FEMALE = 2;

    public static final int NOTIFY_TYPE_LIKE = 0;
    public static final int NOTIFY_TYPE_FOLLOWER = 1;
    public static final int NOTIFY_TYPE_MESSAGE = 2;
    public static final int NOTIFY_TYPE_COMMENT = 3;
    public static final int NOTIFY_TYPE_COMMENT_REPLY = 4;
    public static final int NOTIFY_TYPE_FRIEND_REQUEST_ACCEPTED = 5;
    public static final int NOTIFY_TYPE_GIFT = 6;

    public static final int NOTIFY_TYPE_IMAGE_COMMENT = 7;
    public static final int NOTIFY_TYPE_IMAGE_COMMENT_REPLY = 8;
    public static final int NOTIFY_TYPE_IMAGE_LIKE = 9;

    public static final int NOTIFY_TYPE_VIDEO_COMMENT = 10;
    public static final int NOTIFY_TYPE_VIDEO_COMMENT_REPLY = 11;
    public static final int NOTIFY_TYPE_VIDEO_LIKE = 12;

    public static final int NOTIFY_TYPE_REFERRAL = 14;

    public static final int GCM_NOTIFY_CONFIG = 0;
    public static final int GCM_NOTIFY_SYSTEM = 1;
    public static final int GCM_NOTIFY_CUSTOM = 2;
    public static final int GCM_NOTIFY_LIKE = 3;
    public static final int GCM_NOTIFY_ANSWER = 4;
    public static final int GCM_NOTIFY_QUESTION = 5;
    public static final int GCM_NOTIFY_COMMENT = 6;
    public static final int GCM_NOTIFY_FOLLOWER = 7;
    public static final int GCM_NOTIFY_PERSONAL = 8;
    public static final int GCM_NOTIFY_MESSAGE = 9;
    public static final int GCM_NOTIFY_COMMENT_REPLY = 10;
    public static final int GCM_FRIEND_REQUEST_INBOX = 11;
    public static final int GCM_FRIEND_REQUEST_ACCEPTED = 12;
    public static final int GCM_NOTIFY_GIFT = 14;
    public static final int GCM_NOTIFY_SEEN = 15;
    public static final int GCM_NOTIFY_TYPING = 16;
    public static final int GCM_NOTIFY_URL = 17;

    public static final int GCM_NOTIFY_IMAGE_COMMENT_REPLY = 18;
    public static final int GCM_NOTIFY_IMAGE_COMMENT = 19;
    public static final int GCM_NOTIFY_IMAGE_LIKE = 20;

    public static final int GCM_NOTIFY_VIDEO_COMMENT_REPLY = 21;
    public static final int GCM_NOTIFY_VIDEO_COMMENT = 22;
    public static final int GCM_NOTIFY_VIDEO_LIKE = 23;

    public static final int GCM_NOTIFY_REFERRAL = 24;

    public static final int GCM_NOTIFY_TYPING_START = 27;
    public static final int GCM_NOTIFY_TYPING_END = 28;


    public static final int ERROR_LOGIN_TAKEN = 300;
    public static final int ERROR_EMAIL_TAKEN = 301;
    public static final int ERROR_FACEBOOK_ID_TAKEN = 302;

    public static final int ACCOUNT_STATE_ENABLED = 0;
    public static final int ACCOUNT_STATE_DISABLED = 1;
    public static final int ACCOUNT_STATE_BLOCKED = 2;
    public static final int ACCOUNT_STATE_DEACTIVATED = 3;

    public static final int ACCOUNT_TYPE_USER = 0;
    public static final int ACCOUNT_TYPE_GROUP = 1;

    public static final int ERROR_UNKNOWN = 100;
    public static final int ERROR_ACCESS_TOKEN = 101;

    public static final int ERROR_ACCOUNT_ID = 400;

    public static final int UPLOAD_TYPE_PHOTO = 0;
    public static final int UPLOAD_TYPE_COVER = 1;

    public static final int ACTION_NEW = 1;
    public static final int ACTION_EDIT = 2;
    public static final int SELECT_POST_IMG = 3;
    public static final int VIEW_CHAT = 4;
    public static final int CREATE_POST_IMG = 5;
    public static final int SELECT_CHAT_IMG = 6;
    public static final int CREATE_CHAT_IMG = 7;
    public static final int FEED_NEW_POST = 8;
    public static final int FRIENDS_SEARCH = 9;
    public static final int ITEM_EDIT = 10;
    public static final int STREAM_NEW_POST = 11;
    public static final int ITEM_REPOST = 12;

    public static final int ITEM_TYPE_IMAGE = 0;
    public static final int ITEM_TYPE_VIDEO = 1;
    public static final int ITEM_TYPE_POST = 2;
    public static final int ITEM_TYPE_COMMENT = 3;

    public static final String TAG = "TAG";

    public static final String HASHTAGS_COLOR = "#5BCFF2";
}