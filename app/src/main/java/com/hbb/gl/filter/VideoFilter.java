package com.hbb.gl.filter;

import android.content.Context;

import com.hbb.gl.R;

public class VideoFilter extends AbstractFilter {
    public VideoFilter(Context context) {
        super(context, R.raw.video_vert, R.raw.video_frag);
    }
}
