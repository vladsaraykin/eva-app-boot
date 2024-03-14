#!/usr/bin/env bash

echo "Creating keyspace and table..."
cqlsh cassandra -u cassandra -p cassandra -e "CREATE KEYSPACE IF NOT EXISTS evaapp WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};"
