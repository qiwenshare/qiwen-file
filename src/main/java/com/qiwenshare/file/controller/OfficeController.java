package com.qiwenshare.file.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.qiwenshare.common.exception.NotLoginException;
import com.qiwenshare.common.result.RestResult;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.common.util.security.JwtUser;
import com.qiwenshare.common.util.security.SessionUtil;
import com.qiwenshare.file.api.IFileService;
import com.qiwenshare.file.api.IUserFileService;
import com.qiwenshare.file.api.IUserService;
import com.qiwenshare.file.component.FileDealComp;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.domain.user.UserBean;
import com.qiwenshare.file.dto.file.CreateOfficeFileDTO;
import com.qiwenshare.file.dto.file.EditOfficeFileDTO;
import com.qiwenshare.file.dto.file.PreviewOfficeFileDTO;
import com.qiwenshare.file.office.documentserver.managers.history.HistoryManager;
import com.qiwenshare.file.office.documentserver.models.enums.Action;
import com.qiwenshare.file.office.documentserver.models.enums.Type;
import com.qiwenshare.file.office.documentserver.models.filemodel.FileModel;
import com.qiwenshare.file.office.entities.User;
import com.qiwenshare.file.office.services.configurers.FileConfigurer;
import com.qiwenshare.file.office.services.configurers.wrappers.DefaultFileWrapper;
import com.qiwenshare.ufop.factory.UFOPFactory;
import com.qiwenshare.ufop.operation.copy.Copier;
import com.qiwenshare.ufop.operation.copy.domain.CopyFile;
import com.qiwenshare.ufop.operation.download.domain.DownloadFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.Locale;
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
    UFOPFactory ufopFactory;
    @Resource
    FileDealComp fileDealComp;
    @Value("${deployment.host}")
    private String deploymentHost;
    @Value("${server.port}")
    private String port;
    @Value("${ufop.storage-type}")
    private Integer storageType;

    @Value("${files.docservice.url.site}")
    private String docserviceSite;

    @Value("${files.docservice.url.api}")
    private String docserviceApiUrl;
    @Autowired
    private FileConfigurer<DefaultFileWrapper> fileConfigurer;

    @Resource
    IFileService fileService;
    @Resource
    IUserFileService userFileService;
    @Autowired
    private HistoryManager historyManager;

    @Operation(summary = "预览office文件", description = "预览office文件", tags = {"office"})
    @RequestMapping(value = "/previewofficefile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<Object> previewOfficeFile(HttpServletRequest request, @RequestBody PreviewOfficeFileDTO previewOfficeFileDTO) {
        RestResult<Object> result = new RestResult<>();
        try {

            JwtUser loginUser = SessionUtil.getSession();
            UserFile userFile = userFileService.getById(previewOfficeFileDTO.getUserFileId());



            UserBean userBean = userService.getById(loginUser.getUserId());
            User user = new User(userBean);

            Action action = Action.view;
            Type type = Type.desktop;
            Locale locale = new Locale("zh");
            FileModel fileModel = fileConfigurer.getFileModel(
                    DefaultFileWrapper
                            .builder()
                            .userFileId(userFile.getUserFileId())
                            .fileName(userFile.getFileName() + "." + userFile.getExtendName())
                            .type(type)
                            .lang(locale.toLanguageTag())
                            .action(action)
                            .user(user)
                            .actionData(previewOfficeFileDTO.getPreviewUrl())
                            .build()
            );

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("file",fileModel);
//            jsonObject.put("fileHistory", historyManager.getHistory(fileModel.getDocument()));  // get file history and add it to the model
            jsonObject.put("docserviceApiUrl", docserviceSite + docserviceApiUrl);
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
    @Operation(summary = "编辑office文件", description = "编辑office文件", tags = {"office"})
    @ResponseBody
    @RequestMapping(value = "/editofficefile", method = RequestMethod.POST)
    public RestResult<Object> editOfficeFile(HttpServletRequest request, @RequestBody EditOfficeFileDTO editOfficeFileDTO) {
        RestResult<Object> result = new RestResult<>();
        log.info("editOfficeFile");
        try {
            JwtUser loginUser = SessionUtil.getSession();
            UserFile userFile = userFileService.getById(editOfficeFileDTO.getUserFileId());


            UserBean userBean = userService.getById(loginUser.getUserId());
            User user = new User(userBean);

            Action action = Action.edit;
            Type type = Type.desktop;
            Locale locale = new Locale("zh");
            FileModel fileModel = fileConfigurer.getFileModel(
                    DefaultFileWrapper
                            .builder()
                            .userFileId(userFile.getUserFileId())
                            .fileName(userFile.getFileName() + "." + userFile.getExtendName())
                            .type(type)
                            .lang(locale.toLanguageTag())
                            .action(action)
                            .user(user)
                            .actionData(editOfficeFileDTO.getPreviewUrl())
                            .build()
            );
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("file",fileModel);
            jsonObject.put("docserviceApiUrl", docserviceSite + docserviceApiUrl);
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
        String userId = userService.getUserIdByToken(token);
        if (StringUtils.isEmpty(userId)) {
            throw new NotLoginException();
        }

        PrintWriter writer = response.getWriter();
        Scanner scanner = new Scanner(request.getInputStream()).useDelimiter("\\A");
        String body = scanner.hasNext() ? scanner.next() : "";

        JSONObject jsonObj = JSON.parseObject(body);
        log.info("===saveeditedfile:" + jsonObj.get("status")); ;
        String status = jsonObj != null ? jsonObj.get("status").toString() : "";
        if ("2".equals(status) || "6".equals(status)) {
            String type = request.getParameter("type");
            String downloadUri = (String) jsonObj.get("url");

            if("edit".equals(type)){ //修改报告
                String userFileId = request.getParameter("userFileId");
                UserFile userFile = userFileService.getById(userFileId);
                FileBean fileBean = fileService.getById(userFile.getFileId());
                Long pointCount = fileService.getFilePointCount(userFile.getFileId());
                String fileUrl = fileBean.getFileUrl();
                if (pointCount > 1) {
                    fileUrl = fileDealComp.copyFile(fileBean, userFile);
                }

                URL url = new URL(downloadUri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                try {
                    InputStream stream = connection.getInputStream();
                    fileDealComp.saveFileInputStream(fileBean.getStorageType(), fileUrl, stream);

                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {

                    int fileLength = connection.getContentLength();
                    log.info("当前修改文件大小为：" + (long) fileLength);

                    DownloadFile downloadFile = new DownloadFile();
                    downloadFile.setFileUrl(fileBean.getFileUrl());
                    InputStream inputStream = ufopFactory.getDownloader(fileBean.getStorageType()).getInputStream(downloadFile);
                    String md5Str = DigestUtils.md5Hex(inputStream);

                    fileService.updateFileDetail(userFile.getUserFileId(), md5Str, fileLength);
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