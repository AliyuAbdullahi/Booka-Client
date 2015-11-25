package com.example.bookac.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.bookac.R;
import com.example.bookac.singletons.Chef;
import com.example.bookac.singletons.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aliyuolalekan on 29/10/2015.
 */
public class SearchAutoCompleteAdapter extends ArrayAdapter<Chef> {
  private ArrayList<Chef> items;
  private ArrayList<Chef> itemsAll;
  private ArrayList<Chef> suggestions;
  private ArrayList<Chef> menu;
  private int viewResourceId;

  @SuppressWarnings("unchecked")
  public SearchAutoCompleteAdapter(Context context, int viewResourceId,
                              ArrayList<Chef> items) {
    super(context, viewResourceId, items);
    this.items = items;
    this.itemsAll = (ArrayList<Chef>) items.clone();
    this.suggestions = new ArrayList<Chef>();
    this.viewResourceId = viewResourceId;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    View v = convertView;
    if (v == null) {
      LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
              Context.LAYOUT_INFLATER_SERVICE);
      v = vi.inflate(viewResourceId, null);
    }
    Chef product = items.get(position);
    if (product != null) {
      TextView productLabel = (TextView)  v.findViewById(R.id.title_text);
      TextView contentOne = (TextView)v.findViewById (R.id.content_one);
      TextView contentTwo = (TextView)v.findViewById (R.id.content_two);
      if (productLabel != null) {
        productLabel.setText(product.getNickName ());
      }
      if(contentOne != null){
        contentOne.setText (product.address);
      }
      for(MenuItem item : product.menuItems){
        if(contentTwo != null){
          contentTwo.setText (item.name);
        }
      }
    }
    return v;
  }

  @Override
  public Filter getFilter() {
    return nameFilter;
  }

  Filter nameFilter = new Filter() {
    public String convertResultToString(Object resultValue) {
      String str = ((Chef) (resultValue)).getNickName ();
      return str;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
      if (constraint != null) {
        suggestions.clear();
        for (Chef product : itemsAll) {
          if (product.getNickName ().toLowerCase ()
                  .startsWith (constraint.toString ().toLowerCase ()) ||
                  product.address.toLowerCase ()
                          .startsWith (constraint.toString ().toLowerCase ())) {
            suggestions.add(product);
          }
        }
        FilterResults filterResults = new FilterResults();
        filterResults.values = suggestions;
        filterResults.count = suggestions.size();
        return filterResults;
      } else {
        return new FilterResults();
      }
    }

    @Override
    protected void publishResults(CharSequence constraint,
                                  FilterResults results) {
      @SuppressWarnings("unchecked")
      ArrayList<Chef> filteredList = (ArrayList<Chef>) results.values;
      if (results.count > 0) {
        clear();
        for (Chef c : filteredList) {
          add(c);
        }
        notifyDataSetChanged ();
      }
      else {
        notifyDataSetInvalidated();
      }
    }
  };

}
