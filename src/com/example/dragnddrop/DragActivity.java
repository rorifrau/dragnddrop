/*
 *  http://www.vogella.com/tutorials/AndroidDragAndDrop/article.html
 *  http://www.vogella.com/tutorials/AndroidMedia/article.html
 */

package com.example.dragnddrop;

import android.app.Activity;
import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class DragActivity extends Activity {

	  private SoundPool soundPool;
	  private int soundID;
	  boolean loaded = false;
	
/** Called when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {
  
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_drag);
    findViewById(R.id.myimage1).setOnTouchListener(new MyTouchListener());
    findViewById(R.id.myimage2).setOnTouchListener(new MyTouchListener());

    findViewById(R.id.topleft).setOnDragListener(new MyDragListener());
    findViewById(R.id.topright).setOnDragListener(new MyDragListener());



    // Set the hardware buttons to control the music
    this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    // Load the sound
    soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
    soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
      @Override
      public void onLoadComplete(SoundPool soundPool, int sampleId,
          int status) {
        loaded = true;
      }
    });
    soundID = soundPool.load(this, R.raw.applause, 1);
    
  }

  private final class MyTouchListener implements OnTouchListener {
    public boolean onTouch(View view, MotionEvent motionEvent) {
      if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
        ClipData data = ClipData.newPlainText("", "");
        DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
        view.startDrag(data, shadowBuilder, view, 0);
        view.setVisibility(View.INVISIBLE);
        return true;
      } else {
        return false;
      }
    }
  }

  class MyDragListener implements OnDragListener {
    Drawable enterShape = getResources().getDrawable(R.drawable.shape_droptarget);
    Drawable normalShape = getResources().getDrawable(R.drawable.shape);

    @Override
    public boolean onDrag(View v, DragEvent event) {
      int action = event.getAction();
      switch (event.getAction()) {
      case DragEvent.ACTION_DRAG_STARTED:
        // do nothing
        break;
      case DragEvent.ACTION_DRAG_ENTERED:
        v.setBackgroundDrawable(enterShape);
        break;
      case DragEvent.ACTION_DRAG_EXITED:
        v.setBackgroundDrawable(normalShape);
        break;
      case DragEvent.ACTION_DROP:
        // Dropped, reassign View to ViewGroup
        View view = (View) event.getLocalState();
        ViewGroup owner = (ViewGroup) view.getParent();
        owner.removeView(view);
        LinearLayout container = (LinearLayout) v;
        container.addView(view);
        reproducirSonido();
        view.setVisibility(View.VISIBLE);
        break;
      case DragEvent.ACTION_DRAG_ENDED:
        v.setBackgroundDrawable(normalShape);
      default:
        break;
      }
      return true;
    }
  }
  
  private void reproducirSonido()
  {
	  // Getting the user sound settings
      AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
      float actualVolume = (float) audioManager
          .getStreamVolume(AudioManager.STREAM_MUSIC);
      float maxVolume = (float) audioManager
          .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
      float volume = actualVolume / maxVolume;
      // Is the sound loaded already?
      if (loaded) {
        soundPool.play(soundID, volume, volume, 1, 0, 1f);
        Log.e("Test", "Played sound");
      }	  
	  
  }
  
} 