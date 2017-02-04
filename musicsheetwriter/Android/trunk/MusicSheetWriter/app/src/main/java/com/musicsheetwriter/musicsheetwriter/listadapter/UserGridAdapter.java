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
import com.musicsheetwriter.musicsheetwriter.model.Score;
import com.musicsheetwriter.musicsheetwriter.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Score} and makes a call to the
 * specified {@link OnScoreInteractionListener}.
 */
public class UserGridAdapter extends RecyclerView.Adapter<UserGridAdapter.ViewHolder> {

    private Context mContext;
    private final List<User> mValues;
    private final OnUserInteractionListener mListener;

    public UserGridAdapter(Context context, List<User> items,
                           OnUserInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grid_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mUsername.setText(mValues.get(position).getUsername());
        holder.mSubCount.setText(String.valueOf(mValues.get(position).getNbSubscribers()));
        holder.mScoreCount.setText(String.valueOf(mValues.get(position).getNbScores()));
        holder.mIsSubscribe.setChecked(mValues.get(position).isSubscription());
        Picasso.with(mContext)
                .load(mValues.get(position).getPhoto())
                .fit()
                .centerCrop()
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
        public final TextView mSubCount;
        public final TextView mScoreCount;
        public final ImageView mPicture;
        public final CheckBox mIsSubscribe;
        public User mItem;

        public ViewHolder(View view) {
            super(view);
            mUsername = (TextView) view.findViewById(R.id.username);
            mSubCount = (TextView) view.findViewById(R.id.subscribers_count);
            mScoreCount = (TextView) view.findViewById(R.id.scores_count);
            mPicture = (ImageView) view.findViewById(R.id.picture);
            mIsSubscribe = (CheckBox) view.findViewById(R.id.is_subscribe);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUsername.getText() + "'";
        }
    }
}
