#!/bin/sh
#
# origin: ${utils}/utils/bump-version.sh
#
# bump-vesion script for clojure projects.
# confused using macos's /usr/bin/sed. so gsed.
#
# CAUSION
# The POSIX standard does not support back-references for
# "extended" regular expressions,
# this is a compatible extension to that standard.

if [ -z "$1" ]; then
    echo "usage: $0 <version>"
    exit
fi

# use  extended regular expressions in the script
if [ -x "${HOMEBREW_PREFIX}/bin/gsed" ]; then
    SED="${HOMEBREW_PREFIX}/bin/gsed -E"
else
    SED="/usr/bin/sed -E"
fi

# project.clj
${SED} -i "s/(defproject \S+) \S+/\1 \"$1\"/" project.clj

# login.clj, about.html
${SED} -i "s/(def \^:private version) .+/\1 \"$1\")/" src/clj/py99/routes/login.clj

# cljs
#${SED} -i "s/(def \^:private version) .+/\1 \"$1\")/" src/main.cljs

