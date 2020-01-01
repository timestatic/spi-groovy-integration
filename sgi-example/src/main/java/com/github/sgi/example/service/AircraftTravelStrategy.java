package com.github.sgi.example.service;

import com.github.sgi.core.anno.GImpl;

/**
 * @FileName: AircraftTravelStrategy.java
 * @Description: AircraftTravelStrategy.java类说明
 * @Author: timestatic
 * @Date: 2019/12/22 17:14
 */
@GImpl(value = "air")
public class AircraftTravelStrategy implements TravelStrategy {

    @Override
    public String travel(String destination) {
        return "go to " + destination +  " by aircraft ~";
    }

}
