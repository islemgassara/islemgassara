#!/bin/sh
set -e

N8N_URL="http://n8n:5678"
WORKFLOW_FILE="/workflow/n8n-workflow-cfn.json"

echo "Attente de n8n..."
until curl -s -o /dev/null "$N8N_URL/healthz"; do
  sleep 3
done
echo "n8n est prêt."

echo "Génération d'une clé API n8n..."
# n8n ne permet pas de générer une clé API sans compte existant.
# On utilise l'API interne owner setup si nécessaire, sinon la clé doit être fournie en secret.
API_KEY="$N8N_API_KEY"

echo "Vérification si le workflow existe déjà..."
EXISTING=$(curl -s "$N8N_URL/api/v1/workflows" -H "X-N8N-API-KEY: $API_KEY" | grep -o '"name":"cfec"' || true)

if [ -z "$EXISTING" ]; then
  echo "Import du workflow cfec..."
  RESPONSE=$(curl -s -X POST "$N8N_URL/api/v1/workflows" \
    -H "X-N8N-API-KEY: $API_KEY" \
    -H "Content-Type: application/json" \
    -d @"$WORKFLOW_FILE")
  WF_ID=$(echo "$RESPONSE" | grep -o '"id":"[^"]*' | head -1 | cut -d'"' -f4)
  echo "Workflow créé avec ID: $WF_ID"
  curl -s -X POST "$N8N_URL/api/v1/workflows/$WF_ID/activate" -H "X-N8N-API-KEY: $API_KEY"
  echo "Workflow activé."
else
  echo "Le workflow existe déjà, import ignoré."
fi
