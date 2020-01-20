package com.frozyice.callToQueue;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardsAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CURRENT = 0;
    private static final int TYPE_NEXT = 1;
    private static final int TYPE_NUMBER = 2;
    private List<Card> cardList;


    //https://medium.com/@droidbyme/android-recyclerview-with-multiple-view-type-multiple-view-holder-af798458763b
    public class  CurrentViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public LinearLayout rootView;

        public CurrentViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView);
            rootView = view.findViewById(R.id.rootView);
        }
    }

    public class  NextViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public NextViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView);
        }
    }

    public class  NumberViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public NumberViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView);
        }
    }



    public CardsAdapter (List<Card> cardList)
    {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;

        if (viewType == TYPE_CURRENT) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_current, parent, false);
            return new CurrentViewHolder(itemView);
        }
        else if (viewType == TYPE_NEXT) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_next,parent,false);
            return new NextViewHolder(itemView);
        }
        else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_number,parent,false);
            return new NumberViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == TYPE_CURRENT) {
            if (!cardList.get(position).getPhoneNumber().equals("")) {
                Card card = cardList.get(position);
                ((CurrentViewHolder) holder).textView.setText(card.getPhoneNumber());
            }
            else
                ((CurrentViewHolder) holder).rootView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }

        else if (getItemViewType(position) == TYPE_NEXT) {
            Card card = cardList.get(position);
            ((NextViewHolder) holder).textView.setText(card.getPhoneNumber());
        }

        else {
            Card card = cardList.get(position);
            ((NumberViewHolder) holder).textView.setText(card.getPhoneNumber());
        }

    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0)
            return TYPE_CURRENT;
        else if (position==1)
            return TYPE_NEXT;
        else
            return TYPE_NUMBER;
    }
}


