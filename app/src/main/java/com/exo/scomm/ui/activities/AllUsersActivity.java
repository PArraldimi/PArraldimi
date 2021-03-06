//package com.exo.scomm.ui.activities;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.exo.scomm.R;
//import com.exo.scomm.adapters.DataHolder;
//import com.exo.scomm.data.models.User;
//import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
//import com.google.android.material.appbar.MaterialToolbar;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ServerValue;
//import com.google.firebase.database.ValueEventListener;
//import com.squareup.picasso.Picasso;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Set;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class AllUsersActivity extends AppCompatActivity {
//
//   private static final String TAG = AllUsersActivity.class.getSimpleName();
//   private RecyclerView FindFriendsRecyclerList;
//   private DatabaseReference UsersRef;
//   private DatabaseReference mRootRef;
//   private DatabaseReference mInvitesReqDBRef;
//   private FirebaseUser mCurrentUser;
//   private FloatingActionButton selectedUsersFab;
//   private Set<String> selectedUsers = new HashSet<>();
//   private Set<User> selectedSet = new HashSet<>();
//   private TextView selectedScommers;
//   private String task_id;
//   boolean invite, newTask = false;
//
//   @Override
//   protected void onCreate(@Nullable Bundle savedInstanceState) {
//      super.onCreate(savedInstanceState);
//      setContentView(R.layout.activity_users);
//      MaterialToolbar toolbar = findViewById(R.id.all_users_app_bar);
//      toolbar.setTitle("All Users");
//      setSupportActionBar(toolbar);
//      toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View v) {
//            onBackPressed();  //
//         }
//      });
//
//      mRootRef = FirebaseDatabase.getInstance().getReference();
//      UsersRef = mRootRef.child("Users");
//      mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
//      mInvitesReqDBRef = mRootRef.child("TaskInviteRequests").child(mCurrentUser.getUid());
//      DatabaseReference mCompanionsDatabase = mRootRef.child("TaskCompanions");
//      task_id = getIntent().getStringExtra("task_id");
//      if (getIntent().hasExtra("fromTaskDetails")) {
//         invite = true;
//      }
//      if (getIntent().hasExtra("newTask")) {
//         newTask = true;
//      }
//
//      selectedScommers = findViewById(R.id.selected_scommers);
//      selectedUsersFab = findViewById(R.id.selectedUsersFab);
//      selectedUsersFab.setVisibility(View.INVISIBLE);
//      FindFriendsRecyclerList = findViewById(R.id.find_friends_recycler_list);
//      FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));
//   }
//
////   private void goBack(Set<User> selectedSet) {
////      Log.e(AllUsersActivity.class.getSimpleName(), "" + selectedSet.toString());
////      DataHolder.setSelectedUsers(selectedSet);
////      AddTaskActivity taskActivity = new AddTaskActivity();
////      taskActivity.mSelectedUsers = selectedSet;
////      this.finish();
////   }
//
//   @Override
//   protected void onStart() {
//      super.onStart();
//
//      FirebaseRecyclerOptions<User> options =
//              new FirebaseRecyclerOptions.Builder<User>()
//                      .setQuery(UsersRef, User.class)
//                      .build();
//
//      FirebaseRecyclerAdapter<User, FindFriendViewHolder> adapter =
//              new FirebaseRecyclerAdapter<User, FindFriendViewHolder>(options) {
//                 @Override
//                 protected void onBindViewHolder(@NonNull final FindFriendViewHolder holder, int position, @NonNull final User model) {
//                    final String uid = getRef(position).getKey();
//                    model.setUID(uid);
//                    holder.userName.setText(model.getUsername());
//                    holder.userStatus.setText(model.getStatus());
//                    Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);
//
//                    if (!newTask) {
//                       assert uid != null;
//                       mRootRef.child("TaskCompanions").child(mCurrentUser.getUid()).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
//                          @Override
//                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                             if (dataSnapshot.hasChild("task_id") && Objects.requireNonNull(dataSnapshot.child("task_id").getValue()).toString().equals(task_id)) {
//                                holder.selectCheck.setChecked(true);
//                             }
//                          }
//
//                          @Override
//                          public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                          }
//                       });
//
//                       mInvitesReqDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                          @Override
//                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                             if (dataSnapshot.hasChild(uid)) {
//                                mInvitesReqDBRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
//                                   @Override
//                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                      if (dataSnapshot.hasChild(task_id)) {
//                                         String req_type = Objects.requireNonNull(dataSnapshot.child(task_id).child("request_type").getValue()).toString();
//                                         if (req_type.equals("sent")) {
//                                            holder.selectCheck.setChecked(true);
//                                            selectedUsersFab.setVisibility(View.VISIBLE);
//                                         }
//                                      }
//                                   }
//
//                                   @Override
//                                   public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                   }
//                                });
//                             }
//                          }
//
//                          @Override
//                          public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                          }
//                       });
//                    }
//                    for (int i = 0; i < selectedSet.size(); i++) {
//                       if (selectedUsers.contains(model.getUsername())) {
//                          holder.selectCheck.setChecked(true);
//                          selectedUsersFab.setVisibility(View.VISIBLE);
//                       }
//                    }
//                    selectedUsersFab.setOnClickListener(v -> {
//                       if (invite) {
//                          sendInviteRequests(task_id, selectedSet);
//                       } else {
//                          //goBack(selectedSet);
//                       }
//
//                    });
//                    if (holder.selectCheck.isChecked()) {
//                       selectedUsersFab.setVisibility(View.VISIBLE);
//                       selectedSet.add(model);
//                       selectedUsers.add(model.getUsername());
//                       String str = selectedUsers.toString().replaceAll("\\[|\\]", "");
//                       selectedScommers.setText(str);
//                    } else {
//                       if (selectedUsers.isEmpty()){
//                          selectedUsersFab.setVisibility(View.INVISIBLE);
//                       }
//                       selectedSet.remove(model);
//                       selectedUsers.remove(model.getUsername());
//                       String str = selectedUsers.toString().replaceAll("\\[|\\]", "");
//                       selectedScommers.setText(str);
//                    }
//                    holder.mView.setOnClickListener(v -> selectUser(holder, model));
//                    holder.profileImage.setOnClickListener(v -> {
//                       Intent profileIntent = new Intent(AllUsersActivity.this, Profile.class);
//                       profileIntent.putExtra("uid", uid);
//                       profileIntent.putExtra("task_id", task_id);
//                       profileIntent.putExtra("username", model.getUsername());
//                       startActivity(profileIntent);
//                    });
//                 }
//
//                 @NonNull
//                 @Override
//                 public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display, parent, false);
//                    return new FindFriendViewHolder(view);
//                 }
//              };
//      FindFriendsRecyclerList.setAdapter(adapter);
//      adapter.startListening();
//   }
//
//
//   private void sendInviteRequests(String task_id, final Set<User> selectedSet) {
//      final ProgressDialog mProgressDialog = new ProgressDialog(this);
//      mProgressDialog.setTitle("Sending Task Invite Requests");
//      mProgressDialog.setCanceledOnTouchOutside(false);
//      mProgressDialog.setMessage("Please wait!!");
//      mProgressDialog.show();
//      Map requestSentData = new HashMap();
//      requestSentData.put("request_type", "sent");
//      requestSentData.put("date", ServerValue.TIMESTAMP);
//      requestSentData.put("task_id", task_id);
//      requestSentData.put("accepted", "false");
//
//      Map requestReceivedData = new HashMap();
//      requestReceivedData.put("request_type", "received");
//      requestReceivedData.put("date", ServerValue.TIMESTAMP);
//      requestReceivedData.put("task_id", task_id);
//      requestReceivedData.put("accepted", "false");
//
//      for (User user : selectedSet
//      ) {
//         String userId = user.getUID();
//         String recipientNoteKey = mRootRef.child("Notifications").child(userId).push().getKey();
//         String senderNoteKey = mRootRef.child("Notifications").child(mCurrentUser.getUid()).push().getKey();
//
//         Map recipientNote = new HashMap<>();
//         recipientNote.put("fromUser", mCurrentUser.getUid());
//         recipientNote.put("type", "received");
//         recipientNote.put("task_id", task_id);
//         recipientNote.put("date", ServerValue.TIMESTAMP);
//
//         Map senderNote = new HashMap<>();
//         senderNote.put("toUser", userId);
//         senderNote.put("type", "sent");
//         senderNote.put("task_id", task_id);
//         senderNote.put("date", ServerValue.TIMESTAMP);
//
//         Map requestMap = new HashMap<>();
//         requestMap.put("Notifications/" + userId + "/" + recipientNoteKey, recipientNote);
//         requestMap.put("Notifications/" + mCurrentUser.getUid() + "/" + senderNoteKey, senderNote);
//         requestMap.put("TaskInviteRequests/" + mCurrentUser.getUid() + "/" + userId + "/" + task_id, requestSentData);
//         requestMap.put("TaskInviteRequests/" + userId + "/" + mCurrentUser.getUid() + "/" + task_id, requestReceivedData);
//
//         mRootRef.updateChildren(requestMap, (databaseError, databaseReference) -> {
//            if (databaseError != null) {
//               Toast.makeText(AllUsersActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();
//            } else {
//               selectedUsers.clear();
//               selectedSet.clear();
//               mProgressDialog.dismiss();
//               Toast.makeText(AllUsersActivity.this, "Invitation Requests sent successfully", Toast.LENGTH_SHORT).show();
//            }
//         });
//      }
//   }
//
//   private void selectUser(@NonNull FindFriendViewHolder holder, @NonNull User model) {
//      if (holder.selectCheck.isChecked()) {
//         selectedUsersFab.setVisibility(View.VISIBLE);
//         holder.selectCheck.setChecked(false);
//         selectedSet.remove(model);
//         selectedUsers.remove(model.getUsername());
//         if (selectedUsers.isEmpty()){
//            selectedUsersFab.setVisibility(View.INVISIBLE);
//         }
//         String str = selectedUsers.toString().replaceAll("\\[|\\]", "");
//         selectedScommers.setText(str);
//
//      } else {
//         selectedUsersFab.setVisibility(View.VISIBLE);
//         holder.selectCheck.setChecked(true);
//         selectedSet.add(model);
//         selectedUsers.add(model.getUsername());
//         String str = selectedUsers.toString().replaceAll("\\[|\\]", "");
//         selectedScommers.setText(str);
//      }
//   }
//
//   static class FindFriendViewHolder extends RecyclerView.ViewHolder {
//      View mView;
//      TextView userName, userStatus;
//      CircleImageView profileImage;
//      CheckBox selectCheck;
//
//      FindFriendViewHolder(@NonNull View itemView) {
//         super(itemView);
//         mView = itemView;a
//         userName = mView.findViewById(R.id.single_user_tv_name);
//         userStatus = mView.findViewById(R.id.single_user_status);
//         profileImage = mView.findViewById(R.id.single_user_circle_image);
//         selectCheck = mView.findViewById(R.id.select_check);
//      }
//   }
//}