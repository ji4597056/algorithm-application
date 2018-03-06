package com.github.ji4597056;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;

/**
 * ConsistentHashingWithVirtualNodeTest
 *
 * @author Jeffrey
 * @since 2018/03/06 2:22
 */
public class ConsistentHashingWithVirtualNodeTest {

    private List<String> realServerNodes = Lists
        .newArrayList("172.0.0.1", "172.0.0.2", "172.0.0.3", "172.0.0.4", "172.0.0.5");

    private Random random = new Random();

    @Test
    public void testGetServer() {
        printGetServer(1, 500000);
        printGetServer(10, 500000);
        printGetServer(50, 500000);
        printGetServer(100, 500000);
    }

    @Test
    public void testHashStrategy() {
        // result:fnv hash算法性能优于murmur hash算法
        IntStream.range(0, 5).forEach(i -> {
            System.out.println("=========fnv hash=========");
            printGetServer(100, 500000);
            System.out.println("=========murmur hash=========");
            printGetServer(100, 500000, new MurmurHash());
        });
    }

    @Test
    public void testAddServerNode() {
        ConsistentHashingWithVirtualNode consistentHashing = new ConsistentHashingWithVirtualNode(
            realServerNodes, 5);
        consistentHashing.addServerNode("172.0.0.6");
        Assert.assertEquals(consistentHashing.getRealServerNodes().size(), 6);
        Assert.assertEquals(consistentHashing.getVirtualServerNodes().size(), 30);
    }

    @Test
    public void testRemoveServerNode() {
        ConsistentHashingWithVirtualNode consistentHashing = new ConsistentHashingWithVirtualNode(
            realServerNodes, 5);
        consistentHashing.removeServerNode("172.0.0.1");
        Assert.assertEquals(consistentHashing.getRealServerNodes().size(), 4);
        Assert.assertEquals(consistentHashing.getVirtualServerNodes().size(), 20);
    }

    private void printGetServer(int virtualNodeNum, int nodeNum) {
        printGetServer(virtualNodeNum, nodeNum, null);
    }

    private void printGetServer(int virtualNodeNum, int nodeNum, Hash hash) {
        System.out.println("==================================");
        System.out.println("virtual node num:" + virtualNodeNum + ",node num:" + nodeNum);
        ConsistentHashingWithVirtualNode consistentHashing = Optional.ofNullable(hash)
            .map(hashStrategy -> new ConsistentHashingWithVirtualNode(
                realServerNodes, virtualNodeNum, hashStrategy))
            .orElse(new ConsistentHashingWithVirtualNode(realServerNodes, virtualNodeNum));
        Map<String, Integer> result = new HashMap<>();
        long start = System.currentTimeMillis();
        IntStream.range(0, nodeNum).forEach(i -> {
            String serverNode = consistentHashing.getServer(getRandomNode());
            if (result.get(serverNode) == null) {
                result.put(serverNode, 1);
            } else {
                result.put(serverNode, result.get(serverNode) + 1);
            }
        });
        result.forEach((key, count) -> System.out.println(key + ":" + count));
        System.out.println("cost time:" + (System.currentTimeMillis() - start));
    }

    private String getRandomNode() {
        int max = 256;
        return random.nextInt(max) + "." + random.nextInt(max) + "." + random.nextInt(max) + random
            .nextInt(max);
    }
}
