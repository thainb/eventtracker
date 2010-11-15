/*
 * Copyright 2010 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.ning.metrics.eventtracker;

import com.ning.metrics.serialization.event.Event;
import com.ning.metrics.serialization.event.ThriftToThriftEnvelopeEvent;
import org.apache.thrift.transport.TTransportException;
import org.joda.time.DateTime;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import scribe.thrift.LogEntry;
import scribe.thrift.ResultCode;

import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ScribeSenderTest
{
    private ScribeMockClient scribeClient;
    private ScribeSender scribeSender;
    private Event thriftEvent;

    static class ScribeMockClient implements ScribeClient
    {
        private String host;
        private int port;

        public int getMessagesSent()
        {
            return messagesSent;
        }

        private int messagesSent = 0;

        public ScribeMockClient(String host, int port)
        {
            this.host = host;
            this.port = port;
        }

        public void openLogger() throws TTransportException
        {
            // no-op
        }

        public ResultCode log(List<LogEntry> messages)
        {
            messagesSent += messages.size();
            return ResultCode.OK;
        }

        public void closeLogger()
        {
            // no-op
        }
    }

    @BeforeTest
    public void setUp()
    {
        this.scribeClient = new ScribeMockClient("localhost", 1242);
        this.scribeSender = new ScribeSender(scribeClient, 1000);
        byte[] data = {1, 2, 3, 4, 5};
        this.thriftEvent = ThriftToThriftEnvelopeEvent.extractEvent("thrift", new DateTime(), new Click("thrift", 12, new String(data)));
    }

    @AfterTest
    public void tearDown()
    {
        scribeClient.closeLogger();
    }

    @Test(groups = "fast")
    public void testSendNullScribeSender() throws Exception
    {
        assertFalse(new ScribeSender(null, 0).send(thriftEvent));
    }

    @Test(groups = "fast")
    public void testSend() throws Exception
    {
        assertTrue(scribeSender.send(thriftEvent));
    }
}