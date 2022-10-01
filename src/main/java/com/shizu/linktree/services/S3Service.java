package com.shizu.linktree.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.shizu.linktree.entities.User;
import com.shizu.linktree.repositories.UserRepository;

@Service
public class S3Service {
	
	@Autowired
	private AmazonS3Client s3Client;
	
	@Autowired
	private UserService service;
	
	@Autowired
	private UserRepository repo;
	
	public String uploadFile(String token, MultipartFile file) {
		try {
			File uploadedFile = new File(file.getOriginalFilename());
			FileOutputStream fileStream = new FileOutputStream(uploadedFile);
			fileStream.write(file.getBytes());
			fileStream.close();
			User user = service.findById(token);
			String fileName = user.getUsername() + "_userImg";
			try {
				s3Client.deleteObject("linktree-upload", fileName);
			}catch(AmazonServiceException e) {
				e.printStackTrace();
			}
			s3Client.putObject(new PutObjectRequest("linktree-upload", fileName, uploadedFile).withCannedAcl(CannedAccessControlList.PublicRead));
			String url = s3Client.getResourceUrl("linktree-upload", fileName);
			uploadedFile.delete();
			user.setUserImg(url);
			repo.save(user);
			return url;
		}catch(IOException e) {
			throw new RuntimeException("Failed to upload file.");
		}
	}
}
