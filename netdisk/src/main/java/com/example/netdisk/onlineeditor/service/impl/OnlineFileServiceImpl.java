package com.example.netdisk.onlineeditor.service.impl;

import com.example.netdisk.entity.po.FileCnt;
import com.example.netdisk.entity.po.FileInfo;
import com.example.netdisk.onlineeditor.service.OnlineFileService;
import com.example.netdisk.service.FileService;
import com.example.netdisk.utils.FileUtil;
import com.example.netdisk.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;

/**
 * @ClassName OnlineEditorServiceImpl
 * @Description TODO
 * @Author monody
 * @Date 2022/7/12 4:52 PM
 * Version 1.0
 */
@Service
@Slf4j
public class OnlineFileServiceImpl implements OnlineFileService {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    FileService fileService;
    @Value("${netdisk.upload.storage-root}")
    String UPLOAD_STORAGE_ROOT;
    @Value("${netdisk.edit.storage-root}")
    String EDIT_STORAGE_ROOT;
    @Value("${netdisk.edit.big-size}")
    int MAX_EDIT_FILE_SIZE;

    /**
     * 获取文件的字符串内容
     *
     * @param fileId
     * @return
     */
    @Override
    public String getFileContent(String fileId) {
        // 在MongoDB中查询出文件信息
        FileInfo fileInfo = mongoTemplate.findById(fileId, FileInfo.class);
        if (fileInfo == null) {
            return null;
        }
        // 计算出文件的绝对路径
        String absolutePath;
        if ("edit".equals(fileInfo.getType())) {
            absolutePath = EDIT_STORAGE_ROOT + fileInfo.getRealPath();
        } else {
            absolutePath = UPLOAD_STORAGE_ROOT + fileInfo.getRealPath();
        }
        return FileUtil.readFile(absolutePath);
    }

    /**
     * 用户想要编辑文件
     *
     * @param fileId
     * @return
     */
    @Override
    public String editFile(String fileId) {
        // 在MongoDB中查询出文件信息
        FileInfo fileInfo = mongoTemplate.findById(fileId, FileInfo.class);
        if (fileInfo == null) {
            throw new RuntimeException("文件不存在");
        }
        // 判断文件大小是否超过最大值
//        if (fileInfo.getSize() > MAX_EDIT_FILE_SIZE) {
//            throw new RuntimeException("文件大小超过最大值");
//        }
        // 文件属性是否为上传
        if ("upload".equals(fileInfo.getType())) {
            // 转为可编辑文件
            convertToEdit(fileInfo);
            // 将转换后的新路径查询出来
            fileInfo = mongoTemplate.findById(fileId, FileInfo.class);
        }
        // 读取文件内容然后返回
        return FileUtil.readFile(EDIT_STORAGE_ROOT + fileInfo.getRealPath());
    }

    /**
     * 将上传文件转为可编辑文件
     *
     * @param fileInfo
     */
    @Override
    public void convertToEdit(FileInfo fileInfo) {
        // 查询fileCnt确定文件是复制还是移动
        FileCnt fileCnt = mongoTemplate.findOne(new Query(Criteria.where("_id").is(fileInfo.getRealPath())), FileCnt.class);
        // 决定文件是复制还是移动
        boolean isMove = fileCnt.getNum() == 1;
        // 生成目的文件的绝对路径
        String dscPath = createEditDirAbsolutePath() + UUIDUtil.get();
        // 移动文件
        if (isMove) {
            // 在磁盘中移动文件
            FileUtil.moveFile(UPLOAD_STORAGE_ROOT + fileInfo.getRealPath(), EDIT_STORAGE_ROOT + dscPath);
            log.debug("移动 srcPath: {}  destPath: {}", UPLOAD_STORAGE_ROOT + fileInfo.getRealPath(), EDIT_STORAGE_ROOT + dscPath);
            // 从MongoDB中删除计数器
            mongoTemplate.remove(new Query(Criteria.where("_id").is(fileInfo.getRealPath())), FileCnt.class);
        } else {
            // 在磁盘中复制文件
            FileUtil.copyFile(UPLOAD_STORAGE_ROOT + fileInfo.getRealPath(), EDIT_STORAGE_ROOT + dscPath);
            log.debug("复制 srcPath: {}  destPath: {}", UPLOAD_STORAGE_ROOT + fileInfo.getRealPath(), EDIT_STORAGE_ROOT + dscPath);
            // mongoDB中计数器引用值-1
            mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(fileInfo.getRealPath())), new Update().inc("num", -1), FileCnt.class);
        }
        // 在MongoDB中更新文件信息 重设文件类型为编辑类型，文件路径。取消文件md5、文件大小（以后文件修改后文件大小存储在mysql中）
        mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(fileInfo.getFileId())),
                new Update().set("realPath", dscPath).set("type", "edit")
                        .unset("md5").unset("size"), FileInfo.class);

    }

    /**
     * 创建出来的是相对路径（不含EDIT_STORAGE_ROOT）
     * 示例：2022/7/15
     * 根据当前时间日期生成一个路径（以 / 分隔）
     * 该路径位于编辑文件目录下
     * 该路径对应的文件夹也会被创建（创建时若是Windows系统，则也是 \ 符号，在读盘的时候会做统一转换）
     *
     * @return
     */
    @Override
    public String createEditDirAbsolutePath() {
        LocalDateTime now = LocalDateTime.now();
        String dirPath = now.getYear() + "/" + now.getMonthValue() + "/" + now.getDayOfMonth() + "/";
        File dir = new File(EDIT_STORAGE_ROOT + FileUtil.pathConvert(dirPath));
        boolean mkdirs = dir.exists() || dir.mkdirs();
        if (!mkdirs) {
            throw new RuntimeException("创建文件夹失败");
        }
        return dirPath;
    }

}
