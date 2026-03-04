#!/bin/bash
SCRIPT_PATH=$(dirname "$(realpath "$BASH_SOURCE")")
BASE_PATH=$SCRIPT_PATH/../..
source $BASE_PATH/.env

URL=${ISSUER_ADMIN_ENDPOINT}/schemas

JSON_PAYLOAD=$(cat <<EOF
{
  "schema_name": "smart_meter_role",
  "schema_version": "1.0",
  "attributes": [
    "role"
  ]
}
EOF
)

RESPONSE=$(curl -X POST $URL \
  -H "Content-Type: application/json" \
  -H "X-API-Key: ${ISSUER_ADMIN_API_KEY}" \
  -d "$JSON_PAYLOAD")

SCHEMA_ID=$(echo "$RESPONSE" | jq -r '.sent.schema_id')

if [[ -z "$SCHEMA_ID" || "$SCHEMA_ID" == "null" ]]; then
  echo -e "\nFailed to create Schema: $RESPONSE"
  exit 1
fi
echo -e "\nCreated Schema: $RESPONSE"

sed -i "s/^SCHEMA_ID=.*/SCHEMA_ID=${SCHEMA_ID}/" "${BASE_PATH}/.env"
