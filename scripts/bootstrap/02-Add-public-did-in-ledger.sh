#!/bin/bash
SCRIPT_PATH=$(dirname "$(realpath "$BASH_SOURCE")")
BASE_PATH=$SCRIPT_PATH/../..
source $BASE_PATH/.env

JSON_PAYLOAD=$(cat <<EOF
{
  "did": "${DID}",
  "verkey": "${VERKEY}",
  "role": "ENDORSER"
}
EOF
)

RESPONSE=$(curl -X POST $DID_REGISTRY_URL \
  -H "Content-Type: application/json" \
  -d "$JSON_PAYLOAD")

echo -e "\nDID Registry: $RESPONSE"
