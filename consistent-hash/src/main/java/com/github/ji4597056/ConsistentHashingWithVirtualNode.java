package com.github.ji4597056;

import java.util.*;
import java.util.stream.IntStream;

/**
 * ConsistentHashingWithVirtualNode
 *
 * @author Jeffrey
 * @since 2018/03/06 2:22
 */
public class ConsistentHashingWithVirtualNode {

    /**
     * virtual server node suffix
     * eg:real_node("127.0.0.1"),virtual_nodes("127.0.0.1##0","127.0.0.1##1"...)
     */
    private static final String VIRTUAL_SERVER_NODE_SUFFIX = "##";

    /**
     * virtual server nodes,key:node hash/value:node name
     */
    private static final SortedMap<Integer, String> VIRTUAL_SERVER_NODES = new TreeMap<>();

    /**
     * real server nodes
     */
    private static final List<String> REAL_SERVER_NODES = new LinkedList<>();

    /**
     * hash function
     */
    private final Hash hash;

    /**
     * virtual nodes numbers
     */
    private final int virtualNodesNum;

    public ConsistentHashingWithVirtualNode(List<String> realNodes, int virtualNodeNum, Hash hash) {
        REAL_SERVER_NODES.addAll(realNodes);
        this.virtualNodesNum = virtualNodeNum;
        this.hash = hash;
        init();
    }

    public ConsistentHashingWithVirtualNode(List<String> realNodes, int virtualNodeNum) {
        this(realNodes, virtualNodeNum, new FnvHash());
    }

    /**
     * put virtual server nodes
     */
    private void init() {
        REAL_SERVER_NODES.forEach(node -> IntStream.range(0, virtualNodesNum)
                .forEach(index -> VIRTUAL_SERVER_NODES
                        .put(hash.getHash(getVirtualNodeKey(node, index)),
                                getVirtualNodeKey(node, index))));
    }

    /**
     * get server from node
     *
     * @param node node
     * @return real node
     */
    public String getServer(String node) {
        int hash = this.hash.getHash(node);
        SortedMap<Integer, String> subMap = VIRTUAL_SERVER_NODES.tailMap(hash);
        String virtualNode;
        if (subMap.size() == 0) {
            // if node's hash max,return first virtual server node
            virtualNode = VIRTUAL_SERVER_NODES.get(VIRTUAL_SERVER_NODES.firstKey());
        } else {
            virtualNode = subMap.get(subMap.firstKey());
        }
        return virtualNode.substring(0, virtualNode.indexOf(VIRTUAL_SERVER_NODE_SUFFIX));
    }

    /**
     * add server node
     *
     * @param serverNode server node
     */
    public void addServerNode(String serverNode) {
        REAL_SERVER_NODES.add(serverNode);
        IntStream.range(0, virtualNodesNum).forEach(index -> VIRTUAL_SERVER_NODES
                .put(hash.getHash(getVirtualNodeKey(serverNode, index)),
                        getVirtualNodeKey(serverNode, index)));
    }

    /**
     * remove server node
     *
     * @param serverNode server node
     */
    public void removeServerNode(String serverNode) {
        REAL_SERVER_NODES.remove(serverNode);
        IntStream.range(0, virtualNodesNum).forEach(index -> VIRTUAL_SERVER_NODES
                .remove(hash.getHash(getVirtualNodeKey(serverNode, index))));
    }

    /**
     * get real server nodes
     *
     * @return real server nodes
     */
    public List<String> getRealServerNodes() {
        return Collections.unmodifiableList(REAL_SERVER_NODES);
    }

    /**
     * get virtual server nodes
     *
     * @return virtual server nodes
     */
    public Map<Integer, String> getVirtualServerNodes() {
        return Collections.unmodifiableMap(VIRTUAL_SERVER_NODES);
    }

    /**
     * get virtual node key
     *
     * @param realNodeKey real node
     * @param index       index
     * @return virtual node
     */
    private String getVirtualNodeKey(String realNodeKey, int index) {
        return realNodeKey + VIRTUAL_SERVER_NODE_SUFFIX + index;
    }
}
