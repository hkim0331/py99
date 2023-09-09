#!/bin/sh
if [ -z "$1" ]; then
    echo usage: $0 db-yyyy-mm-dd.sql
    exit 1
fi

PORT=5432
PSQL="psql -h localhost --port=${PORT} -U postgres"
${PSQL} -c "drop database py99"
${PSQL} -c "create database py99 owner='postgres'"
${PSQL} py99 < $1

