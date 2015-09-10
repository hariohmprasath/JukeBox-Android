package com.music.personal.myapplication.adaptor.interfaces;

/**
 * Created by hrajagopal on 9/9/15.
 */
public interface ItemTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
