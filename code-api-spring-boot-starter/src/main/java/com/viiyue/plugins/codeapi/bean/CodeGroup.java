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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.viiyue.plugins.codeapi.CodeExporter;

import lombok.Getter;
import lombok.NonNull;

/**
 * Status code group template
 *
 * <pre>
 * {
 *     "group": "Message group",
 *     "theme": "#FFFFFF",
 *     "codes": [
 *         { "code": 1, "message": "success" },
 *         { "code": 2, "message": "success" },
 *         { "code": 3, "message": "success" },
 *         { "code": 4, "message": "success" }
 *     ]
 * }
 * </pre>
 *
 * @author tangxbai
 * @since 1.0.0
 */
@Getter
public class CodeGroup implements Serializable {

	private static final long serialVersionUID = -8881478785907096969L;

	private String group;
	private String theme;
	private List<CodeBean> codes;

	public CodeGroup( String group, String theme, int size ) {
		this.group = group;
		this.theme = theme;
		this.codes = new ArrayList<>( size );
	}

	public void add( @NonNull CodeExporter code ) {
		String color = StringUtils.defaultIfEmpty( code.getColor(), theme );
		this.codes.add( new CodeBean( code.getCode(), color, code.getMessage() ) );
	}

	public int size() {
		return codes == null ? 0 : codes.size();
	}

	@Override
	public String toString() {
		return group;
	}

}
