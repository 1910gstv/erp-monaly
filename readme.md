# Monaly

> Sistema de gestão comercial para pequenos negócios, desenvolvido em Java e Spring Boot, com foco em arquitetura de software, sistemas distribuídos e boas práticas de desenvolvimento backend.

O **Monaly** é um sistema de gestão comercial voltado para pequenos negócios.

A proposta é permitir o gerenciamento de:

* Empresas
* Usuários
* Clientes
* Produtos
* Estoque
* Pedidos
* Pagamentos
* Entregas
* Notificações

Além de funcionar como um sistema real, o Monaly será utilizado como um projeto de estudo para aprofundar conhecimentos em **Java, Spring Boot, arquitetura de software e sistemas distribuídos**.

---

# 🎯 Objetivos

O projeto tem dois objetivos principais.

### Produto

Criar uma aplicação que resolva problemas reais de pequenos negócios, permitindo controlar vendas, produtos e estoque.

### Estudo

Utilizar o projeto para praticar conceitos importantes de desenvolvimento backend:

* Java
* Spring Boot
* REST APIs
* PostgreSQL
* JPA / Hibernate
* Redis
* RabbitMQ
* Clean Architecture
* Arquitetura Hexagonal
* DDD
* SOLID
* Testes automatizados
* Concorrência
* Idempotência
* Processamento assíncrono
* Observabilidade
* Docker
* CI/CD

A ideia não é adicionar tecnologias apenas para aumentar a quantidade de ferramentas utilizadas.

Cada tecnologia deve existir para resolver um problema real do sistema.

---

# 🏗️ Arquitetura

A arquitetura inicial será baseada em:

```text
                    ┌───────────────┐
                    │    Frontend   │
                    │     React     │
                    └───────┬───────┘
                            │
                            ▼
                  ┌───────────────────┐
                  │    Monaly API       │
                  │   Spring Boot     │
                  │       Java        │
                  └─────────┬─────────┘
                            │
              ┌─────────────┼─────────────┐
              │             │             │
              ▼             ▼             ▼
        ┌──────────┐   ┌──────────┐  ┌──────────┐
        │PostgreSQL│   │  Redis   │  │ RabbitMQ │
        └──────────┘   └──────────┘  └────┬─────┘
                                          │
                         ┌────────────────┼────────────────┐
                         │                │                │
                         ▼                ▼                ▼
                   ┌───────────┐   ┌───────────┐   ┌──────────────┐
                   │   Stock   │   │  Payment  │   │Notification  │
                   │  Worker   │   │  Worker   │   │    Worker    │
                   └───────────┘   └───────────┘   └──────────────┘
```

Inicialmente, o sistema será desenvolvido como um **modular monolith**, utilizando Spring Boot.

A separação entre módulos e responsabilidades permitirá evoluir a arquitetura posteriormente caso exista uma necessidade real.

---

# 🛠️ Stack

## Backend

* Java
* Spring Boot
* Spring Web
* Spring Data JPA
* Hibernate
* Spring Security
* Bean Validation
* Maven

## Banco de dados

* PostgreSQL
* Flyway

## Mensageria

* RabbitMQ

## Cache e concorrência

* Redis

## Frontend

* React

## Testes

* JUnit
* Mockito
* Spring Boot Test
* Testcontainers

## Infraestrutura

* Docker
* Docker Compose

## CI/CD

* Harness

## Futuro

* Kubernetes
* Terraform
* AWS

---

# 📦 Módulos

O sistema será dividido em módulos de negócio.

```text
Monaly
├── Auth
├── Companies
├── Users
├── Customers
├── Products
├── Stock
├── Orders
├── Payments
├── Deliveries
└── Notifications
```

---

# 👥 Multi-tenancy

O Monaly será pensado como uma aplicação SaaS.

Uma mesma instalação poderá possuir diversas empresas.

Exemplo:

```text
                    Monaly
                     │
          ┌──────────┴──────────┐
          │                     │
      Empresa A             Empresa B
          │                     │
     ┌────┼────┐           ┌────┼────┐
     │    │    │           │    │    │
   Users Products Orders  Users Products Orders
```

Os dados de uma empresa não podem ser acessados por outra.

A maioria das entidades de negócio terá:

```text
company_id
```

O isolamento entre tenants será uma preocupação importante da aplicação.

---

# 🔐 Autenticação e autorização

O sistema utilizará Spring Security.

Inicialmente teremos:

```text
ADMIN
MANAGER
EMPLOYEE
```

O fluxo será:

```text
Login
  ↓
Spring Security
  ↓
JWT
  ↓
Authenticated User
  ↓
Authorization
  ↓
Resource
```

Serão estudados:

* Authentication
* Authorization
* JWT
* Password hashing
* Roles
* Permissions
* Security filters
* Method security

---

# 📦 Produtos

Cada empresa poderá cadastrar seus produtos.

Exemplo:

```text
Product
├── id
├── companyId
├── name
├── description
├── price
├── sku
├── active
└── createdAt
```

Funcionalidades:

* Criar produto
* Consultar produto
* Listar produtos
* Atualizar produto
* Inativar produto
* Buscar produtos
* Paginar resultados

---

# 📊 Estoque

O estoque será um dos principais pontos técnicos do projeto.

Operações:

```text
ENTRADA
RESERVA
SAÍDA
AJUSTE
CANCELAMENTO
```

Exemplo:

```text
Produto
Estoque = 10

Pedido criado
     ↓
Reserva = 2
     ↓
Estoque disponível = 8
```

## Concorrência

Um problema real que será tratado:

```text
Estoque = 1

Pedido A ──────┐
               ├──► tentativa de reserva
Pedido B ──────┘
```

Sem controle de concorrência, os dois pedidos podem tentar reservar o mesmo produto.

O Redis será utilizado para implementar mecanismos de lock quando necessário.

---

# 🛒 Pedidos

Fluxo principal:

```text
Cliente
   ↓
Criação do pedido
   ↓
Validação
   ↓
Reserva de estoque
   ↓
Pagamento
   ↓
Pedido confirmado
   ↓
Notificação
   ↓
Entrega
```

Estados iniciais:

```text
PENDING
CONFIRMED
CANCELLED
COMPLETED
```

---

# 💳 Pagamentos

Inicialmente o pagamento será simulado.

Fluxo:

```text
OrderCreated
      ↓
PaymentRequested
      ↓
Payment Worker
      ↓
 ┌────┴─────┐
 ▼          ▼
CONFIRMED  REJECTED
```

Posteriormente poderá existir integração com um gateway real.

---

# 🐇 RabbitMQ

O RabbitMQ será utilizado para processamento assíncrono.

Exemplo:

```text
OrderCreated
     │
     ├────────► Stock Queue
     │
     ├────────► Payment Queue
     │
     └────────► Notification Queue
```

Serão estudados:

* Exchanges
* Queues
* Routing Keys
* Producers
* Consumers
* Acknowledgement
* Retry
* Dead Letter Queue
* Idempotência
* Processamento assíncrono

---

# 🔁 Idempotência

Eventos podem ser entregues mais de uma vez.

Exemplo:

```text
PaymentConfirmed
       ↓
Consumer
       ↓
Processamento
       ↓
ACK falha
       ↓
RabbitMQ entrega novamente
```

O sistema deverá identificar que aquele evento já foi processado.

Isso evita situações como:

```text
Pagamento confirmado
Pagamento confirmado novamente
Pagamento confirmado novamente
```

---

# ⚡ Redis

Redis terá duas responsabilidades principais.

## Cache

Produtos frequentemente consultados poderão ser armazenados em cache.

```text
GET /products/123
       ↓
     Redis
       │
   ┌───┴───┐
   │       │
  HIT     MISS
   │       │
   ▼       ▼
Return  PostgreSQL
           ↓
         Redis
```

## Locks

Redis também poderá ser utilizado para controlar concorrência em operações críticas.

Exemplo:

```text
lock:product:{productId}
```

---

# 🔔 Notificações

O sistema poderá enviar atualizações através de:

* WebSocket
* E-mail

Exemplo:

```text
Pedido confirmado
       ↓
RabbitMQ
       ↓
Notification Worker
       ↓
WebSocket
       ↓
Dashboard
```

---

# 🧱 Arquitetura de software

O projeto utilizará conceitos de:

* Clean Architecture
* Arquitetura Hexagonal
* DDD
* SOLID
* Dependency Inversion
* Separation of Concerns

Estrutura inicial:

```text
src/main/java/com/giro

├── domain
│   ├── entity
│   ├── valueobject
│   ├── repository
│   └── event
│
├── application
│   ├── usecase
│   └── service
│
├── infrastructure
│   ├── persistence
│   ├── messaging
│   ├── cache
│   └── external
│
└── presentation
    ├── controller
    ├── dto
    ├── exception
    └── config
```

A estrutura poderá evoluir conforme o projeto crescer.

---

# 🗄️ Banco de dados

PostgreSQL será o banco principal.

Tecnologias:

* PostgreSQL
* Spring Data JPA
* Hibernate
* Flyway

O banco será versionado através de migrations.

Exemplo:

```text
V1__create_company.sql
V2__create_user.sql
V3__create_product.sql
V4__create_stock.sql
V5__create_order.sql
```

---

# 🧪 Testes

O projeto terá diferentes níveis de testes.

## Unitários

Testar regras de negócio isoladamente.

```text
Use Case
   ↓
JUnit + Mockito
```

## Integração

Testar integração com:

* PostgreSQL
* Redis
* RabbitMQ

## E2E

Testar fluxos completos.

Exemplo:

```text
Login
 ↓
Criar pedido
 ↓
Reservar estoque
 ↓
Processar pagamento
 ↓
Confirmar pedido
 ↓
Enviar notificação
```

Para testes de infraestrutura, será estudado o uso de **Testcontainers**.

---

# 📈 Observabilidade

O sistema terá:

* Logs estruturados
* Correlation ID
* Health checks
* Métricas
* Monitoramento
* Tratamento de erros

Exemplo:

```text
Request
   │
   │ correlationId = abc-123
   ▼
Monaly API
   │
   ▼
RabbitMQ
   │
   ▼
Worker
   │
   ▼
Database
```

Isso permitirá rastrear uma operação através dos diferentes componentes.

---

# 🐳 Docker

Ambiente local:

```text
docker-compose

├── giro-api
├── postgres
├── redis
└── rabbitmq
```

Posteriormente:

```text
giro-api
frontend
postgres
redis
rabbitmq
```

---

# 🚀 CI/CD

O projeto será integrado ao Harness.

Pipeline inicial:

```text
Git Push
   ↓
Build
   ↓
Tests
   ↓
Lint / Quality
   ↓
Docker Build
   ↓
Docker Registry
   ↓
Deploy
```

A implementação do CI/CD será feita depois que a aplicação estiver funcionando localmente.

---

# ☁️ Cloud

Como etapa posterior:

```text
AWS
 │
 ├── Application
 ├── PostgreSQL
 ├── Redis
 ├── RabbitMQ
 └── Observability
```

A infraestrutura poderá posteriormente ser provisionada utilizando Terraform.

---

# ☸️ Kubernetes

Kubernetes será uma etapa posterior do projeto.

Serão estudados:

* Deployment
* Service
* ConfigMap
* Secrets
* Replicas
* Health probes
* Resource limits
* Rolling updates

A adoção de Kubernetes somente ocorrerá depois que existir uma necessidade ou objetivo claro de aprendizado.

---

# 🗺️ Roadmap

## Fase 1 — Fundação

* Java
* Spring Boot
* Maven
* Docker
* PostgreSQL
* Flyway
* Arquitetura inicial

## Fase 2 — Autenticação

* Users
* Companies
* Spring Security
* JWT
* Roles
* Permissions

## Fase 3 — Produtos

* CRUD
* Validação
* Paginação
* Busca

## Fase 4 — Estoque

* Entradas
* Saídas
* Reservas
* Histórico
* Concorrência
* Redis Lock

## Fase 5 — Pedidos

* Customers
* Orders
* Order Items
* Transactions
* Status

## Fase 6 — Mensageria

* RabbitMQ
* Exchanges
* Queues
* Consumers
* Producers
* Retry
* DLQ
* Idempotência

## Fase 7 — Pagamentos

* Payment
* Payment Worker
* Eventos
* Simulação de gateway

## Fase 8 — Notificações

* WebSocket
* Notification Worker
* E-mail

## Fase 9 — Frontend

* React
* Login
* Dashboard
* Produtos
* Estoque
* Clientes
* Pedidos

## Fase 10 — Qualidade

* Unit tests
* Integration tests
* E2E
* Testcontainers
* Coverage

## Fase 11 — Observabilidade

* Logs
* Correlation ID
* Metrics
* Health checks
* Monitoring

## Fase 12 — CI/CD

* Harness
* Docker Registry
* Pipeline
* Automated deployment

## Fase 13 — Cloud

* AWS
* Terraform
* Kubernetes

---

# 📚 O que este projeto pretende demonstrar

Ao final, o Monaly deverá demonstrar conhecimento prático em:

```text
Java
Spring Boot
REST APIs
PostgreSQL
JPA / Hibernate
Redis
RabbitMQ
Docker
Testing
Clean Architecture
DDD
Event-Driven Architecture
Concurrency
Idempotency
Observability
CI/CD
Cloud
```

Mais importante que a quantidade de tecnologias será a capacidade de explicar:

> **Qual problema existia?**

> **Qual decisão técnica foi tomada?**

> **Por que essa solução foi escolhida?**

> **Quais eram as alternativas?**

> **Quais foram os trade-offs?**

Esse será o principal objetivo técnico do projeto.

---

# 📌 Princípio do projeto

> **Não utilizar uma tecnologia apenas para colocá-la no currículo.**

Se Redis estiver no projeto, deve existir uma razão.

Se RabbitMQ estiver no projeto, deve existir uma razão.

Se Kubernetes estiver no projeto, deve existir uma razão.

Se Terraform estiver no projeto, deve existir uma razão.

O objetivo é construir um sistema que permita discutir **problemas reais de engenharia de software**, e não apenas acumular tecnologias.


