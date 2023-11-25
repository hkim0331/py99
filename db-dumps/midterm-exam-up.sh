#!/bin/sh

for i in 211 212 213 214 221 222 223 224 231 232 233 234 241 242 243 244; do
  echo "insert into problems (num, problem) values ($i, '')" | psql py99
done
