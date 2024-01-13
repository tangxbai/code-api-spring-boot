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
package com.viiyue.plugins.codeapi.bean;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.viiyue.plugins.codeapi.downloader.ExcelDownloader;
import com.viiyue.plugins.excel.annotation.Excel;
import com.viiyue.plugins.excel.annotation.ExcelCell;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Status code instance
 * 
 * <pre>
 * { "code": 200, "color": "#FFFFFF", "message": "Message description text" }
 * </pre>
 *
 * @author tangxbai
 * @since 1.0.0
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Excel( styleable = ExcelDownloader.class )
public class CodeBean implements Serializable {

	private static final long serialVersionUID = -8170598104925284929L;
	private static final String CODE_LABEL = "状态码";
	private static final String MSG_LABEL = "描述信息";

	@ExcelCell( label = CODE_LABEL )
	private int code;
	private String color;
	@ExcelCell( label = MSG_LABEL, widthAutoSize = true )
	private String message;

	@Override
	public String toString() {
		return code + ": \"" + message + "\"";
	}
	
	public boolean hasColor() {
		return StringUtils.isNotEmpty( color );
	}
	
	public static final boolean isCode( String label ) {
		return Objects.equals( CODE_LABEL, label );
	}
	
	public static final boolean isMessage( String label ) {
		return Objects.equals( MSG_LABEL, label );
	}

}
