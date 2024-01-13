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
package com.viiyue.plugins.codeapi.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.viiyue.plugins.codeapi.bean.CodeBean;

/**
 * Plugin Helper
 *
 * @author tangxbai
 * @sine 1.0.0
 */
public final class Util {

	/**
	 * Pattern fuzzy to find target data
	 * 
	 * @param <T> the generic type
	 * @param items the list of data that needs to be found 
	 * @param search the search value
	 * @param consumer the match consumer
	 * @return the list of the matched data
	 */
	public static <T> List<T> findByFuzzy( Collection<String> items, String search, Function<String, T> consumer ) {
		if ( StringUtils.isEmpty( search ) ) {
			return null;
		}
		List<T> targets = null;

		// Full matched
		if ( ! ( search.contains( "x" ) || search.contains( "X" ) ) ) {
			targets = new ArrayList<>();
			T result = consumer.apply( search );
			if ( result != null ) {
				targets.add( result );
			}
			return targets;
		}

		// Fuzzy matched
		final char [] searchs = search.toCharArray();
		final int limited = searchs.length;

		// Double cycle judgment
		label :
		for ( String item : items ) {
			if ( StringUtils.isEmpty( item ) ) {
				continue label;
			}
			char [] codes = item.toCharArray();
			int keyLength = codes.length;
			if ( limited > keyLength ) {
				continue label;
			}
			for ( int i = 0; i < limited && i < keyLength; i ++ ) {
				char sc = searchs[ i ];
				if ( ( sc == 'x' || sc == 'X' ) ) {
					continue; // Skip
				}
				char cc = codes[ i ];
				if ( sc != cc ) {
					continue label;
				}
			}
			if ( targets == null ) {
				targets = new ArrayList<>();
			}
			targets.add( consumer.apply( item ) );
		}
		return targets;
	}

	/**
	 * Get the length of the number
	 * 
	 * @param num the target num
	 * @return the length ot the number
	 */
	public static int getNumLength( int num ) {
		num = num > 0 ? num : -num;
		if ( num == 0 ) {
			return 1;
		}
		return ( int ) Math.log10( num ) + 1;
	}

	/**
	 * Get the length of the text
	 * 
	 * @param text the input text string
	 * @return the length of the given text
	 */
	public static int getTextLength( String text ) {
		if ( StringUtils.isEmpty( text ) ) {
			return 0;
		}
		try {
			return text.getBytes( "GBK" ).length;
		} catch ( UnsupportedEncodingException e ) {
			return text.length();
		}
	}

	private static final Comparator<Integer> INT_COMPARATOR = Comparator.comparing( Integer::intValue );

	/**
	 * Export the status code data to the output stream
	 * 
	 * @param out the output stream
	 * @param title the content title
	 * @param data the status code data
	 * @throws IOException if write error
	 */
	public static void export( OutputStream out, String title, List<CodeBean> data ) throws IOException {
		Integer maxOne = data.stream().map( CodeBean::getCode ).max( INT_COMPARATOR ).get();
		Integer width = data.stream().map( b -> getTextLength( b.getMessage() ) ).max( INT_COMPARATOR ).get();
		Integer length = getNumLength( maxOne ) + 1;
		String line = StringUtils.leftPad( "-", length + 4 + width, '-' );
		try ( BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( out ) ) ) {
			writer.write( line );
			writer.newLine();
			for ( CodeBean code : data ) {
				writer.write( String.format( "%" + length + "d", code.getCode() ) + " : " + code.getMessage() );
				writer.newLine();
				writer.write( line );
				writer.newLine();
			}
		}
	}

}
