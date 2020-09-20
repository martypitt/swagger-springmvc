/*
 *
 *  Copyright 2015-2020 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.swagger2.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UserInputLogEncoder {
    private UserInputLogEncoder() {
    }

    public static String urlEncode(String userInput) {
        String sanitizedUserInput = userInput;
        try {
            sanitizedUserInput = URLEncoder.encode(userInput, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ignored) {
            // cannot happen, UTF_8 is supported
        }
        return sanitizedUserInput;
    }
}
