/*
 * Licensed to Metamarkets Group Inc. (Metamarkets) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Metamarkets licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.druid.indexing.appenderator;

import io.druid.indexing.common.actions.SegmentAllocateAction;
import io.druid.indexing.common.actions.TaskActionClient;
import io.druid.segment.indexing.DataSchema;
import io.druid.segment.realtime.appenderator.SegmentAllocator;
import io.druid.segment.realtime.appenderator.SegmentIdentifier;
import org.joda.time.DateTime;

import java.io.IOException;

public class ActionBasedSegmentAllocator implements SegmentAllocator
{
  private final TaskActionClient taskActionClient;
  private final DataSchema dataSchema;
  private final String sequenceName;

  public ActionBasedSegmentAllocator(
      TaskActionClient taskActionClient,
      DataSchema dataSchema,
      String sequenceName
  )
  {
    this.taskActionClient = taskActionClient;
    this.dataSchema = dataSchema;
    this.sequenceName = sequenceName;
  }

  @Override
  public SegmentIdentifier allocate(
      final DateTime timestamp,
      final String previousSegmentId
  ) throws IOException
  {
    return taskActionClient.submit(
        new SegmentAllocateAction(
            dataSchema.getDataSource(),
            timestamp,
            dataSchema.getGranularitySpec().getQueryGranularity(),
            dataSchema.getGranularitySpec().getSegmentGranularity(),
            sequenceName,
            previousSegmentId
        )
    );
  }
}
