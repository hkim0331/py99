#!/bin/sh
pg_dump -h localhost -U postgres --port 5432 py99 > `date +py99-%F.dump`
