#!/bin/sh
set -ex
PG="psql -U postgres -h localhost py99"
${PG} -c "delete from actions where created_at < '2024-09-01'"
${PG} -c "delete from answers where create_at < '2024-09-01'"
${PG} -c "delete from comments where create_at < '2024-09-01'"
${PG} -c "delete from stocks where timestamp < '2024-09-01'"
${PG} -c "delete from midterm"
${PG} -c "delete from endterm"
${PG} -c "delete from gradings"
echo done
