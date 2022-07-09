package com.example.netdisk.mapper;

import com.example.netdisk.entity.Folder;
import com.example.netdisk.entity.dto.BatchOperation;
import com.example.netdisk.entity.vo.FolderVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author monody
 * @date 2022/4/28 8:31 下午
 */
@Mapper
public interface FolderMapper {
    int insert(Folder folder);

    int insertMany(List<Folder> list);

    int delete(String username,String id);

    int deleteMany(BatchOperation batchOperation);

    int update(Folder folder);

    int updateMany(BatchOperation batchOperation);

    Folder selectOne(String username,long folderId);

    List<Folder> selectMany(BatchOperation batchOperation);

    List<FolderVo> selectManyVo(BatchOperation batchOperation);

    /**
     * 查找某个目录下的所有文件夹
     * @param username
     * @param path
     * @return
     */
    List<Folder> selectByPath(String username,String path);

    List<FolderVo> selectByPathVo(String username, String path);

    /**
     * 查找某个目录下的指定文件夹
     * @param username
     * @param fatherPath
     * @param name
     * @return
     */
    Folder selectByName(String username,String fatherPath,String name);

    List<Folder> selectLikePath(String username,String path);
}
