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
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.View;

import com.viiyue.plugins.codeapi.bean.CodeBean;
import com.viiyue.plugins.codeapi.bean.CodeGroup;
import com.viiyue.plugins.codeapi.config.CodeApiProperties;

/**
 * HTML web view for displaying the response status code results
 *
 * @author tangxbai
 * @since 1.0.0
 */
class CodeApiView implements View {

	private static final String UI_NAME = "code-api.html";
	private static final Pattern COMMENT_PATTERN = Pattern.compile( "<!--([A-Z_]+)-->" );
	private static final Logger LOG = LoggerFactory.getLogger( CodeApiView.class );
	private static final AtomicReference<String> UI = new AtomicReference<String>();
	private static final Map<String, BiConsumer<Map<String, Object>, StringBuilder>> CONSUMERS = new HashMap<>( 4 );

	@Override
	public String getContentType() {
		return MediaType.TEXT_HTML_VALUE;
	}

	@Override
	public void render( Map<String, ?> model, HttpServletRequest req, HttpServletResponse res ) throws Exception {
		if ( res.isCommitted() ) {
			String message = getMessage( model );
			LOG.error( message );
			return;
		}
		if ( res.getContentType() == null ) {
			res.setContentType( getContentType() );
		}
		res.setCharacterEncoding( "UTF-8" );
		StringBuffer buffer = new StringBuffer( 1024 );
		Matcher matcher = COMMENT_PATTERN.matcher( loadHtml() );
		Map<String, Object> om = ( Map<String, Object> ) model;
		while ( matcher.find() ) {
			BiConsumer<Map<String, Object>, StringBuilder> consumer = CONSUMERS.get( matcher.group( 1 ) );
			if ( consumer != null ) {
				StringBuilder builder = new StringBuilder();
				consumer.accept( om, builder );
				matcher.appendReplacement( buffer, builder.toString() );
			}
		}
		matcher.appendTail( buffer );
		res.getWriter().append( buffer.toString() );
	}

	private String loadHtml() {
		if ( UI.get() == null ) {
			try {
				InputStream stream = CodeApiView.class.getResourceAsStream( UI_NAME );
				UI.getAndSet( IOUtils.toString( stream, StandardCharsets.UTF_8 ) );
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
		return UI.get();
	}

	private String getMessage( Map<String, ?> model ) {
		Object path = model.get( "path" );
		String message = "Cannot render error page for request [" + path + "]";
		if ( model.get( "message" ) != null ) {
			message += " and exception [" + model.get( "message" ) + "]";
		}
		message += " as the response has already been committed.";
		message += " As a result, the response may have the wrong status code.";
		return message;
	}

	static {
		// Title
		CONSUMERS.put( "TITLE", ( model, builder ) -> {
			CodeApiProperties props = ( CodeApiProperties ) model.get( "props" );
			builder.append( props.getTitle() );
		} );
		
		// Version
		CONSUMERS.put( "VERSION", ( model, builder ) -> {
			builder.append( CodeApiView.class.getPackage().getImplementationVersion() );
		} );
		
		// Download
		CONSUMERS.put( "DOWNLOAD", ( model, builder ) -> {
			CodeApiProperties props = ( CodeApiProperties ) model.get( "props" );
			if ( props.isExportable() ) {
				builder.append( "<a class=\"item download\" target=\"_blank\" title=\"导出数据\"></a>" );
			}
		} );
		
		
		// In-line script
		CONSUMERS.put( "SCRIPT", ( model, builder ) -> {
			// (String) The search code for current URL parameter
			builder.append( "\t\t<script type=\"text/javascript\">" );
			builder.append( "let searchCode = '" + model.getOrDefault( "searchCode", "" ) + "';" );
			builder.append( "</script>\n" );

			// (JSON) Status code mapping
			builder.append( "\t\t<script type=\"text/javascript\">" );
			builder.append( "let StatusMapping = " + model.getOrDefault( "allStatusMapping", "{}" ) + ";" );
			builder.append( "</script>" );
		} );

		// Search input value
		CONSUMERS.put( "INPUT_VALUE", ( model, builder ) -> builder.append( model.getOrDefault( "searchCode", "" ) ) );

		// Search result layout
		CONSUMERS.put( "LAYOUT_SEARCH", ( model, builder ) -> {
			Object object = model.get( "searchResults" );
			builder.append( "\t\t\t<div class=\"search-layout\" " );
			builder.append( "display=\"" + ( object == null ? "none" : "show" ) + "\">" );
			if ( object != null ) {
				List<CodeBean> results = ( List<CodeBean> ) object;
				builder.append( "\n\t\t\t\t<div class=\"item-wrapper\">\n" );
				builder.append( "\t\t\t\t\t<div class=\"item-label\">共搜索到 <b>" + results.size() + "</b> 个结果</div>\n" );
				builder.append( "\t\t\t\t\t<ul class=\"item-codes\">\n" );
				for ( CodeBean bean : results ) {
					builder.append( "\t\t\t\t\t\t<li data-code=\"" + bean.getCode() + "\" title=\"" + bean.getCode()
							+ " - " + bean.getMessage() + "\">" );
					builder.append(
							"<div><span class=\"code-value\"" + getColor( bean ) + ">" + bean.getCode() + "</span></div>" );
					builder.append( "<span class=\"code-message\">" + bean.getMessage() + "</span>" );
					builder.append( "</li>\n" );
				}
				builder.append( "\t\t\t\t\t</ul>\n" );
				builder.append( "\t\t\t\t</div>\n" );
			}
			builder.append( "\t\t\t</div>" );
		} );

		// All status code layout
		CONSUMERS.put( "LAYOUT_DEFAULT", ( model, builder ) -> {
			Object groupObject = model.get( "allStatusGroups" );
			builder.append( "\t\t\t<div class=\"content-layout\" " );
			builder.append( "display=\"" + ( model.get( "searchResults" ) == null ? "show" : "none" ) + "\">" );
			if ( groupObject != null ) {
				builder.append( "\n" );
				for ( CodeGroup groups : ( List<CodeGroup> ) groupObject ) {
					builder.append( "\t\t\t\t<div class=\"item-wrapper\">\n" );
					builder.append( "\t\t\t\t\t<div class=\"item-label\">" + groups.getGroup() + "</div>\n" );
					builder.append( "\t\t\t\t\t<ul class=\"item-codes\">\n" );
					for ( CodeBean bean : groups.getCodes() ) {
						builder.append( "\t\t\t\t\t\t<li data-code=\"" + bean.getCode() + "\" title=\"" + bean.getCode()
								+ " - " + bean.getMessage() + "\">" );
						builder.append(
								"<div><span class=\"code-value\"" + getColor( bean ) + ">" + bean.getCode() + "</span></div>" );
						builder.append( "<span class=\"code-message\">" + bean.getMessage() + "</span>" );
						builder.append( "</li>\n" );
					}
					builder.append( "\t\t\t\t\t</ul>\n" );
					builder.append( "\t\t\t\t</div>\n" );
				}
			}
			builder.append( "\t\t\t</div>" );
		} );
	}

	static String getColor( CodeBean bean ) {
		return StringUtils.isEmpty( bean.getColor() ) ? "" : " style=\"background-color: " + bean.getColor() + "\"";
	}

}
