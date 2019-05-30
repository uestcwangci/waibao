package com.example.festec.udpbrodcastactivity;

import org.junit.Test;

import java.util.Arrays;

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
        byte[] bytes = new byte[100];
        bytes[0] = (byte) 0xfe;
        System.out.println(Arrays.toString("1".getBytes()));
    }
}