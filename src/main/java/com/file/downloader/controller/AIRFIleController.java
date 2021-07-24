package com.file.downloader.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.attribute.HashAttributeSet;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AIRFIleController {

	@Autowired
	private ApplicationContext applicationContext;
	
	@Value("${airfile.resource.wildcard:file:E:/GroupIT_AIR/*/*.AIR}")
	private String resourceWildcard;
	
	@Value("${air.file.linux.separator:/}")
	private String linuxFileSeparator;
	
	@Value("${air.file.windows.separator:\\}")
	private String windowsFileSeparator;
	
	@Value("${air.file.path:E:/GroupIT_AIR/}")
	private String airFilePath;
	
	@GetMapping("/download")
	public void download(@RequestParam String path, HttpServletResponse response) throws IOException {
		
		System.out.println("File to be Downloaded located at Path : " + path);
		
	//	FileSystemResource airFile = new FileSystemResource("E:/GroupIT_AIR/KRFZ3I/KRFZ3I_20210708143212.AIR");
		FileSystemResource airFile = new FileSystemResource(path);
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        StreamUtils.copy(airFile.getInputStream(), response.getOutputStream());
        String line = new BufferedReader(new InputStreamReader(airFile.getInputStream())).readLine();
	}
	
	@GetMapping("/list-air-files/{recordLocator}")
	public List<AirFileDetail> getAIRFiles(@PathVariable String recordLocator) throws IOException {

		List<AirFileDetail> airFileDetails = new ArrayList<>();
		List<Resource> resources = null;
		try {
			resources = Arrays.asList(applicationContext.getResources(resourceWildcard));
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
		
		//file:/E:/GroupIT_AIR/KRFZ3I/KRFZ3I_20210708143232.AIR
		for(Resource resource : resources) {
			String path = resource.getURI().toString();
			System.out.println("path " + path);
			String fileSystemResourcePath = path.substring(path.indexOf(linuxFileSeparator)+1);
			System.out.println("fileSystemResourcePath " + fileSystemResourcePath);
			airFileDetails.add(new AirFileDetail(path.substring(path.lastIndexOf(linuxFileSeparator)+1),fileSystemResourcePath));
		}
		return airFileDetails;
		
	}
	
	
	@GetMapping("/list-air-files2/{recordLocator}")
	public List<AirFileDetail> getAIRFiles2(@PathVariable String recordLocator) throws IOException {

		List<AirFileDetail> airFileDetails = new ArrayList<>();
		File folder = new File(airFilePath+recordLocator);
		
		File[] files = folder.listFiles();
		 
		for (File file : files)
		{
		    System.out.println(file.getName());
		    System.out.println(file.getAbsolutePath());
		    System.out.println(file.getCanonicalPath());
		    
		    System.out.println("----------------------");
		    
		    airFileDetails.add(new AirFileDetail(file.getName(),file.getPath().replace(windowsFileSeparator, linuxFileSeparator)));
		    System.out.println(airFileDetails);
		}
		
		return airFileDetails;
		
	}
	
class AirFileDetail {
	
	private String fileName;

	private String filePath;

	public AirFileDetail(String fileName, String filePath) {
		super();
		this.fileName = fileName;
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	
	
	
	
}
	
}
