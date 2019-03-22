package com.madboss.smsmailer.adapters.login;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.madboss.smsmailer.R;

import java.util.ArrayList;
import java.util.List;

public class CountryListAdapter extends BaseAdapter implements Filterable {

    Context context;
    int layoutResourceId;
    List<Country> countryList = null;
    List<Country> filteredCountryList;
    CountryFilter filter = null;

    static class CountryViewHolder {
        ImageView cFlag;
        TextView cName;
        TextView cCode;
    }

    public CountryListAdapter(Context context, int resource, List<Country> countryList) {
        this.context = context;
        this.layoutResourceId = resource;
        this.countryList = countryList;
        this.filteredCountryList = countryList;
    }

    @Override
    public int getCount() {
        return countryList.size();
    }

    @Override
    public Country getItem(int position) {
        return countryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View customCountryView, ViewGroup parent) {

        CountryViewHolder holder = null;
        if (customCountryView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            customCountryView = inflater.inflate(layoutResourceId, parent, false);
            holder = new CountryViewHolder();
            holder.cFlag = (ImageView) customCountryView.findViewById(R.id.countryFlag);
            holder.cName = (TextView) customCountryView.findViewById(R.id.countryName);
            holder.cCode = (TextView) customCountryView.findViewById(R.id.countryCode);
            customCountryView.setTag(holder);
        } else {
            holder = (CountryViewHolder) customCountryView.getTag();
        }

        Country dataItem = countryList.get(position);
        holder.cName.setText(dataItem.name);
        holder.cFlag.setImageResource(dataItem.flagImageId);
        holder.cCode.setText(dataItem.code);

        return customCountryView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CountryFilter();
        }
        return filter;
    }

    private class CountryFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List<Country> filterList = new ArrayList<Country>();
                for (int i = 0; i < filteredCountryList.size(); i++) {
                    if ((filteredCountryList.get(i).name.toLowerCase()).contains(constraint.toString().toLowerCase())) {
                        filterList.add(filteredCountryList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = filteredCountryList.size();
                results.values = filteredCountryList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            countryList = (List<Country>) results.values;
            notifyDataSetChanged();
        }


    }

}
