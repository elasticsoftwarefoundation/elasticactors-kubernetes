package org.elasticsoftwarefoundation.elasticactors.container.util;

import com.datastax.driver.core.*;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import com.datastax.driver.core.policies.ConstantReconnectionPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.LoggingRetryPolicy;
import com.datastax.driver.core.policies.RoundRobinPolicy;

/**
 * @author Joost van de Wijgerd
 */
public class Cassandra3SchemaCreator {
    public static void main(String... args) {
        PoolingOptions poolingOptions = new PoolingOptions();
        poolingOptions.setHeartbeatIntervalSeconds(60);
        poolingOptions.setConnectionsPerHost(HostDistance.LOCAL, 2, Runtime.getRuntime().availableProcessors() * 3);
        poolingOptions.setPoolTimeoutMillis(2000);

        String[] contactPoints = new String[] {args[0]};
        Integer cassandraPort = Integer.parseInt(args[1]);

        Cluster cassandraCluster =
                Cluster.builder().withClusterName("ElasticActorsCluster")
                        .addContactPoints(contactPoints)
                        .withPort(cassandraPort)
                        .withLoadBalancingPolicy(new RoundRobinPolicy())
                        .withRetryPolicy(new LoggingRetryPolicy(DefaultRetryPolicy.INSTANCE))
                        .withPoolingOptions(poolingOptions)
                        .withReconnectionPolicy(new ConstantReconnectionPolicy( 1000))
                        .withQueryOptions(new QueryOptions().setConsistencyLevel(ConsistencyLevel.QUORUM)).build();

        Session cassandraSession = cassandraCluster.connect();

        try {
            // see if the keyspace already exists
            boolean create = false;
            try {
                cassandraSession.execute("USE \"ElasticActors\";");
            } catch(InvalidQueryException e) {
                if("Keyspace 'ElasticActors' does not exist".equals(e.getMessage())) {
                    System.out.println("Keyspace 'ElasticActors' not found, creating...");
                    create = true;
                }
            }

            if(create) {
                cassandraSession.execute("CREATE KEYSPACE \"ElasticActors\" WITH replication = {\n" +
                        "  'class': 'NetworkTopologyStrategy',\n" +
                        "  'datacenter1': '3'\n" +
                        "};");
                cassandraSession.execute("USE \"ElasticActors\";");
                cassandraSession.execute("CREATE TABLE \"ActorSystemEventListeners\" (\n" +
                        "  key text,\n" +
                        "  key2 text,\n" +
                        "  key3 text,\n" +
                        "  column1 text,\n" +
                        "  value blob,\n" +
                        "  PRIMARY KEY ((key, key2, key3), column1)\n" +
                        ") WITH COMPACT STORAGE AND\n" +
                        "  bloom_filter_fp_chance=0.010000 AND\n" +
                        "  caching={ 'keys' : 'ALL', 'rows_per_partition' : 'NONE' } AND\n" +
                        "  comment='' AND\n" +
                        "  dclocal_read_repair_chance=0.000000 AND\n" +
                        "  gc_grace_seconds=864000 AND\n" +
                        "  read_repair_chance=0.100000 AND\n" +
                        "  compaction={'class': 'SizeTieredCompactionStrategy'} AND\n" +
                        "  compression={'sstable_compression': 'SnappyCompressor'};");
                cassandraSession.execute("CREATE TABLE \"PersistentActors\" (\n" +
                        "  key text,\n" +
                        "  key2 text,\n" +
                        "  column1 text,\n" +
                        "  value blob,\n" +
                        "  PRIMARY KEY ((key, key2), column1)\n" +
                        ") WITH COMPACT STORAGE AND\n" +
                        "  bloom_filter_fp_chance=0.010000 AND\n" +
                        "  caching={ 'keys' : 'ALL', 'rows_per_partition' : 'NONE' } AND\n" +
                        "  comment='' AND\n" +
                        "  dclocal_read_repair_chance=0.000000 AND\n" +
                        "  gc_grace_seconds=864000 AND\n" +
                        "  read_repair_chance=0.100000 AND\n" +
                        "  compaction={'class': 'SizeTieredCompactionStrategy'} AND\n" +
                        "  compression={};");
                cassandraSession.execute("CREATE TABLE \"ScheduledMessages\" (\n" +
                        "  key text,\n" +
                        "  key2 text,\n" +
                        "  column1 bigint,\n" +
                        "  column2 timeuuid,\n" +
                        "  value blob,\n" +
                        "  PRIMARY KEY ((key, key2), column1, column2)\n" +
                        ") WITH COMPACT STORAGE AND\n" +
                        "  bloom_filter_fp_chance=0.010000 AND\n" +
                        "  caching={ 'keys' : 'ALL', 'rows_per_partition' : 'NONE' } AND\n" +
                        "  comment='' AND\n" +
                        "  dclocal_read_repair_chance=0.000000 AND\n" +
                        "  gc_grace_seconds=3600 AND\n" +
                        "  read_repair_chance=0.100000 AND\n" +
                        "  compaction={'class': 'SizeTieredCompactionStrategy'} AND\n" +
                        "  compression={'sstable_compression': 'SnappyCompressor'};");
            } else {
                System.out.println("Keyspace 'ElasticActors' already exists, skipping creation step...");
            }
        } finally {
            cassandraSession.close();
            cassandraCluster.close();
        }
    }
}
