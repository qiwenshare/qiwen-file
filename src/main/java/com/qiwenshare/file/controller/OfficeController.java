package com.qiwenshare.file.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qiwenshare.common.exception.NotLoginException;
import com.qiwenshare.common.result.RestResult;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.file.anno.MyLog;
import com.qiwenshare.file.api.IFileService;
import com.qiwenshare.file.api.IFiletransferService;
import com.qiwenshare.file.api.IUserFileService;
import com.qiwenshare.file.api.IUserService;
import com.qiwenshare.file.domain.*;
import com.qiwenshare.file.dto.DownloadFileDTO;
import com.qiwenshare.file.dto.file.CreateOfficeFileDTO;
import com.qiwenshare.file.dto.file.EditOfficeFileDTO;
import com.qiwenshare.file.dto.file.PreviewOfficeFileDTO;
import com.qiwenshare.file.helper.ConfigManager;
import com.qiwenshare.ufo.factory.UFOFactory;
import com.qiwenshare.ufo.operation.download.domain.DownloadFile;
import com.qiwenshare.ufo.operation.write.Writer;
import com.qiwenshare.ufo.operation.write.domain.WriteFile;
import com.qiwenshare.ufo.util.PathUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Tag(name = "office", description = "该接口为Onlyoffice文件操作接口，主要用来做一些文档的编辑，浏览等。")
@RestController
@Slf4j
@RequestMapping({"/office"})
public class OfficeController {
    public static final String CURRENT_MODULE = "Onlyoffice文件操作接口";
    @Resource
    IUserService userService;
    @Resource
    UFOFactory ufoFactory;

    @Value("${deployment.host}")
    private String deploymentHost;


    @Resource
    IFileService fileService;
    @Resource
    IUserFileService userFileService;

    @ResponseBody
    @RequestMapping(value = "/createofficefile", method = RequestMethod.POST)
    public RestResult<Object> createOfficeFile(HttpServletRequest request, @RequestBody CreateOfficeFileDTO createOfficeFileDTO, @RequestHeader("token") String token) {
        RestResult<Object> result = new RestResult<>();
        try{

            UserBean loginUser = userService.getUserBeanByToken(token);
            if (loginUser == null) {
                throw new NotLoginException();
            }
            String fileName = createOfficeFileDTO.getFileName();
            String filePath = createOfficeFileDTO.getFilePath();
            String extendName = createOfficeFileDTO.getExtendName();
            List<UserFile> userFiles = userFileService.selectSameUserFile(fileName, filePath, extendName, loginUser.getUserId());
            if (userFiles != null && !userFiles.isEmpty()) {
                return RestResult.fail().message("同名文件已存在");
            }
            String uuid = UUID.randomUUID().toString().replaceAll("-","");

            SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");


            String fileSavePath = PathUtil.getStaticPath() + "/create/" + formater.format(new Date());
            File fileSavePathFile = new File(fileSavePath);

            if (!fileSavePathFile.exists()) {
                fileSavePathFile.mkdirs();
            }
            String fileUrl = "/create/" + formater.format(new Date()) + "/" + uuid + "." + extendName;

            File file = new File(fileSavePath + "/" + uuid + "." + extendName);
            if(!file.exists()){
                try {
                    if("docx".equals(extendName)){
                        //创建word文档
                        XWPFDocument document= new XWPFDocument();
                        //Write the Document in file system
                        FileOutputStream out = new FileOutputStream(file);
                        document.write(out);
                        out.close();
                    }else if("xlsx".equals(extendName)){
                        //创建excel表格
                         XSSFWorkbook workbook = new XSSFWorkbook();
                         //创建工作表
                         workbook.createSheet("Sheet1");
                        //Write the Document in file system
                        FileOutputStream out = new FileOutputStream(file);
                        workbook.write(out);
                        out.close();
                    }else if("pptx".equals(extendName)){
                        //创建pptx演示文稿
                        XMLSlideShow pptx = new XMLSlideShow();
                        //创建工作表
                        //Write the Document in file system
                        FileOutputStream out = new FileOutputStream(file);
                        pptx.write(out);
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileBean fileBean = new FileBean();
            fileBean.setFileSize(Long.valueOf(1024 * 3));
            fileBean.setFileUrl(fileUrl);
            fileBean.setStorageType(0);
            fileBean.setPointCount(1);
            fileBean.setIdentifier(uuid);
            fileBean.setTimeStampName(uuid);
            boolean saveFlag = fileService.save(fileBean);
            UserFile userFile = new UserFile();
            if(saveFlag) {
                userFile.setUserId(loginUser.getUserId());
                userFile.setFileName(fileName);
                userFile.setFilePath(filePath);
                userFile.setDeleteFlag(0);
                userFile.setIsDir(0);
                userFile.setExtendName(extendName);
                userFile.setUploadTime(DateUtil.getCurrentTime());
                userFile.setFileId(fileBean.getFileId());
                userFileService.save(userFile);
            }
            Long newFileSize = file.length();
            //更新文件修改信息
            LambdaUpdateWrapper<FileBean> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            FileInputStream fins = new FileInputStream(fileSavePath);
            String md5Str = DigestUtils.md5Hex(fins);
            fins.close();

            lambdaUpdateWrapper
                    .set(FileBean::getIdentifier, md5Str)
                    .set(FileBean::getTimeStampName, md5Str)
                    .set(FileBean::getFileSize, newFileSize)
                    .eq(FileBean::getFileId, fileBean.getFileId());
            fileService.update(lambdaUpdateWrapper);

            result.success();
            result.setMessage("文件创建成功！");
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setCode(500);
            result.setMessage("服务器错误！");
        }
        return result;
    }

    @Operation(summary = "预览office文件", description = "预览office文件", tags = {"office"})
    @MyLog(operation = "查看报告接口", module = CURRENT_MODULE)
    @RequestMapping(value = "/previewofficefile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<Object> previewOfficeFile(HttpServletRequest request, @RequestBody PreviewOfficeFileDTO previewOfficeFileDTO, @RequestHeader("token") String token) {
        RestResult<Object> result = new RestResult<>();
        try {

            UserBean loginUser = userService.getUserBeanByToken(token);
            if (loginUser == null) {
                throw new NotLoginException();
            }
            UserFile userFile = userFileService.getById(previewOfficeFileDTO.getUserFileId());

            String baseUrl = request.getScheme()+"://"+ deploymentHost + request.getContextPath();

            FileModel file = new FileModel(userFile.getFileName() + "." + userFile.getExtendName(),
                    previewOfficeFileDTO.getPreviewUrl(),
                    String.valueOf(new Date().getTime()),
                    String.valueOf(loginUser.getUserId()),
                    loginUser.getUsername(),
                    "view");

            String query = "?type=show&token="+token;
            file.editorConfig.callbackUrl= baseUrl + "/office/IndexServlet" + query;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("file",file);
            jsonObject.put("docserviceApiUrl", ConfigManager.GetProperty("files.docservice.url.site") + ConfigManager.GetProperty("files.docservice.url.api"));
            jsonObject.put("reportName",userFile.getFileName());
            result.setData(jsonObject);
            result.setCode(200);
            result.setMessage("获取报告成功！");
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setCode(500);
            result.setMessage("服务器错误！");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/editofficefile", method = RequestMethod.POST)
    public RestResult<Object> editOfficeFile(HttpServletRequest request, @RequestBody EditOfficeFileDTO editOfficeFileDTO, @RequestHeader("token") String token) {
        RestResult<Object> result = new RestResult<>();
        log.info("editOfficeFile");
        try {

            UserBean loginUser = userService.getUserBeanByToken(token);
            if (loginUser == null) {
                throw new NotLoginException();
            }
            UserFile userFile = userFileService.getById(editOfficeFileDTO.getUserFileId());

            String baseUrl = request.getScheme()+"://"+ deploymentHost + request.getContextPath();

            log.info("回调地址baseUrl：" + baseUrl);

            FileModel file = new FileModel(userFile.getFileName() + "." + userFile.getExtendName(),
                    editOfficeFileDTO.getPreviewUrl(),
                    String.valueOf(new Date().getTime()),
                    String.valueOf(loginUser.getUserId()),
                    loginUser.getUsername(),
                    "edit");
            file.changeType(request.getParameter("mode"), "edit");

            String query = "?type=edit&fileId="+userFile.getFileId()+"&token="+token;
            file.editorConfig.callbackUrl= baseUrl + "/office/IndexServlet" + query;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("file",file);
            jsonObject.put("docserviceApiUrl",ConfigManager.GetProperty("files.docservice.url.site") + ConfigManager.GetProperty("files.docservice.url.api"));
            jsonObject.put("reportName",userFile.getFileName());
            result.setData(jsonObject);
            result.setCode(200);
            result.setMessage("编辑报告成功！");
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setCode(500);
            result.setMessage("服务器错误！");
        }
        return result;
    }


    @RequestMapping(value = "/IndexServlet", method = RequestMethod.POST)
    @ResponseBody
    public void IndexServlet(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String token = request.getParameter("token");
        UserBean loginUser = userService.getUserBeanByToken(token);
        if (loginUser == null) {
            throw new NotLoginException();
        }

        PrintWriter writer = response.getWriter();
        Scanner scanner = new Scanner(request.getInputStream()).useDelimiter("\\A");
        String body = scanner.hasNext() ? scanner.next() : "";

        JSONObject jsonObj = JSON.parseObject(body);
        log.info("===saveeditedfile:" + jsonObj.get("status")); ;
        String status = jsonObj != null ? jsonObj.get("status").toString() : "";
        if ("2".equals(status)) {//新建报告不强制手动操作时状态为2
            String type = request.getParameter("type");
            String downloadUri = (String) jsonObj.get("url");

            if("edit".equals(type)){//修改报告
                String fileId = request.getParameter("fileId");
                String userFileId = request.getParameter("userFileId");
                FileBean fileBean = fileService.getById(fileId);
                if (fileBean.getPointCount() > 1) {
                    //该场景，暂不支持编辑修改
                    writer.write("{\"error\":1}");
                    return ;
                }

                URL url = new URL(downloadUri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                int fileLength = 0;
                try {
                    InputStream stream = connection.getInputStream();

                    Writer writer1 = ufoFactory.getWriter(fileBean.getStorageType());
                    WriteFile writeFile = new WriteFile();
                    writeFile.setFileUrl(fileBean.getFileUrl());

                    writeFile.setFileSize(connection.getContentLength());
                    writer1.write(stream, writeFile);
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    //更新文件修改信息
                    LambdaUpdateWrapper<UserFile> userFileUpdateWrapper = new LambdaUpdateWrapper<>();
                    userFileUpdateWrapper
                            .set(UserFile::getUploadTime, DateUtil.getCurrentTime())
                            .eq(UserFile::getUserFileId, userFileId);
                    userFileService.update(userFileUpdateWrapper);
                    LambdaUpdateWrapper<FileBean> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                    fileLength = connection.getContentLength();
                    log.info("当前修改文件大小为：" + Long.valueOf(fileLength));

                    DownloadFile downloadFile = new DownloadFile();
                    downloadFile.setFileUrl(fileBean.getFileUrl());
                    InputStream inputStream = ufoFactory.getDownloader(fileBean.getStorageType()).getInputStream(downloadFile);
                    String md5Str = DigestUtils.md5Hex(inputStream);
                    lambdaUpdateWrapper
                            .set(FileBean::getIdentifier, md5Str)
                            .set(FileBean::getFileSize, Long.valueOf(fileLength))
                            .eq(FileBean::getFileId, fileId);
                    fileService.update(lambdaUpdateWrapper);

                    connection.disconnect();
                }
            }
        }

        if("3".equals(status)||"7".equals(status)) {//不强制手动保存时为6,"6".equals(status)
            log.debug("====保存失败:");
            writer.write("{\"error\":1}");
        }else {
            log.debug("状态为：0") ;
            writer.write("{\"error\":" + "0" + "}");
        }
    }

}