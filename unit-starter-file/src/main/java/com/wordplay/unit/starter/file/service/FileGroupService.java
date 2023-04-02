package com.wordplay.unit.starter.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.file.entity.FileGroup;

public interface FileGroupService extends IService<FileGroup> {

	Leaf<FileGroup> page(FileGroup fileGroup);

	Long saveGroupAndInfoList(FileGroup fileGroup);

}
