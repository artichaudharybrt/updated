package com.gamegards.gaming27.Interface;

import android.view.View;

import java.io.Serializable;

public interface OnItemClickListener extends Serializable {

    void Response(View v, int position, Object object);

}
