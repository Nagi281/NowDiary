package com.example.naginowdiary.customAdapter;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.naginowdiary.R;
import com.example.naginowdiary.model.JournalItem;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {
    private List<JournalItem> itemList;
    private Activity activity;
    private Calendar currentCal = Calendar.getInstance();
    public final static String[] month = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};

    public JournalAdapter(Activity activity, List<JournalItem> itemList) {
        this.activity = activity;
        this.itemList = itemList;
    }

    @Override
    public JournalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_journal, parent, false);
        return new JournalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(JournalViewHolder holder, int position) {
        JournalItem journalItem = itemList.get(position);
        holder.mTvDate.setText(getDateDifference(journalItem.getDate()));
        holder.mTvTime.setText(getTimeToShow(journalItem.getDate()));
        holder.mTvTitle.setText(journalItem.getTitle());
        holder.mTvContent.setText(journalItem.getContent());
        holder.mLoBackground.getBackground().clearColorFilter();
        int color = journalItem.getColor();
        holder.mLoBackground.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private String getTimeToShow(Date date) {
        String time = "";
        Calendar itemCal = Calendar.getInstance();
        itemCal.setTime(date);
        time += itemCal.get(Calendar.HOUR) + ":" + itemCal.get(Calendar.MINUTE);
        if (itemCal.get(Calendar.AM_PM) == Calendar.AM) {
            time += "\nA.M";
        } else {
            time += "\nP.M";
        }
        return time;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private String getDateDifference(Date date) {
        String datediff = "";
        Calendar itemCal = Calendar.getInstance();
        itemCal.setTime(date);
        Date currentDate = Calendar.getInstance().getTime();
        long timediff = currentDate.getTime() - date.getTime();
        float daydiff = (float) timediff / (1000 * 60 * 60 * 24);
        if (daydiff == 0.0) {
            datediff += "Today";
        } else if (daydiff > 0 && daydiff < 7) {
            datediff += (int) (daydiff + 1) + " days ago";
        } else {
            datediff += month[itemCal.get(Calendar.MONTH)] + " " + itemCal.get(Calendar.DATE);
            if (!(currentCal.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR))) {
                datediff += " " + itemCal.get(Calendar.YEAR);
            }
        }
        return datediff;
    }

    class JournalViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvDate;
        private TextView mTvTime;
        private TextView mTvTitle;
        private TextView mTvContent;
        private LinearLayout mLoBackground;

        public JournalViewHolder(View itemView) {
            super(itemView);
            mTvDate = itemView.findViewById(R.id.tv_itemDate);
            mTvTime = itemView.findViewById(R.id.tv_itemTime);
            mTvTitle = itemView.findViewById(R.id.tv_itemTitle);
            mTvContent = itemView.findViewById(R.id.tv_itemContent);
            mLoBackground = itemView.findViewById(R.id.lo_journal_background);
        }
    }
}
