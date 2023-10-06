#!/bin/sh
pg="psql -h localhost -U postgres py99"
tables="answers comments endterm gradings midterm problems stocks"

for tbl in ${tables}; do
  ${pg} -c "delete from ${tbl};"
done

${pg} -c 'alter table stocks add column note text;'
