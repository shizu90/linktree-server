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
import com.shizu.linktree.entities.LinkTree;
import com.shizu.linktree.repositories.LinkTreeRepository;

@Service
public class S3Service {
	
	@Autowired
	private AmazonS3Client s3Client;
	
	@Autowired
	private LinkTreeService service;
	
	@Autowired
	private LinkTreeRepository repo;
	
	public String uploadFile(Long id, MultipartFile file) {
		try {
			File uploadedFile = new File(file.getOriginalFilename());
			FileOutputStream fileStream = new FileOutputStream(uploadedFile);
			fileStream.write(file.getBytes());
			fileStream.close();
			LinkTree linkTree = service.findById(id);
			String fileName = linkTree.getUser().getUsername() + "_userImg";
			try {
				s3Client.deleteObject("linktree-upload", fileName);
			}catch(AmazonServiceException e) {
				e.printStackTrace();
			}
			s3Client.putObject(new PutObjectRequest("linktree-upload", fileName, uploadedFile).withCannedAcl(CannedAccessControlList.PublicRead));
			String url = s3Client.getResourceUrl("linktree-upload", fileName);
			uploadedFile.delete();
			linkTree.setUserImg(url);
			repo.save(linkTree);
			return url;
		}catch(IOException e) {
			throw new RuntimeException("Failed to upload file.");
		}
	}
}
