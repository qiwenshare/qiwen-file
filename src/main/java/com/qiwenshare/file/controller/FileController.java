package com.qiwenshare.file.controller;

import com.alibaba.fastjson.JSON;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qiwenshare.common.exception.NotLoginException;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.common.result.RestResult;
import com.qiwenshare.common.operation.FileOperation;
import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.file.anno.MyLog;
import com.qiwenshare.file.api.*;
import com.qiwenshare.file.component.FileDealComp;
import com.qiwenshare.file.config.es.FileSearch;
import com.qiwenshare.file.domain.*;
import com.qiwenshare.file.dto.*;
import com.qiwenshare.file.dto.file.*;
import com.qiwenshare.file.vo.file.FileListVo;
import com.qiwenshare.ufo.factory.UFOFactory;
import com.qiwenshare.ufo.operation.rename.domain.RenameFile;
import com.qiwenshare.ufo.util.PathUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.qiwenshare.common.util.FileUtil.getFileExtendsByType;

@Tag(name = "file", description = "该接口为文件接口，主要用来做一些文件的基本操作，如创建目录，删除，移动，复制等。")
@RestController
@Slf4j
@RequestMapping("/file")
public class FileController {

    @Resource
    IFileService fileService;
    @Resource
    IUserService userService;
    @Resource
    IUserFileService userFileService;
    @Resource
    UFOFactory ufoFactory;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Resource
    FileDealComp fileDealComp;

    public static Executor executor = Executors.newFixedThreadPool(20);

    public static final String CURRENT_MODULE = "文件接口";


    @Operation(summary = "创建文件", description = "目录(文件夹)的创建", tags = {"file"})
    @RequestMapping(value = "/createfile", method = RequestMethod.POST)
    @MyLog(operation = "创建文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> createFile(@RequestBody CreateFileDTO createFileDto, @RequestHeader("token") String token) {

        UserBean sessionUserBean = userService.getUserBeanByToken(token);

        List<UserFile> userFiles = userFileService.selectUserFileByNameAndPath(createFileDto.getFileName(), createFileDto.getFilePath(), sessionUserBean.getUserId());
        if (userFiles != null && !userFiles.isEmpty()) {
            return RestResult.fail().message("同名文件已存在");
        }

        UserFile userFile = new UserFile();
        userFile.setUserId(sessionUserBean.getUserId());
        userFile.setFileName(createFileDto.getFileName());
        userFile.setFilePath(createFileDto.getFilePath());
        userFile.setDeleteFlag(0);
        userFile.setIsDir(1);
        userFile.setUploadTime(DateUtil.getCurrentTime());

        userFileService.save(userFile);
        fileDealComp.uploadESByUserFileId(userFile.getUserFileId());
        return RestResult.success();
    }

    @Operation(summary = "文件搜索", description = "文件搜索", tags = {"file"})
    @GetMapping(value = "/search")
    @MyLog(operation = "文件搜索", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<SearchHits<FileSearch>> searchFile(SearchFileDTO searchFileDTO, @RequestHeader("token") String token) {
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        HighlightBuilder.Field allHighLight = new HighlightBuilder.Field("*").preTags("<span class='keyword'>")
                .postTags("</span>");

        queryBuilder.withHighlightFields(allHighLight);

        //设置分页
        int currentPage = (int)searchFileDTO.getCurrentPage() - 1;
        int pageCount = (int)(searchFileDTO.getPageCount() == 0 ? 10 : searchFileDTO.getPageCount());
        String order = searchFileDTO.getOrder();
        Sort.Direction direction = null;
        if (searchFileDTO.getDirection() == null) {
            direction = Sort.Direction.DESC;
        } else if ("asc".equals(searchFileDTO.getDirection())) {
            direction = Sort.Direction.ASC;
        } else if ("desc".equals(searchFileDTO.getDirection())) {
            direction = Sort.Direction.DESC;
        } else {
            direction = Sort.Direction.DESC;
        }
        if (order == null) {
            queryBuilder.withPageable(PageRequest.of(currentPage, pageCount));
        } else {
            queryBuilder.withPageable(PageRequest.of(currentPage, pageCount, Sort.by(direction, order)));
        }

        queryBuilder.withQuery(QueryBuilders.boolQuery()
//                .must(QueryBuilders.matchQuery("fileName", searchFileDTO.getFileName()))
                .must(QueryBuilders.multiMatchQuery(searchFileDTO.getFileName(),"fileName", "content"))
                .must(QueryBuilders.termQuery("userId", sessionUserBean.getUserId()))
                );
        SearchHits<FileSearch> search = elasticsearchRestTemplate.search(queryBuilder.build(), FileSearch.class);

        return RestResult.success().data(search);
    }

    @Operation(summary = "文件重命名", description = "文件重命名", tags = {"file"})
    @RequestMapping(value = "/renamefile", method = RequestMethod.POST)
    @MyLog(operation = "文件重命名", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> renameFile(@RequestBody RenameFileDTO renameFileDto, @RequestHeader("token") String token) {

        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        UserFile userFile = userFileService.getById(renameFileDto.getUserFileId());

        List<UserFile> userFiles = userFileService.selectUserFileByNameAndPath(renameFileDto.getFileName(), renameFileDto.getFilePath(), sessionUserBean.getUserId());
        if (userFiles != null && !userFiles.isEmpty()) {
            return RestResult.fail().message("同名文件已存在");
        }

        if (1 == userFile.getIsDir()) {
            LambdaUpdateWrapper<UserFile> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.set(UserFile::getFileName, renameFileDto.getFileName())
                    .set(UserFile::getUploadTime, DateUtil.getCurrentTime())
                    .eq(UserFile::getUserFileId, renameFileDto.getUserFileId());
            userFileService.update(lambdaUpdateWrapper);
            userFileService.replaceUserFilePath(userFile.getFilePath() + renameFileDto.getFileName() + "/",
                    userFile.getFilePath() + userFile.getFileName() + "/", sessionUserBean.getUserId());
        } else {
            FileBean file = fileService.getById(userFile.getFileId());
            if (file.getStorageType() == 1) {

                String fileUrl = file.getFileUrl();
                String newFileUrl = fileUrl.replace(userFile.getFileName(), renameFileDto.getFileName());
                RenameFile renameFile = new RenameFile();
                renameFile.setSrcName(fileUrl.substring(1));
                renameFile.setDestName(newFileUrl.substring(1));
                ufoFactory.getRenamer(file.getStorageType()).rename(renameFile);
                LambdaUpdateWrapper<FileBean> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper
                        .set(FileBean::getFileUrl, newFileUrl)
                        .eq(FileBean::getFileId, file.getFileId());
                fileService.update(lambdaUpdateWrapper);

                LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                userFileLambdaUpdateWrapper
                        .set(UserFile::getFileName, renameFileDto.getFileName())
                        .set(UserFile::getUploadTime, DateUtil.getCurrentTime())
                        .eq(UserFile::getUserFileId, renameFileDto.getUserFileId());
                userFileService.update(userFileLambdaUpdateWrapper);
            } else {
                LambdaUpdateWrapper<UserFile> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.set(UserFile::getFileName, renameFileDto.getFileName())
                        .set(UserFile::getUploadTime, DateUtil.getCurrentTime())
                        .eq(UserFile::getUserFileId, renameFileDto.getUserFileId());
                userFileService.update(lambdaUpdateWrapper);
            }

        }
        fileDealComp.uploadESByUserFileId(renameFileDto.getUserFileId());
        return RestResult.success();
    }




    @Operation(summary = "获取文件列表", description = "用来做前台列表展示", tags = {"file"})
    @RequestMapping(value = "/getfilelist", method = RequestMethod.GET)
    @ResponseBody
    public RestResult getFileList(FileListDTO fileListDto, @RequestHeader("token") String token){

        UserFile userFile = new UserFile();

        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        if (userFile == null) {
            return RestResult.fail();

        }
        userFile.setUserId(sessionUserBean.getUserId());


        List<FileListVo> fileList = null;
        userFile.setFilePath(PathUtil.urlDecode(fileListDto.getFilePath()));
        if (fileListDto.getCurrentPage() == null || fileListDto.getPageCount() == null) {
            fileList = userFileService.userFileList(userFile, 0L, 10L);
        } else {
            Long beginCount = (fileListDto.getCurrentPage() - 1) * fileListDto.getPageCount();

            fileList = userFileService.userFileList(userFile, beginCount, fileListDto.getPageCount()); //fileService.selectFileListByPath(fileBean);

        }

        LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userFileLambdaQueryWrapper.eq(UserFile::getUserId, userFile.getUserId())
                .eq(UserFile::getFilePath, userFile.getFilePath())
                .eq(UserFile::getDeleteFlag, 0);
        int total = userFileService.count(userFileLambdaQueryWrapper);

        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("list", fileList);


        return RestResult.success().data(map);

    }

    @Operation(summary = "批量删除文件", description = "批量删除文件", tags = {"file"})
    @RequestMapping(value = "/batchdeletefile", method = RequestMethod.POST)
    @MyLog(operation = "批量删除文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> deleteImageByIds(@RequestBody BatchDeleteFileDTO batchDeleteFileDto, @RequestHeader("token") String token) {

        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        List<UserFile> userFiles = JSON.parseArray(batchDeleteFileDto.getFiles(), UserFile.class);
        DigestUtils.md5Hex("data");
        for (UserFile userFile : userFiles) {

            userFileService.deleteUserFile(userFile.getUserFileId(),sessionUserBean.getUserId());
            fileDealComp.deleteESByUserFileId(userFile.getUserFileId());
        }

        return RestResult.success().message("批量删除文件成功");
    }

    @Operation(summary = "删除文件", description = "可以删除文件或者目录", tags = {"file"})
    @RequestMapping(value = "/deletefile", method = RequestMethod.POST)
    @MyLog(operation = "删除文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult deleteFile(@RequestBody DeleteFileDTO deleteFileDto, @RequestHeader("token") String token) {

        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        userFileService.deleteUserFile(deleteFileDto.getUserFileId(), sessionUserBean.getUserId());
        fileDealComp.deleteESByUserFileId(deleteFileDto.getUserFileId());

        return RestResult.success();

    }

    @Operation(summary = "解压文件", description = "压缩功能为体验功能，目前持续优化中。", tags = {"file"})
    @RequestMapping(value = "/unzipfile", method = RequestMethod.POST)
    @MyLog(operation = "解压文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> unzipFile(@RequestBody UnzipFileDTO unzipFileDto, @RequestHeader("token") String token) {

        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        String zipFileUrl = PathUtil.getStaticPath() + unzipFileDto.getFileUrl();
        File file = FileOperation.newFile(zipFileUrl);
        String extendName = FileUtil.getFileExtendName(zipFileUrl);
        String unzipUrl = zipFileUrl.replace("." + extendName, "");
        String[] arr = unzipFileDto.getFileUrl().split("\\.");
        if (arr.length <= 1) {
            return RestResult.fail().message("文件名格式错误！");
        }
        List<String> fileEntryNameList = new ArrayList<>();
        if ("zip".equals(arr[1])) {
            fileEntryNameList = FileOperation.unzip(file, unzipUrl);
        } else if ("rar".equals(arr[1])) {
            try {
                fileEntryNameList = FileOperation.unrar(file, unzipUrl);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("rar解压失败" + e);
                return RestResult.fail().message("rar解压失败！");


            }
        } else {
            return RestResult.fail().message("不支持的文件格式！");
        }

        List<FileBean> fileBeanList = new ArrayList<>();

        for (int i = 0; i < fileEntryNameList.size(); i++){
            String entryName = fileEntryNameList.get(i);
            log.info("文件名："+ entryName);
            executor.execute(() -> {
                String totalFileUrl = unzipUrl + entryName;
                File currentFile = FileOperation.newFile(totalFileUrl);

                FileBean tempFileBean = new FileBean();
                UserFile userFile = new UserFile();

                userFile.setUploadTime(DateUtil.getCurrentTime());
                userFile.setUserId(sessionUserBean.getUserId());
                userFile.setFilePath(FileUtil.pathSplitFormat(unzipFileDto.getFilePath() + entryName.replace(currentFile.getName(), "")).replace("\\", "/"));

                if (currentFile.isDirectory()){

                    userFile.setIsDir(1);

                    userFile.setFileName(currentFile.getName());
                    tempFileBean.setTimeStampName(currentFile.getName());
                }else{

                    userFile.setIsDir(0);
                    userFile.setExtendName(FileUtil.getFileExtendName(totalFileUrl));
                    userFile.setFileName(FileUtil.getFileNameNotExtend(currentFile.getName()));
                    tempFileBean.setFileSize(currentFile.length());
                    tempFileBean.setTimeStampName(FileUtil.getFileNameNotExtend(currentFile.getName()));
                    tempFileBean.setFileUrl(File.separator + (currentFile.getPath()).replace(PathUtil.getStaticPath(), ""));
                    tempFileBean.setPointCount(1);
                    tempFileBean.setStorageType(0);
                    fileService.save(tempFileBean);
                }

                userFile.setFileId(tempFileBean.getFileId());
                userFile.setDeleteFlag(0);
                userFileService.save(userFile);
            });

        }
        return RestResult.success();

    }


    @Operation(summary = "文件移动", description = "可以移动文件或者目录", tags = {"file"})
    @RequestMapping(value = "/movefile", method = RequestMethod.POST)
    @MyLog(operation = "文件移动", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> moveFile(@RequestBody MoveFileDTO moveFileDto, @RequestHeader("token") String token) {

        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        String oldfilePath = moveFileDto.getOldFilePath();
        String newfilePath = moveFileDto.getFilePath();
        String fileName = moveFileDto.getFileName();
        String extendName = moveFileDto.getExtendName();

        userFileService.updateFilepathByFilepath(oldfilePath, newfilePath, fileName, extendName, sessionUserBean.getUserId());
        return RestResult.success();

    }

    @Operation(summary = "批量移动文件", description = "可以同时选择移动多个文件或者目录", tags = {"file"})
    @RequestMapping(value = "/batchmovefile", method = RequestMethod.POST)
    @MyLog(operation = "批量移动文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> batchMoveFile(@RequestBody BatchMoveFileDTO batchMoveFileDto, @RequestHeader("token") String token) {

        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        String files = batchMoveFileDto.getFiles();
        String newfilePath = batchMoveFileDto.getFilePath();

        List<UserFile> fileList = JSON.parseArray(files, UserFile.class);

        for (UserFile userFile : fileList) {
            userFileService.updateFilepathByFilepath(userFile.getFilePath(), newfilePath, userFile.getFileName(), userFile.getExtendName(), sessionUserBean.getUserId());
        }

        return RestResult.success().data("批量移动文件成功");

    }



    @Operation(summary = "通过文件类型选择文件", description = "该接口可以实现文件格式分类查看", tags = {"file"})
    @RequestMapping(value = "/selectfilebyfiletype", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<List<Map<String, Object>>> selectFileByFileType(int fileType, Long currentPage, Long pageCount, @RequestHeader("token") String token) {

        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        long userId = sessionUserBean.getUserId();

        List<FileListVo> fileList = new ArrayList<>();
        Long beginCount = 0L;
        if (pageCount == null || currentPage == null) {
            beginCount = 0L;
            pageCount = 10L;
        } else {
            beginCount = (currentPage - 1) * pageCount;
        }

        Long total = 0L;
        if (fileType == FileUtil.OTHER_TYPE) {

            List<String> arrList = new ArrayList<>();
            arrList.addAll(Arrays.asList(FileUtil.DOC_FILE));
            arrList.addAll(Arrays.asList(FileUtil.IMG_FILE));
            arrList.addAll(Arrays.asList(FileUtil.VIDEO_FILE));
            arrList.addAll(Arrays.asList(FileUtil.MUSIC_FILE));

            fileList = userFileService.selectFileNotInExtendNames(arrList,beginCount, pageCount, userId);
            total = userFileService.selectCountNotInExtendNames(arrList,beginCount, pageCount, userId);
        } else {
            fileList = userFileService.selectFileByExtendName(getFileExtendsByType(fileType), beginCount, pageCount,userId);
            total = userFileService.selectCountByExtendName(getFileExtendsByType(fileType), beginCount, pageCount,userId);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("list",fileList);
        map.put("total", total);
        return RestResult.success().data(map);

    }

    @Operation(summary = "获取文件树", description = "文件移动的时候需要用到该接口，用来展示目录树", tags = {"file"})
    @RequestMapping(value = "/getfiletree", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<TreeNode> getFileTree(@RequestHeader("token") String token) {
        RestResult<TreeNode> result = new RestResult<TreeNode>();

        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }

        List<UserFile> userFileList = userFileService.selectFilePathTreeByUserId(sessionUserBean.getUserId());
        TreeNode resultTreeNode = new TreeNode();
        resultTreeNode.setLabel("/");
        resultTreeNode.setId(0L);
        long id = 1;
        for (int i = 0; i < userFileList.size(); i++){
            UserFile userFile = userFileList.get(i);
            String filePath = userFile.getFilePath() + userFile.getFileName() + "/";

            Queue<String> queue = new LinkedList<>();

            String[] strArr = filePath.split("/");
            for (int j = 0; j < strArr.length; j++){
                if (!"".equals(strArr[j]) && strArr[j] != null){
                    queue.add(strArr[j]);
                }

            }
            if (queue.size() == 0){
                continue;
            }

            resultTreeNode = fileDealComp.insertTreeNode(resultTreeNode, id++, "/" , queue);


        }
        List<TreeNode> treeNodeList = resultTreeNode.getChildren();
        Collections.sort(treeNodeList, new Comparator<TreeNode>() {
            @Override
            public int compare(TreeNode o1, TreeNode o2) {
                long i = o1.getId() - o2.getId();
                return (int) i;
            }
        });
        result.setSuccess(true);
        result.setData(resultTreeNode);
        return result;

    }



}
