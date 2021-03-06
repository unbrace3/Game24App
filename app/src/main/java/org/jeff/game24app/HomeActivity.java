package org.jeff.game24app;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.jeff.game24app.game.FreePlayGameActivity;
import org.jeff.game24app.game.TimedGameActivity;

public class HomeActivity extends BaseActivity {

    /**
     * Intent key that determines generating fraction puzzles
     */
    public static final String GEN_FRAC = "gen_frac";
    public static final String FRAC_PREF = "frac_pref";

    private ImageButton start;

    private Button timeTrial, freePlay, onlinePlay;
    /**
     * False is classic, true is fractional
     */
    private boolean difficultyMode;
    private AlertDialog settingsDialog;
    private boolean fadingOut;
    private boolean atSelectionScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartClicked();
            }
        });

        ImageButton settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSettingsClicked();
            }
        });
        difficultyMode = getSharedPreferences(PREFS, 0).getBoolean(FRAC_PREF, false);
        setupSettingsDialog();

        ImageButton help = findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHelpClicked();
            }
        });

        timeTrial = findViewById(R.id.time_trial);
        timeTrial.setVisibility(View.GONE);
        timeTrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTimeTrialClicked();
            }
        });

        freePlay = findViewById(R.id.free_play);
        freePlay.setVisibility(View.GONE);
        freePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFreePlayClicked();
            }
        });

        onlinePlay = findViewById(R.id.online_play);
        onlinePlay.setVisibility(View.GONE);
        onlinePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOnlinePlayClicked();
            }
        });

        atSelectionScreen = false;

        final ConstraintLayout layout = findViewById(R.id.layout);
        layout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        //Make the title spread out across the view (at least mostly)
                        TextView titleTop = findViewById(R.id.title_top);
                        Rect bounds = new Rect();
                        Paint paint = titleTop.getPaint();
                        String text = titleTop.getText().toString();
                        paint.getTextBounds(text, 0, text.length(), bounds);
                        int textWidth = bounds.width();
                        int viewWidth = titleTop.getWidth();
                        float ems = (viewWidth - textWidth) / titleTop.getTextSize();
                        titleTop.setLetterSpacing(ems / text.length());
                        TextView titleBottom = findViewById(R.id.title_bottom);
                        paint = titleBottom.getPaint();
                        text = titleBottom.getText().toString();
                        paint.getTextBounds(text, 0, text.length(), bounds);
                        textWidth = bounds.width();
                        viewWidth = titleBottom.getWidth();
                        ems = (viewWidth - textWidth) / titleBottom.getTextSize();
                        titleBottom.setLetterSpacing(ems / text.length());
                    }
                });
    }

    private void setupSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_settings, null);
        ToggleButton musicButton = layout.findViewById(R.id.music);
        musicButton.setChecked(playMusic);
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusic = ((ToggleButton) v).isChecked();
                if (playMusic) {
                    MusicService.musicTime = 0;
                    startService(musicIntent);
                } else {
                    stopService(musicIntent);
                }
                updateMusicPrefs(playMusic);
                playTapSound();
            }
        });
        ToggleButton soundButton = layout.findViewById(R.id.sound);
        soundButton.setChecked(playSound);
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound = ((ToggleButton) v).isChecked();
                updateSoundPrefs(playSound);
                playTapSound();
            }
        });
        Switch modeSelect = layout.findViewById(R.id.mode_select);
        modeSelect.setChecked(difficultyMode);
        modeSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                playTapSound();
                difficultyMode = isChecked;
                getSharedPreferences(PREFS, 0).edit().putBoolean(FRAC_PREF, isChecked).apply();
            }
        });
        builder.setView(layout);
        settingsDialog = builder.create();
    }

    private void onStartClicked() {
        playTapSound();
        fadeOut(start);
        atSelectionScreen = true;
    }

    private void onSettingsClicked() {
        playTapSound();
        settingsDialog.show();
    }

    private void onHelpClicked() {
        playTapSound();
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    private void onTimeTrialClicked() {
        playTapSound();
        Intent intent = new Intent(this, TimedGameActivity.class);
        intent.putExtra(GEN_FRAC, difficultyMode);
        startActivity(intent);
    }

    private void onFreePlayClicked() {
        playTapSound();
        Intent intent = new Intent(this, FreePlayGameActivity.class);
        intent.putExtra(GEN_FRAC, difficultyMode);
        startActivity(intent);
    }

    private void onOnlinePlayClicked() {
        playTapSound();
        Intent intent = new Intent(this, OnlineActivity.class);
        intent.putExtra(GEN_FRAC, difficultyMode);
        startActivity(intent);
    }

    private void fadeTransition() {
        if (fadingOut) {
            if (atSelectionScreen) {
                fadeIn(timeTrial);
                fadeIn(freePlay);
                fadeIn(onlinePlay);
            } else {
                fadeIn(start);
            }
            fadingOut = false;
        }
    }

    private void fadeIn(final View v) {
        Animator a = AnimatorInflater.loadAnimator(this, R.animator.grow);
        a.setTarget(v);
        a.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                v.setClickable(true);
            }
        });
        a.start();
    }

    private void fadeOut(final View v) {
        fadingOut = true;
        Animator a = AnimatorInflater.loadAnimator(this, R.animator.shrink);
        a.setTarget(v);
        a.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                v.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                v.setVisibility(View.GONE);
                fadeTransition();
            }
        });
        a.start();
    }

    @Override
    public void onBackPressed() {
        if (atSelectionScreen) {
            fadeOut(timeTrial);
            fadeOut(freePlay);
            fadeOut(onlinePlay);
            atSelectionScreen = false;
        } else {
            super.onBackPressed();
        }
    }
}
