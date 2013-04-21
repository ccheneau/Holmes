/*
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
 *
 */

package com.sun.syndication.feed.module.opensearch;

/**
 * The Class RequiredAttributeMissingException.
 *
 * @author Michael W. Nassif (enrouteinc@gmail.com)
 */
public class RequiredAttributeMissingException extends RuntimeException {
    private static final long serialVersionUID = 4513552369632251956L;

    /**
     * Constructor.
     *
     * @param arg0 the arg0
     * @param arg1 the arg1
     */
    public RequiredAttributeMissingException(final String arg0, final Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * Constructor.
     *
     * @param arg0 the arg0
     */
    public RequiredAttributeMissingException(final String arg0) {
        super(arg0);
    }

    /**
     * Constructor.
     *
     * @param arg0 the arg0
     */
    public RequiredAttributeMissingException(final Throwable arg0) {
        super(arg0);
    }
}
