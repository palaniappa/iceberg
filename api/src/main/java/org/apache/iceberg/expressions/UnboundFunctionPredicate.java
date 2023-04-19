/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.iceberg.expressions;

import java.util.List;
import java.util.Set;
import org.apache.iceberg.relocated.com.google.common.collect.Iterables;
import org.apache.iceberg.types.Types;

public class UnboundFunctionPredicate<T> extends UnboundPredicate<T> {

  private final String functionName;

  public UnboundFunctionPredicate(
      Operation op, UnboundTerm<T> term, Iterable<T> values, String functionName) {
    super(op, term, values);
    this.functionName = functionName;
  }

  public String getFunctionName() {
    return functionName;
  }

  @Override
  public Expression bind(Types.StructType struct, boolean caseSensitive) {
    BoundTerm<T> bound = term().bind(struct, caseSensitive);
    if (op() == Operation.IN) {
      List<Literal<T>> convertedLiterals = getConvertedLiterals(bound);
      Set<T> literalSet = setOf(convertedLiterals);
      if (literalSet.size() == 1) {
        return new BoundLiteralFunctionPredicate<>(
            Operation.EQ, bound, Iterables.get(convertedLiterals, 0), functionName);
      }
    }
    return super.bind(struct, caseSensitive);
  }
}
