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
package com.viiyue.plugins.codeapi.ui;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.viiyue.plugins.codeapi.bean.CodeBean;
import com.viiyue.plugins.codeapi.bean.CodeWrapper;
import com.viiyue.plugins.codeapi.config.CodeApiProperties;
import com.viiyue.plugins.codeapi.downloader.CodeDownloader;

import lombok.RequiredArgsConstructor;

/**
 * <p>Response status code api controller
 * 
 * <ul>
 * <li>[JSON][GET|POST] - /code-api?value=xxx: [Object]
 * <li>[HTML][GET|POST] - /code-api?value=xxx: HTML
 * </ul>
 *
 * @author tangxbai
 * @since 1.0.0
 */
// Will be dynamically injected into spring
@RequiredArgsConstructor
@RequestMapping( "${codeapi.path:/code-api}" )
public class CodeApiController {

	private static final String HTML = MediaType.TEXT_HTML_VALUE;
	private static final String JSON = MediaType.APPLICATION_JSON_VALUE;

	private final CodeApiProperties props;
	private final CodeWrapper codeWrapper;
	private final CodeDownloader downloader;

	/**
	 * Response to the search's status code results in JSON format
	 * 
	 * @param value the search code, which can contain the ambiguity code 'x' or 'X'.
	 * @return the searched status code results
	 */
	@ResponseBody
	@RequestMapping( produces = JSON, method = { RequestMethod.GET, RequestMethod.POST } )
	public Object selectByCode( String value ) {
		return StringUtils.isEmpty( value ) ? codeWrapper.getGroups() : codeWrapper.getByCode( value );
	}

	/**
	 * Response to the search's status code results as HTML page
	 * 
	 * @param value the search code, which can contain the ambiguity code 'x' or 'X'.
	 * @return the searched results to HTML page
	 */
	@RequestMapping( produces = HTML, method = { RequestMethod.GET, RequestMethod.POST } )
	public ModelAndView toResponseCodeUI( String value ) {
		ModelAndView view = new ModelAndView( new CodeApiView() );
		view.addObject( "props", props );
		view.addObject( "allStatusGroups", codeWrapper.getGroups() );
		view.addObject( "allStatusMapping", codeWrapper.getStringify() );
		if ( value != null ) {
			view.addObject( "searchCode", value );
			view.addObject( "searchResults", codeWrapper.getByCode( value ) );
		}
		return view;
	}

	/**
	 * Export status code data, which depends on the {@code codeapi.exportable} configuration.
	 * 
	 * @param response the http response instance
	 * @throws IOException if write error
	 */
	@ResponseBody
	@RequestMapping( path = "/export", produces = JSON, method = { RequestMethod.GET, RequestMethod.POST } )
	public void export( HttpServletResponse response ) throws IOException {
		Assert.isTrue( props.isExportable(), "Please enable \"codeapi.exportable\" first" );

		String charset = StandardCharsets.UTF_8.displayName();
		String fileName = URLEncoder.encode( props.getTitle() + "." + downloader.getExtension(), charset );

		response.setCharacterEncoding( charset );
		response.setContentType( "multipart/form-data" );
		response.setHeader( "Content-Disposition", "attachment;fileName=" + fileName );

		ServletOutputStream out = response.getOutputStream();
		List<CodeBean> codes = codeWrapper.getAllAndSorting();
		if ( codes == null || codes.isEmpty() ) {
			out.flush();
			response.flushBuffer();
		} else {
			downloader.download( out, props, codes );
		}
	}

}
