package org.obiba.magma.support;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Set;

import org.obiba.magma.Datasource;
import org.obiba.magma.MagmaEngine;
import org.obiba.magma.NoSuchValueSetException;
import org.obiba.magma.NoSuchVariableException;
import org.obiba.magma.Timestamps;
import org.obiba.magma.Value;
import org.obiba.magma.ValueSet;
import org.obiba.magma.ValueTable;
import org.obiba.magma.Variable;
import org.obiba.magma.VariableEntity;
import org.obiba.magma.VariableValueSource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class NullValueTable implements ValueTable {

  private static WeakReference<NullValueTable> instance = MagmaEngine.get().registerInstance(new NullValueTable());

  private NullValueTable() {
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public Datasource getDatasource() {
    return null;
  }

  @Override
  public String getEntityType() {
    return "";
  }

  @Override
  public boolean isForEntityType(String entityType) {
    return false;
  }

  public Set<VariableEntity> getVariableEntities() {
    return ImmutableSet.of();
  }

  @Override
  public boolean hasValueSet(VariableEntity entity) {
    return false;
  }

  @Override
  public Iterable<ValueSet> getValueSets() {
    return ImmutableList.of();
  }

  @Override
  public ValueSet getValueSet(VariableEntity entity) throws NoSuchValueSetException {
    throw new NoSuchValueSetException(this, entity);
  }

  @Override
  public boolean hasVariable(String name) {
    return false;
  }

  @Override
  public Iterable<Variable> getVariables() {
    return Collections.emptyList();
  }

  @Override
  public Variable getVariable(String name) throws NoSuchVariableException {
    throw new NoSuchVariableException("null", name);
  }

  @Override
  public Value getValue(Variable variable, ValueSet valueSet) {
    return variable.getValueType().nullValue();
  }

  @Override
  public VariableValueSource getVariableValueSource(String name) throws NoSuchVariableException {
    throw new NoSuchVariableException("null", name);
  }

  @Override
  public Timestamps getTimestamps() {
    return NullTimestamps.get();
  }

  @Override
  public boolean isView() {
    return false;
  }

  public static NullValueTable get() {
    if(instance == null || instance.get() == null) {
      instance = MagmaEngine.get().registerInstance(new NullValueTable());
    }
    return instance.get();
  }

}