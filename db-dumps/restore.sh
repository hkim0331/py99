#!/bin/sh
if [ -z "$1" ]; then
    echo usage: $0 yyyy-mm-dd.dumo
    exit 1
fi

PSQL="psql -h localhost -U postgres -W"
${PSQL} -c "drop database py99"
${PSQL} -c "create database py99 owner='postgres'"
${PSQL} py99 < $1

