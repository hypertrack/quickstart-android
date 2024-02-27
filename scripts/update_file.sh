#!/bin/bash
set -euo pipefail

# Check if params are provided
if [ $# -ne 3 ]; then
    echo "Usage: $0 <file> <source_text> <new_text>"
    exit 1
fi

file="$1"
source_text="$2"
new_text="$3"

if [ ! -f "$file" ]; then
    echo "Error: $file not found."
    exit 1
fi

cat $file | sed "s/$source_text/$new_text/" >tmp
cat tmp >$file
rm -f tmp

echo "Updated $source_text in $file to $new_text"
