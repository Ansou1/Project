package com.musicsheetwriter.musicsheetwriter.listadapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.musicsheetwriter.musicsheetwriter.R;
import com.musicsheetwriter.musicsheetwriter.model.Score;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Score} and makes a call to the
 * specified {@link OnScoreInteractionListener}.
 */
public class ScoreListAdapter extends RecyclerView.Adapter<ScoreListAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Score> mValues;
    private final OnScoreInteractionListener mListener;

    boolean mRemovable;
    boolean mAuthorShown;

    public ScoreListAdapter(Context context, List<Score> items, OnScoreInteractionListener listener) {
        mContext = context;
        mValues = items;
        mListener = listener;
        mRemovable = false;
        mAuthorShown = true;
    }

    public void setRemovable(boolean removable) {
        this.mRemovable = removable;
    }

    public void setAuthorShown(boolean authorShown) {
        this.mAuthorShown = authorShown;
    }

    public void removeItem(Score item) {
        if (mRemovable) {
            int position = mValues.indexOf(item);
            mValues.remove(position);
            notifyItemRemoved(position);
        } else {
            throw new RuntimeException("The items cannot be removed from the list");
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (mAuthorShown) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_score_with_author, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_score, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitle.setText(mValues.get(position).getTitle());

        if (mAuthorShown)
            holder.mAuthorName.setText(mValues.get(position).getAuthor().getUsername());

        holder.mIsFavourite.setChecked(mValues.get(position).isFavourite());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onViewScorePreview(holder.mItem, holder.itemView);
                }
            }
        });

        holder.mIsFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                if (null != mListener) {
                    if (cb.isChecked()) {
                        mListener.onPutAsFavourite(holder.mItem, holder.itemView);
                    } else {
                        mListener.onRemoveFromFavourite(holder.mItem, holder.itemView);
                    }
                }
            }
        });

        holder.mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(holder);
            }
        });

        if (mRemovable) {
            holder.mMenuButton.setImageResource(R.drawable.ic_menu_moreoverflow_black);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mTitle;
        public final TextView mAuthorName;
        public final CheckBox mIsFavourite;
        public final ImageButton mMenuButton;
        public Score mItem;

        public ViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.title);
            mAuthorName = (TextView) view.findViewById(R.id.author);
            mIsFavourite = (CheckBox) view.findViewById(R.id.is_favourite);
            mMenuButton = (ImageButton) view.findViewById(R.id.score_menu);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitle.getText() + "'";
        }
    }


    public void showPopup(final ViewHolder holder) {
        PopupMenu popupMenu = new PopupMenu(mContext, holder.mMenuButton);
        if (mRemovable) {
            popupMenu.inflate(R.menu.menu_score_item_removable);
        } else {
            popupMenu.inflate(R.menu.menu_score_item);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.action_score_download_png) {
                    mListener.onDownloadScorePreview(holder.mItem, holder.itemView);
                    return true;
                }
                if (id == R.id.action_score_download_msw) {
                    mListener.onDownloadScoreProject(holder.mItem, holder.itemView);
                    return true;
                }
                if (id == R.id.action_score_remove) {
                    mListener.onDeleteScore(holder.mItem, holder.itemView);
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }
}
