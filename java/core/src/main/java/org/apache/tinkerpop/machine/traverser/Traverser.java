/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.machine.traverser;

import org.apache.tinkerpop.machine.coefficient.Coefficient;
import org.apache.tinkerpop.machine.function.CFunction;
import org.apache.tinkerpop.machine.function.FilterFunction;
import org.apache.tinkerpop.machine.function.FlatMapFunction;
import org.apache.tinkerpop.machine.function.MapFunction;
import org.apache.tinkerpop.machine.function.ReduceFunction;
import org.apache.tinkerpop.util.IteratorUtils;

import java.io.Serializable;
import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Traverser<C, S> extends Serializable, Cloneable {

    public Coefficient<C> coefficient();

    public S object();

    public Path path();

    public default boolean filter(final FilterFunction<C, S> function) {
        if (function.test(this)) {
            this.path().addLabels(function.labels());
            this.coefficient().multiply(function.coefficient().value());
            return true;
        } else {
            return false;
        }
    }

    public default <E> Traverser<C, E> map(final MapFunction<C, S, E> function) {
        return this.split(function, function.apply(this));
    }

    public default <E> Iterator<Traverser<C, E>> flatMap(final FlatMapFunction<C, S, E> function) {
        return IteratorUtils.map(function.apply(this), e -> this.split(function, e));
    }

    /*public default <C,S> Traverser<C,S> repeatLoop(final RepeatBranch<C,S> repeatBranch) {
        // set loops
    }*/

    public default <E> Traverser<C, E> reduce(final ReduceFunction<C, S, E> function, final E reducedValue) {
        return this.split(function, reducedValue);
    }

    public <E> Traverser<C, E> split(final CFunction<C> function, final E object);

    public Traverser<C, S> clone();
}
