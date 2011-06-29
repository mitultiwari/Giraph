package org.apache.giraph.graph;

import java.util.HashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.log4j.Logger;
import org.json.JSONArray;

/**
 * This is the basic implementation for all of the {@link BasicVertexRangeBalancer}
 * methods except the rebalance() method.  That is up to the user to implement.
 *
 * @param <I> vertex id
 * @param <V> vertex data
 * @param <E> edge data
 * @param <M> message data
 */
@SuppressWarnings("rawtypes")
public abstract class VertexRangeBalancer<
        I extends WritableComparable,
        V extends Writable,
        E extends Writable,
        M extends Writable>
        implements BasicVertexRangeBalancer<I, V, E, M> {
    /** map of prev vertex ranges, in order */
    private NavigableMap<I, VertexRange<I, V, E, M>>
        m_prevVertexRangeMap = null;
    /** map of next vertex ranges, in order */
    private NavigableMap<I, VertexRange<I, V, E, M>>
        m_nextVertexRangeMap = null;
    /** map of available workers to JSONArray(hostname + partition id) */
    private Map<String, JSONArray> m_workerHostnameIdMap = null;
    /** Current superstep */
    private long m_superstep;
    /** Class logger */
    private static final Logger LOG = Logger.getLogger(VertexRangeBalancer.class);

    @Override
    final public long getSuperstep() {
        return m_superstep;
    }

    /**
     * Do not override.  This will be used to set the previous vertex range map
     *
     * @param lastVertexRangeMap map of vertex ranges from the last superstep
     */
    final public void setPrevVertexRangeMap(
        final NavigableMap<I, VertexRange<I, V, E, M>> prevVertexRangeMap) {
        m_prevVertexRangeMap = prevVertexRangeMap;
    }

    @Override
    final public NavigableMap<I, VertexRange<I, V, E, M>>
            getPrevVertexRangeMap() {
        return m_prevVertexRangeMap;
    }

    /**
     * Set the vertex ranges in rebalance() for the upcoming superstep.
     */
    final void setNextVertexRangeMap(
            NavigableMap<I, VertexRange<I, V, E, M>> vertexRangeMap) {
        m_nextVertexRangeMap = vertexRangeMap;
    }

    /**
     * Get the next vertex range map.
     *
     * @return
     */
    final NavigableMap<I, VertexRange<I, V, E, M>> getNextVertexRangeMap() {
        return m_nextVertexRangeMap;
    }

    /**
     * Do not override.  This will be used to set the available workers and
     * associated hostname and port information for the
     * rebalance() method.
     *
     * @param hostnameIdList list of available workers
     */
    final public void setWorkerHostnamePortMap(
            Map<String, JSONArray> workerHostnamePortMap) {
        m_workerHostnameIdMap = workerHostnamePortMap;
    }

    /**
     * Do not override.  This will be used to set the previous hostname and
     * port information for the next vertex range list based on the last
     * vertex range list information.
     */
    final void setPreviousHostnamePort() {
        if (getNextVertexRangeMap().size() !=
                getPrevVertexRangeMap().size()) {
            throw new RuntimeException(
                "setPreviousHostnamePort: Next vertex range set size " +
                getNextVertexRangeMap().size() +
                ", prev vertex range set size " +
                getPrevVertexRangeMap().size());
        }

        for (VertexRange<I, V, E, M> prevVertexRange :
                m_prevVertexRangeMap.values()) {
            if (!m_nextVertexRangeMap.containsKey(
                    prevVertexRange.getMaxIndex())) {
                throw new RuntimeException(
                    "setPreviousHostnamePort: Prev vertex range " +
                    prevVertexRange.getMaxIndex() +
                    " doesn't exist in new vertex range map.");
            }
            VertexRange<I, V, E, M> nextVertexRange =
                m_nextVertexRangeMap.get(prevVertexRange.getMaxIndex());
            nextVertexRange.setPreviousHostname(prevVertexRange.getHostname());
            nextVertexRange.setPreviousPort(prevVertexRange.getPort());
            nextVertexRange.setPreviousHostnameId(
                prevVertexRange.getHostnameId());
        }
    }

    @Override
    final public Map<String, JSONArray> getWorkerHostnamePortMap() {
        return m_workerHostnameIdMap;
    }

    /**
     * Set the upcoming superstep number (Do not use, this is only meant for
     * the infrastructure)
     */
    final void setSuperstep(long superstep) {
        m_superstep = superstep;
    }

    /**
     * Meant for the infrastructure only
     *
     * @return Number of vertex range changes.
     */
    final long getVertexRangeChanges() {
        Set<String> hostnamePortSet = new HashSet<String>();
        for (VertexRange<I, V, E, M> vertexRange :
                getNextVertexRangeMap().values()) {
            if (!vertexRange.getHostname().equals(
                    vertexRange.getPreviousHostname()) ||
                    (vertexRange.getPort() != vertexRange.getPreviousPort())) {
                String hostnamePort = "";
                hostnamePort += vertexRange.getHostname();
                hostnamePort += Integer.toString(vertexRange.getPort());
                hostnamePortSet.add(hostnamePort);
                String prevHostnamePort = new String();
                prevHostnamePort += vertexRange.getPreviousHostname();
                prevHostnamePort +=
                    Integer.toString(vertexRange.getPreviousPort());
                hostnamePortSet.add(prevHostnamePort);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("getVertexRangeChanges: Waiting for " +
                      hostnamePortSet.size() + " workers");
        }
        return hostnamePortSet.size();
    }
}