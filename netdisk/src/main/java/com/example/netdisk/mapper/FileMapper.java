package com.example.netdisk.mapper;

import com.example.netdisk.entity.File;
import com.example.netdisk.entity.dto.BatchOperation;
import com.example.netdisk.entity.vo.FileVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author monody
 * @date 2022/4/28 7:50 下午
 */
@Mapper
public interface FileMapper {
    int insert(File file);

    int insertMany(List<File>list);

    int delete(long fileId);

    int update(File file);

    File selectOne(long fileId);
    List<File> selectMany(BatchOperation batchOperation);
    List<FileVo> selectManyVo(BatchOperation batchOperation);
    List<File> selectByPath(BatchOperation batchOperation);
    List<FileVo> selectByPathVo(BatchOperation batchOperation);
    List<File> selectLikePath(String username,String path);

    int updateMany(BatchOperation batchOperation);

}
