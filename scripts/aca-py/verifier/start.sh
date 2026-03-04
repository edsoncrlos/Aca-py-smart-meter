#!/bin/bash
set -e

echo "Starting ACA-Py..."
exec aca-py start \
  --endpoint "$VERIFIER_ENDPOINT" \
  --label "$VERIFIER_LABEL" \
  \
  --inbound-transport http 0.0.0.0 $VERIFIER_PORT_INBOUND \
  --outbound-transport http \
  --admin 0.0.0.0 $VERIFIER_PORT_ADMIN \
  --webhook-url "$VERIFIER_WEBHOOK_URL" \
  --admin-api-key "$ISSUER_ADMIN_API_KEY" \
  \
  --wallet-type askar \
  --wallet-storage-type postgres_storage \
  --wallet-name "$VERIFIER_WALLET_NAME" \
  --wallet-key "$VERIFIER_WALLET_KEY" \
  --wallet-storage-config "{\"url\":\"$POSTGRES_HOST:$POSTGRES_PORT\", \"wallet_scheme\":\"MultiWalletSingleTable\"}" \
  --wallet-storage-creds "{\"account\":\"$POSTGRES_USER\",\"password\":\"$POSTGRES_PASSWORD\",\"admin_account\":\"$POSTGRES_USER\",\"admin_password\":\"$POSTGRES_PASSWORD\"}" \
  --auto-provision \
  \
  --genesis-url "$GENESIS_URL" \
  \
  --auto-accept-invites \
  --auto-accept-requests
