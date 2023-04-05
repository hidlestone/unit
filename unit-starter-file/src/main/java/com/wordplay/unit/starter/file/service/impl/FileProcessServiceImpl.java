package com.wordplay.unit.starter.file.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.wordplay.unit.starter.api.response.ResponseResult;
import com.wordplay.unit.starter.file.entity.FileGroup;
import com.wordplay.unit.starter.file.entity.FileInfo;
import com.wordplay.unit.starter.file.model.FileGroupUploadDto;
import com.wordplay.unit.starter.file.service.FileGroupService;
import com.wordplay.unit.starter.file.service.FileInfoService;
import com.wordplay.unit.starter.file.service.FileProcessService;
import com.wordplay.unit.starter.file.util.FileStarterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * SpringBoot实现文件的上传下载
 *
 * @author zhuangpf
 */
@Service
public class FileProcessServiceImpl implements FileProcessService {

	/**
	 * 文件上传的位置，在yml中进行配置
	 */
	@Value("${unit-starter.file.upload-file-path}")
	private String uploadFilePath;
	@Autowired
	private FileGroupService fileGroupService;
	@Autowired
	private FileInfoService fileInfoService;

	/**
	 * 上传文件组多文件
	 */
	@Override
	@Transactional
	public ResponseResult<Long> uploadFileGroup(FileGroupUploadDto dto) {
		// // 所有待上传的文件 MultipartFile
		List<MultipartFile> files = dto.getFiles();
		if (CollectionUtil.isEmpty(files)) {
			return ResponseResult.fail("files are not exist.");
		}
		FileGroup fileGroupRequest = new FileGroup();
		List<FileInfo> fileInfoRequestList = new ArrayList<>();
		fileGroupRequest.setFileInfos(fileInfoRequestList);
		fileGroupRequest.setDesc(dto.getDesc());
		try {
			for (MultipartFile multipartFile : files) {
				String fname = multipartFile.getOriginalFilename();
				// 上传文件的类型：application/xml
				String contentType = multipartFile.getContentType();
				UUID uuid = UUID.randomUUID();
				// 获取文件后缀名
				String fkey = uuid + "." + FileStarterUtil.getFileExt(fname);
				// 设置文件保存的路径，不存在则创建文件夹
				String filepath = FileStarterUtil.getFilePath(fkey, uploadFilePath);
				File file = new File(filepath);
				// 进行对上传文件的IO拷贝操作
				FileCopyUtils.copy(multipartFile.getInputStream(), new FileOutputStream(file));

				FileInfo infoRequest = new FileInfo();
				infoRequest.setName(fname);
				infoRequest.setNonsenseName(fkey);
				infoRequest.setExtension(FileStarterUtil.getFileExt(fname));
				infoRequest.setStorageType(dto.getStorageType());
				fileInfoRequestList.add(infoRequest);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Long groupId = fileGroupService.saveGroupAndInfoList(fileGroupRequest);
		return ResponseResult.success(groupId);
	}

	@Override
	public ResponseResult downloadFile(Long fileInfoId, HttpServletResponse response) {
		FileInfo fileInfo = fileInfoService.getById(fileInfoId);
		if (null == fileInfo) {
			return ResponseResult.fail("file is not exist.");
		}
		File file = new File(uploadFilePath + "/" + fileInfo.getNonsenseName());
		if (!file.exists()) {
			return ResponseResult.fail("file is not exist.");
		}
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/octet-stream");
		response.setContentLength((int) file.length());
		// 设置强制下载不打开
		response.setContentType("application/force-download");
		// 转换中文否则可能会产生乱码
		String downloadFilename = null;
		try {
			downloadFilename = URLEncoder.encode(fileInfo.getNonsenseName(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 设置文件名
		response.addHeader("Content-Disposition", "attachment;fileName=" + downloadFilename);
		byte[] buffer = new byte[1024];
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			OutputStream os = response.getOutputStream();
			int i = bis.read(buffer);
			while (i != -1) {
				os.write(buffer, 0, i);
				i = bis.read(buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 做关闭操作
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseResult.success();
	}

}
