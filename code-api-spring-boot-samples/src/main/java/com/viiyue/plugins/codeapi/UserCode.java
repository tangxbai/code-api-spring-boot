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
package com.viiyue.plugins.codeapi;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@CodeDescriptor( "用户业务模块（500xx）" )
public enum UserCode implements CodeExporter {

    A( 50001, "账户余额不足，请先充值" ),
    B( 50002, "账户等级不足，无法继续操作" ),
    C( 50003, "抱歉，你无操作权限" ),
    D( 50004, "你异常登录次数超过限制，请{0}分钟之后再试" ),
    E( 50005, "为了保证你的账户安全，请定时更改密码为了保证你的账户安全，请定时更改密码为了保证你的账户安全，请定时更改密码为了保证你的账户安全，请定时更改密码为了保证你的账户安全，请定时更改密码" ),
    F( 50006, "系统异常" ),
    H( 50008, "系统异常" ),
    I( 50009, "系统异常" ),
    J( 50010, "系统异常" ),
    K( 50011, "系统异常" ),
    L( 50012, "系统异常" ),
    M( 50013, "系统异常" ),
    N( 50015, "系统异常" ),
    O( 50015, "系统异常" ),
    P( 50016, "系统异常" ),
    Q( 50017, "系统异常" ),
    R( 50018, "系统异常" ),
    S( 50019, "系统异常" ),
    T( 50020, "系统异常" ),
    U( 50021, "系统异常" ),
    V( 50022, "系统异常" ),
    W( 50023, "系统异常" ),
    X( 50024, "系统异常" ),
    Y( 50025, "系统异常" ),
    Z( 50026, "系统异常" );

    private int code;
    private String message;

}
