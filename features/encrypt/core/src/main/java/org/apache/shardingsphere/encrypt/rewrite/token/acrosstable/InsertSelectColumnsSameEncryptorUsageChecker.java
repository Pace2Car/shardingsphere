/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.encrypt.rewrite.token.acrosstable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.encrypt.rule.EncryptRule;
import org.apache.shardingsphere.encrypt.spi.EncryptAlgorithm;
import org.apache.shardingsphere.infra.binder.context.segment.select.projection.Projection;
import org.apache.shardingsphere.infra.binder.context.segment.select.projection.impl.ColumnProjection;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dml.column.ColumnSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.generic.bound.ColumnSegmentBoundInfo;
import org.apache.shardingsphere.sql.parser.statement.core.value.identifier.IdentifierValue;

import java.util.Collection;
import java.util.Iterator;

/**
 * Insert select columns same encryptor usage checker.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InsertSelectColumnsSameEncryptorUsageChecker {
    
    /**
     * Judge whether all insert select columns use same encryptor or not.
     *
     * @param insertColumns insert columns
     * @param projections projections
     * @param encryptRule encrypt rule
     * @return same or different encryptors are using 
     */
    public static boolean isSame(final Collection<ColumnSegment> insertColumns, final Collection<Projection> projections, final EncryptRule encryptRule) {
        Iterator<ColumnSegment> insertColumnsIterator = insertColumns.iterator();
        Iterator<Projection> projectionIterator = projections.iterator();
        while (insertColumnsIterator.hasNext()) {
            ColumnSegment columnSegment = insertColumnsIterator.next();
            EncryptAlgorithm columnEncryptor = encryptRule.findQueryEncryptor(
                    columnSegment.getColumnBoundInfo().getOriginalTable().getValue(), columnSegment.getColumnBoundInfo().getOriginalColumn().getValue()).orElse(null);
            Projection projection = projectionIterator.next();
            ColumnSegmentBoundInfo columnBoundInfo = getColumnSegmentBoundInfo(projection);
            EncryptAlgorithm projectionEncryptor = encryptRule.findQueryEncryptor(columnBoundInfo.getOriginalTable().getValue(), columnBoundInfo.getOriginalColumn().getValue()).orElse(null);
            if (!SameEncryptorComparator.isSame(columnEncryptor, projectionEncryptor)) {
                return false;
            }
        }
        return true;
    }
    
    private static ColumnSegmentBoundInfo getColumnSegmentBoundInfo(final Projection projection) {
        return projection instanceof ColumnProjection
                ? new ColumnSegmentBoundInfo(null, null, ((ColumnProjection) projection).getOriginalTable(), ((ColumnProjection) projection).getOriginalColumn())
                : new ColumnSegmentBoundInfo(new IdentifierValue(projection.getColumnLabel()));
    }
}
