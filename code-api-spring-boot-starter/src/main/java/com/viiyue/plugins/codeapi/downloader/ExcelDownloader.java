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

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Workbook;

import com.viiyue.plugins.codeapi.bean.CodeBean;
import com.viiyue.plugins.codeapi.config.CodeApiProperties;
import com.viiyue.plugins.excel.ExcelWriter;
import com.viiyue.plugins.excel.converter.Styleable;
import com.viiyue.plugins.excel.enums.Alignment;
import com.viiyue.plugins.excel.metadata.Style;

/**
 * Download data of the excel type
 *
 * @author tangxbai
 * @sine 1.0.0
 */
public class ExcelDownloader implements CodeDownloader, Styleable<CodeBean> {

	@Override
	public String getExtension() {
		return "xls";
	}

	@Override
	public void download( OutputStream out, CodeApiProperties props, List<CodeBean> codes ) throws IOException {
		ExcelWriter.of( CodeBean.class ).addSheet( props.getTitle(), codes ).writeTo( out, false );
	}

	@Override
	public Style beautifyHeader( Workbook wb, String label ) {
		Style style = Style.of( wb, "header:" + label );
		if ( CodeBean.isCode( label ) ) {
			style.alignment( Alignment.CENTER );
		}
		return style.font( "Microsoft YaHei", 10, true ).bgColor( "#D8D8D8" );
	}

	@Override
	public Style beautifyIt( Workbook wb, String label, Object value, CodeBean element, Integer num ) {
		Style style = null;
		boolean even = num % 2 == 0;
		String namesapce = ( even ? "row:even:cell:" : "row:odd:cell:" ) + label;
		if ( CodeBean.isCode( label ) && element.hasColor() ) {
			String color = element.getColor();
			style = Style.of( wb, namesapce + ":" + color ).font( null, color, -1, true );
		}
		if ( style == null ) {
			style = Style.of( wb, namesapce );
		}
		if ( even ) {
			style.bgColor( "#F5F5F5" );
		}
		return style;
	}

	@Override
	public void applyAll( Style style, String label ) {
		style.border( style.is( "header" ) ? BorderStyle.THIN : BorderStyle.HAIR, "#BFBFBF" );
		style.alignment( CodeBean.isCode( label ) ? Alignment.CENTER : Alignment.LEFT_CENTER );
	}

}
