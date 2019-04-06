package com.example.meterreadercwd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter_1 extends BaseAdapter implements Filterable {
    Context context;
    ArrayList<Zone_Helper> list;
    LayoutInflater inflater;
    CustomFilter filter;
    ArrayList<Zone_Helper> filterList;

    public CustomAdapter_1(Context context, ArrayList<Zone_Helper>list) {
        super();
        this.context = context;
        this.list = list;
        this.filterList = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ZoneHandler handler = null;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_1, null);
            handler = new ZoneHandler();
            handler.zone_number = (TextView) convertView.findViewById(R.id.list_item_v1_text1);
            handler.zone_name = (TextView) convertView.findViewById(R.id.list_item_v1_text2);
            convertView.setTag(handler);
        } else {
            handler = (ZoneHandler) convertView.getTag();
        }

        handler.zone_number.setText(list.get(position).getZone_number());
        handler.zone_name.setText(list.get(position).getZone_name());

        return convertView;
    }

    public String getZoneNumber(int position) {
        return list.get(position).getZone_number();
    }

    public String getZoneName(int position) {
        return list.get(position).getZone_name();
    }

    public String getZoneid(int position) { return list.get(position).getZone_id(); }

    static class ZoneHandler {
        TextView zone_number, zone_name;
    }

    @Override
    public Filter getFilter() {
        // TODO Auto-generated method stub
        if(filter == null)
        {
            filter=new CustomFilter();
        }

        return filter;
    }

    //INNER CLASS
    class CustomFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // TODO Auto-generated method stub

            FilterResults results=new FilterResults();

            if(constraint != null && constraint.length()>0)
            {
                //CONSTARINT TO UPPER
                constraint=constraint.toString().toUpperCase();

                ArrayList<Zone_Helper> filters=new ArrayList<Zone_Helper>();

                //get specific items
                for(int i=0;i<filterList.size();i++)
                {
                    if(filterList.get(i).getZone_number().toUpperCase().contains(constraint))
                    {
                        Zone_Helper p=new Zone_Helper(filterList.get(i).getZone_number(), filterList.get(i).getZone_name(), filterList.get(i).getZone_id());

                        filters.add(p);
                    }
                }

                results.count=filters.size();
                results.values=filters;

            }else
            {
                results.count=filterList.size();
                results.values=filterList;

            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // TODO Auto-generated method stub

            list =(ArrayList<Zone_Helper>) results.values;
            notifyDataSetChanged();
        }

    }
}