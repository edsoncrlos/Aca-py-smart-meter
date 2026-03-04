#!/bin/bash
set -e

echo "Starting ACA-Py..."
exec aca-py start \
  --endpoint "$ISSUER_ENDPOINT" \
  --label "$ISSUER_LABEL" \
  \
  --inbound-transport http 0.0.0.0 $ISSUER_PORT_INBOUND \
  --outbound-transport http \
  --admin 0.0.0.0 $ISSUER_PORT_ADMIN \
  --webhook-url "$ISSUER_WEBHOOK_URL" \
  --admin-api-key "$ISSUER_ADMIN_API_KEY" \
  \
  --wallet-type askar \
  --wallet-storage-type postgres_storage \
  --wallet-name "$ISSUER_WALLET_NAME" \
  --wallet-key "$ISSUER_WALLET_KEY" \
  --wallet-storage-config "{\"url\":\"$POSTGRES_HOST:$POSTGRES_PORT\", \"wallet_scheme\":\"MultiWalletSingleTable\"}" \
  --wallet-storage-creds "{\"account\":\"$POSTGRES_USER\",\"password\":\"$POSTGRES_PASSWORD\",\"admin_account\":\"$POSTGRES_USER\",\"admin_password\":\"$POSTGRES_PASSWORD\"}" \
  --auto-provision \
  \
  --genesis-url "$GENESIS_URL" \
  \
  --auto-accept-invites \
  --auto-accept-requests
