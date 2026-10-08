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

# ▶️ Como executar o projeto

Atualmente, o fluxo recomendado para desenvolvimento é executar o **PostgreSQL, Redis e RabbitMQ em Docker** e a **API localmente**. O repositório ainda não possui um arquivo `.env`; as variáveis abaixo são exemplos para configurar o ambiente local futuramente.

## Pré-requisitos

- Java Development Kit (JDK) 24.
- Docker Desktop com Docker Compose.
- Git, para clonar o repositório.
- PowerShell no Windows para os comandos deste guia.

O Maven é executado pelo Maven Wrapper incluído no repositório (`mvnw.cmd`), que baixa a versão necessária na primeira execução.

Confira as ferramentas:

```powershell
java -version
docker --version
docker compose version
```

## 1. Obter o projeto

Se ainda não tiver uma cópia local:

```powershell
git clone <URL_DO_REPOSITORIO>
Set-Location erp-monaly
```

Se o repositório já estiver clonado, entre na pasta do projeto:

```powershell
Set-Location C:\erp-monaly
```

## 2. Iniciar PostgreSQL, Redis e RabbitMQ em Docker

Na raiz do projeto, inicie apenas os serviços de infraestrutura:

```powershell
docker compose up -d postgres redis rabbitmq
docker compose ps
```

O PostgreSQL possui health check no Compose. Aguarde o estado ficar saudável antes de iniciar a API. Redis e RabbitMQ também podem ser conferidos em `docker compose ps`.

Portas locais configuradas:

| Serviço | Endereço local | Credenciais de desenvolvimento do Compose |
|---|---|---|
| PostgreSQL | `localhost:5432` (banco `monaly`) | usuário `monaly`, senha `monaly` |
| Redis | `localhost:6379` | sem senha configurada no Compose |
| RabbitMQ | `localhost:5672` | usuário `monaly`, senha `monaly` |
| Painel de gerenciamento do RabbitMQ | <http://localhost:15672> | usuário `monaly`, senha `monaly` |

> **Atenção:** esses valores são apenas para desenvolvimento local, conforme definidos no `docker-compose.yaml`. Não os use em produção nem os reutilize em ambientes compartilhados. Em produção, use credenciais fortes e configuração de segredos externa ao repositório.

Os dados do PostgreSQL, Redis e RabbitMQ são mantidos nos volumes Docker. Para parar os serviços sem remover os dados:

```powershell
docker compose stop postgres redis rabbitmq
```

## 3. Configurar as variáveis da aplicação local

O `application.properties` já usa `localhost:5432/monaly` e o usuário `monaly` como padrão para a conexão local com PostgreSQL. Para deixar a configuração explícita, configure as variáveis no terminal PowerShell antes de iniciar a API:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/monaly"
$env:SPRING_DATASOURCE_USERNAME = "monaly"
$env:SPRING_DATASOURCE_PASSWORD = "monaly"
```

As variáveis abaixo correspondem à configuração disponível no Docker Compose para Redis e RabbitMQ. Podem ser configuradas quando a aplicação passar a integrar esses serviços:

```powershell
$env:SPRING_DATA_REDIS_HOST = "localhost"
$env:SPRING_DATA_REDIS_PORT = "6379"

$env:SPRING_RABBITMQ_HOST = "localhost"
$env:SPRING_RABBITMQ_PORT = "5672"
$env:SPRING_RABBITMQ_USERNAME = "monaly"
$env:SPRING_RABBITMQ_PASSWORD = "monaly"
```

> **Estado atual:** as variáveis de Redis e RabbitMQ estão preparadas para uso futuro; isso não significa que a aplicação já tenha integração funcional com esses serviços. As variáveis definidas com `$env:` valem somente para a janela atual do PowerShell. Ainda não há um `.env` ou `.env.example` no projeto.

Não coloque credenciais reais no `application.properties`, no código ou em arquivos versionados. Quando for criado um arquivo local para variáveis, mantenha valores reais fora do Git e forneça apenas um exemplo sem segredos para outros desenvolvedores.

## 4. Aplicar migrations e iniciar a API localmente

Com o PostgreSQL em execução e as variáveis configuradas, inicie a aplicação na raiz do projeto:

```powershell
.\mvnw.cmd spring-boot:run
```

O Flyway está habilitado na aplicação. As migrations em `src/main/resources/db/migration` são aplicadas automaticamente na inicialização, antes da API ficar disponível. O Hibernate está configurado para validar o schema, não para criá-lo automaticamente.

Para gerar o pacote executável:

```powershell
.\mvnw.cmd clean package
```

Depois, o JAR pode ser iniciado com:

```powershell
java -jar .\target\erp-monaly-0.0.1-SNAPSHOT.jar
```

## 5. Conferir a prontidão da aplicação

Com a aplicação iniciada, abra outro terminal PowerShell e consulte o endpoint de prontidão:

```powershell
Invoke-RestMethod http://localhost:8080/readyz
```

Quando a aplicação e o PostgreSQL estiverem prontos, o Actuator retorna um status `UP`. Se o endpoint responder com erro ou status diferente de sucesso, confira os logs da aplicação e o estado do PostgreSQL.

O endpoint `/readyz` é liberado sem autenticação e verifica tanto a prontidão da aplicação quanto a conexão com o banco. O `/health` existente continua disponível como verificação simples da API, mas não deve ser usado para confirmar a prontidão do banco.

No modo Docker Compose, confira o estado `healthy`:

```powershell
docker compose ps
```

O health check da aplicação consulta `/readyz`; o container só fica saudável após a API iniciar e o indicador de saúde do PostgreSQL ficar `UP`.

## 6. Executar os testes

Os testes unitários usam mocks e não precisam de PostgreSQL, Redis, RabbitMQ ou Docker. Execute-os na raiz do projeto:

```powershell
.\mvnw.cmd test
```

O teste de integração da persistência inicia um PostgreSQL temporário com Testcontainers, aplica as migrations Flyway e valida uma gravação. Para executá-lo junto com os testes unitários, o Docker precisa estar instalado e em execução:

```powershell
.\mvnw.cmd verify
```

O ciclo `verify` é o comando recomendado para validar toda a suíte. O build da imagem Docker executa `mvn clean package`, que inclui os testes unitários sem tentar iniciar os testes de integração durante a construção da imagem.

## Alternativa: iniciar todos os serviços pelo Docker Compose

O Compose também define um container para a aplicação. Para construir e iniciar **API, PostgreSQL, Redis e RabbitMQ**:

```powershell
docker compose up --build
```

Nesse modo, os serviços se comunicam pelos nomes internos `postgres`, `redis` e `rabbitmq`; não use `localhost` como host do banco dentro do container da API. A configuração correspondente já está no `docker-compose.yaml`.

Confira os containers em outro terminal:

```powershell
docker compose ps
```

E verifique a API com:

```powershell
Invoke-RestMethod http://localhost:8080/readyz
```

Para parar os containers:

```powershell
docker compose down
```

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


---
