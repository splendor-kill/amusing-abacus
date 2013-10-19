package com.tentacle.common.util;

import java.util.ArrayList;
import java.util.List;

public class VersionString implements Comparable<VersionString> {
    private static final int SECTION_NUM = 3;
    private static final String SEPARATOR = ".";
    private static final String SEPARATOR_REGEX = "\\.";
    
    private List<Integer> version = new ArrayList<Integer>(SECTION_NUM);
    private String strVer;
    
    public VersionString() {
        for (int i = 0; i < SECTION_NUM; i++) {
            version.add(0);
        }
        update();
    }
    
    public VersionString(String ver) {
        String[] parts = ver.split(SEPARATOR_REGEX);        
        for (int i = 0; i < SECTION_NUM; i++) {
            if (i < parts.length) {              
                int n = 0;
                try {
                    n = Integer.parseInt(parts[i]);
                } catch (NumberFormatException e) {
                }
                version.add(n);
            } else {
                version.add(0);
            }
        }
        update();
    }
    
    private void update() {
        StringBuilder sb = new StringBuilder();
        for (Integer i : version) {
            sb.append(i);
            sb.append(SEPARATOR);
        }
        int len = sb.length();
        sb.delete(len - SEPARATOR.length(), len);
        strVer = sb.toString();
    }
    
    public String toString() {
        return strVer;
    }
    
    public static void main(String[] args) {
        System.out.println(new VersionString());
        System.out.println(new VersionString(""));
        System.out.println(new VersionString("1"));
        System.out.println(new VersionString("1."));
        System.out.println(new VersionString("1.0"));
        System.out.println(new VersionString("..1"));
        System.out.println(new VersionString("1.2"));
        System.out.println(new VersionString("1.0.2"));
        System.out.println(new VersionString("1.0.2.3"));
    }

    @Override
    public int compareTo(VersionString other) {
        for (int i = 0; i < SECTION_NUM; i++) {
            Integer part = version.get(i);
            Integer partOther = other.version.get(i);
            if (part < partOther) {
                return -1;
            } else if (part > partOther) {
                return 1;
            }
        }
        return 0;
    }

}
