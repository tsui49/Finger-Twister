package com.example.fingertwister;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private TextView mNavigator;
    private Button mStart;
    private int panelId[] = new int[]{R.id.panel_id_0, R.id.panel_id_1, R.id.panel_id_2, R.id.panel_id_3, R.id.panel_id_4, R.id.panel_id_5,
            R.id.panel_id_6, R.id.panel_id_7, R.id.panel_id_8, R.id.panel_id_9, R.id.panel_id_10, R.id.panel_id_11};
    private ImageButton panels[] = new ImageButton[12];
    private int pointerIdToPanel[] = new int[10];
    String navigator = "";
    boolean isContinue = true;
    int turn = 0;
    Twister twister = new Twister();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStart = (Button)findViewById(R.id.button_start);
        mNavigator = (TextView)findViewById(R.id.text_view_1);

        for (int i = 0; i < 12; i++) {
            panels[i] = (ImageButton)findViewById(panelId[i]);
            panels[i].setOnTouchListener(this);
        }

        mStart.setOnClickListener(this);
    }

    @Override
    // "最初から"ボタンを押したときの処理
    public void onClick(View view) {
        Log.d("FingerTwister", "wlog onClick");
        // パネルの初期化
        for (int i = 0; i < 12; i++) {
            panels[i].setBackgroundColor(Color.WHITE);
        }
        // ポインタの初期化
        for (int i = 0; i < 10; i++) {
            pointerIdToPanel[i] = 0;
        }
        twister.clear();
        isContinue = true;
        turn = 0;
        mNavigator.setText("Turn 1 : Player1");
        mNavigator.setTypeface(Typeface.DEFAULT);
        twister.placeToTouch();
        int id = twister.getPlace();
//        Log.d("FingerTwister", "id: " + id);
        panels[id].setBackgroundColor(Color.rgb(255, 115, 115));   //最初の色パネルを薄い赤色で指定
    }

    @Override
    // パネルをタッチしたときの処理
    public boolean onTouch(View view, MotionEvent event) {
        if (isContinue) {
            int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
            int fingerId = event.getPointerId(pointerIndex);

            int w = view.getWidth();
            int h = view.getHeight();

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    pointerIdToPanel[fingerId] = 0;
                    if (twister.isContain(indexOf(panelId, 12, view.getId()))) {
                        ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                        switch (colorDrawable.getColor()) {
                            case 0xFFFF7373:    // 赤色(透過度70%)のとき
                                view.setBackgroundColor(Color.RED);
                                mNavigator.setText("Player2 WIN!");
                                mNavigator.setTypeface(Typeface.DEFAULT_BOLD);
                                break;
                            case 0xFF7373FF:    // 青色(透過度70%)のとき
                                view.setBackgroundColor(Color.BLUE);
                                mNavigator.setText("Player1 WIN!");
                                mNavigator.setTypeface(Typeface.DEFAULT_BOLD);
                                break;
                        }
                        isContinue = false;
                        return true;    // タッチイベントが発生しなくなる
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
//                Log.d("coordinates", "top-bottom-left-right: "+top+"-"+bottom+"-"+left+"-"+right);
                    if (twister.getPlace() == indexOf(panelId, 12, view.getId())) {
                        pointerIdToPanel[fingerId] = view.getId();
                        turn++;
                        twister.placeToTouch();
                        int id = twister.getPlace();
                        if (turn % 2 == 0) {
                            navigator = "Turn " + (turn + 1) + " : Player1";
                            mNavigator.setText(navigator);
                            panels[id].setBackgroundColor(Color.rgb(255, 115, 115));
                        } else {
                            navigator = "Turn " + (turn + 1) + " : Player2";
                            mNavigator.setText(navigator);
                            panels[id].setBackgroundColor(Color.rgb(115, 115, 255));
                        }
                        int num = twister.placeToRelease();
                        if (num >= 0) {
                            panels[num].setBackgroundColor(Color.WHITE);
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    for (int i = 0; i < 10; i++) {
                        if (pointerIdToPanel[i] == view.getId()) {
                            // viewの中に指があるときは処理を続ける
                            if (0 <= event.getX(0) && event.getX(0) <= w && 0 <= event.getY(0) && event.getY(0) <= h) { // 各viewの中の指は1本と仮定するため0
                                return false;
                            } else {    // そうでないときは、タップされていないパネルの色を濃くして勝者を決める
                                ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                                switch (colorDrawable.getColor()) {
                                    case 0xFFFF7373:
                                        view.setBackgroundColor(Color.RED);
                                        mNavigator.setText("Player2 WIN!");
                                        mNavigator.setTypeface(Typeface.DEFAULT_BOLD);
                                        break;
                                    case 0xFF7373FF:
                                        view.setBackgroundColor(Color.BLUE);
                                        mNavigator.setText("Player1 WIN!");
                                        mNavigator.setTypeface(Typeface.DEFAULT_BOLD);
                                        break;
                                }
                                isContinue = false;
                                return true;
                            }
                        }
                    }
//                Log.d("coordinates", "top-left-bottom-right: "+top+"-"+left+"-"+bottom+"-"+right);

                    break;
            }
            return false;
        } else {
            return true;
        }
    }

    // 配列listの中の要素numのインデックスを返す. なければ-1.
    public int indexOf(int[] list, int size, int num) {
        int index = -1;
        for (int i = 0; i < size; i++){
            if (list[i] == num) {
                index = i;
                break;
            }
        }
        return index;
    }

}