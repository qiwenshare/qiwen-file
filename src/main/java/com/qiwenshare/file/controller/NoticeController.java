package com.qiwenshare.file.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qiwenshare.common.result.RestResult;
import com.qiwenshare.file.api.INoticeService;
import com.qiwenshare.file.domain.Notice;
import com.qiwenshare.file.dto.notice.NoticeListDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "公告管理")
@RestController
@RequestMapping("/notice")
public class NoticeController {
    public static final String CURRENT_MODULE = "公告管理";
    @Resource
    INoticeService noticeService;

    /**
     * 得到所有的公告
     *
     * @return
     */
    @Operation(summary = "得到所有的公告列表", tags = {"公告管理"})
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<Map> selectUserList(@Parameter(description = "当前页，从1开始")  @RequestParam(defaultValue = "1") int page,
                                          @Parameter(description = "页大小")  @RequestParam(defaultValue = "10") int pageSize,
                                          @Parameter(description = "标题") @RequestParam(required = false) String title,
                                          @Parameter(description = "发布者")  @RequestParam(required = false) Long publisher,
                                          @Parameter(description = "开始发布时间")  @RequestParam(required = false) String beginTime,
                                          @Parameter(description = "开始发布时间")  @RequestParam(required = false) String endTime) {
        NoticeListDTO noticeListDTO = new NoticeListDTO();
        noticeListDTO.setPage(page);
        noticeListDTO.setPageSize(pageSize);
        noticeListDTO.setTitle(title);
        noticeListDTO.setPlatform(3);
        noticeListDTO.setPublisher(publisher);
        noticeListDTO.setBeginTime(beginTime);
        noticeListDTO.setEndTime(endTime);
        IPage<Notice> noticeIPage = noticeService.selectUserPage(noticeListDTO);

        Map<String, Object> map = new HashMap<>();
        map.put("total", noticeIPage.getTotal());
        map.put("list", noticeIPage.getRecords());
        return RestResult.success().data(map);
    }

    @Operation(summary = "查询公告详情", tags = {"公告管理"})
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<Notice> getNoticeDetail(@Parameter(description = "公告id", required = true) long noticeId) {
        RestResult<Notice> result = new RestResult<Notice>();

        Notice notice = noticeService.getById(noticeId);

        return RestResult.success().data(notice);
    }




}
