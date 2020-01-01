package com.github.sgi.example.service;

import com.github.sgi.core.anno.GImpl;

/**
 * @FileName: CRHTravelStrategy.java
 * @Description: CRHTravelStrategy.java类说明
 * @Author: timestatic
 * @Date: 2019/12/22 17:12
 */
@GImpl(value = "CRH")
public class CRHTravelStrategy implements TravelStrategy {

    @Override
    public String travel(String destination) {
        return "go to " + destination +  " by CRH ~";
    }
}
