package com.example.festec.udpbrodcastactivity.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalValues {
    public static int udpPort = 6888;
    public static int tcpServerPort = 0;
    public static String localMac = null;
    public static List<Integer> portList;
    public static List<Integer> onlineList;
    public static Set<Integer> checkSet;
    public static Map<Integer, String> portMacMap;

    public static Set<Integer> checkedPort;
}
