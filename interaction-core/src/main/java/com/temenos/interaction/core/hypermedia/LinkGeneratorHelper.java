package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides utility methods for Link Generator.
 */
public class LinkGeneratorHelper {

    private static final Logger logger = LoggerFactory.getLogger(LinkGeneratorHelper.class);

    /**
     * Replace multivalue params in inputParam with entries in the properties map. Example: For inputParam "Id={A.B.C}"
     * and a map having A.B.C = value, return "Id={value}"
     * <p>
     * Additional process is required where the replacement value resolves to a criteria. Example: For inputParam
     * "{A.B.C}" and a map having A.B.C = 'PRODUCT.ID LE 12345', return "ProductId le %2712345%27"
     * 
     * @param inputValue
     * @param properties
     * @return
     */
    public static String replaceParamValue(String inputParam, Map<String, Object> properties) {
        String result = inputParam;
        try {
            boolean requiresSpecialParsing = false;
            if (inputParam != null && inputParam.contains("{") && inputParam.contains("}")) {
                Matcher m = HypermediaTemplateHelper.TEMPLATE_PATTERN.matcher(inputParam);

                while (m.find()) {
                    String param = m.group(1);
                    if (properties.containsKey(param)) {
                        // replace template tokens
                        String paramValue = properties.get(param).toString();
                        if (isCriteriaParam(paramValue)) {
                            requiresSpecialParsing = true;
                            result = result.replaceAll("\\{" + Pattern.quote(param) + "\\}", paramValue);
                        } else {
                            result = result.replaceAll("\\{" + Pattern.quote(param) + "\\}", URLEncoder.encode(paramValue, "UTF-8"));
                        }
                    }
                }
            }

            if (requiresSpecialParsing) {
                result = processCriteriaParam(result);
            }

        } catch (Exception e) {
            logger.error("An error occurred while replacing tokens in [" + inputParam + "]", e);
        }

        // Don't return params like " '' and '' "
        if (StringUtils.isBlank(result.trim().replaceAll("'", "").replaceAll("and", "").replaceAll("or", ""))) {
            return null;
        }

        return result;
    }

    private static String processCriteriaParam(String inputParam) throws UnsupportedEncodingException {
        StringBuilder encodedInputParam = new StringBuilder();
        String[] tokens = inputParam.replaceAll("'", "").split(" ");
        boolean encodeNextEntry = false;
        boolean setCamelCase = true;
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (setCamelCase) {
                String camelCaseToken = WordUtils.capitalizeFully(token.replaceAll("\\."," ")).replace(" ", "");
                encodedInputParam.append(camelCaseToken + " ");
                setCamelCase = false;
            } else if (isRelationalOperator(token)) {
                encodedInputParam.append(token.toLowerCase() + " ");
                encodeNextEntry = true;
            } else if (encodeNextEntry) {
                encodedInputParam.append(URLEncoder.encode("'" + token + "'", "UTF-8") + " ");
                encodeNextEntry = false;
            } else if (StringUtils.containsIgnoreCase(token, "AND") || StringUtils.containsIgnoreCase(token, "OR")) {
                encodedInputParam.append(token.toLowerCase() + " ");
                setCamelCase = true;
            } else {
                encodedInputParam.append(token + " ");
            }
        }

        return removeMissingParams(encodedInputParam.toString().trim());
    }

    private static String removeMissingParams(String encodedInputParamStr) {
        String cleanParams = encodedInputParamStr;
        while (cleanParams.startsWith("and ")) {
            cleanParams = cleanParams.substring(4);
        }
        while (cleanParams.startsWith("or ")) {
            cleanParams = cleanParams.substring(3);
        }
        while (cleanParams.endsWith(" and")) {
            cleanParams = cleanParams.substring(0, cleanParams.length() - 4);
        }
        while (cleanParams.endsWith(" or")) {
            cleanParams = cleanParams.substring(0, cleanParams.length() - 3);
        }
        return cleanParams.replaceAll("and  ", "");
    }

    private static boolean isCriteriaParam(String input) {
        return StringUtils.containsIgnoreCase(input, " EQ ") || StringUtils.containsIgnoreCase(input, " GT ") || StringUtils.containsIgnoreCase(input, " LT ") || StringUtils.containsIgnoreCase(input, " GE ") || StringUtils.containsIgnoreCase(input, " LE ");
    }

    private static boolean isRelationalOperator(String input) {
        return StringUtils.containsIgnoreCase(input, "EQ") || StringUtils.containsIgnoreCase(input, "GT") || StringUtils.containsIgnoreCase(input, "LT") || StringUtils.containsIgnoreCase(input, "GE") || StringUtils.containsIgnoreCase(input, "LE");
    }

}
