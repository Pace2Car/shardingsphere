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

package org.apache.shardingsphere.mode.node.path.config.rule;

import org.apache.shardingsphere.mode.node.path.config.rule.item.NamedRuleItemNodePath;
import org.apache.shardingsphere.mode.node.path.config.rule.item.UniqueRuleItemNodePath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class RuleNodePathTest {
    
    private RuleNodePath ruleNodePath;
    
    @BeforeEach
    void setup() {
        List<String> namedRuleItemNodePathTypes = Collections.singletonList("tables");
        List<String> uniqueRuleItemNodePathTypes = Arrays.asList("tables", "tables.type");
        ruleNodePath = new RuleNodePath("foo", namedRuleItemNodePathTypes, uniqueRuleItemNodePathTypes);
    }
    
    @Test
    void assertFindNameByVersion() {
        NamedRuleItemNodePath namedRulePath = ruleNodePath.getNamedItem("tables");
        assertThat(namedRulePath.getPath("foo_tbl"), is("tables/foo_tbl"));
    }
    
    @Test
    void assertGetUniqueItem() {
        UniqueRuleItemNodePath uniqueRulePath = ruleNodePath.getUniqueItem("tables");
        assertThat(uniqueRulePath.getPath(), is("tables"));
        UniqueRuleItemNodePath uniqueRulePathWithType = ruleNodePath.getUniqueItem("type");
        assertThat(uniqueRulePathWithType.getPath(), is("tables/type"));
    }
}
