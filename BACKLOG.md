# 📋 Backlog — Monaly

Sistema de gestão comercial para pequenos negócios.

---

# 📌 Legenda

### Prioridade

* 🔴 Alta — necessária para o MVP
* 🟡 Média — importante para evolução
* 🟢 Baixa — evolução futura

### Status

* `TODO`
* `IN PROGRESS`
* `REVIEW`
* `DONE`

---

# 🏁 MVP

O MVP deverá permitir:

```text
Empresa
   ↓
Usuários
   ↓
Produtos
   ↓
Estoque
   ↓
Clientes
   ↓
Pedidos
```

Depois disso entram mensageria, pagamentos, notificações, frontend e infraestrutura.

---

# ÉPICO 1 — Fundação Java

## G01 — Criar projeto Spring Boot

🔴 Alta

* [ ] Criar projeto Maven
* [ ] Configurar Java
* [ ] Configurar Spring Boot
* [ ] Configurar Spring Web
* [ ] Configurar Spring Validation
* [ ] Configurar estrutura inicial
* [ ] Criar primeiro endpoint

## G02 — Configurar qualidade de código

🔴 Alta

* [ ] Configurar `.gitignore`
* [ ] Configurar formatter
* [ ] Configurar análise estática
* [ ] Configurar estrutura de testes
* [ ] Criar `.env.example`

## G03 — Docker

🔴 Alta

* [ ] Criar Dockerfile
* [ ] Criar docker-compose
* [ ] Configurar API
* [ ] Configurar PostgreSQL
* [ ] Configurar Redis
* [ ] Configurar RabbitMQ

## G04 — PostgreSQL

🔴 Alta

* [ ] Configurar datasource
* [ ] Configurar JPA
* [ ] Configurar Hibernate
* [ ] Configurar Flyway
* [ ] Criar primeira migration

## G05 — Arquitetura

🔴 Alta

* [ ] Criar camada Domain
* [ ] Criar camada Application
* [ ] Criar camada Infrastructure
* [ ] Criar camada Presentation
* [ ] Definir regras de dependência
* [ ] Criar primeiro Use Case

---

# ÉPICO 2 — Empresas

## G06 — Company

🔴 Alta

* [ ] Criar entidade Company
* [ ] Criar repository
* [ ] Criar migration
* [ ] Criar Use Case
* [ ] Criar DTO
* [ ] Criar Controller
* [ ] Criar validações
* [ ] Criar testes

## G07 — Multi-tenancy

🔴 Alta

* [ ] Definir estratégia de tenant
* [ ] Criar `company_id`
* [ ] Associar usuário à empresa
* [ ] Criar resolução do tenant
* [ ] Filtrar dados por empresa
* [ ] Testar isolamento entre empresas

---

# ÉPICO 3 — Autenticação

## G08 — Cadastro

🔴 Alta

* [ ] Criar User
* [ ] Criar migration
* [ ] Criar repository
* [ ] Hash de senha
* [ ] Endpoint de cadastro
* [ ] Validação
* [ ] Testes

## G09 — Login

🔴 Alta

* [ ] Configurar Spring Security
* [ ] Criar AuthenticationService
* [ ] Implementar JWT
* [ ] Criar Security Filter
* [ ] Endpoint `/auth/login`
* [ ] Testar autenticação

## G10 — Autorização

🟡 Média

* [ ] Criar roles
* [ ] Criar permissions
* [ ] Configurar autorização
* [ ] Restringir endpoints
* [ ] Testar permissões

Roles:

```text
ADMIN
MANAGER
EMPLOYEE
```

---

# ÉPICO 4 — Produtos

## G11 — Criar produto

🔴 Alta

* [ ] Product Entity
* [ ] Repository
* [ ] Migration
* [ ] CreateProductUseCase
* [ ] DTO
* [ ] Controller
* [ ] Validation
* [ ] Tests

## G12 — Consultar produtos

🔴 Alta

* [ ] Listar produtos
* [ ] Buscar por ID
* [ ] Paginação
* [ ] Filtros
* [ ] Busca por nome/SKU

## G13 — Atualizar produto

🔴 Alta

* [ ] Update Use Case
* [ ] Endpoint
* [ ] Validation
* [ ] Cache invalidation
* [ ] Tests

## G14 — Inativar produto

🟡 Média

* [ ] Soft delete
* [ ] Endpoint
* [ ] Impedir venda de produto inativo

---

# ÉPICO 5 — Estoque

## G15 — Entrada de estoque

🔴 Alta

* [ ] StockMovement
* [ ] Entrada
* [ ] Atualização de quantidade
* [ ] Histórico
* [ ] Transaction
* [ ] Tests

## G16 — Saída de estoque

🔴 Alta

* [ ] Validar quantidade
* [ ] Atualizar estoque
* [ ] Registrar movimentação
* [ ] Transaction
* [ ] Tests

## G17 — Reserva de estoque

🔴 Alta

* [ ] Criar conceito de reserva
* [ ] Validar disponibilidade
* [ ] Reservar produto
* [ ] Liberar reserva
* [ ] Tests

## G18 — Concorrência

🔴 Alta

Problema:

```text
Estoque = 1

Pedido A ─────┐
              ├──► reserva
Pedido B ─────┘
```

* [ ] Reproduzir problema
* [ ] Estudar concorrência
* [ ] Definir estratégia
* [ ] Implementar Redis Lock
* [ ] Definir TTL
* [ ] Liberar lock
* [ ] Tratar falha
* [ ] Criar teste concorrente

---

# ÉPICO 6 — Clientes

## G19 — Cadastro

🔴 Alta

* [ ] Customer Entity
* [ ] Repository
* [ ] Migration
* [ ] CreateCustomerUseCase
* [ ] Controller
* [ ] Validation
* [ ] Tests

## G20 — Consulta

🟡 Média

* [ ] Listagem
* [ ] Busca
* [ ] Paginação
* [ ] Filtros

---

# ÉPICO 7 — Pedidos

## G21 — Criar pedido

🔴 Alta

* [ ] Order Entity
* [ ] OrderItem
* [ ] Repository
* [ ] Validar cliente
* [ ] Validar produtos
* [ ] Validar estoque
* [ ] Calcular total
* [ ] Criar pedido
* [ ] Criar transaction
* [ ] Tests

## G22 — Consultar pedidos

🔴 Alta

* [ ] Listagem
* [ ] Busca por ID
* [ ] Busca por cliente
* [ ] Filtro por status
* [ ] Paginação

## G23 — Cancelar pedido

🟡 Média

* [ ] Validar status
* [ ] Cancelar
* [ ] Liberar estoque
* [ ] Registrar evento
* [ ] Tests

---

# ÉPICO 8 — RabbitMQ

## G24 — Configurar RabbitMQ

🟡 Média

* [ ] Configurar conexão
* [ ] Criar Exchange
* [ ] Criar Queues
* [ ] Criar Routing Keys
* [ ] Configurar bindings

## G25 — OrderCreated

🟡 Média

* [ ] Criar evento
* [ ] Criar producer
* [ ] Publicar evento
* [ ] Criar consumer
* [ ] Processar mensagem

Fluxo:

```text
OrderCreated
     │
     ├──► Stock
     ├──► Payment
     └──► Notification
```

## G26 — Retry

🟡 Média

* [ ] Definir estratégia
* [ ] Configurar retry
* [ ] Limitar tentativas
* [ ] Registrar erros

## G27 — Dead Letter Queue

🟡 Média

* [ ] Criar DLQ
* [ ] Configurar routing
* [ ] Registrar mensagens
* [ ] Criar estratégia de reprocessamento

## G28 — Idempotência

🟡 Média

* [ ] Criar identificador do evento
* [ ] Criar registro de eventos processados
* [ ] Detectar duplicidade
* [ ] Impedir processamento duplicado
* [ ] Criar testes

---

# ÉPICO 9 — Pagamentos

## G29 — Payment

🟡 Média

* [ ] Payment Entity
* [ ] Repository
* [ ] Migration
* [ ] Relacionar com Order
* [ ] Criar status

## G30 — Payment Worker

🟡 Média

* [ ] Criar consumer
* [ ] Consumir PaymentRequested
* [ ] Simular processamento
* [ ] Publicar PaymentConfirmed
* [ ] Publicar PaymentRejected

---

# ÉPICO 10 — Notificações

## G31 — Notification Worker

🟡 Média

* [ ] Criar consumer
* [ ] Consumir eventos
* [ ] Criar Notification Service
* [ ] Registrar notificações

## G32 — WebSocket

🟡 Média

* [ ] Configurar WebSocket
* [ ] Criar conexão
* [ ] Autenticação
* [ ] Enviar eventos
* [ ] Tratar desconexão

---

# ÉPICO 11 — Redis

## G33 — Cache de produtos

🟡 Média

* [ ] Configurar Redis
* [ ] Criar cache service
* [ ] Cache de GET
* [ ] Definir TTL
* [ ] Cache miss
* [ ] Cache hit
* [ ] Invalidação
* [ ] Tests

## G34 — Locks

🔴 Alta

* [ ] Criar LockService
* [ ] Criar chave por produto
* [ ] Definir TTL
* [ ] Implementar acquire
* [ ] Implementar release
* [ ] Tratar timeout
* [ ] Tests

---

# ÉPICO 12 — Frontend

## G35 — Estrutura

🟡 Média

* [ ] Criar React
* [ ] Configurar rotas
* [ ] Login
* [ ] Layout
* [ ] Autenticação

## G36 — Dashboard

🟡 Média

* [ ] Vendas
* [ ] Pedidos
* [ ] Estoque
* [ ] Produtos
* [ ] Indicadores

## G37 — Produtos

🟡 Média

* [ ] Listagem
* [ ] Cadastro
* [ ] Edição
* [ ] Inativação

## G38 — Pedidos

🟡 Média

* [ ] Listagem
* [ ] Detalhes
* [ ] Status
* [ ] Atualização via WebSocket

---

# ÉPICO 13 — Testes

## G39 — Unit Tests

🟡 Média

* [ ] Entities
* [ ] Value Objects
* [ ] Use Cases
* [ ] Business Rules
* [ ] Mockito

## G40 — Integration Tests

🟡 Média

* [ ] PostgreSQL
* [ ] Redis
* [ ] RabbitMQ
* [ ] Repository
* [ ] Controllers

## G41 — Testcontainers

🟡 Média

* [ ] PostgreSQL container
* [ ] Redis container
* [ ] RabbitMQ container
* [ ] Configurar testes

## G42 — E2E

🟢 Baixa

Fluxo:

```text
Login
 ↓
Criar pedido
 ↓
Reservar estoque
 ↓
Pagamento
 ↓
Confirmar pedido
 ↓
Notificação
```

---

# ÉPICO 14 — Observabilidade

## G43 — Logging

🟡 Média

* [ ] Configurar logs
* [ ] Definir níveis
* [ ] Adicionar contexto
* [ ] Evitar dados sensíveis

## G44 — Correlation ID

🟡 Média

* [ ] Gerar ID
* [ ] Propagar HTTP
* [ ] Propagar RabbitMQ
* [ ] Adicionar aos logs

## G45 — Health Check

🟡 Média

* [ ] API health
* [ ] Database health
* [ ] Redis health
* [ ] RabbitMQ health

## G46 — Métricas

🟢 Baixa

* [ ] Configurar métricas
* [ ] Métricas HTTP
* [ ] Métricas de RabbitMQ
* [ ] Métricas de negócio

---

# ÉPICO 15 — CI/CD

## G47 — Pipeline Harness

🟡 Média

```text
Git Push
   ↓
Build
   ↓
Tests
   ↓
Quality
   ↓
Docker Build
   ↓
Registry
   ↓
Deploy
```

* [ ] Criar pipeline
* [ ] Build Maven
* [ ] Executar testes
* [ ] Build Docker
* [ ] Publicar imagem
* [ ] Deploy

## G48 — Docker Registry

🟢 Baixa

* [ ] Criar repository
* [ ] Push da imagem
* [ ] Versionamento
* [ ] Tags

---

# ÉPICO 16 — Kubernetes

## G49 — Kubernetes local

🟢 Baixa

* [ ] Deployment
* [ ] Service
* [ ] ConfigMap
* [ ] Secrets
* [ ] Replicas
* [ ] Health probes

---

# ÉPICO 17 — Terraform

## G50 — Infrastructure as Code

🟢 Baixa

* [ ] Configurar Terraform
* [ ] Provider
* [ ] Variables
* [ ] Outputs
* [ ] Networking
* [ ] Recursos da aplicação

---

# ÉPICO 18 — AWS

## G51 — Deploy Cloud

🟢 Baixa

* [ ] Definir arquitetura
* [ ] Provisionar infraestrutura
* [ ] Deploy
* [ ] Banco
* [ ] Redis
* [ ] Mensageria
* [ ] Observabilidade

---

# 🗓️ Sprints

## Sprint 1 — Java + Spring Boot

**Objetivo:** aplicação Spring Boot funcionando.

* G01
* G02
* G03
* G04
* G05

---

## Sprint 2 — Empresas + Autenticação

**Objetivo:** usuário consegue criar uma empresa e autenticar.

* G06
* G07
* G08
* G09
* G10

---

## Sprint 3 — Produtos

**Objetivo:** empresa consegue gerenciar produtos.

* G11
* G12
* G13
* G14

---

## Sprint 4 — Estoque

**Objetivo:** controlar estoque e estudar concorrência.

* G15
* G16
* G17
* G18

Essa será uma das sprints mais importantes tecnicamente.

---

## Sprint 5 — Clientes + Pedidos

**Objetivo:** criar o primeiro fluxo completo de venda.

* G19
* G20
* G21
* G22
* G23

---

## Sprint 6 — Redis

**Objetivo:** implementar cache e controle de concorrência.

* G33
* G34

---

## Sprint 7 — RabbitMQ

**Objetivo:** transformar partes do fluxo em processamento assíncrono.

* G24
* G25
* G26
* G27
* G28

---

## Sprint 8 — Pagamentos + Notificações

**Objetivo:** completar o processamento do pedido.

* G29
* G30
* G31
* G32

---

## Sprint 9 — Frontend

**Objetivo:** disponibilizar uma interface para utilização do sistema.

* G35
* G36
* G37
* G38

---

## Sprint 10 — Testes

**Objetivo:** aumentar a confiabilidade da aplicação.

* G39
* G40
* G41
* G42

---

## Sprint 11 — Observabilidade

**Objetivo:** conseguir acompanhar o comportamento do sistema.

* G43
* G44
* G45
* G46

---

## Sprint 12 — CI/CD

**Objetivo:** automatizar build, testes e entrega.

* G47
* G48

---

# 🚀 Pós-MVP

Depois que todo o fluxo principal estiver funcionando:

* G49 — Kubernetes
* G50 — Terraform
* G51 — AWS

Também poderão ser adicionados posteriormente:

* Load testing
* Rate limiting
* Auditoria
* Segurança avançada
* Performance
* Disaster recovery
* Tracing distribuído
* Métricas avançadas
