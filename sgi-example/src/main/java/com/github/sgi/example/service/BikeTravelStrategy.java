package com.github.sgi.example.service;

import com.github.sgi.core.anno.GImpl;

/**
 * @FileName: BikeTravelStrategy.java
 * @Description: BikeTravelStrategy.java类说明
 * @Author: timestatic
 * @Date: 2019/12/22 17:11
 */
@GImpl(value = "bike")
public class BikeTravelStrategy implements TravelStrategy {
    @Override
    public String travel(String destination) {
        return "go to " + destination +  " by bike ~";
    }
}
