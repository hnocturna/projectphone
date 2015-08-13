package com.android.projectphone;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by hnoct on 7/31/2015.
 */
public class ThumbnailCardLoader {
    List<CardInfo> phoneCardList;           // Partial List of phone Cards that is passed to the Adapter.
    List<CardInfo> fullPhoneCardList;       // List of all the phone Cards.
    public boolean isBusy;                  // Prevents the Loader from running if it is already running.
    public int currentPageValue;            // Total number of phones Cards that should be in the List.
    public boolean canLoadMoreItems;        // Check for whether any more Cards are in the full List to be added to the partial List passed to the Adapter.

    public ThumbnailCardLoader(List<CardInfo> fullPhoneCardList) {
        // Holds all the phone Cards and begins a new List with a partial list to be increased with scrolling.
        this.fullPhoneCardList = fullPhoneCardList;
        phoneCardList = new LinkedList<>();
    }

    public CardInfo getCurrentPhoneCard(int i) {
        return phoneCardList.get(i);
    }

    public int getPhoneCardListSize() {
        return phoneCardList.size();
    }

    public void loadMoreItems(int itemsPerPage) {
        // Adds additional phone Cards to the List that is passed to the Adapter.
        isBusy = true;
        for (int i = currentPageValue; i < currentPageValue + itemsPerPage; i++) {
            // Adds phones to the List passed to the Adapter up to the currentPageValue.
            if (i == fullPhoneCardList.size()) {
                // Stop adding phone Cards when it is the size of the full List.
                break;
            }
            phoneCardList.add(fullPhoneCardList.get(i));
        }
        if (currentPageValue + itemsPerPage >= fullPhoneCardList.size()) {
            canLoadMoreItems = false;
        } else {
            canLoadMoreItems = true;
        }
        currentPageValue = phoneCardList.size();
        isBusy = false;
    }
}
