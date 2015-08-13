package com.android.projectphone;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Nocturna on 5/24/2015.
 */
public class ThumbnailLoader {

    public List<String> phoneList;
    public List<String> fullPhoneList;
    public boolean isBusy;
    public int currentPageValue;
    public boolean canLoadMoreItems;

    public String getCurrentPhone(int i) {
        return phoneList.get(i);
    }

    public int getPhoneListSize() {
        return phoneList.size();
    }

    public ThumbnailLoader(List<String> fullPhoneList) {
        this.fullPhoneList = new LinkedList<String>(fullPhoneList);
        phoneList = new LinkedList<String>();
    }

    public void LoadMoreItems(int itemsPerPage) {

        isBusy = true;
        for (int i = currentPageValue; i < currentPageValue + itemsPerPage; i++) {
            if (i == fullPhoneList.size()) {
                break;
            }
            phoneList.add(fullPhoneList.get(i));
        }
        if (currentPageValue + itemsPerPage >= fullPhoneList.size()) {
            canLoadMoreItems = false;
        } else {
            canLoadMoreItems = true;
        }
        currentPageValue = phoneList.size();
        isBusy = false;
    }
}
