#!/bin/sh
pg_dump -h localhost -U postgres -W py99 > `date +py99-%F.dump`

