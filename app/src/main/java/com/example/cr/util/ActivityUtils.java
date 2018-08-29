package com.example.cr.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.concurrent.TimeUnit;

public class ActivityUtils {
    public static void replaceFragmentInContainer(int resId,
                                                  FragmentManager fragmentManager,
                                                  Fragment fragment) {
        // Смена фрагмента запускается в отдельном потоке и задерживается на 0.3 секунды,
        // чтобы избежать пролагивания при переключении
        new Thread(new Runnable() {
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(300);

                    FragmentTransaction transaction = fragmentManager.beginTransaction();

                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                    transaction.replace(resId, fragment);

                    transaction.commitAllowingStateLoss();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
}
