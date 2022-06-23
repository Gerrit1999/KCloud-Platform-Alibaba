package io.laokou.admin.interfaces.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.laokou.admin.application.service.SysLogApplicationService;
import io.laokou.admin.interfaces.qo.LoginLogQO;
import io.laokou.admin.interfaces.qo.OperateLogQO;
import io.laokou.admin.interfaces.vo.LoginLogVO;
import io.laokou.admin.interfaces.vo.OperateLogVO;
import io.laokou.common.dto.LoginLogDTO;
import io.laokou.common.dto.OperateLogDTO;
import io.laokou.common.utils.HttpResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统日志控制器
 * @author Kou Shenhai
 */
@RestController
@Api(value = "系统日志API",protocols = "http",tags = "系统日志API")
@RequestMapping("/sys/log/api")
public class SysLogApiController {

    @Autowired
    private SysLogApplicationService sysLogApplicationService;

    @PostMapping(value = "/operate/insert")
    @CrossOrigin()
    @ApiOperation("系统日志>操作日志>新增")
    public HttpResultUtil<Boolean> insertOperateLog(@RequestBody OperateLogDTO dto) {
        return new HttpResultUtil<Boolean>().ok(sysLogApplicationService.insertOperateLog(dto));
    }

    @PostMapping(value = "/operate/query")
    @CrossOrigin()
    @ApiOperation("系统日志>操作日志>查询")
    public HttpResultUtil<IPage<OperateLogVO>> queryOperateLog(@RequestBody OperateLogQO qo) {
        return new HttpResultUtil<IPage<OperateLogVO>>().ok(sysLogApplicationService.queryOperateLogPage(qo));
    }

    @PostMapping(value = "/login/insert")
    @CrossOrigin()
    @ApiOperation("系统日志>登录日志>新增")
    public HttpResultUtil<Boolean> insertLoginLog(@RequestBody LoginLogDTO dto) {
        return new HttpResultUtil<Boolean>().ok(sysLogApplicationService.insertLoginLog(dto));
    }

    @PostMapping(value = "/login/query")
    @CrossOrigin()
    @ApiOperation("系统日志>登录日志>查询")
    public HttpResultUtil<IPage<LoginLogVO>> queryLoginLog(@RequestBody LoginLogQO qo) {
        return new HttpResultUtil<IPage<LoginLogVO>>().ok(sysLogApplicationService.queryLoginLogPage(qo));
    }

}