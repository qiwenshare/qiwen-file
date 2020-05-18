package com.mac.scp;

import cn.hutool.core.lang.Console;
import com.google.common.collect.Maps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.TreeMap;

@SpringBootApplication
@ComponentScan(basePackages = {"com.mac.common", "com.mac.scp"})
@EnableScheduling
public class FileApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(FileApplication.class, args);
		TreeMap<String, String> map = Maps.newTreeMap();
		Arrays.asList(run.getBeanDefinitionNames()).forEach(k -> map.put(k, run.getBean(k).getClass().getName()));
		map.forEach((k, v) -> Console.log("{} \t\t {}", k, v));
	}


}
