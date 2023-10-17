#!/bin/sh
mkdir -p target/classes/static
cp -r web/* target/classes/static
tar -czvf site.tgz target/classes/static/
if ! base64 site.tgz -w0 > site.tgz.base64 2>/dev/null; then
  base64 -i site.tgz -o site.tgz.base64
fi

echo "Starting malicious RMI Server"
./mvnw -q package exec:exec
