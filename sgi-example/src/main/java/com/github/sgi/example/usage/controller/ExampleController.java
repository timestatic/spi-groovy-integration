package com.github.sgi.example.usage.controller;

import com.github.sgi.core.ExtensionLoader;
import com.github.sgi.core.ExtensionSpringLoader;
import com.github.sgi.core.scanner.FileScanner;
import com.github.sgi.core.scanner.PackageScanner;
import com.github.sgi.example.service.TravelStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.script.ScriptException;
import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @FileName: ExampleController.java
 * @Description: ExampleController.java类说明
 * @Author: timestatic
 * @Date: 2020/1/1 17:21
 */
@RestController
public class ExampleController {

    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);

    @Autowired
    private ApplicationContext applicationContext;
    private volatile boolean init = false;

    private synchronized void init() throws InstantiationException, IllegalAccessException, ScriptException, NoSuchFieldException {
        if (init) {
            return;
        }
        String script = "import com.github.sgi.example.service.TravelStrategy\n" +
                "\n" +
                "@GImpl(isSpringBean = true, value = 'flight')\n" +
                "class FlightTravelStrategy implements TravelStrategy {\n" +
                "\n" +
                "    @Override\n" +
                "    String travel(String destination) {\n" +
                "        return \"go to \" + destination +  \" by Air ~\"\n" +
                "    }\n" +
                "}";
        try {
            // 加载文本并注册为spring bean
            ExtensionSpringLoader.getExtensionSpringLoader(TravelStrategy.class).addExtensionSpringContext(script);
        } catch (Exception e) {
        }


        // 项目内
        ExtensionLoader<TravelStrategy> loader = ExtensionLoader.getExtensionLoader(TravelStrategy.class);
        for (Class<?> clazz : PackageScanner.findGImplClass(TravelStrategy.class)) {
            loader.addExtension(clazz);
        }

        // local file
        String path = "D:\\Temp\\NuoyafangzhuoTravelStrategy.groovy";
        String code = FileScanner.readFile(path);
        loader.addExtension(code);
        fileRefreshSchedule(path, loader);

        init = true;
    }

    @GetMapping("/hello")
    public String travel() throws InstantiationException, IllegalAccessException, NoSuchFieldException, ScriptException {
        init();

        StringBuilder result = new StringBuilder();

        // spring
        String flight = ((TravelStrategy) applicationContext.getBean("flight")).travel("hangZhou");
        result.append(flight);
        result.append("\n");

        // inner
        ExtensionLoader<TravelStrategy> loader = ExtensionLoader.getExtensionLoader(TravelStrategy.class);
        String crh = loader.getExtension("CRH").travel("hangZhou");
        result.append(crh);
        result.append("\n");

        // local file
        String localFile = loader.getExtension("NuoYaFangZhouTrave").travel("hangzhou");
        result.append(localFile);
        result.append("\n");

        return result.toString();
    }


    private static long lastModifyTimeCache = 0L;
    private void fileRefreshSchedule(String filePath, ExtensionLoader<TravelStrategy> loader) {
        executor.scheduleWithFixedDelay(() -> {
            File file = new File(filePath);
            long modifyTime;
            if ((modifyTime = file.lastModified()) != lastModifyTimeCache) {
                try {
                    loader.addExtension(FileScanner.readFile(filePath));
                } catch (Exception e) {
                }
            }
            lastModifyTimeCache = modifyTime;

        }, 10L, 10L, TimeUnit.SECONDS);
    }

}
