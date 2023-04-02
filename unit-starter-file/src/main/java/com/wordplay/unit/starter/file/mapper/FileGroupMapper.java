package com.wordplay.unit.starter.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wordplay.unit.starter.file.entity.FileGroup;

public interface FileGroupMapper extends BaseMapper<FileGroup> {

	Page<FileGroup> page(Page<FileGroup> page, FileGroup fileGroup);

}