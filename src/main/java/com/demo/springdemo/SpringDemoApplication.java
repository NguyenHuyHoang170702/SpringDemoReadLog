	package com.demo.springdemo;
	
	import java.io.BufferedReader;
	import java.io.File;
	import java.io.FileInputStream;
	import java.io.IOException;
	import java.io.InputStreamReader;
	import java.nio.file.Files;
	import java.nio.file.Path;
	import java.nio.file.Paths;
	import java.util.ArrayList;
	import java.util.Date;
	import java.util.List;
	import java.util.Map;
	import java.util.stream.Collector;
	import java.util.stream.Collectors;
	import java.util.stream.Stream;
	import java.util.zip.ZipEntry;
	import java.util.zip.ZipInputStream;
	
	import org.apache.juli.OneLineFormatter;
	import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.boot.SpringApplication;
	import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.demo.springdemo.entity.Request;
	import com.demo.springdemo.service.RequestService;
	import com.fasterxml.jackson.core.JsonProcessingException;
	import com.fasterxml.jackson.databind.ObjectMapper;
	
	import ch.qos.logback.core.filter.Filter;
	import lombok.experimental.var;
	
	@SpringBootApplication
	@EnableScheduling
	public class SpringDemoApplication {	
		public static void main(String[] args) {
			SpringApplication.run(SpringDemoApplication.class, args);
		}
	
	
	
	}
