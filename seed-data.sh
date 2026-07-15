#!/bin/bash
BASE="http://localhost:8085/router-api/2"

echo "Création du connecteur..."
curl -s -X POST $BASE/connectors -H "Content-Type: application/json" \
  -d '{"id":"cfn","tenantId":"GEMY","n8nWebhookPath":"/mock-external-system","active":true}' > /dev/null

echo "Création des règles de mapping..."
curl -s -X POST $BASE/connectors/cfn/mappings -H "Content-Type: application/json" \
  -d '{"targetField":"nom_client","sourceExpr":"client.fullName","required":true}' > /dev/null

curl -s -X POST $BASE/connectors/cfn/mappings -H "Content-Type: application/json" \
  -d '{"targetField":"pays","sourceExpr":"STATIC:FR","required":false}' > /dev/null

echo "Création de la config..."
curl -s -X PUT $BASE/connectors/cfn/config -H "Content-Type: application/json" \
  -d '{"configKey":"baseUrl","configValue":"http://mock-cfec:4000","isSecret":false}' > /dev/null

curl -s -X PUT $BASE/connectors/cfn/config -H "Content-Type: application/json" \
  -d '{"configKey":"login","configValue":"demo","isSecret":false}' > /dev/null

curl -s -X PUT $BASE/connectors/cfn/config -H "Content-Type: application/json" \
  -d '{"configKey":"password","configValue":"demo123","isSecret":true}' > /dev/null

echo "Terminé. Test :"
curl -s -X POST $BASE/connectors/cfn/execute -H "Content-Type: application/json" \
  -d '{"client":{"fullName":"Test Seed"},"dossier":{"montant":1000}}'

echo ""
