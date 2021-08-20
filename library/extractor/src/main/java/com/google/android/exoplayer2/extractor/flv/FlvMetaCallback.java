package com.google.android.exoplayer2.extractor.flv;

import com.google.android.exoplayer2.util.ParsableByteArray;

public interface FlvMetaCallback {
  public void onMeta(ParsableByteArray byteArray);
}
