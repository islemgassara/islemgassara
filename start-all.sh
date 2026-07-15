cat > ~/projects/start-all.sh << 'EOF'
#!/bin/bash
echo "Démarrage des conteneurs existants..."
docker start poc-postgres poc-n8n mock-cfec

echo "Attente que PostgreSQL soit prêt..."
sleep 3

echo "Tout est démarré. Lance maintenant le Hub avec :"
echo "  cd ~/projects/integration-hub && ./mvnw spring-boot:run"
EOF
chmod +x ~/projects/start-all.sh