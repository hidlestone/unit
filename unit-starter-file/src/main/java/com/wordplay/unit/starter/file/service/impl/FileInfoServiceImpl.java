package com.wordplay.unit.starter.file.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.file.entity.FileInfo;
import com.wordplay.unit.starter.file.mapper.FileInfoMapper;
import com.wordplay.unit.starter.file.service.FileInfoService;
import org.springframework.stereotype.Service;

@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {

	@Override
	public Leaf<FileInfo> page(FileInfo fileInfo) {
		Page<FileInfo> page = new Page<>(fileInfo.getPageNum(), fileInfo.getPageSize());
		page = this.baseMapper.page(page, fileInfo);
		return LeafPageUtil.pageToLeaf(page);
	}

}
