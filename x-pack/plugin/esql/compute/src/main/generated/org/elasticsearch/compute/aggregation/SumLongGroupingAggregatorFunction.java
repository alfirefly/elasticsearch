// Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
// or more contributor license agreements. Licensed under the Elastic License
// 2.0; you may not use this file except in compliance with the Elastic License
// 2.0.
package org.elasticsearch.compute.aggregation;

import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.List;
import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.compute.data.AggregatorStateVector;
import org.elasticsearch.compute.data.Block;
import org.elasticsearch.compute.data.IntVector;
import org.elasticsearch.compute.data.LongBlock;
import org.elasticsearch.compute.data.LongVector;
import org.elasticsearch.compute.data.Page;
import org.elasticsearch.compute.data.Vector;

/**
 * {@link GroupingAggregatorFunction} implementation for {@link SumLongAggregator}.
 * This class is generated. Do not edit it.
 */
public final class SumLongGroupingAggregatorFunction implements GroupingAggregatorFunction {
  private final LongArrayState state;

  private final List<Integer> channels;

  public SumLongGroupingAggregatorFunction(List<Integer> channels, LongArrayState state) {
    this.channels = channels;
    this.state = state;
  }

  public static SumLongGroupingAggregatorFunction create(List<Integer> channels,
      BigArrays bigArrays) {
    return new SumLongGroupingAggregatorFunction(channels, new LongArrayState(bigArrays, SumLongAggregator.init()));
  }

  @Override
  public void addRawInput(LongVector groups, Page page) {
    assert groups.getPositionCount() == page.getPositionCount();
    Block uncastValuesBlock = page.getBlock(channels.get(0));
    if (uncastValuesBlock.areAllValuesNull()) {
      addRawInputAllNulls(groups, uncastValuesBlock);
      return;
    }
    LongBlock valuesBlock = (LongBlock) uncastValuesBlock;
    LongVector valuesVector = valuesBlock.asVector();
    if (valuesVector == null) {
      addRawInput(groups, valuesBlock);
    } else {
      addRawInput(groups, valuesVector);
    }
  }

  private void addRawInput(LongVector groups, LongBlock values) {
    for (int position = 0; position < groups.getPositionCount(); position++) {
      int groupId = Math.toIntExact(groups.getLong(position));
      if (values.isNull(position)) {
        state.putNull(groupId);
        continue;
      }
      int valuesStart = values.getFirstValueIndex(position);
      int valuesEnd = valuesStart + values.getValueCount(position);
      for (int v = valuesStart; v < valuesEnd; v++) {
        state.set(SumLongAggregator.combine(state.getOrDefault(groupId), values.getLong(v)), groupId);
      }
    }
  }

  private void addRawInput(LongVector groups, LongVector values) {
    for (int position = 0; position < groups.getPositionCount(); position++) {
      int groupId = Math.toIntExact(groups.getLong(position));
      state.set(SumLongAggregator.combine(state.getOrDefault(groupId), values.getLong(position)), groupId);
    }
  }

  private void addRawInputAllNulls(LongVector groups, Block values) {
    for (int position = 0; position < groups.getPositionCount(); position++) {
      int groupId = Math.toIntExact(groups.getLong(position));
      assert values.isNull(position);
      state.putNull(groupId);
    }
  }

  @Override
  public void addRawInput(LongBlock groups, Page page) {
    assert groups.getPositionCount() == page.getPositionCount();
    Block uncastValuesBlock = page.getBlock(channels.get(0));
    if (uncastValuesBlock.areAllValuesNull()) {
      addRawInputAllNulls(groups, uncastValuesBlock);
      return;
    }
    LongBlock valuesBlock = (LongBlock) uncastValuesBlock;
    LongVector valuesVector = valuesBlock.asVector();
    if (valuesVector == null) {
      addRawInput(groups, valuesBlock);
    } else {
      addRawInput(groups, valuesVector);
    }
  }

  private void addRawInput(LongBlock groups, LongBlock values) {
    for (int position = 0; position < groups.getPositionCount(); position++) {
      if (groups.isNull(position)) {
        continue;
      }
      int groupStart = groups.getFirstValueIndex(position);
      int groupEnd = groupStart + groups.getValueCount(position);
      for (int g = groupStart; g < groupEnd; g++) {
        int groupId = Math.toIntExact(groups.getLong(g));
        if (values.isNull(position)) {
          state.putNull(groupId);
          continue;
        }
        int valuesStart = values.getFirstValueIndex(position);
        int valuesEnd = valuesStart + values.getValueCount(position);
        for (int v = valuesStart; v < valuesEnd; v++) {
          state.set(SumLongAggregator.combine(state.getOrDefault(groupId), values.getLong(v)), groupId);
        }
      }
    }
  }

  private void addRawInput(LongBlock groups, LongVector values) {
    for (int position = 0; position < groups.getPositionCount(); position++) {
      if (groups.isNull(position)) {
        continue;
      }
      int groupStart = groups.getFirstValueIndex(position);
      int groupEnd = groupStart + groups.getValueCount(position);
      for (int g = groupStart; g < groupEnd; g++) {
        int groupId = Math.toIntExact(groups.getLong(g));
        state.set(SumLongAggregator.combine(state.getOrDefault(groupId), values.getLong(position)), groupId);
      }
    }
  }

  private void addRawInputAllNulls(LongBlock groups, Block values) {
    for (int position = 0; position < groups.getPositionCount(); position++) {
      if (groups.isNull(position)) {
        continue;
      }
      int groupStart = groups.getFirstValueIndex(position);
      int groupEnd = groupStart + groups.getValueCount(position);
      for (int g = groupStart; g < groupEnd; g++) {
        int groupId = Math.toIntExact(groups.getLong(g));
        assert values.isNull(position);
        state.putNull(groupId);
      }
    }
  }

  @Override
  public void addIntermediateInput(LongVector groupIdVector, Page page) {
    Block block = page.getBlock(channels.get(0));
    Vector vector = block.asVector();
    if (vector == null || vector instanceof AggregatorStateVector == false) {
      throw new RuntimeException("expected AggregatorStateBlock, got:" + block);
    }
    @SuppressWarnings("unchecked") AggregatorStateVector<LongArrayState> blobVector = (AggregatorStateVector<LongArrayState>) vector;
    // TODO exchange big arrays directly without funny serialization - no more copying
    BigArrays bigArrays = BigArrays.NON_RECYCLING_INSTANCE;
    LongArrayState inState = new LongArrayState(bigArrays, SumLongAggregator.init());
    blobVector.get(0, inState);
    for (int position = 0; position < groupIdVector.getPositionCount(); position++) {
      int groupId = Math.toIntExact(groupIdVector.getLong(position));
      if (inState.hasValue(position)) {
        state.set(SumLongAggregator.combine(state.getOrDefault(groupId), inState.get(position)), groupId);
      } else {
        state.putNull(groupId);
      }
    }
    inState.close();
  }

  @Override
  public void addIntermediateRowInput(int groupId, GroupingAggregatorFunction input, int position) {
    if (input.getClass() != getClass()) {
      throw new IllegalArgumentException("expected " + getClass() + "; got " + input.getClass());
    }
    LongArrayState inState = ((SumLongGroupingAggregatorFunction) input).state;
    if (inState.hasValue(position)) {
      state.set(SumLongAggregator.combine(state.getOrDefault(groupId), inState.get(position)), groupId);
    } else {
      state.putNull(groupId);
    }
  }

  @Override
  public void evaluateIntermediate(Block[] blocks, int offset, IntVector selected) {
    AggregatorStateVector.Builder<AggregatorStateVector<LongArrayState>, LongArrayState> builder =
        AggregatorStateVector.builderOfAggregatorState(LongArrayState.class, state.getEstimatedSize());
    builder.add(state, selected);
    blocks[offset] = builder.build().asBlock();
  }

  @Override
  public void evaluateFinal(Block[] blocks, int offset, IntVector selected) {
    blocks[offset] = state.toValuesBlock(selected);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName()).append("[");
    sb.append("channels=").append(channels);
    sb.append("]");
    return sb.toString();
  }

  @Override
  public void close() {
    state.close();
  }
}
