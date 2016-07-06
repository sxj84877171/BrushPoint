package com.sxj.brush.brushpoint;

import com.sxj.brush.brushpoint.model.FileDownload;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void testFileName() {
        String str = "http://gdl.25pp.com/wm/0/16/sanguo_uc-2016-06-30_22_03_36-release_2390512_1448214b2bf7.apk?cc=1075400199&vh=f5b4440e73d2c7fa73129a1a7e43b406&sf=157558064 ";

        File file = FileDownload.downloadFile(str);


        System.out.println(file);
    }
}