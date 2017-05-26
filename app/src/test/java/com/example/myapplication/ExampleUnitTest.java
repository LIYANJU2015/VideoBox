package com.example.myapplication;

import com.videobox.util.YouTubeUtil;


import org.junit.Test;

import java.util.ArrayList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
        String time = YouTubeUtil.convertDuration("PT39M38S");
        System.out.println(time);

        Test2 test222 = new Test2();

        set(test222.arrayList);

        test222.arrayList.removeAll(arrayList1);

        for (Test2.Test22 test2 : test222.arrayList) {
            System.out.print(" " + test2.object);
        }


    }

    ArrayList<Test2.Test22> arrayList1;

    public void set(ArrayList<Test2.Test22> arrayList) {


        arrayList1 = new ArrayList<>();
        Test2.Test22 test22 = new Test2.Test22();
        arrayList1.add(test22);


        arrayList.addAll(arrayList1);

        for (Test2.Test22 t : arrayList1) {
            t.str = "14444";
            t.object = new Test2();
        }



    }


}