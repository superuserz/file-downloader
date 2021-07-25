package com.file.downloader.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * E:/GroupIT_AIR/ (airFilePath - Windows)
 * /GroupIT_AIR/   (airFilePath - Linux)
 *
 *
 */
@RestController
public class AIRFIleController {

	@Value("${air.file.path:E:/GroupIT_AIR/}")
	private String airFilePath;

	@Value("${air.file.linux.separator:/}")
	private String linuxFileSeparator;

	@Value("${air.file.windows.separator:\\}")
	private String windowsFileSeparator;

	@GetMapping("/download")
	public ResponseEntity<byte[]> download(@RequestParam String path, HttpServletResponse response) throws IOException {

		System.out.println("File to be Downloaded located at Path : " + path);
		byte[] content = Files.readAllBytes(Paths.get(path));
		return ResponseEntity.ok().contentLength(content.length).header(HttpHeaders.CONTENT_TYPE, "text/plain")
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "File.txt").body(content);
	}

	@GetMapping("/list-air-files/{recordLocator}")
	public List<AirFileDetail> getAIRFiles(@PathVariable String recordLocator) throws Exception {

		List<AirFileDetail> airFileDetails = new ArrayList<>();
		File airFileDirectory = new File(airFilePath + recordLocator);

		if (airFileDirectory.exists()) {
			File[] files = airFileDirectory.listFiles();

			if (files != null && files.length > 0) {
				for (File file : files) {
					System.out.println("Fetching File Name : " + file.getName());
					System.out.println("Fetching File Path : " + file.getAbsolutePath());
					airFileDetails.add(new AirFileDetail(file.getName(),
							file.getPath().replace(windowsFileSeparator, linuxFileSeparator)));
				}
			} else {
				throw new IOException("No AIR Files present in directory : " + airFilePath + recordLocator);
			}
		} else {
			throw new IOException("Path to AIR File not Found : " + airFilePath + recordLocator);
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

		@Override
		public String toString() {
			return "AirFileDetail [fileName=" + fileName + ", filePath=" + filePath + "]";
		}

	}

}
