/*
 * Copyright 2019 Symphony Communication Services, LLC.
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

package com.symphony.oss.allegro.api;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.symphonyoss.s2.common.hash.Hash;

import com.symphony.oss.models.chat.canon.facade.ThreadId;
import com.symphony.oss.models.fundamental.canon.facade.IOpenSigningKey;
import com.symphony.oss.models.fundamental.canon.facade.IOpenSimpleSecurityContext;
import com.symphony.oss.models.fundamental.canon.facade.RotationId;

/**
 * Interface for all cryptographic capabilities used by Allegro.
 *  
 * @author Bruce Skingle
 *
 */
public interface IAllegroCryptoClient
{
  String decrypt(ThreadId threadId, String cipherText);

  RotationId getRotationForThread(ThreadId threadId);

  /**
   * Encrypt the given clear text with the content key for the given thread.
   * 
   * @param threadId The id of the thread.
   * @param clearText Text to be encrypted.
   * 
   * @return cipher text.
   */
  String encrypt(ThreadId threadId, String clearText);

  String encryptTagV1(String plaintext);

  String encryptTagV2(String plaintext);

  List<String> tokenize(ThreadId threadId, String clear, Set<String> clearTokens);

  IOpenSimpleSecurityContext getOrCreateThreadSecurityContext(ThreadId threadId);

  IOpenSigningKey getSigningKey();

  IOpenSimpleSecurityContext getSecurityContext(Hash securityContextHash, @Nullable ThreadId threadId);

}
