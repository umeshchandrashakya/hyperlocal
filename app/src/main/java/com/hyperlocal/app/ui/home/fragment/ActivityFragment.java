package com.hyperlocal.app.ui.home.fragment;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hyperlocal.app.MySharedPreferences;
import com.hyperlocal.app.R;
import com.hyperlocal.app.model.ActivityItemDataModel;
import com.hyperlocal.app.model.IncomingRequestModel;
import com.hyperlocal.app.ui.chats.ChatActivity;
import com.hyperlocal.app.ui.chats.UnreadCountModel;
import com.hyperlocal.app.ui.customview.MessageAlertDialog;
import com.hyperlocal.app.ui.home.adapter.MySideMenuFirstAdapter;
import com.hyperlocal.app.ui.notification.NotificationAcceptRequestModel;
import com.hyperlocal.app.ui.notification.NotificationFireBaseServcie;
import com.hyperlocal.app.ui.notification.NotificationPresenter;
import com.hyperlocal.app.ui.notification.NotificationView;
import com.hyperlocal.app.utlity.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @author ${Umesh} on 05-04-2018.
 */

public class ActivityFragment extends Fragment implements MySideMenuFirstAdapter.ItemClickListener, NotificationView {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_container)
    FrameLayout frameLayout;
    @BindView(R.id.progressBar)
    FrameLayout progressBarLayout;

    private Context context;
    private ArrayList<ActivityItemDataModel> arrayList = new ArrayList<>();

    private MySharedPreferences sharedPreferences;
    private MySideMenuFirstAdapter adapter;

    private ArrayList<ValueEventListener> arrayListListeners = new ArrayList<>();

    private MessageAlertDialog dialog;
    private ActivityItemDataModel itemDataModel;

    private ChildEventListener outStandingEventListener;
    private ChildEventListener conversionEventListener;
    private ValueEventListener unreadCountEventListener;
    private ChildEventListener incomingEventListener;

    private DatabaseReference outStandingRef;
    private DatabaseReference ConversationRef;
    private DatabaseReference unreadCountRef;
    private DatabaseReference incomingDbRef;
    private ArrayList<ValueEventListener>valueEventListeners = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment, container, false);
        ButterKnife.bind(this, view);
        adapter = new MySideMenuFirstAdapter(context, this);
        sharedPreferences = MySharedPreferences.getInstance(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        NotificationManager no = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        no.cancelAll();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (arrayList.size() > 0) {
            arrayList.clear();
        }
        progressBarLayout.setVisibility(View.VISIBLE);
        setupRecyclerView();
        getFromConversation();
        getIncomingRequest();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (outStandingRef != null) {
            outStandingRef.removeEventListener(outStandingEventListener);
        }
        if (ConversationRef != null) {
            ConversationRef.removeEventListener(conversionEventListener);
        }
        if (unreadCountRef != null) {
            for (int i=0;i<valueEventListeners.size();i++){
                unreadCountRef.removeEventListener(valueEventListeners.get(i));
            }
            unreadCountRef = null;
        }
        if (incomingDbRef != null) {
            incomingDbRef.removeEventListener(incomingEventListener);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setupRecyclerView() {
        outStandingRef = FirebaseDatabase
                .getInstance().getReference("Outstanding")
                .child(sharedPreferences.readString(Constants.USER_ID));

        outStandingEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                progressBarLayout.setVisibility(View.GONE);
                ActivityItemDataModel dataModel = dataSnapshot.getValue(ActivityItemDataModel.class);

                if (dataModel != null && dataModel.getStatus() != null) {
                    if (dataModel.getStatus().equals("0")) {
                        arrayList.add(dataModel);
                        adapter.addItemIn(arrayList);
                        adapter.notifyDataSetChanged();
                    } else {
                        for (int i = 0; i < arrayList.size(); i++) {
                            ActivityItemDataModel activityItemDataModel = arrayList.get(i);
                            arrayList.remove(activityItemDataModel);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                progressBarLayout.setVisibility(View.GONE);
                ActivityItemDataModel dataModel = dataSnapshot.getValue(ActivityItemDataModel.class);
                if (dataModel != null && dataModel.getStatus().equalsIgnoreCase("0")) {
                    arrayList.add(dataModel);
                    adapter.addItemIn(arrayList);
                    adapter.notifyDataSetChanged();

                } else {
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (arrayList.get(i).getRequestType().equalsIgnoreCase("1")) {
                            ActivityItemDataModel activityItemDataModel = arrayList.get(i);
                            arrayList.remove(activityItemDataModel);
                            adapter.notifyDataSetChanged();
                        }

                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
            }
        };

        outStandingRef.addChildEventListener(outStandingEventListener);

    }


    private void getFromConversation() {
        ConversationRef =
                FirebaseDatabase.getInstance()
                        .getReference("Conversation")
                        .child(sharedPreferences.readString(Constants.USER_ID));

        conversionEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                progressBarLayout.setVisibility(View.GONE);
                if(dataSnapshot!=null){
                    ActivityItemDataModel dataModel = dataSnapshot.getValue(ActivityItemDataModel.class);
                   if(dataModel!=null){
                       dataModel.setItemPushKey(dataSnapshot.getKey());
                       if(dataModel.getRequestType().equals("3") && dataModel.getStatus().equals("0")){
                           arrayList.add(dataModel);
                           adapter.addItemIn(arrayList);
                           adapter.notifyDataSetChanged();
                           getUnreadCount(dataModel);
                       }else if(dataModel.getStatus().equals("2")){
                           arrayList.add(dataModel);
                           adapter.addItemIn(arrayList);
                           adapter.notifyDataSetChanged();
                           getUnreadCount(dataModel);
                       }
                   }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ActivityItemDataModel dataModel = dataSnapshot.getValue(ActivityItemDataModel.class);
                dataModel.setItemPushKey(dataSnapshot.getKey());
                for(int i=0;i<arrayList.size();i++){
                    if(arrayList.get(i).getItemPushKey()!=null &&arrayList.get(i).getItemPushKey().equalsIgnoreCase(dataModel.getItemPushKey())){
                        if(dataModel.getStatus().equals("1")){
                            arrayList.remove(i);
                            adapter.notifyDataSetChanged();
                        }else{
                            arrayList.get(i).setStatus(dataModel.getStatus());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ConversationRef.addChildEventListener(conversionEventListener);
    }


    private void getUnreadCount(ActivityItemDataModel dataModel) {
     ValueEventListener   unreadCountEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBarLayout.setVisibility(View.GONE);
                if (dataSnapshot != null) {
                    UnreadCountModel model = dataSnapshot.getValue(UnreadCountModel.class);
                    if (model != null) {
                        dataModel.setIsBlocked(model.getIsBlocked());
                        dataModel.setHasBlocked(model.getHasBlocked());
                        dataModel.setUnreadCount(String.valueOf(model.getUnreadMessageCount()));
                    }
                    adapter.addItemIn(arrayList);
                    adapter.notifyDataSetChanged();
                    Log.d("Event Listner:",""+arrayList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        valueEventListeners.add(unreadCountEventListener);
        if (dataModel != null) {
            String arr[] = {dataModel.getFromId(), dataModel.getToId()};
            Arrays.sort(arr);
            unreadCountRef = FirebaseDatabase.getInstance().getReference()
                    .child("Chats")
                    .child(arr[0] + "-" + arr[1])
                    .child(dataModel.getItemKey())
                    .child(dataModel.getFromId() + "-Info");
            unreadCountRef.addValueEventListener(unreadCountEventListener);
            arrayListListeners.add(unreadCountEventListener);
        }
    }


    private void getIncomingRequest() {
        incomingDbRef = FirebaseDatabase.getInstance().getReference("Incoming");

        incomingEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                progressBarLayout.setVisibility(View.GONE);
                if (dataSnapshot != null) {
                    String key = dataSnapshot.getKey();
                    if (key != null && key.equals("status")) {
                    } else {
                        IncomingRequestModel incomingRequestModel = dataSnapshot.getValue(IncomingRequestModel.class);
                        boolean isUserAvailable = checkUserId(incomingRequestModel.getUsers());
                        if (isUserAvailable) {
                            addIncomingRequestToList(incomingRequestModel);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                IncomingRequestModel incomingRequestModel = dataSnapshot.getValue(IncomingRequestModel.class);
                updateListItemWhenStatusUpdated(incomingRequestModel);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        incomingDbRef.addChildEventListener(incomingEventListener);
    }


    private void updateListItemWhenStatusUpdated(IncomingRequestModel incomingRequestModel) {
        if (incomingRequestModel.getStatus().equalsIgnoreCase("1")) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).getTimestamp() == incomingRequestModel.getTimestamp()) {
                    arrayList.remove(i);
                    adapter.addItemIn(arrayList);
                    adapter.notifyDataSetChanged();
                }
            }
            adapter.addItemIn(arrayList);
            adapter.notifyDataSetChanged();
        } else {
            ActivityItemDataModel activityItemDataModel = new ActivityItemDataModel();
            activityItemDataModel.setItem(incomingRequestModel.getItem());
            activityItemDataModel.setTimestamp(incomingRequestModel.getTimestamp());
            activityItemDataModel.setRequestType(incomingRequestModel.getRequestType());
            activityItemDataModel.setTimestamp(incomingRequestModel.getTimestamp());
            activityItemDataModel.setTimeout(incomingRequestModel.getTimeout());
            activityItemDataModel.setType(incomingRequestModel.getType());
            activityItemDataModel.setFromId(incomingRequestModel.getFromId());
            activityItemDataModel.setRequest_id(incomingRequestModel.getRequest_id());
            activityItemDataModel.setDevice_token(incomingRequestModel.getDevice_token());
            activityItemDataModel.setDevice_type(incomingRequestModel.getDevice_type());
            String requestId1 = activityItemDataModel.getRequest_id();
            for (int i = 0; i < arrayList.size(); i++) {
                ActivityItemDataModel activityItemDataModel1 = arrayList.get(i);
                if (activityItemDataModel1 != null) {
                    String requestId = activityItemDataModel1.getRequest_id();
                    if (requestId != null && requestId.equalsIgnoreCase(requestId1)) {
                        arrayList.set(i, activityItemDataModel);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }


    private void addIncomingRequestToList(IncomingRequestModel incomingRequestModel) {
        if (incomingRequestModel != null && incomingRequestModel.getStatus().equalsIgnoreCase("1")) {
            adapter.notifyDataSetChanged();
        } else if (checkRequestTimeOut(incomingRequestModel.getTimeout(), incomingRequestModel.getTimestamp()) > 0) {
            ActivityItemDataModel activityItemDataModel = new ActivityItemDataModel();
            activityItemDataModel.setItem(incomingRequestModel.getItem());
            activityItemDataModel.setTimestamp(incomingRequestModel.getTimestamp());
            activityItemDataModel.setRequestType(incomingRequestModel.getRequestType());
            activityItemDataModel.setTimestamp(incomingRequestModel.getTimestamp());
            activityItemDataModel.setTimeout(incomingRequestModel.getTimeout());
            activityItemDataModel.setType(incomingRequestModel.getType());
            activityItemDataModel.setRequest_id(incomingRequestModel.getRequest_id());
            activityItemDataModel.setFromId(incomingRequestModel.getFromId());
            activityItemDataModel.setDevice_token(incomingRequestModel.getDevice_token());
            activityItemDataModel.setDevice_type(incomingRequestModel.getDevice_type());
            activityItemDataModel.setUsers(incomingRequestModel.getUsers());
            arrayList.add(activityItemDataModel);
            adapter.addItemIn(arrayList);
            adapter.notifyDataSetChanged();
        }
    }


    private boolean checkUserId(String users) {
        if (users != null) {
            String arr[] = users.split(",");
            List lsList = Arrays.asList(arr);
            String userId = sharedPreferences.readString(Constants.USER_ID);
            return lsList.contains(userId);
        } else {
            return false;
        }
    }


    @Override
    public void onItemClick(int position) {
        NotificationAcceptRequestModel model = new NotificationAcceptRequestModel();
        itemDataModel = arrayList.get(position);
        model.setMessage(itemDataModel.getItem());
        model.setRequestId(itemDataModel.getRequest_id());
        model.setRequestedUserId(itemDataModel.getFromId());
        model.setRequestType(itemDataModel.getType());
        model.setRequestDeviceToken(itemDataModel.getDevice_token());
        model.setDevcieType(itemDataModel.getDevice_type());
        model.setUsers(itemDataModel.getUsers());

        long timeOut = itemDataModel.getTimeout();
        long timeStamp = itemDataModel.getTimestamp();
        long sumTime = timeOut+timeStamp;

        long endTime = System.currentTimeMillis();

        long timeStamp1 = sumTime-endTime;

        if (itemDataModel.getRequestType().equalsIgnoreCase("1")) {
            if (timeStamp1 > 0) {
                dialog = new MessageAlertDialog(getActivity(), model);
                dialog.setTitle(itemDataModel.getItem());
                dialog.setYesButtonListener(listener);
                dialog.setNoButtonListener(listener);
                dialog.show();
            } else {
                Toast.makeText(context, "Request has been expire few minutes ago", Toast.LENGTH_SHORT).show();
            }
        } else if (itemDataModel.getRequestType().equalsIgnoreCase("0") && itemDataModel.getType().equalsIgnoreCase("ask")) {
            replaceFragmentWithAsk(itemDataModel, position);
        } else if (itemDataModel.getRequestType().equalsIgnoreCase("0") && itemDataModel.getType().equalsIgnoreCase("give")) {
            replaceWithGiveFragment(itemDataModel, position);
        } else if (itemDataModel.getRequestType().equalsIgnoreCase("3")) {
            replaceWithChatActivity(itemDataModel, position);
        }
    }


    @Override
    public void onLongPress(int position) {
        String type = arrayList.get(position).getRequestType();
        if(type!=null&&type.equals("3")){
            final Dialog dialog = new Dialog(getActivity());
            dialog.setCancelable(false);
            dialog.setTitle("Hyperlocal");
            dialog.setContentView(R.layout.custom_aleart_dialog);
            Button dialogButtonYes = dialog.findViewById(R.id.btn_yes);
            Button dialogButtonNo = dialog.findViewById(R.id.btn_cancel);
            dialogButtonNo.setVisibility(View.VISIBLE);
            TextView alertSubTextView = dialog.findViewById(R.id.sub_title);
            alertSubTextView.setText("do you really want to delete the chat?");
            dialogButtonYes.setOnClickListener((View v) -> {
                deleteItemFromList(position);
                dialog.dismiss();

            });
            dialog.show();
            dialogButtonNo.setOnClickListener(view -> dialog.dismiss());
        }

    }


    private void deleteItemFromList(int position) {
        if(arrayList.get(position).getStatus().equals("2")){
            updateOnlyMyNode(position);
        }else {
            updateBothNode(position);
        }
    }

    private void updateBothNode(int position) {
        progressBarLayout.setVisibility(View.VISIBLE);
        ActivityItemDataModel dataModel = arrayList.get(position);
        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Conversation")
                .child(sharedPreferences.readString(Constants.USER_ID))
                .child(dataModel.getItemPushKey());
        HashMap hashMap = new HashMap();
        hashMap.put("status","1");
        databaseReference.updateChildren(hashMap);


      ActivityItemDataModel dataModel2 = arrayList.get(position);
        DatabaseReference databaseReference2 = FirebaseDatabase
                .getInstance()
                .getReference("Conversation")
                .child(dataModel2.getToId())
                .child(dataModel2.getItemPushKey());

        HashMap hashMap2 = new HashMap();
        hashMap2.put("status","2");
        databaseReference2.updateChildren(hashMap2).addOnCompleteListener(task -> progressBarLayout.setVisibility(View.GONE));
        arrayList.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyDataSetChanged();
    }


    private void updateOnlyMyNode(int position) {
        progressBarLayout.setVisibility(View.VISIBLE);
        ActivityItemDataModel dataModel = arrayList.get(position);
        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Conversation")
                .child(sharedPreferences.readString(Constants.USER_ID))
                .child(dataModel.getItemPushKey());
        HashMap hashMap = new HashMap();
        hashMap.put("status","1");
        databaseReference.updateChildren(hashMap).addOnCompleteListener(task -> progressBarLayout.setVisibility(View.GONE));
        arrayList.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyDataSetChanged();
    }

    private void replaceWithChatActivity(ActivityItemDataModel itemDataModel, int position) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.TO_ID, itemDataModel.getToId());
        intent.putExtra(Constants.FROM_ID, itemDataModel.getFromId());
        intent.putExtra(Constants.IS_USER_BLOCK, itemDataModel.getIsBlocked());
        intent.putExtra(Constants.HAS_USER_BLOCK, itemDataModel.getHasBlocked());
        intent.putExtra(Constants.ITEM_KEY, itemDataModel.getItemKey());
        intent.putExtra(Constants.STATUS,itemDataModel.getStatus());
        startActivity(intent);
    }

    private void replaceWithGiveFragment(ActivityItemDataModel itemDataModel, int position) {
        if (isGiveRequestExpired(itemDataModel)) {
            String str = sharedPreferences.readString(Constants.Give_AWAY_REQUEST_KEY);
            updateRequest(str, "1");
            //Toast.makeText(getActivity(), "This request has been expired", Toast.LENGTH_SHORT).show();
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.TITLE, arrayList.get(position).getItem());
            Fragment childFragment = new GiveFragment();
            childFragment.setArguments(bundle);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left);
            transaction.addToBackStack(null);
            transaction.replace(R.id.fragment_container, childFragment).commit();
        }

    }

    private void replaceFragmentWithAsk(ActivityItemDataModel itemDataModel, int position) {
        if (isAskRequestExpired(itemDataModel)) {
            String str = sharedPreferences.readString(Constants.ASK_REQUEST_KEY);
            updateRequest(str, "0");
           // Toast.makeText(context, "This request has been expired", Toast.LENGTH_SHORT).show();
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.TITLE, arrayList.get(position).getItem());
            Fragment childFragment = new AskFragment();
            childFragment.setArguments(bundle);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            // transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left);
            // transaction.addToBackStack(null);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.replace(R.id.fragment_container, childFragment).commit();
        }

    }

    private void updateRequest(String requestKey, String type) {
        DatabaseReference firebaseDatabase = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Incoming")
                .child(requestKey);
        HashMap hashMap = new HashMap();
        hashMap.put(Constants.STATUS, "1");
        hashMap.put(Constants.USERS, "");
        firebaseDatabase.updateChildren(hashMap);

        DatabaseReference firebaseDatabase1 = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Outstanding").child(sharedPreferences.readString(Constants.USER_ID))
                .child(type);
        HashMap hashMap1 = new HashMap();
        hashMap1.put(Constants.STATUS, "1");
        firebaseDatabase1.updateChildren(hashMap1);

    }

    private boolean isAskRequestExpired(ActivityItemDataModel itemDataModel) {
        // MySharedPreferences preferences = MySharedPreferences.getInstance(getActivity());
        long mil = itemDataModel.getTimeout() + itemDataModel.getTimestamp();
        long endTime = System.currentTimeMillis();
        long timeRemains = mil - endTime;
        if (timeRemains >= 0) {
            return false;
        } else {
            return true;
        }
    }


    private boolean isGiveRequestExpired(ActivityItemDataModel itemDataModel) {
        // MySharedPreferences preferences = MySharedPreferences.getInstance(getActivity());
        long mil = itemDataModel.getTimeout() + itemDataModel.getTimestamp();
        long endTime = System.currentTimeMillis();
        long timeRemains = mil - endTime;
        if (timeRemains >= 0) {
            return false;
        } else {
            return true;
        }
    }

    public MessageAlertDialog.DialogButtonClickListener listener = new MessageAlertDialog.DialogButtonClickListener() {
        @Override
        public void acceptRequestButtonClick(NotificationAcceptRequestModel model) {
            dialog.showProgressBar();
            NotificationPresenter presenter = new NotificationPresenter(ActivityFragment.this, new NotificationFireBaseServcie(getActivity()));
            presenter.acceptRequestButtonClick(sharedPreferences.readString(Constants.USER_ID), model);

        }

        @Override
        public void cancelButtonClick() {
            dialog.dismiss();
        }
    };


    public long checkRequestTimeOut(long millisecond, long timeStamp) {
        long endTime = System.currentTimeMillis();
        long mil = millisecond + timeStamp;
        return mil - endTime;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }



 /*   private void startChatActivity(String to_id,String requestId) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.TO_ID, to_id);
        intent.putExtra(Constants.HAS_USER_BLOCK,0);
        intent.putExtra(Constants.IS_USER_BLOCK,0);
        intent.putExtra(Constants.ITEM_KEY,requestId);
        intent.putExtra(Constants.FROM_ID, sharedPreferences.readString(Constants.USER_ID));
        startActivity(intent);
    }*/

    @Override
    public void showProgressBar() {
        if (dialog != null) {
            dialog.showProgressBar();
        }
    }

    @Override
    public void hideProgressBar() {
        if (dialog != null) {
            dialog.hideProgressBar();
        }
    }

    @Override
    public void showSuccesMessage(String message) {
        Toast.makeText(getActivity(), "" + message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(getActivity(), "" + message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void startChatActivity() {
        hideProgressBar();
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.TO_ID, itemDataModel.getFromId());
        intent.putExtra(Constants.HAS_USER_BLOCK, 0);
        intent.putExtra(Constants.IS_USER_BLOCK, 0);
        intent.putExtra(Constants.ITEM_KEY, itemDataModel.getRequest_id());
        intent.putExtra(Constants.STATUS,itemDataModel.getStatus());
        intent.putExtra(Constants.FROM_ID, sharedPreferences.readString(Constants.USER_ID));
        startActivity(intent);
    }
}
