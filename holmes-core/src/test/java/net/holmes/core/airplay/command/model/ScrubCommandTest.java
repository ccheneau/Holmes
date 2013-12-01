/*
 * Copyright (C) 2012-2013  Cedric Cheneau
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.holmes.core.airplay.command.model;


import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;

public class ScrubCommandTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScrubCommandTest.class);

    @Test
    public void tesScrubCommand() {
        ScrubCommand scrubCommand = new ScrubCommand(0d);
        HttpRequestBase request = scrubCommand.getHttpRequest("127.0.0.1", 8080);
        assertNotNull(request);
        LOGGER.debug(request.toString());
    }
}
