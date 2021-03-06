/*
 *
 *
 * Copyright 2020 Symphony Communication Services, LLC.
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

package com.symphony.oss.allegro.ui;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.allegro.api.IAllegroMultiTenantApi;
import com.symphony.oss.canon.runtime.exception.CanonException;
import com.symphony.oss.commons.hash.Hash;
import com.symphony.oss.models.core.canon.facade.ThreadId;
import com.symphony.oss.models.object.canon.IEncryptedApplicationPayload;
import com.symphony.oss.models.object.canon.facade.ApplicationObjectPayload;
import com.symphony.oss.models.object.canon.facade.IApplicationObjectPayload;
import com.symphony.oss.models.object.canon.facade.IStoredApplicationObject;

class InsertObjectServlet extends AbstractObjectServlet
{
  private static final long            serialVersionUID = 1L;

  private final IAllegroMultiTenantApi accessApi_;
  private final IAllegroApi            userApi_;

  InsertObjectServlet(IAllegroMultiTenantApi accessApi, IAllegroApi userApi)
  {
    accessApi_ = accessApi;
    userApi_ = userApi;
  }

  @Override
  public String getUrlPath()
  {
    return "/insert";
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
  {
    try
    {
      handle(req, resp);
    }
    catch(CanonException e)
    {
      resp.sendError(e.getHttpStatusCode(), e.getLocalizedMessage());
    }
    catch(RuntimeException e)
    {
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
    }
  }

  private void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException
  {
    Map<String, String[]> params = req.getParameterMap();

    System.out.println("params " + params);
    
    Hash      partitionHash = getHash(req, Projection.ATTRIBUTEID_PARTITION_HASH);
    ThreadId  threadId      = getThreadId(req, Projection.ATTRIBUTEID_THREAD_ID);
    String    json          = getString(req, "payload");
    String    sortKey       = getSortKey(req, Projection.ATTRIBUTEID_SORT_KEY);
    
    IApplicationObjectPayload payload = new ApplicationObjectPayload(json);
    IEncryptedApplicationPayload encryptedPayload = userApi_.newEncryptedApplicationPayloadBuilder()
      .withThreadId(threadId)
      .withPayload(payload)
      .build();
    
    IStoredApplicationObject storedObject = accessApi_.newEncryptedApplicationObjectBuilder()
      .withEncryptedPayload(encryptedPayload)
      .withPartition(partitionHash)
      .withThreadId(threadId)
      .withSortKey(sortKey)
      .build();
    
    accessApi_.store(storedObject);
  }
}