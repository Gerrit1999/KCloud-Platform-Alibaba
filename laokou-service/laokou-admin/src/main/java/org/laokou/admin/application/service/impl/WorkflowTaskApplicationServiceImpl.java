/**
 * Copyright (c) 2022 KCloud-Platform Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.admin.application.service.impl;
import com.google.common.collect.Lists;
import org.laokou.admin.application.service.WorkflowTaskApplicationService;
import org.laokou.admin.infrastructure.common.enums.FlowCommentEnum;
import org.laokou.admin.infrastructure.config.CustomProcessDiagramGenerator;
import org.laokou.common.user.SecurityUser;
import org.laokou.admin.interfaces.dto.AuditDTO;
import org.laokou.admin.interfaces.dto.ClaimDTO;
import org.laokou.admin.interfaces.dto.UnClaimDTO;
import org.laokou.common.exception.CustomException;
import org.laokou.common.utils.FileUtil;
import org.laokou.datasource.annotation.DataSource;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.collections.MapUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
/**
 * @author Kou Shenhai
 */
@Service
@GlobalTransactional(rollbackFor = Exception.class)
public class WorkflowTaskApplicationServiceImpl implements WorkflowTaskApplicationService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Qualifier("processEngine")
    @Autowired
    private ProcessEngine processEngine;

    @Override
    @DataSource("master")
    public Boolean auditTask(AuditDTO dto, HttpServletRequest request) {
        Task task = taskService.createTaskQuery().taskId(dto.getTaskId()).singleResult();
        if (null == task) {
            throw new CustomException("任务不存在");
        }
        if (DelegationState.PENDING.equals(task.getDelegationState())) {
            taskService.addComment(dto.getTaskId(),dto.getInstanceId(), FlowCommentEnum.DELEGATE.getType(),dto.getComment());
            //委派
            taskService.resolveTask(dto.getTaskId(),dto.getValues());
        } else {
            taskService.addComment(dto.getTaskId(),dto.getInstanceId(), FlowCommentEnum.NORMAL.getType(),dto.getComment());
            taskService.setAssignee(dto.getTaskId(), SecurityUser.getUserId(request).toString());
            if (MapUtils.isNotEmpty(dto.getValues())) {
                taskService.complete(dto.getTaskId(),dto.getValues());
            } else {
                taskService.complete(dto.getTaskId());
            }
        }
        return true;
    }

    @Override
    @DataSource("master")
    public Boolean claimTask(ClaimDTO dto,HttpServletRequest request) {
        final Task task = taskService.createTaskQuery()
                .taskId(dto.getTaskId())
                .singleResult();
        if (null == task) {
            throw new CustomException("任务不存在");
        }
        taskService.claim(dto.getTaskId(),SecurityUser.getUserId(request).toString());
        return true;
    }

    @Override
    @DataSource("master")
    public Boolean unClaimTask(UnClaimDTO dto) {
        taskService.unclaim(dto.getTaskId());
        return true;
    }

    @Override
    @DataSource("master")
    public Boolean deleteTask(String taskId) {
        taskService.deleteTask(taskId);
        return true;
    }

    @Override
    @DataSource("master")
    public void diagramProcess(String processInstanceId, HttpServletResponse response) throws IOException {
        final InputStream inputStream = getInputStream(processInstanceId);
        final BufferedImage image = ImageIO.read(inputStream);
        response.setContentType("image/png");
        final ServletOutputStream outputStream = response.getOutputStream();
        if (null != image) {
            ImageIO.write(image,"png",outputStream);
        }
        outputStream.flush();
        FileUtil.closeStream(inputStream,outputStream);
    }

    private InputStream getInputStream(String processInstanceId) {
        String processDefinitionId;
        //获取当前的流程实例
        final ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        //如果流程已结束，则得到结束节点
        if (null == processInstance) {
            final HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();
            processDefinitionId = hpi.getProcessDefinitionId();
        } else {
            //没有结束，获取当前活动节点
            //根据流程实例id获取当前处于ActivityId集合
            final ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();
            processDefinitionId = pi.getProcessDefinitionId();
        }
        //获取活动节点
        final List<HistoricActivityInstance> highLightedFlowList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
        List<String> highLightedFlows = Lists.newArrayList();
        List<String> highLightedNodes = Lists.newArrayList();
        //高亮线
        for (HistoricActivityInstance temActivityInstance : highLightedFlowList) {
            if ("sequenceFlow".equals(temActivityInstance.getActivityType())) {
                //高亮线
                highLightedFlows.add(temActivityInstance.getActivityId());
            } else {
                //高亮节点
                highLightedNodes.add(temActivityInstance.getActivityId());
            }
        }
        //获取流程图
        final BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        final ProcessEngineConfiguration configuration = processEngine.getProcessEngineConfiguration();
        //获取自定义图片生成器
        ProcessDiagramGenerator diagramGenerator = new CustomProcessDiagramGenerator();
        return diagramGenerator.generateDiagram(bpmnModel, "png", highLightedNodes, highLightedFlows, configuration.getActivityFontName(),
                configuration.getLabelFontName(), configuration.getAnnotationFontName(), configuration.getClassLoader(), 1.0, true);
    }

}