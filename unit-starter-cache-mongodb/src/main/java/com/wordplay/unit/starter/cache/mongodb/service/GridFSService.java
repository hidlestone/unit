package com.wordplay.unit.starter.cache.mongodb.service;

import com.wordplay.unit.starter.api.response.ResponseResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author zhuangpf
 */
public interface GridFSService {

	/**
	 * GridFS 保存文件
	 *
	 * @param file 文件
	 * @return 是否保存成功
	 */
	ResponseResult store(File file) throws FileNotFoundException;

	/**
	 * 查询文件
	 *
	 * @param fileId 文件ID
	 * @return
	 */
	ResponseResult queryFile(String fileId) throws IOException;

}
