package com.wordplay.unit.starter.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wordplay.unit.starter.file.entity.FileInfo;

public interface FileInfoMapper extends BaseMapper<FileInfo> {

	Page<FileInfo> page(Page<FileInfo> page, FileInfo fileInfo);

}