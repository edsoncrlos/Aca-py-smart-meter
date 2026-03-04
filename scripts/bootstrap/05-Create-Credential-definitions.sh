#!/bin/bash
SCRIPT_PATH=$(dirname "$(realpath "$BASH_SOURCE")")
BASE_PATH=$SCRIPT_PATH/../..
source $BASE_PATH/.env

URL=${ISSUER_ADMIN_ENDPOINT}/credential-definitions

JSON_PAYLOAD=$(cat <<EOF
{
  "schema_id": "${SCHEMA_ID}",
  "tag": "default",
  "support_revocation": false
}
EOF
)

RESPONSE=$(curl -X POST $URL \
  -H "Content-Type: application/json" \
  -H "X-API-Key: ${ISSUER_ADMIN_API_KEY}" \
  -d "$JSON_PAYLOAD")

CREDENTIAL_DEFINITION_ID=$(echo "$RESPONSE" | jq -r '.credential_definition_id')

if [[ -z "$CREDENTIAL_DEFINITION_ID" || "$CREDENTIAL_DEFINITION_ID" == "null" ]]; then
  echo -e "\nFailed to create Credential Definition: $RESPONSE"
  exit 1
fi

echo -e "\nCreated Credential Definition: $RESPONSE"
sed -i "s/^CREDENTIAL_DEFINITION_ID=.*/CREDENTIAL_DEFINITION_ID=${CREDENTIAL_DEFINITION_ID}/" "${BASE_PATH}/.env"
