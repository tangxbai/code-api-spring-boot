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
package com.viiyue.plugins.codeapi.downloader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.viiyue.plugins.codeapi.bean.CodeBean;
import com.viiyue.plugins.codeapi.config.CodeApiProperties;
import com.viiyue.plugins.codeapi.utils.Util;

/**
 * Download data for text type
 *
 * @author tangxbai
 * @sine 1.0.0
 */
public class TextDownloader implements CodeDownloader {

	private static final Comparator<Integer> INT_COMPARATOR = Comparator.comparing( Integer::intValue );

	@Override
	public String getExtension() {
		return "txt";
	}

	@Override
	public void download( OutputStream out, CodeApiProperties props, List<CodeBean> codes ) throws IOException {
		Integer maxOne = codes.stream().map( CodeBean::getCode ).max( INT_COMPARATOR ).get();
		Integer width = codes.stream().map( str -> Util.getTextLength( str.getMessage() ) ).max( INT_COMPARATOR ).get();
		Integer length = Util.getNumLength( maxOne ) + 1;
		String line = StringUtils.leftPad( "-", length + 4 + width, '-' );
		try ( BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( out ) ) ) {
			writer.write( line );
			writer.newLine();
			for ( CodeBean code : codes ) {
				writer.write( String.format( "%" + length + "d", code.getCode() ) + " : " + code.getMessage() );
				writer.newLine();
				writer.write( line );
				writer.newLine();
			}
		}
	}

}
