package com.wordplay.unit.starter.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.file.entity.FileInfo;

public interface FileInfoService extends IService<FileInfo> {

	Leaf<FileInfo> page(FileInfo fileInfo);

}
