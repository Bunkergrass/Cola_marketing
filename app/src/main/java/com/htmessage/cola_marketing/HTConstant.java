package com.htmessage.cola_marketing;

import android.os.Environment;

/**
 * this class saves static keys
 * */
public class HTConstant {
    public static final String  ImgUrl = "http://kls.kakusi.cn/";
    private static final String BASE_IP = "http://kls.kakusi.cn/";
    //服务器端
    public static final String HOST =BASE_IP + "api/";
    public static final String URL_AVATAR = HOST + "upload/";
    public static final String URL_REGISTER = HOST + "register";//注册
    public static final String URL_LOGIN = HOST + "login";//登录
    public static final String URL_THIRDLOGIN = HOST + "thirdLogin";//第三方登录
    public static final String URL_FriendList = HOST + "fetchFriends";//获取好友列表
    public static final String URL_Search_User = HOST + "searchUser";//查询好友
    public static final String URL_Get_UserInfo = HOST + "getUserInfo";//获取详情
    public static final String URL_UPDATE = HOST + "update";//更新
    public static final String URL_RESETPASSWORD = HOST + "resetPassword";//更新密码
    public static final String URL_ADD_FRIEND = HOST + "addFriend"; //添加好友
    public static final String URL_DELETE_FRIEND = HOST + "removeFriend";//删除好友
    public static final String URL_ADD_BLACKLIST = HOST + "addBlackList";//添加黑名单
    //    朋友圈接口
    //    服务器端
    public static final String URL_PUBLISH = HOST + "publish";//发布动态
    public static final String URL_SOCIAL = HOST + "fetchTimeline";//获取动态列表
    public static final String URL_SOCIAL_DELETE = HOST + "removeTimeline";//删除动态
    public static final String URL_SOCIAL_FRIEND = HOST + "fetchOtherTimeline";//获取好友朋友圈列表
    public static final String URL_SOCIAL_COMMENT = HOST + "commentTimeline";//朋友圈动态评论
    public static final String URL_SOCIAL_DELETE_COMMENT = HOST + "deleteCommentTimeline";//删除朋友圈动态评论
    public static final String URL_SOCIAL_REPLY_COMMENT = HOST + "replyCommentTimeline";//回复朋友圈动态评论
    public static final String URL_SOCIAL_DELETE_REPLY_COMMENT = HOST + "deleteReplyCommentTimeline";//删除朋友圈动态评论回复
    public static final String URL_SOCIAL_GOOD = HOST + "praiseTimeline";//点赞
    public static final String URL_SOCIAL_GOOD_CANCEL = HOST + "deletePraiseTimeline";//取消点赞
    public static final String URL_SOCIAL_GET_PRAISELIST = HOST + "fetchTimelineParises";//获取赞列表
    public static final String URL_SOCIAL_GET_COMMENTLIST = HOST + "fetchTimelineComments";//获取评论列表
    public static final String URL_SOCIAL_GET_DETAIL = HOST + "dynamicInfo";//获取评论列表
    //首页
    public static final String URL_HOMEPAGE_DYNAMIC=HOST+"getShowRjhotList";//
    public static final String URL_HOMEPAGE_NEWSLIST=HOST+"getShowNewsList";//新闻图片
    public static final String URL_HOMEPAGE_ADV=HOST+"getShowAdsList";//广告

    //发送验证码  postCode.php
    public static final String SEND_SMS_CODE = HOST + "postCode.php";//获取验证码
    //发送验证码  postCode.php
    public static final String SEND_YUNPIAN_CODE = HOST + "yunpianwang.php";//获取验证码
    //群相关接口
    public static final String GROUP_HOST = BASE_IP + "group/";  //116.62.180.69
    public static final String URL_GROUP_CREATE = GROUP_HOST + "groupCreate.php";
    public static final String URL_GROUP_MEMBERS = GROUP_HOST + "mucMembers.php";
    public static final String URL_CHECK_UPDATE = HOST + "version.php";    //查询更新
    public static final String URL_UPLOAD_MOMENT_BACKGROUND = HOST + "uploadpic";//上传朋友圈背景图片
    public static final String URL_GET_RECENTLY_PEOPLE = HOST + "getRecentlyUser";//获取最近上线的人
    public static final String URL_SEND_LOCAL_LOGIN_TIME = HOST + "updateLocalTimestamp";//获取最近上线的人
    public static final String URL_SEND_CONTANCTS = HOST + "filteruser";//上传联系人到服务器
    //威客接口
    public static final String URL_WEIKE_SEND_POST = HOST + "send_post"; //发帖
    public static final String URL_WEIKE_POST_LIST = HOST + "post_list"; //帖子列表
    public static final String URL_WEIKE_SEND_COMMENT = HOST + "send_comment"; //发布帖子评论
    public static final String URL_WEIKE_POST_DETAIL = HOST + "post_detail"; //帖子详情
    public static final String URL_WEIKE_COMMENT_LIST = HOST + "comment_list"; //帖子评论列表
    public static final String URL_WEIKE_COMMENT_REPLY_LIST = HOST + "comment_reply_list"; //楼中楼列表
    public static final String URL_WEIKE_POST_FABULOUS = HOST + "post_goods"; //评论点赞
    public static final String URL_WEIKE_SEND_REPLY = HOST + "send_comment_reply"; //评论回复
    //商品相关接口
    public static final String URL_TRADE_ONLY_LIST = BASE_IP + "only/only_list";
    public static final String URL_TRADE_ONLY_DETAIL = BASE_IP + "only/only_detail";
    public static final String URL_PROJECT_ADD_EXPLAIN = BASE_IP + "only/add_explain";
    public static final String URL_PROJECT_UP_EXPLAIN = BASE_IP + "only/up_explain";
    public static final String URL_PROJECT_EXPLAIN_DETAIL = BASE_IP + "only/explain_detail";
    public static final String URL_GOODS_DETAIL = BASE_IP + "only/my_goods_detail";
    public static final String URL_GOODS_DETAIL_BAK = BASE_IP + "only/goods_detail_bak";
    public static final String URL_GOODS_IMAGES = BASE_IP + "only/goods_imgs";
    public static final String URL_GOODS_ADD = BASE_IP + "only/add_goods";
    public static final String URL_GOODS_UP = BASE_IP + "only/up_goods";
    public static final String URL_GO_APPLY = BASE_IP + "only/go_apply";
    public static final String URL_MY_APPLY_LIST = BASE_IP + "only/my_apply_list";
    public static final String URL_MY_SALE_LIST = BASE_IP + "only/my_sale_list";
    public static final String URL_MY_PROJECT_LIST = BASE_IP + "only/my_explain_list";
    public static final String URL_ENCRYPT_URL = BASE_IP + "only/encrypt_url";
    //考核接口
    public static final String URL_CHECK_LIST = BASE_IP + "check/check_list";//我的考题
    public static final String URL_CHECK_RESULT = BASE_IP + "check/check_result";
    //任务相关接口
    public static final String URL_TASK_LIST = BASE_IP + "task/task_list";
    public static final String URL_GET_TASK = BASE_IP + "task/get_task";
    public static final String URL_MAKE_TASK = BASE_IP + "task/make_task";
    public static final String URL_SHARE_TASK = BASE_IP + "task/share_task";
    public static final String URL_ADD_PRICE_ADDRESS = BASE_IP + "task/add_prize_address";


    //文件/及图片上传接口
    public static final String baseImgUrl = "http://ossims.oss-cn-beijing.aliyuncs.com/";// "http://mleke.oss-cn-shanghai.aliyuncs.com/";
    //?x-oss-process=image/resize,m_fill,h_100,w_100
    // 缩略图处理---等高宽
    public static final String baseImgUrl_set = "?x-oss-process=image/resize,m_fill,h_300,w_300";
    //缩略图处理,固定搞
    public static final String reSize = "?x-oss-process=image/resize,h_300";

    //商品地址 和商城
    public static final String baseGoodsUrl = "http://klshop.kakusi.cn/";



    //音视频相关 anyrtc
    public static final String DEVELOPERID = "18567631";
    public static final String APPID = "anyrtc6RfkVqAyFYlK";
    public static final String APPKEY = "errfC2Fg5v9hy8g02adJwGL1Lk+NzBE2/rhcAAt4WcU";
    public static final String APPTOKEN = "edd887ea0fa7f49a935df4f7fb441488";




    //Buglykey
    public static final String BUGLY_KEY = "8688132d79";
    public static final String DIR_AVATAR = Environment.getExternalStorageDirectory().toString() + "/Cola_marketing/";
    //user json key
    public static final String JSON_KEY_NICK = "nick";
    public static final String JSON_KEY_HXID = "userId";
    public static final String JSON_KEY_FXID = "fxid";
    public static final String JSON_KEY_SEX = "sex";
    public static final String JSON_KEY_AVATAR = "avatar";
    public static final String JSON_KEY_CITY = "city";
    public static final String JSON_KEY_PASSWORD = "hx_password";
    public static final String JSON_KEY_PROVINCE = "province";
    public static final String JSON_KEY_TEL = "tel";
    public static final String JSON_KEY_SIGN = "sign";
    public static final String JSON_KEY_ROLE = "role";
    public static final String JSON_KEY_BIGREGIONS = "bigRegions";
    public static final String JSON_KEY_SESSION = "session";
    //添加好友的原因
    public static final String CMD_ADD_REASON = "ADD_REASON";
    //进入用户详情页传递json字符串
    public static final String KEY_USER_INFO = "userInfo";
    //修改用户资料的广播
    public static final String KEY_CHANGE_TYPE = "type";

    //web扫描授权相关
    public static final String JSON_KEY_LOGINID = "loginId";
    public static final String JSON_KEY_STATUS = "status";
    public static final String JSON_KEY_APPNAME = "loginName";
    public static final String JSON_KEY_APPICON = "loginIcon";
    //APP授权相关
    public static final String JSON_KEY_THIRDAPPNAME = "appname";
    public static final String JSON_KEY_PACKAGENAME = "packagename";
    public static final String JSON_KEY_THIRDAPPICON = "appicon";
    public static final String JSON_KEY_ISWEB = "isWeb";
}
