package com.wordplay.unit.starter.cache.mongodb.service.impl;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.wordplay.unit.starter.api.response.ResponseResult;
import com.wordplay.unit.starter.cache.mongodb.service.GridFSService;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * GridFS是MongoDB提供的用于持久化存储文件的模块，使用GridFS可以快速集成开发。<br/>
 * 原理：<br/>
 * 在GridFS存储文件是将文件分块存储，文件会按照256KB的大小分割成多个块进行存储，GridFS使用两个集合 （collection）存储文件，一个集合是chunks, 用于存储文件的二进制数据；一个集合是ﬁles，用于存储文件的元数 据信息（文件名称、块大小、上传时间等信息）。
 * 从GridFS中读取文件要对文件的各各块进行组装、合并。
 * 详细参考：https://docs.mongodb.com/manual/core/gridfs/
 *
 * @author zhuangpf
 */
@Service
public class GridFSServiceImpl implements GridFSService {

	@Autowired
	private GridFsTemplate gridFsTemplate;

	@Autowired
	private GridFSBucket gridFSBucket;

	/**
	 * GridFS 保存文件
	 *
	 * @param file 文件
	 * @return 是否保存成功
	 */
	@Override
	public ResponseResult store(File file) throws FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream(file);
		ObjectId objectId = gridFsTemplate.store(fileInputStream, "course.ftl");
		return ResponseResult.success(objectId);
	}

	@Override
	public ResponseResult queryFile(String fileId) throws IOException {
		// 根据文件id查询文件
		GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
		// 打开一个下载流对象
		GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
		// 创建GridFsResource对象，获取流
		GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
		// 从流中取数据
		String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
		return ResponseResult.success();
	}
}
