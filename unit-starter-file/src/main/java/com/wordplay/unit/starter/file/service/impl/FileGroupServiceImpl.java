package com.wordplay.unit.starter.file.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.file.entity.FileGroup;
import com.wordplay.unit.starter.file.entity.FileInfo;
import com.wordplay.unit.starter.file.mapper.FileGroupMapper;
import com.wordplay.unit.starter.file.service.FileGroupService;
import com.wordplay.unit.starter.file.service.FileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileGroupServiceImpl extends ServiceImpl<FileGroupMapper, FileGroup> implements FileGroupService {

	@Autowired
	private FileInfoService fileInfoService;

	@Override
	public Leaf<FileGroup> page(FileGroup fileGroup) {
		Page<FileGroup> page = new Page<>(fileGroup.getPageNum(), fileGroup.getPageSize());
		page = this.baseMapper.page(page, fileGroup);
		return LeafPageUtil.pageToLeaf(page);
	}

	@Override
	public Long saveGroupAndInfoList(FileGroup fileGroup) {
		save(fileGroup);
		List<FileInfo> fileInfoList = fileGroup.getFileInfos();
		fileInfoList.forEach(dtl -> dtl.setFileGroupId(fileGroup.getId()));
		fileInfoService.saveBatch(fileInfoList);
		return fileGroup.getId();
	}

}
