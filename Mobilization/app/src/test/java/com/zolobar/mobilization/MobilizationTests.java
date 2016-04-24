package com.zolobar.mobilization;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Александр on 24.04.2016.
 */
public class MobilizationTests {
    @Test
    public void ArtistEqual() throws Exception{
        Artist one = new Artist();
        Artist two = new Artist();
        one.name = "name";
        one.genres = new String[2];
        one.genres[0] = "genre1";
        one.genres[1] = "genre2";
        one.albums = 2;
        one.cover = new Covers();
        one.cover.big = "url";
        one.cover.small = "url";
        one.description = "some description...";
        one.link = "url";
        one.id = 0;
        two.name = "name";
        two.genres = new String[2];
        two.genres[0] = "genre1";
        two.genres[1] = "genre2";
        two.albums = 2;
        two.cover = new Covers();
        two.cover.big = "url";
        two.cover.small = "url";
        two.description = "some description...";
        two.link = "url";
        two.id = 0;
        assertEquals(one, two);
    }
}
