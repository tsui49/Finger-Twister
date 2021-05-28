package com.example.fingertwister;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Twister {
    private int fingers = 3;    // 使用する指の本数
    private List<Integer> places = new ArrayList(); // 指定する場所の記録

    public void placeToTouch() {
        Random rand = new Random();
        int num = rand.nextInt(12);
        // 重複しないようにする
        while (places.contains(num)) {
            num = rand.nextInt(12);
        }
        places.add(num);
    }

    public int placeToRelease() {
        int num = -1;
        // 一度すべての指を使ったら、先頭のものから順に置き換える
        if ((places.size() - 1) / 2 >= fingers) {
            num = places.get(0);
            places.remove(0);
        }
        return num;
    }

    public void clear() {
        places.clear();
    }

    public int getPlace() {
        return places.get(places.size() - 1);
    }

    public boolean isContain(int i) {
        return places.contains(i);
    }
}
