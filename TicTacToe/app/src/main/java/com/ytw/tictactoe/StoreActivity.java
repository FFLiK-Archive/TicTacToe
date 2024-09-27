package com.ytw.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class StoreActivity extends AppCompatActivity {

    private int store;
    private AlertDialog dialog;

    private enum PayType {HEART, ITEM_REVIVIAL, ITEM_FREEZE}

    private PayType paytype;
    private boolean item_close;

    private SoundPool sound;
    private int button_click;

    private RewardedAd rewardedAd;

    @Override
    public void onBackPressed(){
        if(GameData.sound)
            sound.play(button_click, 1.0F, 1.0F,  0,  0,  1);
        super.onBackPressed();
    }

    private CountDownTimer timer;

    public RewardedAd createAndLoadRewardedAd() {
        RewardedAd rewardedAd = new RewardedAd(this,
                "ca-app-pub-3804302046620739/2155638903");
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        return rewardedAd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        rewardedAd = createAndLoadRewardedAd();

        sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        button_click = sound.load(this, R.raw.button_click, 1);

        store = 1;

        item_close = false;

        switch(store) {
            case 1:
                Store1();
                break;
            case 2:
                Store2();
                break;
        }

        timer = new CountDownTimer(60000, 5000) {
            @Override
            public void onTick(long l) {
                GameData.SetText(findViewById(R.id.hp), findViewById(R.id.coin));
            }

            @Override
            public void onFinish() {
                timer.start();
            }
        }.start();
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(item_close){
            overridePendingTransition(R.anim.slide_not_move, R.anim.slide_down_close);
            item_close = false;
        }
    }

    private void Store1(){
        setContentView(R.layout.activity_store_1pg);

        GameData.SetText(findViewById(R.id.hp), findViewById(R.id.coin));

        Button heart_t = (Button)findViewById(R.id.heart_t_btn);
        Button heart_ad = (Button)findViewById(R.id.heart_ad_btn);
        Button coin_ad = (Button)findViewById(R.id.coin_ad_btn);
        Button item = (Button)findViewById(R.id.item_button);

        item.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GameData.sound)
                    sound.play(button_click, 1.0F, 1.0F,  0,  0,  1);
                item_close = true;
                Intent intent= new Intent(getApplicationContext(), ItemScreen.class);
                startActivity(intent);
            }
        });

        Button right_move = (Button)findViewById(R.id.right_button);

        right_move.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Store2();
            }
        });

        heart_t.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GameData.sound)
                    sound.play(button_click, 1.0F, 1.0F,  0,  0,  1);
                paytype = PayType.HEART;
                MsgShow("하트를 구매하시겠습니까?", "50T로 2개의 하트를 얻습니다.", "YESNO");
            }
        });

        heart_ad.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GameData.sound)
                    sound.play(button_click, 1.0F, 1.0F,  0,  0,  1);

                if (rewardedAd.isLoaded()) {
                    Activity activityContext = StoreActivity.this;
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            rewardedAd = createAndLoadRewardedAd();
                        }

                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem reward) {
                            Toast.makeText(StoreActivity.this, "+1 heart", Toast.LENGTH_SHORT).show();
                            GameData.heart += 1;
                            GameData.SetText(findViewById(R.id.hp), findViewById(R.id.coin));
                            GameData.SaveData();
                            rewardedAd = createAndLoadRewardedAd();
                        }

                        @Override
                        public void onRewardedAdFailedToShow(AdError adError) {
                            Toast.makeText(StoreActivity.this, "광고 로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    };
                    rewardedAd.show(activityContext, adCallback);
                }
                else Toast.makeText(StoreActivity.this, "광고가 아직 로드되지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        coin_ad.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GameData.sound)
                    sound.play(button_click, 1.0F, 1.0F,  0,  0,  1);
                if (rewardedAd.isLoaded()) {
                    Activity activityContext = StoreActivity.this;
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            rewardedAd = createAndLoadRewardedAd();
                        }

                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem reward) {
                            Toast.makeText(StoreActivity.this, "+10 T", Toast.LENGTH_SHORT).show();
                            GameData.coin += 10;
                            GameData.SetText(findViewById(R.id.hp), findViewById(R.id.coin));
                            GameData.SaveData();
                            rewardedAd = createAndLoadRewardedAd();
                        }

                        @Override
                        public void onRewardedAdFailedToShow(AdError adError) {
                            Toast.makeText(StoreActivity.this, "광고 로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    };
                    rewardedAd.show(activityContext, adCallback);
                }
                else Toast.makeText(StoreActivity.this, "광고가 아직 로드되지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Store2(){
        setContentView(R.layout.activity_store_2pg);
        GameData.SetText(findViewById(R.id.hp), findViewById(R.id.coin));

        Button left_move = (Button)findViewById(R.id.left_button);
        Button right_move = (Button)findViewById(R.id.right_button);

        Button revival_t = (Button)findViewById(R.id.revival_btn);
        Button freeze_t = (Button)findViewById(R.id.freeze_btn);

        revival_t.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GameData.sound)
                    sound.play(button_click, 1.0F, 1.0F,  0,  0,  1);
                paytype = PayType.ITEM_REVIVIAL;
                MsgShow("아이템을 구매하시겠습니까?", "[ 부활 ]\n게임이 종료되면 부활할 수 있습니다.", "YESNO");
            }
        });

        freeze_t.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GameData.sound)
                    sound.play(button_click, 1.0F, 1.0F,  0,  0,  1);
                paytype = PayType.ITEM_FREEZE;
                MsgShow("아이템을 구매하시겠습니까?", "[ 얼음 ]\n제한시간이 멈춥니다.", "YESNO");
            }
        });

        Button item = (Button)findViewById(R.id.item_button);

        item.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GameData.sound)
                    sound.play(button_click, 1.0F, 1.0F,  0,  0,  1);
                item_close = true;
                Intent intent= new Intent(getApplicationContext(), ItemScreen.class);
                startActivity(intent);
            }
        });

        left_move.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Store1();
            }
        });
    }

    private void MsgShow(String title, String message, String mode) {
        switch(mode) {
            case "OK":
                dialog = new AlertDialog.Builder(StoreActivity.this) // Pass a reference to your main activity here
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.cancel();
                            }
                        })
                        .show();
                break;
            case "OKCANCLE":
                dialog = new AlertDialog.Builder(StoreActivity.this) // Pass a reference to your main activity here
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.cancel();
                            }
                        })
                        .show();
                break;
            case "YESNO":
                dialog = new AlertDialog.Builder(StoreActivity.this) // Pass a reference to your main activity here
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                payment();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.cancel();
                            }
                        })
                        .show();
                break;
        }
    }

    private void payment(){
        switch(paytype){
            case HEART:
                if (GameData.coin >= 50) {
                    GameData.coin -= 50;
                    GameData.heart += 2;
                    GameData.SetText(findViewById(R.id.hp), findViewById(R.id.coin));
                    Toast.makeText(StoreActivity.this, "구매하였습니다. (+2)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StoreActivity.this, "T 코인이 부족합니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case ITEM_REVIVIAL:
                if (GameData.coin >= 30) {
                    GameData.coin -= 30;
                    GameData.item.revival ++;
                    GameData.SetText(findViewById(R.id.hp), findViewById(R.id.coin));
                    Toast.makeText(StoreActivity.this, "구매하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StoreActivity.this, "T 코인이 부족합니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case ITEM_FREEZE:
                if (GameData.coin >= 50) {
                    GameData.coin -= 50;
                    GameData.item.freeze ++;
                    GameData.SetText(findViewById(R.id.hp), findViewById(R.id.coin));
                    Toast.makeText(StoreActivity.this, "구매하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StoreActivity.this, "T 코인이 부족합니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        GameData.SaveData();
    }
}