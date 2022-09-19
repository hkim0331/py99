#!/bin/sh
# initdb.d for frozen-py99

DIR="/docker-entrypoint-initdb.d"
PSQL="psql -U postgres"
${PSQL} -c "create database py99 owner='postgres'"

#${PSQL} py99 < ${DIR}/py99-2022-03-29.dump
#${PSQL} py99 -c "alter table users drop column if exists sid"
#${PSQL} py99 -c "alter table users drop column if exists name"
#${PSQL} -c "alter database py99 set default_transaction_read_only = on;"
