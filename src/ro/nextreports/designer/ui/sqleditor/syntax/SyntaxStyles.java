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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.text.Segment;
import javax.swing.text.TabExpander;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Styles to use for each TokenType. The defaults are created here, and
 * then the resource syntaxstyles.properties is read and merged. 
 * You can also pass a properties instance and merge your prefered
 * styles into the default styles.
 * 
 * @author Decebal Suiu
 */
public class SyntaxStyles {
    
    private static final Log LOG = LogFactory.getLog(SyntaxStyles.class.getName());
    
    private static SyntaxStyle DEFAULT_STYLE = new SyntaxStyle(Color.BLACK, Font.PLAIN);
    private static SyntaxStyles instance = createInstance();

    private Map<TokenType, SyntaxStyle> styles;
    
    private SyntaxStyles() {
    }

    public static SyntaxStyles getInstance() {
        return instance;
    }

    /**
     * Set the graphics font and others to the style for the given token
     * @param g
     * @param type
     */
    @Deprecated
    public void setGraphicsStyle(Graphics g, TokenType type) {
        SyntaxStyle ss = styles.get(type);
        if (ss != null) {
            g.setFont(g.getFont().deriveFont(ss.getFontStyle()));
            g.setColor(ss.getColor());
        } else {
            g.setFont(g.getFont().deriveFont(Font.PLAIN));
            g.setColor(Color.BLACK);
        }
    }

    /**
     * Return the style for the given TokenType.
     * 
     * @param type
     * @return
     */
    public SyntaxStyle getStyle(TokenType type) {
        if (styles.containsKey(type)) {
            return styles.get(type);
        } else {
            return DEFAULT_STYLE;
        }
    }

    /**
     * Draw the given Token. This will simply find the proper SyntaxStyle for
     * the TokenType and then asks the proper Style to draw the text of the
     * Token.
     * 
     * @param segment
     * @param x
     * @param y
     * @param graphics
     * @param e
     * @param token
     * @return
     */
    public int drawText(Segment segment, int x, int y,
            Graphics graphics, TabExpander e, Token token) {
        SyntaxStyle syntaxStyle = getStyle(token.type);
        return syntaxStyle.drawText(segment, x, y, graphics, e, token.start);
    }

    public void put(TokenType type, SyntaxStyle style) {
        if (styles == null) {
            styles = new HashMap<TokenType, SyntaxStyle>();
        }
        styles.put(type, style);
    }

    /**
     * You can call the mergeStyles method with a Properties file to customize
     * the existing styles.  Any existing styles will be overwritten by the
     * styles you provide.
     * 
     * @param styles
     * @param s
     */
    public void mergeStyles(Properties styles) {
        for (String token : getPropertyNames(styles)) {
            String property = styles.getProperty(token);
            try {
                TokenType tokenType = TokenType.valueOf(token);
                SyntaxStyle tokenStyle = new SyntaxStyle(property);
                put(tokenType, tokenStyle);
            } catch (IllegalArgumentException e) {
                LOG.warn("illegal token type or style for: " + token);
            }
        }
    }

    /**
     * Create default styles.
     * 
     * @return
     */
    private static SyntaxStyles createInstance() {
        SyntaxStyles syntaxstyles = new SyntaxStyles();
        Properties styles = new Properties();
        try {
			styles.load(SyntaxStyles.class.getResourceAsStream("/syntaxstyles.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
        syntaxstyles.mergeStyles(styles);
        
        return syntaxstyles;
    }
    
    private Set<String> getPropertyNames(Properties properties) {
    	Set<Object> keys = properties.keySet();
    	Set<String> propertyNames = new HashSet<String>(keys.size());
    	for (Object key : keys) {
    		propertyNames.add((String) key);
    	}
    	
    	return propertyNames;
	}
    
}
