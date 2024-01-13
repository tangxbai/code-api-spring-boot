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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AssignableTypeFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viiyue.plugins.codeapi.CodeDescriptor;
import com.viiyue.plugins.codeapi.CodeExporter;
import com.viiyue.plugins.codeapi.bean.CodeGroup;
import com.viiyue.plugins.codeapi.bean.CodeWrapper;
import com.viiyue.plugins.codeapi.downloader.CodeDownloader;
import com.viiyue.plugins.codeapi.downloader.ExcelDownloader;
import com.viiyue.plugins.codeapi.downloader.TextDownloader;
import com.viiyue.plugins.codeapi.ui.CodeApiController;

/**
 * CodeApi Registrar
 *
 * @author tangxbai
 * @sine 1.0.0
 */
@EnableConfigurationProperties( CodeApiProperties.class )
public class CodeApiRegistrar implements EnvironmentAware, ResourceLoaderAware, ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger( CodeApiRegistrar.class );

	private Environment environment;
	private ResourceLoader resourceLoader;
	private String defaultPackage = "*";
	
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass( name = CodeDownloader.EXCEL_DOWNLOADER )
	public CodeDownloader excelDownloader() {
		return new ExcelDownloader();
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnMissingClass( CodeDownloader.EXCEL_DOWNLOADER )
	public CodeDownloader txtDownloader() {
		return new TextDownloader();
	}

	@Bean
	@Primary
	public CodeWrapper codeWrapper( CodeApiProperties props, ObjectProvider<ObjectMapper> omop )
			throws JsonProcessingException {
		return new CodeWrapper( scanStatusCodes( props ), omop.getIfAvailable( ObjectMapper::new ) );
	}

	@Bean
	@Primary
	public CodeApiController codeApiController( CodeApiProperties props, CodeWrapper codeWrapper, CodeDownloader downloader ) {
		return new CodeApiController( props, codeWrapper, downloader );
	}

	@Override
	public void setEnvironment( Environment environment ) {
		this.environment = environment;
	}

	@Override
	public void setResourceLoader( ResourceLoader resourceLoader ) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void setApplicationContext( ApplicationContext applicationContext ) throws BeansException {
		String [] starter = applicationContext.getBeanNamesForAnnotation( SpringBootApplication.class );
		if ( ArrayUtils.isNotEmpty( starter ) ) {
			Object bean = applicationContext.getBean( starter[ 0 ] );
			this.defaultPackage = bean.getClass().getPackage().getName();
		}
	}

	protected ClassPathScanningCandidateComponentProvider getScanner() {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider( false,
				this.environment );
		provider.setResourceLoader( this.resourceLoader );
		provider.addIncludeFilter( new AssignableTypeFilter( CodeExporter.class ) );
		return provider;
	}

	private List<CodeGroup> scanStatusCodes( CodeApiProperties props ) {
		ClassPathScanningCandidateComponentProvider scanner = getScanner();
		String basePackage = StringUtils.defaultIfEmpty( props.getBasePackage(), defaultPackage );
		Set<BeanDefinition> components = scanner.findCandidateComponents( basePackage );
		List<CodeGroup> elements = new ArrayList<>( components.size() );
		for ( BeanDefinition def : components ) {
			try {
				Class<?> beanType = ( ( AbstractBeanDefinition ) def ).resolveBeanClass( null );
				if ( beanType.isInterface() ) {
					continue;
				}
				if ( beanType.isEnum() ) {
					elements.add( getFromEnum( ( Class<CodeExporter> ) beanType ) );
				} else {
					elements.add( getFromBean( ( Class<CodeExporter> ) beanType ) );
				}
			} catch ( ClassNotFoundException e ) {
				if ( LOG.isErrorEnabled() ) {
					LOG.error( e.getMessage(), e );
				} else {
					e.printStackTrace();
				}
			}
		}
		return elements;
	}

	private CodeGroup getFromBean( Class<CodeExporter> beanType ) {
		CodeGroup group = newGroup( beanType, 1 );
		CodeExporter exporter = BeanUtils.instantiateClass( beanType );
		group.add( exporter );
		return group;
	}

	private CodeGroup getFromEnum( Class<CodeExporter> enumType ) {
		CodeExporter [] codes = enumType.getEnumConstants();
		CodeGroup group = newGroup( enumType, codes.length );
		for ( CodeExporter code : codes ) {
			group.add( code );
		}
		return group;
	}

	private CodeGroup newGroup( Class<?> beanType, int initSize ) {
		CodeDescriptor descriptor = beanType.getAnnotation( CodeDescriptor.class );
		String groupName = descriptor == null ? beanType.getName() : descriptor.value();
		return new CodeGroup( groupName, descriptor == null ? null : descriptor.theme(), initSize );
	}

}
