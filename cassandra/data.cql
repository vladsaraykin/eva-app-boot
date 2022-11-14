CREATE  KEYSPACE IF NOT EXISTS evaapp
   WITH REPLICATION = {
      'class' : 'SimpleStrategy', 'replication_factor' : N };