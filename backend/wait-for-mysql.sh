#!/bin/sh

HOST="$1"
PORT="$2"
shift 2

# Skip "--" if it's there
if [ "$1" = "--" ]; then
  shift
fi

echo "Waiting for MySQL at $HOST:$PORT..."
until nc -z "$HOST" "$PORT"; do
  sleep 1
  echo "Waiting for MySQL at $HOST:$PORT..."
done

echo "MySQL is up - executing command"
exec "$@"
