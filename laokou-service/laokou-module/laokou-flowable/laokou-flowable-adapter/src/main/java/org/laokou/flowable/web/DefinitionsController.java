/*
 * Copyright (c) 2022 KCloud-Platform-Alibaba Authors. All Rights Reserved.
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
 *
 */

package org.laokou.flowable.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.laokou.common.i18n.dto.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author laokou
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "DefinitionsController", description = "流程定义")
public class DefinitionsController {

	@PostMapping(value = "v1/definitions")
	@Operation(summary = "流程定义", description = "新增流程")
	public Result<Boolean> insert(@RequestPart("file") MultipartFile file) {
		return Result.of(null);
	}

	@PostMapping(value = "v1/definitions/list")
	@Operation(summary = "流程定义", description = "查询流程列表")
	public Result<?> list() {
		return Result.of(null);
	}

	@GetMapping(value = "v1/definitions/{definitionId}/diagram")
	@Operation(summary = "流程定义", description = "流程图")
	public Result<String> diagram(@PathVariable("definitionId") String definitionId) {
		return Result.of(null);
	}

	@DeleteMapping(value = "v1/definitions/{deploymentId}")
	@Operation(summary = "流程定义", description = "删除流程")
	public Result<Boolean> delete(@PathVariable("deploymentId") String deploymentId) {
		return Result.of(null);
	}

	@PutMapping(value = "v1/definitions/{definitionId}/suspend")
	@Operation(summary = "流程定义", description = "挂起流程")
	public Result<Boolean> suspend(@PathVariable("definitionId") String definitionId) {
		return Result.of(null);
	}

	@PutMapping(value = "v1/definitions/{definitionId}/activate")
	@Operation(summary = "流程定义", description = "激活流程")
	public Result<Boolean> activate(@PathVariable("definitionId") String definitionId) {
		return Result.of(null);
	}

}