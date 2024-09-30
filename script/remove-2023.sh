#!/bin/sh
set -ex
PG="psql -U postgres -h localhost py99"
DAY="2024-09-30"
${PG} -c "delete from actions where created_at < ${DAY}"
${PG} -c "delete from answers where create_at < ${DAY}"
${PG} -c "delete from comments where create_at < ${DAY}"
${PG} -c "delete from stocks where timestamp < ${DAY}"
${PG} -c "delete from midterm"
${PG} -c "delete from endterm"
${PG} -c "delete from gradings"
echo done
