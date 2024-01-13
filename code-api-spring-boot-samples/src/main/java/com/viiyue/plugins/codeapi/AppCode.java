/**
 * Copyright (C) 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.viiyue.plugins.codeapi;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@CodeDescriptor( value = "通用状态码（1000以下）", theme = "#EC26BD" )
public enum AppCode implements CodeExporter {

	OK( 200, "请求成功" ),
	EMPTY( 201, "暂无更多数据" ),
	FAILURE( 500, "系统异常" ),
	BAD_PARAMETERS( 400, "提交的参数内容或者格式有问题" );

	private int code;
	private String message;

	public String getColor() {
		return this.code == 500 ? "#83CF9C" : null;
	}

}
