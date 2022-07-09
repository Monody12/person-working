package com.example.netdisk.service;

import com.example.netdisk.entity.File;
import com.example.netdisk.entity.po.FileInfo;
import com.example.netdisk.entity.vo.FileVo;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author monody
 * @date 2022/4/27 9:46 下午
 */
public interface FileService {

    /**
     * 插入一个文件的信息
     * @param file
     * @return
     */
    public abstract int insert(File file);

    /**
     * 插入一些文件信息
     * @param files
     * @return
     */
    public abstract int insertMany(List<File> files);

    /**
     * 查询一个文件的信息
     * @param fileId
     * @return
     */
    public abstract File queryById(long fileId);

    public abstract List<File> queryByIds(String username,List<Long> list);

    public abstract List<FileVo> queryByIdVo(String username,List<Long> list);

    public abstract List<FileInfo> queryFileInfos(List<Long> list);

    /**
     * 查询文件下载地址
     * @param fileId
     * @return
     */
    public abstract String queryDownloadAdd(String fileId);

    /**
     * 查询一个用户指定路径下的文件，使用分页
     * @param username
     * @param path
     * @param page
     * @param size
     * @return
     */
    public abstract PageInfo<FileVo> queryByPathVo(String username, String path, int page, int size ,String field,String order);


    /**
     * 模糊查询路径
     * 用于递归查找当前文件夹下的文件
     * @param username
     * @param path
     * @return
     */
    public abstract List<File> queryLikePath(String username, String path);

    /**
     * 更新一个文件的信息
     * @param file
     * @return
     */
    public abstract int update(File file);

    /**
     * 删除一个文件
     * @param fileId
     * @return
     * @throws IOException
     */
    public abstract int delete(long fileId) throws IOException;

    public abstract int batchUpdate(String username, String setColumn, Object value, String whereColumn, List list);

    public abstract int batchUpdate(List<File> list);

    /**
     * 用于随机生成新建文件用的相对路径
     * 日期+UUID
     * @return 相对路径
     */
    public String generateRealPath();

    /**
     * 从相对路径中取出 UUID
     * @param realPath
     * @return
     */
    String getUuidFromRealPath(String realPath);

    /**
     * 获取一个文件在磁盘上的绝对路径
     * @param fileId
     * @return
     */
    public String getAbsolutePath(String fileId);

    /**
     * 将上传的文件内容或者指定字符串写入磁盘
     *
     * @param file 上传的文件对象
     * @param content 保存文件的内容 （该选项与file二选一传入）
     * @param realPath 保存文件的绝对路径
     * @return 文件绝对路径
     */
    public abstract String writeHardDisk(MultipartFile file,String content,String realPath) throws IOException;

    /**
     * 读取磁盘上的文件
     * @param realPath 与存储位置的相对路径
     * @return 文本内容
     * @throws IOException
     */
    public abstract String readHardDisk(String realPath) throws IOException;


    /**
     * 从磁盘中物理删除一个文件
     * @param realPath 文件的realPath
     * @throws IOException
     */
    public void deleteHardDisk(String realPath) throws IOException;
}
