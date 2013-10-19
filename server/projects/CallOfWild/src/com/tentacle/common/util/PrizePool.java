package com.tentacle.common.util;

import java.util.Random;

public class PrizePool {
    private int category;
    private int capacity;
    private int pretend;
    private int genuine;
    private Random rand;
    
    public PrizePool(int cat, int cap, int fake, int real, Random rand) {
        reset(cap, fake, real);
        this.category = cat;
        this.rand = (rand == null) ? new Random() : rand;
    }

    public void reset(int cap, int fake, int real) {
        if (cap < 0 || fake < 0 || real < 0 || cap < fake + real)
            throw new IllegalArgumentException();
        this.pretend = fake;
        this.genuine = real;
        this.capacity = (cap < fake + real) ? fake + real : cap;
    }
    
    /**
     * @param num of draw
     * @return result of draw, int[0] num of bad luck, int[1] num of good luck 
     */
    public int[] draw(int num) {
        int[] luck = new int[2];
        if (num <= 0)
            return luck;

        for (int i = 0; i < num; i++) {
            int left = pretend + genuine;
            if (left <= 0) {
                luck[0] += num - i;
                break;
            }
            int r = rand.nextInt(left);
            if (r < pretend) {
                luck[0]++;
                pretend--;
            } else {
                luck[1]++;
                genuine--;
            }
        }
        return luck;
    }
    
    /**
     * @param num of draw
     * @return result of draw, int[0] num of bad luck, int[1] num of good luck 
     */
    public int[] draw2(int num) {
        if (num <= 0)
            throw new IllegalArgumentException();
        int left = pretend + genuine;
        if (num > left)
            throw new RuntimeException("not enough lottery.");

        int[] luck = new int[2];
        for (int i = 0; i < num; i++) {
            int r = rand.nextInt(pretend + genuine);
            if (r < pretend) {
                luck[0]++;
                pretend--;
            } else {
                luck[1]++;
                genuine--;
            }
        }
        return luck;
    }
    
    public boolean isEmpty() {
        return pretend + genuine == 0;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getPretend() {
        return pretend;
    }

    public void setPretend(int pretend) {
        this.pretend = pretend;
    }

    public int getGenuine() {
        return genuine;
    }

    public void setGenuine(int genuine) {
        this.genuine = genuine;
    }
    
}
