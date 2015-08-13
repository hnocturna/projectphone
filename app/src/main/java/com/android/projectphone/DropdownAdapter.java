package com.android.projectphone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.List;

/**
 * Created by hnoct on 8/2/2015.
 */
public class DropdownAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Context context;
    private List<String> checkList;
    private List<String> userChoice;


    public DropdownAdapter(Context context, List<String> checkList, List<String> userChoice) {
        this.context = context;
        this.checkList = checkList;
        this.userChoice = userChoice;
        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return checkList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String checkListText = checkList.get(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.dropdown_row, parent, false);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.dropdown_checkbox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.checkBox.setText(checkListText);

        viewHolder.checkBox.setOnCheckedChangeListener(null);

        if (userChoice.contains(checkListText)) {
            viewHolder.checkBox.setChecked(true);
        } else if (!userChoice.contains(checkListText)){
            viewHolder.checkBox.setChecked(false);
        }

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    userChoice.add(checkListText);
                } else if (!isChecked) {
                    if (userChoice.contains(checkListText)) {
                        userChoice.remove(checkListText);
                    }
                }
            }
        });

        return convertView;
    }

    static class ViewHolder{
        CheckBox checkBox;
    }

}
