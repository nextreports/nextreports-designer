/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ro.nextreports.designer.ui.sqleditor.syntax;

import java.io.Serializable;

/**
 * @author Decebal Suiu
 */
public class Token implements Serializable, Comparable<Token> {

    public TokenType type;
    public int start;
    public int length;

    public Token(TokenType type, int start, int length) {
        this.type = type;
        this.start = start;
        this.length = length;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Object) {
            Token token = (Token) obj;
            return ((this.start == token.start) &&
                    (this.length == token.length) &&
                    (this.type.equals(token.type)));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return start;
    }

    @Override
    public String toString() {
        return String.format("%s (%d, %d)", type, start, length);
    }

    public int compareTo(Token token) {
        if (this.start !=  token.start) {
            return (this.start - token.start);
        } else if(this.length != token.length) {
            return (this.length - token.length);
        } else {
            return this.type.compareTo(token.type);
        }
    }
    
}

