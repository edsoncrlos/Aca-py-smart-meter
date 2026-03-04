#!/bin/bash
SCRIPT_PATH=$(dirname "$(realpath "$BASH_SOURCE")")
BASE_PATH=$SCRIPT_PATH/../..
source $BASE_PATH/.env

URL=${ISSUER_ADMIN_ENDPOINT}/wallet/did/public?did=${DID}

RESPONSE=$(curl -X POST $URL \
  -H "X-API-Key: ${ISSUER_ADMIN_API_KEY}" \
  -H "Content-Type: application/json")

echo -e "\nDid public: $RESPONSE"