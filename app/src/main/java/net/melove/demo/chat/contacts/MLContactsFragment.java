package net.melove.demo.chat.contacts;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.database.MLContactsDao;
import net.melove.demo.chat.communal.base.MLBaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 联系人界面 Fragment
 * 定义创建实例的工厂方法 {@link MLContactsFragment#newInstance}，可使用此方法创建实例
 */
public class MLContactsFragment extends MLBaseFragment {

    // 代替 ListView 用来显示联系人列表
    private RecyclerView mRecyclerView;
    // 联系人界面适配器
    private MLContactsAdapter mContactsAdapter;
    private View mHeadView;

    // 联系人信息数据库操作类
    private MLContactsDao mContactsDao;
    private List<MLContactsEntity> mContactsList = new ArrayList<MLContactsEntity>();

    // 应用内广播管理器，为了完全这里使用局域广播
    private LocalBroadcastManager mLocalBroadcastManager;
    // 会话界面监听会话变化的广播接收器
    private BroadcastReceiver mBroadcastReceiver;

    /**
     * 工厂方法，用来创建一个Fragment的实例
     *
     * @return MLSingleContactsFragment
     */
    public static MLContactsFragment newInstance() {
        MLContactsFragment fragment = new MLContactsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MLContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();

    }

    /**
     * 初始化联系人列表界面
     */
    private void initView() {
        mActivity = getParentFragment().getActivity();
        // 加载会话到list集合
        //        loadConversationList();
        //        // 实例化会话列表的 Adapter 对象
        //        mConversationAdapter = new MLConversationAdapter(mActivity, mConversations);
        //        // 初始化会话列表的 ListView 控件
        //        mRecyclerView = (RecyclerView) getView().findViewById(R.id.ml_recyclerview_conversation);
        /**
         * 为RecyclerView 设置布局管理器，这里使用线性布局
         * RececlerView 默认的布局管理器：
         * LinearLayoutManager          显示垂直滚动列表或水平的项目
         * GridLayoutManager            显示在一个网格项目
         * StaggeredGridLayoutManager   显示在交错网格项目
         * 自定义的布局管理器，需要继承 {@link android.support.v7.widget.RecyclerView.LayoutManager}
         *
         * add/remove items时的动画是默认启用的。
         * 自定义这些动画需要继承{@link android.support.v7.widget.RecyclerView.ItemAnimator}，
         * 并实现{@link RecyclerView#setItemAnimator(RecyclerView.ItemAnimator)}
         */
        //        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        //        mRecyclerView.setAdapter(mConversationAdapter);
        //
        //        // 通过自定义接口来实现RecyclerView item的点击和长按事件
        //        setItemClickListener();
    }

    /**
     * 初始化联系人界面ListView
     */
    private void initListView() {
        //        mListView = (ListView) getView().findViewById(R.id.ml_recyclerview_contacts);
        //        mContactsDao = new MLContactsDao(mActivity);
        //
        //        // 加载所有联系人
        //        loadUserList();
        //
        //        // 实例化联系人列表的适配器
        //        mAdapter = new MLContactsAdapter(mActivity, mContactsList);
        //
        //        mListView.setAdapter(mAdapter);

    }

    /**
     * 刷新邀请信息界面
     */
    private void refreshContacts() {
        mContactsList.clear();
        mContactsList.addAll(loadUserList());
        if (mContactsAdapter != null) {
            mContactsAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 加载联系人列表，TODO 后期需要实现排序
     *
     * @return 返回加载的联系人集合
     */
    private List<MLContactsEntity> loadUserList() {
        mContactsDao = new MLContactsDao(mActivity);
        // TODO 这里暂时只进行了简单的获取用户列表，后期需要实现排序
        List<MLContactsEntity> list = mContactsDao.getContactList();
        return list;
    }

    /**
     *
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            }
        }
    };

    /**
     * 注册广播接收器，用来监听全局监听监听到新消息之后发送的广播，然后刷新界面
     */
    private void registerBroadcastReceiver() {
        // 获取局域广播管理器
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(mActivity);
        // 实例化Intent 过滤器
        IntentFilter intentFilter = new IntentFilter();
        // 为过滤器添加一个 Action
        intentFilter.addAction(MLConstants.ML_ACTION_CONTACT);
        // 实例化广播接收器，用来接收自己过滤的广播
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshContacts();
            }
        };
        // 注册广播接收器
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    /**
     * 取消广播接收器的注册
     */
    private void unregisterBroadcastReceiver() {
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * 重写父类的onResume方法， 在这里注册广播
     */
    @Override
    public void onResume() {
        super.onResume();
        // 刷新联系人界面
        refreshContacts();
        // 注册广播监听
        registerBroadcastReceiver();
    }

    /**
     * 重写父类的onStop方法，在这里边记得将注册的广播取消
     */
    @Override
    public void onStop() {
        super.onStop();
        unregisterBroadcastReceiver();
    }
}
