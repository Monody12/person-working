package com.example.netdisk.service.impl;

import com.example.netdisk.entity.File;
import com.example.netdisk.entity.Folder;
import com.example.netdisk.entity.dto.BatchOperation;
import com.example.netdisk.entity.po.FileCnt;
import com.example.netdisk.entity.po.FileInfo;
import com.example.netdisk.entity.vo.FileVo;
import com.example.netdisk.mapper.FileMapper;
import com.example.netdisk.service.FileService;
import com.example.netdisk.service.FolderService;
import com.example.netdisk.utils.FileUtil;
import com.example.netdisk.utils.UUIDUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author monody
 * @date 2022/4/27 10:28 下午
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Autowired
    FileMapper fileMapper;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    FileService fileService;
    @Autowired
    FolderService folderService;
    @Value("${netdisk.upload.storage-root}")
    private String storageRoot;

    @Override
    public int insert(File file) {
        return fileMapper.insert(file);
    }

    @Override
    public int insertMany(List<File> files) {
        return fileMapper.insertMany(files);
    }

    @Override
    public File queryById(long fileId) {
        return fileMapper.selectOne(fileId);
    }

    @Override
    public List<File> queryByIds(String username, List<Long> list) {
        if (list.size() == 0) {
            return new ArrayList<>();
        }
        BatchOperation batchOperation = new BatchOperation(username,list);
        return fileMapper.selectMany(batchOperation);
    }

    @Override
    public List<FileVo> queryByIdVo(String username, List<Long> list) {
        if (list.isEmpty()){
            return new ArrayList<>();
        }
        BatchOperation batchOperation = new BatchOperation(username,list);
        return fileMapper.selectManyVo(batchOperation);
    }

    @Override
    public List<FileInfo> queryFileInfos( List<Long> list) {
        if (list.isEmpty()){
            return new ArrayList<>();
        }
        List<String> collect = list.parallelStream().map(String::valueOf).collect(Collectors.toList());
        return mongoTemplate.find(Query.query(Criteria.where("_id").in(collect)),FileInfo.class);
    }

    @Override
    public String queryDownloadAdd(String fileId) {
        Query query = Query.query(Criteria.where("_id").is(fileId));
        return mongoTemplate.findOne(query, FileInfo.class).getRealPath();
    }

    @Override
    public PageInfo<FileVo> queryByPathVo(String username, String path, int page, int size ,String field,String order) {
        PageHelper.startPage(page, size);
        BatchOperation batchOperation = new BatchOperation(username,path,field,order);
        List<FileVo> files = fileMapper.selectByPathVo(batchOperation);
        return new PageInfo<>(files);
    }


    @Override
    public List<File> queryLikePath(String username, String path) {
        return fileMapper.selectLikePath(username, path);
    }

    @Override
    public int update(File file) {
        // 若修改文件名，则需要读取文件的扩展名，然后修改文件的类型
        if (file.getName()!=null){
            String ext = FileUtil.getType(file.getName());
            file.setType(ext);
        }
        return fileMapper.update(file);
    }

    @Override
    // TODO bug修复以后删除回滚
    @Transactional(rollbackFor = Exception.class)
    public int delete(long fileId) throws IOException {
        int i = fileMapper.delete(fileId);
        if (i == 0) {
            return 0;
        }
        // 查询条件
        // 注意：查询MongoDB主键一定要转为String
        Query queryId = Query.query(Criteria.where("_id").is(String.valueOf(fileId)));
        // 查询出当前文件的真实路径
        FileInfo fileInfo = mongoTemplate.findOne(queryId, FileInfo.class);
        // 查询出逻辑引用该文件的数量
        Query queryPath = Query.query(Criteria.where("_id").is(fileInfo.getRealPath()));
        FileCnt fileCnt = mongoTemplate.findOne(queryPath, FileCnt.class);
        // 引用这个文件的数量
        long num = fileCnt.getNum();
        // 目前只有一个引用，物理删除该文件
        if (num == 1) {
            // 删除文件
            deleteHardDisk(fileInfo.getRealPath());
            // 删除引用
            DeleteResult remove = mongoTemplate.remove(queryPath, FileCnt.class);
            log.debug("FileCnt删除结果：{}",remove.getDeletedCount());
        } else {
            // 引用数量减一
            mongoTemplate.updateFirst(queryPath, new Update().inc("num", -1), FileCnt.class);
        }
        // 删除该文件在MongoDB上的扩展信息
        mongoTemplate.remove(queryId, FileInfo.class);
        return i;
    }

    @Override
    public int batchUpdate(String username, String setColumn, Object value, String whereColumn, List list) {
        BatchOperation batchOperation = new BatchOperation(username,setColumn,value,whereColumn,list);
        log.debug("batchUpdate：{}",batchOperation);
        return fileMapper.updateMany(batchOperation);
    }

    @Override
    public int batchUpdate(List<File> list) {
        int cnt = 0;
        for (File file : list) {
            cnt += fileMapper.update(file);
        }
        return cnt;
    }

    @Override
    public String generateRealPath() {
        // 获取并解析当前时间
        LocalDate now = LocalDate.now();
        int year = now.getYear(), month = now.getMonthValue(), day = now.getDayOfMonth();
        // 计算出写磁盘的相对路径
        return year + java.io.File.separator + month + java.io.File.separator + day + java.io.File.separator + UUIDUtil.get();
    }

    @Override
    public String getUuidFromRealPath(String realPath){
        int index = realPath.lastIndexOf('/');
        if (index==-1){
            throw new IllegalArgumentException("该字符串不是realPath："+ realPath);
        }
        return realPath.substring(index+1);
    }

    @Override
    public String getAbsolutePath(String fileId) {
        FileInfo fileInfo = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)), FileInfo.class);
        if (fileInfo==null){
            throw new NullPointerException("找不到该文件");
        }
        return storageRoot + fileInfo.getRealPath();
    }

    @Override
    public String writeHardDisk(MultipartFile file, String content,String realPath) throws IOException {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
        // 随机生成存储文件用的相对路径
        if (realPath==null) {
            realPath = fileService.generateRealPath();
        }
        // 计算出写磁盘的绝对路径
        String absolutePath = storageRoot + realPath;
        String fileDirPath = null;
        if (isWindows){
            // 如果操作系统为Windows 则需要将 / 替换为 \
            absolutePath = absolutePath.replace('/', '\\');
            fileDirPath = absolutePath.substring(0,absolutePath.lastIndexOf('\\')+1);
        }else {
            fileDirPath = absolutePath.substring(0, absolutePath.lastIndexOf('/') + 1);
        }
        // 如果文件夹不存在则进行创建
        java.io.File fileDir = new java.io.File(fileDirPath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        // 写文件需要用绝对路径
        java.io.File newFile = new java.io.File(absolutePath);
        log.debug("写入磁盘的绝对路径：{}",absolutePath);
        boolean newFile1 = newFile.createNewFile();
        if (!newFile1) {
            log.debug("文件已存在");
        }
        // 文件流不为空
        if (file != null) {
            file.transferTo(newFile);
        }
        // 要写入的字符串不为空
        else if (content != null) {
            FileWriter fileWriter = new FileWriter(newFile);
            fileWriter.write(content);
            fileWriter.close();
        }
        return realPath;
    }

    @Override
    public void deleteHardDisk(String realPath) throws IOException {
        Path path = Paths.get(storageRoot+realPath);
        Files.delete(path);
    }

    @Override
    public String readHardDisk(String realPath) throws IOException {
        // 计算出写磁盘的绝对路径
        String absolutePath = storageRoot + realPath;
        Path path = Paths.get(absolutePath);
        Scanner scanner = new Scanner(path);
        // 文件内容
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()){
            sb.append(scanner.nextLine());
            sb.append('\n');
        }
        scanner.close();
        return sb.toString();
    }


    public static void main(String[] args) throws IOException {
        String folderPath = "C:\\Users\\Administrator\\Desktop\\test\\";
        java.io.File folder = new java.io.File(folderPath);
        folder.mkdirs();
        String absolutePath = folderPath + "test.txt";
        java.io.File file = new java.io.File(absolutePath);
        boolean newFile = file.createNewFile();
        System.out.println(newFile);
    }
}
