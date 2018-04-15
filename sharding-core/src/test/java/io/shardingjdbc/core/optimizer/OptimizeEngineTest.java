package io.shardingjdbc.core.optimizer;

import com.google.common.collect.Range;
import io.shardingjdbc.core.api.algorithm.sharding.ListShardingValue;
import io.shardingjdbc.core.api.algorithm.sharding.RangeShardingValue;
import io.shardingjdbc.core.api.algorithm.sharding.ShardingValue;
import io.shardingjdbc.core.parsing.parser.context.condition.AndCondition;
import io.shardingjdbc.core.parsing.parser.context.condition.Column;
import io.shardingjdbc.core.parsing.parser.context.condition.Condition;
import io.shardingjdbc.core.parsing.parser.context.condition.OrCondition;
import io.shardingjdbc.core.parsing.parser.expression.SQLExpression;
import io.shardingjdbc.core.parsing.parser.expression.SQLNumberExpression;
import io.shardingjdbc.core.routing.sharding.ShardingCondition;
import io.shardingjdbc.core.routing.sharding.ShardingConditions;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class OptimizeEngineTest {
    
    @Test
    public void assertOptimizeAlwaysFalseListConditions() {
        Condition condition1 = new Condition(new Column("test", "test"), Arrays.<SQLExpression>asList(new SQLNumberExpression(1), new SQLNumberExpression(2)));
        Condition condition2 = new Condition(new Column("test", "test"), new SQLNumberExpression(3));
        AndCondition andCondition = new AndCondition();
        andCondition.getConditions().add(condition1);
        andCondition.getConditions().add(condition2);
        OrCondition orCondition = new OrCondition();
        orCondition.getAndConditions().add(andCondition);
        ShardingConditions shardingConditions = new OptimizeEngine().optimize(orCondition, Collections.emptyList(), null);
        assertTrue(shardingConditions.isAlwaysFalse());
    }
    
    @Test
    public void assertOptimizeAlwaysFalseRangeConditions() {
        Condition condition1 = new Condition(new Column("test", "test"), new SQLNumberExpression(1), new SQLNumberExpression(2));
        Condition condition2 = new Condition(new Column("test", "test"), new SQLNumberExpression(3), new SQLNumberExpression(4));
        AndCondition andCondition = new AndCondition();
        andCondition.getConditions().add(condition1);
        andCondition.getConditions().add(condition2);
        OrCondition orCondition = new OrCondition();
        orCondition.getAndConditions().add(andCondition);
        ShardingConditions shardingConditions = new OptimizeEngine().optimize(orCondition, Collections.emptyList(), null);
        assertTrue(shardingConditions.isAlwaysFalse());
    }
    
    @Test
    public void assertOptimizeAlwaysFalseListConditionsAndRangeConditions() {
        Condition condition1 = new Condition(new Column("test", "test"), Arrays.<SQLExpression>asList(new SQLNumberExpression(1), new SQLNumberExpression(2)));
        Condition condition2 = new Condition(new Column("test", "test"), new SQLNumberExpression(3), new SQLNumberExpression(4));
        AndCondition andCondition = new AndCondition();
        andCondition.getConditions().add(condition1);
        andCondition.getConditions().add(condition2);
        OrCondition orCondition = new OrCondition();
        orCondition.getAndConditions().add(andCondition);
        ShardingConditions shardingConditions = new OptimizeEngine().optimize(orCondition, Collections.emptyList(), null);
        assertTrue(shardingConditions.isAlwaysFalse());
    }
    
    @Test
    public void assertOptimizeListConditions() {
        Condition condition1 = new Condition(new Column("test", "test"), Arrays.<SQLExpression>asList(new SQLNumberExpression(1), new SQLNumberExpression(2)));
        Condition condition2 = new Condition(new Column("test", "test"), new SQLNumberExpression(1));
        AndCondition andCondition = new AndCondition();
        andCondition.getConditions().add(condition1);
        andCondition.getConditions().add(condition2);
        OrCondition orCondition = new OrCondition();
        orCondition.getAndConditions().add(andCondition);
        ShardingConditions shardingConditions = new OptimizeEngine().optimize(orCondition, Collections.emptyList(), null);
        assertFalse(shardingConditions.isAlwaysFalse());
        ShardingCondition shardingCondition = shardingConditions.getShardingConditions().get(0);
        ShardingValue shardingValue = shardingCondition.getShardingValues().get(0);
        Collection<Comparable<?>> values = ((ListShardingValue<Comparable<?>>) shardingValue).getValues();
        assertThat(values.size(), is(1));
        assertTrue(values.containsAll(Collections.singleton(1)));
    }
    
    @Test
    public void assertOptimizeRangeConditions() {
        Condition condition1 = new Condition(new Column("test", "test"), new SQLNumberExpression(1), new SQLNumberExpression(2));
        Condition condition2 = new Condition(new Column("test", "test"), new SQLNumberExpression(1), new SQLNumberExpression(3));
        AndCondition andCondition = new AndCondition();
        andCondition.getConditions().add(condition1);
        andCondition.getConditions().add(condition2);
        OrCondition orCondition = new OrCondition();
        orCondition.getAndConditions().add(andCondition);
        ShardingConditions shardingConditions = new OptimizeEngine().optimize(orCondition, Collections.emptyList(), null);
        assertFalse(shardingConditions.isAlwaysFalse());
        ShardingCondition shardingCondition = shardingConditions.getShardingConditions().get(0);
        ShardingValue shardingValue = shardingCondition.getShardingValues().get(0);
        Range<Comparable<?>> values = ((RangeShardingValue<Comparable<?>>) shardingValue).getValueRange();
        assertThat(values.lowerEndpoint(), CoreMatchers.<Comparable>is(1));
        assertThat(values.upperEndpoint(), CoreMatchers.<Comparable>is(2));
    }
    
    @Test
    public void assertOptimizeListConditionsAndRangeConditions() {
        Condition condition1 = new Condition(new Column("test", "test"), Arrays.<SQLExpression>asList(new SQLNumberExpression(1), new SQLNumberExpression(2)));
        Condition condition2 = new Condition(new Column("test", "test"), new SQLNumberExpression(1), new SQLNumberExpression(2));
        AndCondition andCondition = new AndCondition();
        andCondition.getConditions().add(condition1);
        andCondition.getConditions().add(condition2);
        OrCondition orCondition = new OrCondition();
        orCondition.getAndConditions().add(andCondition);
        ShardingConditions shardingConditions = new OptimizeEngine().optimize(orCondition, Collections.emptyList(), null);
        assertFalse(shardingConditions.isAlwaysFalse());
        ShardingCondition shardingCondition = shardingConditions.getShardingConditions().get(0);
        ShardingValue shardingValue = shardingCondition.getShardingValues().get(0);
        Collection<Comparable<?>> values = ((ListShardingValue<Comparable<?>>) shardingValue).getValues();
        assertThat(values.size(), is(2));
        assertTrue(values.containsAll(Arrays.asList(1, 2)));
    }
    
}
