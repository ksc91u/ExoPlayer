/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.upstream;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;
import java.io.IOException;

/** Default implementation of {@link LoadErrorHandlingPolicy}. */
public final class DefaultLoadErrorHandlingPolicy implements LoadErrorHandlingPolicy {

  /** The default minimum number of times to retry loading data prior to propagating the error. */
  public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;

  private final int minimumLoadableRetryCount;

  /** Creates an instance that returns the default values. */
  public DefaultLoadErrorHandlingPolicy() {
    this(DEFAULT_MIN_LOADABLE_RETRY_COUNT);
  }

  /**
   * Creates an instance with the given value for {@link #getMinimumLoadableRetryCount(int)}.
   *
   * @param minimumLoadableRetryCount See {@link #getMinimumLoadableRetryCount}.
   */
  public DefaultLoadErrorHandlingPolicy(int minimumLoadableRetryCount) {
    this.minimumLoadableRetryCount = minimumLoadableRetryCount;
  }

  /**
   * Blacklists resources whose load error was an {@link InvalidResponseCodeException} with response
   * code HTTP 404 or 410. The duration of the blacklisting is {@link
   * ChunkedTrackBlacklistUtil#DEFAULT_TRACK_BLACKLIST_MS}.
   */
  @Override
  public long getBlacklistDurationMsFor(
      int dataType, long loadDurationMs, IOException exception, int errorCount) {
    if (exception instanceof InvalidResponseCodeException) {
      int responseCode = ((InvalidResponseCodeException) exception).responseCode;
      return responseCode == 404 // HTTP 404 Not Found.
              || responseCode == 410 // HTTP 410 Gone.
          ? ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS
          : C.TIME_UNSET;
    }
    return C.TIME_UNSET;
  }

  /**
   * Retries for any exception that is not a subclass of {@link ParserException}. The retry delay is
   * calculated as {@code Math.min((errorCount - 1) * 1000, 5000)}.
   */
  @Override
  public long getRetryDelayMsFor(
      int dataType, long loadDurationMs, IOException exception, int errorCount) {
    return exception instanceof ParserException
        ? C.TIME_UNSET
        : Math.min((errorCount - 1) * 1000, 5000);
  }

  /** Returns {@link #DEFAULT_MIN_LOADABLE_RETRY_COUNT}. */
  @Override
  public int getMinimumLoadableRetryCount(int dataType) {
    return minimumLoadableRetryCount;
  }
}