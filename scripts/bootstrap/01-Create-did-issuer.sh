#!/bin/bash
SCRIPT_PATH=$(dirname "$(realpath "$BASH_SOURCE")")
BASE_PATH=$SCRIPT_PATH/../..
source $BASE_PATH/.env

URL=${ISSUER_ADMIN_ENDPOINT}/wallet/did/create

JSON_PAYLOAD=$(cat <<EOF
{
  "method": "sov",
  "options": {
    "key_type": "ed25519"
  }
}
EOF
)

RESPONSE=$(curl -X POST $URL \
  -H "Content-Type: application/json" \
  -H "X-API-Key: ${ISSUER_ADMIN_API_KEY}" \
  -d "$JSON_PAYLOAD")

DID=$(echo "$RESPONSE" | jq -r '.result.did')
VERKEY=$(echo "$RESPONSE" | jq -r '.result.verkey')

if [[ -z "$DID" || "$DID" == "null" ]]; then
  echo -e "\nFailed to create DID for Issuer: $RESPONSE"
  exit 1
fi
echo -e "\nCreated DID for Issuer: $DID"
echo -e "Verkey: $VERKEY"

sed -i "s/^DID=.*/DID=${DID}/" "${BASE_PATH}/.env"
sed -i "s/^VERKEY=.*/VERKEY=${VERKEY}/" "${BASE_PATH}/.env"
