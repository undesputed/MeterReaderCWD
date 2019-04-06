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

public class CustomAdapter_2 extends BaseAdapter implements Filterable {
    Context context;
    ArrayList<Applicant_Helper> list;
    LayoutInflater inflater;
    CustomFilter filter;
    ArrayList<Applicant_Helper> filterList;

    public CustomAdapter_2(Context context, ArrayList<Applicant_Helper>list) {
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
        ApplicantHandler handler = null;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_2, null);
            handler = new ApplicantHandler();
            handler.account_number = (TextView) convertView.findViewById(R.id.textView_id);
            handler.applicant_name = (TextView) convertView.findViewById(R.id.textView_name);
            convertView.setTag(handler);
        } else {
            handler = (ApplicantHandler) convertView.getTag();
        }

        handler.account_number.setText(list.get(position).getAcount_number());
        handler.applicant_name.setText(list.get(position).getApplicant_name());

        return convertView;
    }

    public String getAccountNumber(int position) {
        return list.get(position).getAcount_number();
    }

    public String getApplicantName(int position) {
        return list.get(position).getApplicant_name();
    }

    static class ApplicantHandler {
        TextView account_number, applicant_name;
    }


    @Override
    public Filter getFilter() {
        // TODO Auto-generated method stub
        if(filter == null)
        {
            filter=new CustomAdapter_2.CustomFilter();
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

                ArrayList<Applicant_Helper> filters=new ArrayList<Applicant_Helper>();

                //get specific items
                for(int i=0;i<filterList.size();i++)
                {
                    if(filterList.get(i).getAcount_number().toUpperCase().contains(constraint))
                    {
                        Applicant_Helper p=new Applicant_Helper(filterList.get(i).getAcount_number(), filterList.get(i).getApplicant_name());

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

            list =(ArrayList<Applicant_Helper>) results.values;
            notifyDataSetChanged();
        }

    }
}
