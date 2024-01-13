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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viiyue.plugins.codeapi.utils.Util;

import lombok.Getter;

/**
 * Status code wrapper for spring container bean
 *
 * @author tangxbai
 * @since 1.0.0
 */
@Getter
// @Component // Will be dynamically injected into spring
public class CodeWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Map<String, List<CodeBean>> caches = new ConcurrentHashMap<>( 64 ); // Quick search

	/**
	 * All status code group
	 */
	private final List<CodeGroup> groups;
	
	/**
	 * Status code mapping, You can directly locate basic information by code.
	 */
	private final Map<String, CodeBean> codeMapping;
	
	/**
	 * The JSON string of the status code lists
	 */
	private final String stringify;

	/**
	 * Quickly build the {@link CodeWrapper} object
	 * 
	 * @param groups the status code group
	 * @param om the {@code Jackson} object instance
	 * @throws JsonProcessingException If the JSON conversion fails
	 */
	public CodeWrapper( List<CodeGroup> groups, ObjectMapper om ) throws JsonProcessingException {
		this.groups = groups;
		int capacity = groups.stream().collect( Collectors.summingInt( CodeGroup::size ) );
		this.codeMapping = new HashMap<>( capacity );
		for ( CodeGroup group : groups ) {
			for ( CodeBean code : group.getCodes() ) {
				this.codeMapping.put( String.valueOf( code.getCode() ), code );
			}
		}
		this.stringify = om.writeValueAsString( codeMapping );
	}

	/**
	 * Search directly by code
	 * 
	 * @param code the input code
	 * @return the bean of {@link CodeBean}
	 */
	public CodeBean getByCode( int code ) {
		return codeMapping.get( String.valueOf( code ) );
	}
	
	/**
	 * Get all the status codes and arrange them in order
	 * 
	 * @return the list of List&lt;{@link CodeBean}&gt; results 
	 */
	public List<CodeBean> getAllAndSorting() {
		return codeMapping.values().stream().sorted( Comparator.comparingInt( CodeBean::getCode ) ).collect( Collectors.toList() );
	}

	/**
	 * You can use code to search the status code list, which supports fuzzy lookup 'x' or 'X' (e.g., 2xx, 3x)
	 * 
	 * @param code the search code
	 * @return the list of List&lt;{@link CodeBean}&gt; results
	 */
	public List<CodeBean> getByCode( String code ) {
		return caches.computeIfAbsent( code, k -> {
			CodeBean codeBean = codeMapping.get( code );
			if ( codeBean == null ) {
				List<CodeBean> results = Util.findByFuzzy( codeMapping.keySet(), code, codeMapping::get );
				if ( results != null ) {
					Collections.sort( results, Comparator.comparingInt( CodeBean::getCode ) );
				}
				return results;
			}
			return Arrays.asList( codeBean );
		} );
	}

}
