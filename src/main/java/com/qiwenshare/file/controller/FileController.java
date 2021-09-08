package com.qiwenshare.file.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qiwenshare.common.anno.MyLog;
import com.qiwenshare.common.exception.NotLoginException;
import com.qiwenshare.common.result.RestResult;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.file.advice.QiwenException;
import com.qiwenshare.file.api.IFileService;
import com.qiwenshare.file.api.IUserFileService;
import com.qiwenshare.file.api.IUserService;
import com.qiwenshare.file.component.FileDealComp;
import com.qiwenshare.file.config.es.FileSearch;
import com.qiwenshare.file.domain.TreeNode;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.dto.file.BatchMoveFileDTO;
import com.qiwenshare.file.dto.file.CopyFileDTO;
import com.qiwenshare.file.dto.file.MoveFileDTO;
import com.qiwenshare.file.dto.file.*;
import com.qiwenshare.file.util.SessionUtil;
import com.qiwenshare.file.vo.file.FileListVo;
import com.qiwenshare.ufop.util.UFOPUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jetty.util.StringUtil;
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
import javax.validation.Valid;
import java.util.*;

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

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Resource
    FileDealComp fileDealComp;

    public static final String CURRENT_MODULE = "文件接口";


    @Operation(summary = "创建文件", description = "目录(文件夹)的创建", tags = {"file"})
    @RequestMapping(value = "/createfile", method = RequestMethod.POST)
    @MyLog(operation = "创建文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> createFile(@Valid @RequestBody CreateFileDTO createFileDto) {

        UserBean sessionUserBean = (UserBean) SessionUtil.getSession();

        boolean isDirExist = userFileService.isDirExist(createFileDto.getFileName(), createFileDto.getFilePath(), sessionUserBean.getUserId());

        if (isDirExist) {
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
    public RestResult<SearchHits<FileSearch>> searchFile(SearchFileDTO searchFileDTO) {
        UserBean sessionUserBean = (UserBean) SessionUtil.getSession();
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
    public RestResult<String> renameFile(@RequestBody RenameFileDTO renameFileDto) {

        UserBean sessionUserBean = (UserBean) SessionUtil.getSession();
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        UserFile userFile = userFileService.getById(renameFileDto.getUserFileId());

        List<UserFile> userFiles = userFileService.selectUserFileByNameAndPath(renameFileDto.getFileName(), userFile.getFilePath(), sessionUserBean.getUserId());
        if (userFiles != null && !userFiles.isEmpty()) {
            return RestResult.fail().message("同名文件已存在");
        }

        LambdaUpdateWrapper<UserFile> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(UserFile::getFileName, renameFileDto.getFileName())
                .set(UserFile::getUploadTime, DateUtil.getCurrentTime())
                .eq(UserFile::getUserFileId, renameFileDto.getUserFileId());
        userFileService.update(lambdaUpdateWrapper);
        if (1 == userFile.getIsDir()) {
            userFileService.replaceUserFilePath(userFile.getFilePath() + renameFileDto.getFileName() + "/",
                    userFile.getFilePath() + userFile.getFileName() + "/", sessionUserBean.getUserId());
        }

        fileDealComp.uploadESByUserFileId(renameFileDto.getUserFileId());
        return RestResult.success();
    }




    @Operation(summary = "获取文件列表", description = "用来做前台列表展示", tags = {"file"})
    @RequestMapping(value = "/getfilelist", method = RequestMethod.GET)
    @ResponseBody
    public RestResult getFileList(
            @Parameter(description = "文件路径", required = true) String filePath,
            @Parameter(description = "当前页", required = true) long currentPage,
            @Parameter(description = "页面数量", required = true) long pageCount){

        UserFile userFile = new UserFile();
        UserBean sessionUserBean = (UserBean) SessionUtil.getSession();
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        if (userFile == null) {
            return RestResult.fail();

        }
        userFile.setUserId(sessionUserBean.getUserId());


        List<FileListVo> fileList = null;
        userFile.setFilePath(UFOPUtils.urlDecode(filePath));
        if (currentPage == 0 || pageCount == 0) {
            fileList = userFileService.userFileList(userFile, 0L, 10L);
        } else {
            long beginCount = (currentPage - 1) * pageCount;

            fileList = userFileService.userFileList(userFile, beginCount, pageCount);

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
    public RestResult<String> deleteImageByIds(@RequestBody BatchDeleteFileDTO batchDeleteFileDto) {

        UserBean sessionUserBean = (UserBean) SessionUtil.getSession();
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
    public RestResult deleteFile(@RequestBody DeleteFileDTO deleteFileDto) {

        UserBean sessionUserBean = (UserBean) SessionUtil.getSession();
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        userFileService.deleteUserFile(deleteFileDto.getUserFileId(), sessionUserBean.getUserId());
        fileDealComp.deleteESByUserFileId(deleteFileDto.getUserFileId());

        return RestResult.success();

    }

    @Operation(summary = "解压文件", description = "解压缩功能为体验功能，可以解压zip和rar格式的压缩文件，目前只支持本地存储文件解压，部分高版本rar格式不支持。", tags = {"file"})
    @RequestMapping(value = "/unzipfile", method = RequestMethod.POST)
    @MyLog(operation = "解压文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> unzipFile(@RequestBody UnzipFileDTO unzipFileDto) {

        UserBean sessionUserBean = (UserBean) SessionUtil.getSession();
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        try {
            fileService.unzipFile(unzipFileDto.getUserFileId(), unzipFileDto.getUnzipMode(), unzipFileDto.getFilePath());
        } catch (QiwenException e) {
            return RestResult.fail().message(e.getMessage());
        }

        return RestResult.success();

    }

    @Operation(summary = "文件复制", description = "可以复制文件或者目录", tags = {"file"})
    @RequestMapping(value = "/copyfile", method = RequestMethod.POST)
    @MyLog(operation = "文件复制", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> copyFile(@RequestBody CopyFileDTO copyFileDTO) {

        UserBean sessionUserBean = (UserBean) SessionUtil.getSession();
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        long userFileId = copyFileDTO.getUserFileId();
        UserFile userFile = userFileService.getById(userFileId);
        String oldfilePath = userFile.getFilePath();
        String newfilePath = copyFileDTO.getFilePath();
        String fileName = userFile.getFileName();
        String extendName = userFile.getExtendName();
        if (userFile.getIsDir() == 1) {
            String testFilePath = oldfilePath + fileName +  "/";
            if (newfilePath.startsWith(testFilePath)) {
                return RestResult.fail().message("原路径与目标路径冲突，不能移动");
            }
        }

        userFileService.updateFilepathByFilepath(oldfilePath, newfilePath, fileName, extendName, sessionUserBean.getUserId());
        return RestResult.success();

    }

    @Operation(summary = "文件移动", description = "可以移动文件或者目录", tags = {"file"})
    @RequestMapping(value = "/movefile", method = RequestMethod.POST)
    @MyLog(operation = "文件移动", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> moveFile(@RequestBody MoveFileDTO moveFileDto) {

        UserBean sessionUserBean = (UserBean) SessionUtil.getSession();
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        String oldfilePath = moveFileDto.getOldFilePath();
        String newfilePath = moveFileDto.getFilePath();
        String fileName = moveFileDto.getFileName();
        String extendName = moveFileDto.getExtendName();
        if (StringUtil.isEmpty(extendName)) {
            String testFilePath = oldfilePath + fileName +  "/";
            if (newfilePath.startsWith(testFilePath)) {
                return RestResult.fail().message("原路径与目标路径冲突，不能移动");
            }
        }

        userFileService.updateFilepathByFilepath(oldfilePath, newfilePath, fileName, extendName, sessionUserBean.getUserId());
        return RestResult.success();

    }

    @Operation(summary = "批量移动文件", description = "可以同时选择移动多个文件或者目录", tags = {"file"})
    @RequestMapping(value = "/batchmovefile", method = RequestMethod.POST)
    @MyLog(operation = "批量移动文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> batchMoveFile(@RequestBody BatchMoveFileDTO batchMoveFileDto) {

        UserBean sessionUserBean = (UserBean) SessionUtil.getSession();
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        String files = batchMoveFileDto.getFiles();
        String newfilePath = batchMoveFileDto.getFilePath();

        List<UserFile> fileList = JSON.parseArray(files, UserFile.class);

        for (UserFile userFile : fileList) {
           
            if (StringUtil.isEmpty(userFile.getExtendName())) {
                String testFilePath = userFile.getFilePath() + userFile.getFileName() +  "/";
                if (newfilePath.startsWith(testFilePath)) {
                    return RestResult.fail().message("原路径与目标路径冲突，不能移动");
                }
            }

            userFileService.updateFilepathByFilepath(userFile.getFilePath(), newfilePath, userFile.getFileName(), userFile.getExtendName(), sessionUserBean.getUserId());
        }

        return RestResult.success().data("批量移动文件成功");

    }



    @Operation(summary = "通过文件类型选择文件", description = "该接口可以实现文件格式分类查看", tags = {"file"})
    @RequestMapping(value = "/selectfilebyfiletype", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<List<Map<String, Object>>> selectFileByFileType(@Parameter(description = "文件类型", required = true) int fileType,
                                                                      @Parameter(description = "当前页", required = true) long currentPage,
                                                                      @Parameter(description = "页面数量", required = true) long pageCount) {

        UserBean sessionUserBean = (UserBean) SessionUtil.getSession();
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        long userId = sessionUserBean.getUserId();

        List<FileListVo> fileList = new ArrayList<>();
        Long beginCount = 0L;
        if (pageCount == 0 || currentPage == 0) {
            beginCount = 0L;
            pageCount = 10L;
        } else {
            beginCount = (currentPage - 1) * pageCount;
        }

        Long total = 0L;
        if (fileType == UFOPUtils.OTHER_TYPE) {

            List<String> arrList = new ArrayList<>();
            arrList.addAll(Arrays.asList(UFOPUtils.DOC_FILE));
            arrList.addAll(Arrays.asList(UFOPUtils.IMG_FILE));
            arrList.addAll(Arrays.asList(UFOPUtils.VIDEO_FILE));
            arrList.addAll(Arrays.asList(UFOPUtils.MUSIC_FILE));

            fileList = userFileService.selectFileNotInExtendNames(arrList,beginCount, pageCount, userId);
            total = userFileService.selectCountNotInExtendNames(arrList,beginCount, pageCount, userId);
        } else {
            fileList = userFileService.selectFileByExtendName(UFOPUtils.getFileExtendsByType(fileType), beginCount, pageCount,userId);
            total = userFileService.selectCountByExtendName(UFOPUtils.getFileExtendsByType(fileType), beginCount, pageCount,userId);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("list",fileList);
        map.put("total", total);
        return RestResult.success().data(map);

    }

    @Operation(summary = "获取文件树", description = "文件移动的时候需要用到该接口，用来展示目录树", tags = {"file"})
    @RequestMapping(value = "/getfiletree", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<TreeNode> getFileTree() {
        RestResult<TreeNode> result = new RestResult<TreeNode>();

        UserBean sessionUserBean = (UserBean) SessionUtil.getSession();
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
