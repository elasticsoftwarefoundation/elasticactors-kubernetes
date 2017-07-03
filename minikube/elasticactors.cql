CREATE KEYSPACE "ElasticActors" WITH replication = {
  'class': 'NetworkTopologyStrategy',
  'datacenter1': '3'
};

USE "ElasticActors";

CREATE TABLE "ActorSystemEventListeners" (
  key text,
  key2 text,
  key3 text,
  column1 text,
  value blob,
  PRIMARY KEY ((key, key2, key3), column1)
) WITH COMPACT STORAGE AND
  bloom_filter_fp_chance=0.010000 AND
  caching={ 'keys' : 'ALL', 'rows_per_partition' : 'NONE' } AND
  comment='' AND
  gc_grace_seconds=864000 AND
  read_repair_chance=0.100000 AND
  compaction={'class': 'SizeTieredCompactionStrategy'} AND
  compression={'sstable_compression': 'SnappyCompressor'};

CREATE TABLE "PersistentActors" (
  key text,
  key2 text,
  column1 text,
  value blob,
  PRIMARY KEY ((key, key2), column1)
) WITH COMPACT STORAGE AND
  bloom_filter_fp_chance=0.010000 AND
  caching={ 'keys' : 'ALL', 'rows_per_partition' : 'NONE' } AND
  comment='' AND
  gc_grace_seconds=864000 AND
  read_repair_chance=0.100000 AND
  compaction={'class': 'SizeTieredCompactionStrategy'} AND
  compression={};

CREATE TABLE "ScheduledMessages" (
  key text,
  key2 text,
  column1 bigint,
  column2 timeuuid,
  value blob,
  PRIMARY KEY ((key, key2), column1, column2)
) WITH COMPACT STORAGE AND
  bloom_filter_fp_chance=0.010000 AND
  caching={ 'keys' : 'ALL', 'rows_per_partition' : 'NONE' } AND
  comment='' AND
  gc_grace_seconds=3600 AND
  read_repair_chance=0.100000 AND
  compaction={'class': 'SizeTieredCompactionStrategy'} AND
  compression={'sstable_compression': 'SnappyCompressor'};