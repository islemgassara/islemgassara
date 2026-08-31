#!/usr/bin/env bash
set -e

CLUSTER_NAME="integration-hub-cluster"
INGRESS_PORT="8090"
NAMESPACE="integration-hub"

log() { echo -e "\n==> $1"; }

log "1/7 - Verification des prerequis"
for cmd in docker k3d kubectl helm; do
  if ! command -v "$cmd" &> /dev/null; then
    echo "ERREUR : '$cmd' n'est pas installe."
    exit 1
  fi
done
if ! docker ps &> /dev/null; then
  echo "ERREUR : Docker n'est pas accessible."
  exit 1
fi
echo "OK - tous les prerequis sont presents"

log "2/7 - Creation du cluster Kubernetes (k3d)"
if k3d cluster list | grep -q "$CLUSTER_NAME"; then
  echo "Cluster '$CLUSTER_NAME' deja existant, reutilisation."
  k3d cluster start "$CLUSTER_NAME" || true
else
  k3d cluster create "$CLUSTER_NAME" -p "${INGRESS_PORT}:80@loadbalancer"
fi
kubectl cluster-info

log "3/7 - Installation de Vault (mode developpement)"
helm repo add hashicorp https://helm.releases.hashicorp.com --force-update
helm repo add external-secrets https://charts.external-secrets.io --force-update
helm repo update

if ! kubectl get namespace vault &> /dev/null || ! kubectl get statefulset vault -n vault &> /dev/null; then
  helm install vault hashicorp/vault \
    -n vault --create-namespace \
    --set "server.dev.enabled=true" \
    --set "server.dev.devRootToken=root-token-demo"
fi
kubectl rollout status statefulset vault -n vault --timeout=120s

log "4/7 - Installation d'External Secrets Operator"
if ! kubectl get deployment external-secrets -n external-secrets &> /dev/null; then
  helm install external-secrets external-secrets/external-secrets \
    -n external-secrets --create-namespace --set installCRDs=true
fi
kubectl rollout status deployment external-secrets -n external-secrets --timeout=120s

log "5/7 - Configuration automatique de Vault (secrets + auth Kubernetes)"
kubectl exec -n vault vault-0 -- vault kv put secret/integration-hub \
  dbPassword="hub_password" \
  cryptoSecretKey="cxkoEGNRRaAAX5OnzEZs4bBdXl+9ex+YqiyaVcxN0DQ=" \
  webhookSecret="L3Gc0CIdI4pGKPSYNyon19Jlj7Q6b6cm" \
  jwtSecret="AEtF73KtjsYBuCIGKa+9Vuib8qWUBKC2uQtqY/FmrcB+Iv2xDiEZfGv4dSyGDi+upptBEKC/NRKQyMbo5eO4wg==" \
  n8nApiKey="non-utilise-import-cli" > /dev/null

kubectl exec -n vault vault-0 -- vault auth enable kubernetes 2>/dev/null || echo "  (auth kubernetes deja activee)"

kubectl exec -n vault vault-0 -- sh -c 'vault write auth/kubernetes/config \
  kubernetes_host="https://kubernetes.default.svc:443"' > /dev/null

kubectl exec -n vault vault-0 -- sh -c 'vault policy write integration-hub-policy - <<EOF
path "secret/data/integration-hub" {
  capabilities = ["read"]
}
EOF' > /dev/null

kubectl exec -n vault vault-0 -- vault write auth/kubernetes/role/integration-hub-role \
  bound_service_account_names=external-secrets \
  bound_service_account_namespaces=external-secrets \
  policies=integration-hub-policy \
  ttl=24h > /dev/null

echo "OK - Vault configure et peuple automatiquement"

log "6/7 - Deploiement de l'application"
kubectl create namespace "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f -
helm upgrade --install hub-release ./hub-chart -n "$NAMESPACE"

echo "Attente de la stabilisation des pods (jusqu'a 3 minutes)..."
kubectl wait --for=condition=Ready pods --all -n "$NAMESPACE" --timeout=180s 2>/dev/null || true
kubectl get pods -n "$NAMESPACE"

log "7/7 - Verification de bout en bout"
kubectl port-forward -n "$NAMESPACE" svc/integration-hub 18086:8085 &> /dev/null &
PF_PID=$!
sleep 5

TOKEN=$(curl -s -X POST "http://localhost:18086/router-api/2/_dev/generate-token?tenantId=GEMY" | grep -o '"token":"[^"]*' | cut -d'"' -f4)

RESULT=$(curl -s -X POST "http://localhost:18086/router-api/2/connectors/cfn/execute" \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"client":{"fullName":"Verification install.sh"},"dossier":{"montant":15000}}')

kill $PF_PID 2>/dev/null || true

echo "$RESULT"
if echo "$RESULT" | grep -q '"status":"SUCCESS"'; then
  echo -e "\n=========================================="
  echo " INSTALLATION REUSSIE - projet fonctionnel"
  echo "=========================================="
else
  echo -e "\nATTENTION : la verification finale n'a pas renvoye SUCCESS."
fi
