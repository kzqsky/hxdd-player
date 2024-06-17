package com.edu.hxdd_player.callback;

public interface TimeCallBack {
    /**
     * 时间到的回调
     */
    default void onTime() {
    }

    /**
     * 时间到的回调
     *
     * @param currentCatalogID 章节id
     * @param coursewareCode   课件code
     */
    default void onTime(String currentCatalogID, String coursewareCode) {
    }

//
//    /**
//     * 识别结束的回调
//     */
//    void continuePlaying();
}
