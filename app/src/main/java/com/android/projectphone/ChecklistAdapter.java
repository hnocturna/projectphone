package com.android.projectphone;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by hnoct on 8/2/2015.
 */
public class ChecklistAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Context context;
    private List<String> checkList;
    private List<String> userChoice;
    private MySQLiteHelper db;


    public ChecklistAdapter(Context context, List<String> checkList, List<String> userChoice) {
        this.db = new MySQLiteHelper(context);
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
        final List<String> subCategoryList = new LinkedList<>();

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.checklist_popup);


        if (checkListText.contains("Qualcomm") && !checkListText.contains("S4")) {
            subCategoryList.addAll(db.getSoCChoices(checkListText));
        }

        /* final LinearLayout popupLayout = (LinearLayout)layoutInflater.inflate(R.layout.checklist_popup, (ViewGroup) convertView.findViewById(R.id.popup_view));
        final PopupWindow pw = new PopupWindow(popupLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setTouchable(true);
        pw.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    pw.dismiss();
                    return true;
                }
                return false;
            }
        }); */

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.checklist_row, parent, false);
            viewHolder.checklistLayout = (RelativeLayout) convertView.findViewById(R.id.checklist_layout);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checklist_checkbox);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.checklist_imageview);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.checklist_textview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final View tempView = convertView;
        if (!subCategoryList.isEmpty()) {
            viewHolder.imageView.setVisibility(View.VISIBLE);
            viewHolder.checklistLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.show();

                    ListView dropdownListView = (ListView) dialog.findViewById(R.id.dropdown_listview);
                    DropdownAdapter dropdownAdapter = new DropdownAdapter(context, subCategoryList, userChoice);
                    dropdownListView.setAdapter(dropdownAdapter);
                }
            });
        } else {
            viewHolder.imageView.setVisibility(View.INVISIBLE);
            viewHolder.checklistLayout.setOnClickListener(null);
        }

        viewHolder.textView.setText(checkListText);
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
        RelativeLayout checklistLayout;
        TextView textView;
        CheckBox checkBox;
        ImageView imageView;
    }

}
