package com.atstakegames.library;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

public class GameResultsReporter {
  public interface FinishedCallback {
    public void created(Uri newRecordUri);

    public void failed(Exception e);
  }

  private static final String CREATE_GAME_RECORD = 
      "content://com.atstakegames.api.ApiServerContentProvider/api/user/me/gamerecord?key=";
  private final Uri CREATE_GAME_RECORD_URI;

  private final String gameId;

  public GameResultsReporter(String gameId, String accessToken) {
    this.gameId = gameId;

    CREATE_GAME_RECORD_URI = Uri.parse(CREATE_GAME_RECORD + accessToken);
  }

  public void reportUserScore(Context context, int score, FinishedCallback cb) {
    ContentValues createRecord = new ContentValues();
    createRecord.put("gameId", gameId);
    createRecord.put("score", score);

    run(context, createRecord, cb);
  }

  public void reportAdversarialScore(Context context, int score, String gameInstanceId,
      FinishedCallback cb) {
    ContentValues createRecord = new ContentValues();
    createRecord.put("gameId", gameId);
    createRecord.put("score", score);
    createRecord.put("gameInstanceId", gameInstanceId);

    run(context, createRecord, cb);
  }

  public void run(final Context context, final ContentValues createRecord, final FinishedCallback cb) {
    new Thread() {
      @Override
      public void run() {
        try {
          Uri created = context.getContentResolver().insert(CREATE_GAME_RECORD_URI, createRecord);
          cb.created(created);
        } catch (Exception e) {
          cb.failed(e);
        }
      }
    }.run();
  }
}
