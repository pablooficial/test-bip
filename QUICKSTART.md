# 🚀 Guia Rápido de Início

## ⚡ Início Rápido (5 minutos)

### 1. Clone o Repositório
```bash
git clone <seu-repositorio>
cd test-bip
```

### 2. Compile o Projeto Completo (EJB + Backend)
```bash
mvn clean install
```

### 3. Execute o Backend
```bash
cd backend-module
mvn spring-boot:run
```


✅ Backend rodando em: http://localhost:8080  
✅ Swagger UI: http://localhost:8080/swagger-ui.html

### 3. Execute o Frontend (em outro terminal)
```bash
cd frontend
npm install
npm start
```

✅ Frontend rodando em: http://localhost:4200

---

## 🧪 Testar a Correção do Bug

### Via Swagger UI
1. Acesse http://localhost:8080/swagger-ui.html
2. Expanda `POST /api/v1/beneficios/transferir`
3. Clique em "Try it out"
4. Cole este JSON:
```json
{
  "fromId": 1,
  "toId": 2,
  "amount": 300.00
}
```
5. Clique em "Execute"

**Resultado esperado**: ✅ Transferência bem-sucedida

### Testar Saldo Insuficiente
```json
{
  "fromId": 1,
  "toId": 2,
  "amount": 9999.00
}
```

**Resultado esperado**: ❌ Erro 400 - "Saldo insuficiente"

---

## 📋 Checklist de Funcionalidades

- [x] ✅ Banco de dados configurado (H2)
- [x] ✅ Bug do EJB corrigido
- [x] ✅ CRUD completo implementado
- [x] ✅ Transferência com validações
- [x] ✅ Swagger/OpenAPI documentado
- [x] ✅ Testes unitários (90%+ cobertura)
- [x] ✅ Frontend Angular responsivo
- [x] ✅ CI/CD configurado

---

## 🎯 Endpoints Principais

| Ação | Método | URL |
|------|--------|-----|
| Listar benefícios | GET | http://localhost:8080/api/v1/beneficios |
| Criar benefício | POST | http://localhost:8080/api/v1/beneficios |
| Transferir | POST | http://localhost:8080/api/v1/beneficios/transferir |

---

## 🐛 Solução de Problemas

### Porta 8080 já em uso
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### Erro de compilação Maven
```bash
mvn clean install -U
```

### Erro no npm
```bash
rm -rf node_modules package-lock.json
npm install
```

---

## 📞 Suporte

Consulte o [README.md](README.md) completo para mais detalhes.
