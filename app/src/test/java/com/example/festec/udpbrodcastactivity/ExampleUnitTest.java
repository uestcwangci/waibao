package com.example.festec.udpbrodcastactivity;

import com.example.festec.udpbrodcastactivity.module.utils.ByteUtils;

import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testUdp() {
        Calendar cal = Calendar.getInstance();
        System.out.println(Long.valueOf(cal.getTimeInMillis() % 100000000).intValue());
    }
}