#!/bin/bash
set -e
cd "$(dirname "$0")"
clojure -M:test
