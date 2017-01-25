package com.musicsheetwriter.musicsheetwriter.listadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicsheetwriter.musicsheetwriter.R;
import com.musicsheetwriter.musicsheetwriter.model.User;
import com.musicsheetwriter.musicsheetwriter.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private Context mContext;
    private final List<User> mValues;
    private final OnUserInteractionListener mListener;

    public UserListAdapter(Context context, List<User> items,
                           OnUserInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mUsername.setText(mValues.get(position).getUsername());
        holder.mIsSubscribe.setChecked(mValues.get(position).isSubscription());

        Picasso.with(mContext)
                .load(mValues.get(position).getPhoto())
                .fit()
                .centerCrop()
                .transform(new CircleTransform())
                .error(R.drawable.default_avatar)
                .into(holder.mPicture);

        holder.mIsSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                if (null != mListener) {
                    if (cb.isChecked()) {
                        mListener.onSubscribe(holder.mItem, holder.itemView);
                    } else {
                        mListener.onUnsubscribe(holder.mItem, holder.itemView);
                    }
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onViewUserProfile(holder.mItem, holder.itemView);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mUsername;
        public final ImageView mPicture;
        public final CheckBox mIsSubscribe;
        public User mItem;

        public ViewHolder(View view) {
            super(view);
            mUsername = (TextView) view.findViewById(R.id.username);
            mPicture = (ImageView) view.findViewById(R.id.picture);
            mIsSubscribe = (CheckBox) view.findViewById(R.id.is_subscribe);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUsername.getText() + "'";
        }
    }
}
