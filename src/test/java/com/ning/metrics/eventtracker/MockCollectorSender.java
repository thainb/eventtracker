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

import com.google.inject.Inject;
import com.ning.metrics.serialization.event.Event;

import java.io.IOException;

class MockCollectorSender implements EventSender
{
    private MockCollectorClient collectorClient;
    private Event receivedEvent;

    @Inject
    public MockCollectorSender()
    {
        this.collectorClient = new MockCollectorClient();
    }

    @Override
    public boolean send(Event event) throws IOException
    {
        receivedEvent = event;
        return collectorClient.postThrift(event);
    }

    public void setFail(boolean fail)
    {
        collectorClient.setFail(fail);
    }

    public void clear()
    {
        collectorClient.clear();
    }

    public long getSuccessCount()
    {
        return collectorClient.getSuccessCount();
    }

    public Event getReceivedEvent()
    {
        return receivedEvent;
    }
}
