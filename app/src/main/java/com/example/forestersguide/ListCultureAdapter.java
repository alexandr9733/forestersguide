package com.example.forestersguide;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListCultureAdapter extends BaseAdapter {
        private Context mContext;
        private List<ListCulture> mList;

        public ListCultureAdapter(Context mContext, List<ListCulture> mProductList){
            this.mContext = mContext;
            this.mList = mProductList;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public ListCulture getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mList.get(position).getId();
        }

       // @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View v = View.inflate(mContext, R.layout.list_item, null);
            TextView tvName = (TextView)v.findViewById(R.id.tvName);
            TextView tvDescription = (TextView)v.findViewById(R.id.tvDescription);
            ImageView tvImage = (ImageView)v.findViewById(R.id.ivIcon);
            tvName.setText(mList.get(position).getName());
            tvDescription.setText(String.valueOf(mList.get(position).getDescription()));
            tvImage.setImageBitmap(mList.get(position).getImage());
            return v;
        }
    }
