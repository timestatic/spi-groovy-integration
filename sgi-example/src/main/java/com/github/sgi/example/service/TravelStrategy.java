package com.github.sgi.example.service;

import com.github.sgi.core.anno.SGI;

/**
 * @FileName: TravelStrategy.java
 * @Description: TravelStrategy.java类说明
 * @Author: timestatic
 * @Date: 2019/12/22 17:09
 */
@SGI
public interface TravelStrategy {

    String travel(String destination);

}
