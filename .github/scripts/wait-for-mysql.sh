#!/usr/bin/env bash
# Waits until the MySQL container accepts logins and the world data is loaded.
# Used by every pipeline job that needs the database.
#
# Settings (all optional):
#   MYSQL_CONTAINER  container name       (default: population-report-mysql)
#   DB_USER          database user        (default: appuser)
#   DB_PASSWORD      database password    (default: apppassword)
#   DB_NAME          database name        (default: world)
#   MAX_ATTEMPTS     number of tries      (default: 60)
#   DELAY_SECONDS    seconds between tries (default: 2)

set -euo pipefail

CONTAINER="${MYSQL_CONTAINER:-population-report-mysql}"
USER_NAME="${DB_USER:-appuser}"
PASSWORD="${DB_PASSWORD:-apppassword}"
DATABASE="${DB_NAME:-world}"
MAX_ATTEMPTS="${MAX_ATTEMPTS:-60}"
DELAY_SECONDS="${DELAY_SECONDS:-2}"

echo "Waiting for MySQL (${CONTAINER}) to be ready..."

for attempt in $(seq 1 "${MAX_ATTEMPTS}"); do

  # A successful query on the country table means the server is up
  # and world.sql has finished loading.
  if docker exec "${CONTAINER}" mysql \
       -u"${USER_NAME}" -p"${PASSWORD}" "${DATABASE}" \
       --silent --skip-column-names \
       -e "SELECT COUNT(*) FROM country" > /dev/null 2>&1; then
    echo "MySQL is ready (attempt ${attempt})."
    exit 0
  fi

  sleep "${DELAY_SECONDS}"
done

echo "MySQL was not ready after $((MAX_ATTEMPTS * DELAY_SECONDS)) seconds." >&2
docker logs --tail 50 "${CONTAINER}" >&2 || true
exit 1