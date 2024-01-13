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
package com.viiyue.plugins.codeapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Code api configuration properties
 *
 * @author tangxbai
 * @sine 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties( prefix = "codeapi", ignoreInvalidFields = true )
public class CodeApiProperties {

	/**
	 * HTML page title
	 */
	private String title = "CodeApi";

	/**
	 * The path of the exported api. The default value is: "/code-api".
	 */
	private String path = "/code-api";

	/**
	 * <p>The base package of the scan. 
	 * <b>By default</b>, the scan starts with the package in which the startup class is located.
	 */
	private String basePackage;

	/**
	 * Whether to enable the export feature? The default is disabled.
	 */
	private boolean exportable;

}
