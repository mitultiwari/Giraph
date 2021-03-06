/*
 * Licensed to Yahoo! under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  Yahoo! licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.giraph.examples;

import java.util.Iterator;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;

import org.apache.giraph.graph.Vertex;

/**
 * Just a simple Vertex compute implementation that executes 3 supersteps, then
 * finishes.
 */
public class SimpleSuperstepVertex extends
    Vertex<LongWritable, IntWritable, FloatWritable, IntWritable> {
    public void compute(Iterator<IntWritable> msgIterator) {
        if (getSuperstep() > 3) {
            voteToHalt();
        }
    }

    public IntWritable createMsgValue() {
        return new IntWritable(0);
    }
}
