# 🏗️ Arquitetura — Giro

Este documento explica como a API, os Workers, o RabbitMQ e os módulos do Giro se relacionam, além de definir como a arquitetura será evoluída ao longo do projeto.

A ideia principal é **não começar com uma arquitetura distribuída complexa sem necessidade**.

O Giro será inicialmente desenvolvido como um **Modular Monolith** e poderá evoluir gradualmente para processamento assíncrono e, posteriormente, para serviços independentes caso exista uma necessidade real.

---

# 1. Visão geral

O Giro será um sistema de gestão comercial para pequenos negócios.

A aplicação terá funcionalidades como:

* Autenticação
* Empresas
* Usuários
* Clientes
* Produtos
* Estoque
* Pedidos
* Pagamentos
* Notificações

Inicialmente, todas essas funcionalidades estarão dentro de uma única aplicação Spring Boot.

```text
                    ┌───────────────┐
                    │    Frontend   │
                    │     React     │
                    └───────┬───────┘
                            │
                            │ HTTP
                            ▼
                    ┌───────────────┐
                    │    Giro API   │
                    │  Spring Boot  │
                    │      Java     │
                    └───────┬───────┘
                            │
              ┌─────────────┼─────────────┐
              │             │             │
              ▼             ▼             ▼
        ┌──────────┐   ┌──────────┐  ┌──────────┐
        │PostgreSQL│   │  Redis   │  │ RabbitMQ │
        └──────────┘   └──────────┘  └──────────┘
```

---

# 2. O que é a API?

A API é a **porta de entrada principal do sistema**.

Ela recebe requisições do frontend, valida os dados, executa as regras necessárias e retorna uma resposta.

Por exemplo:

```text
React
  │
  │ POST /orders
  ▼
Giro API
  │
  ├── valida usuário
  ├── valida cliente
  ├── valida produtos
  ├── calcula pedido
  ├── grava pedido no PostgreSQL
  └── publica OrderCreated
```

A API terá endpoints como:

```http
POST /api/products
GET /api/products
GET /api/products/{id}

POST /api/customers
GET /api/customers

POST /api/orders
GET /api/orders
GET /api/orders/{id}
```

---

# 3. O que a API engloba?

A aplicação Spring Boot inicialmente será responsável pelos principais módulos do sistema:

```text
Giro API
│
├── Auth
├── Companies
├── Users
├── Customers
├── Products
├── Orders
├── Stock
├── Payments
└── Notifications
```

Isso não significa que cada item será um serviço separado.

Todos serão módulos dentro da mesma aplicação.

---

# 4. O que é um Worker?

Um Worker é um processo responsável por executar **tarefas assíncronas**.

Imagine que um pedido tenha sido criado.

Uma implementação simples poderia fazer:

```text
Criar pedido
    ↓
Processar pagamento
    ↓
Atualizar estoque
    ↓
Enviar e-mail
    ↓
Enviar WebSocket
    ↓
Responder HTTP
```

Isso pode tornar a requisição mais lenta e fazer com que várias responsabilidades fiquem acopladas ao processamento do pedido.

Uma alternativa é utilizar eventos e processamento assíncrono:

```text
                    API
                     │
                     │ OrderCreated
                     ▼
                 RabbitMQ
                     │
          ┌──────────┼──────────┐
          │          │          │
          ▼          ▼          ▼
       Stock      Payment   Notification
       Worker      Worker      Worker
```

Cada consumidor fica responsável por uma determinada tarefa.

---

# 5. Stock Worker

O Stock Worker seria responsável por processar eventos relacionados ao estoque.

Exemplo:

```text
OrderCreated
      ↓
RabbitMQ
      ↓
Stock Worker
      ↓
Verifica estoque
      ↓
Reserva produtos
      ↓
StockReserved
```

Ele poderia cuidar de:

* Reserva de estoque
* Liberação de reserva
* Atualização de estoque
* Processamento de eventos relacionados ao estoque

Porém, **não será criado como uma aplicação independente inicialmente**.

---

# 6. Payment Worker

O Payment Worker seria responsável pelo processamento de pagamentos.

Exemplo:

```text
OrderCreated
      ↓
RabbitMQ
      ↓
Payment Worker
      ↓
Processa pagamento
      ↓
PaymentConfirmed
```

Inicialmente o pagamento será simulado.

Por exemplo:

```text
PaymentRequested
       ↓
PaymentService
       ↓
Pagamento aprovado
```

Posteriormente esse processamento poderá ser transformado em uma operação assíncrona.

---

# 7. Notification Worker

O Notification Worker será responsável por processar eventos relacionados a notificações.

Exemplo:

```text
Pedido confirmado
       ↓
OrderConfirmed
       ↓
RabbitMQ
       ↓
Notification Worker
       ↓
Enviar notificação
```

As notificações poderão inicialmente utilizar:

* WebSocket
* E-mail

Posteriormente:

* WhatsApp
* SMS

---

# 8. Teremos vários projetos Java?

**Não inicialmente.**

Essa é uma decisão importante do projeto.

Não começaremos com:

```text
giro-api
stock-worker
payment-worker
notification-worker
```

como quatro aplicações independentes.

Isso adicionaria complexidade antes de existir uma necessidade real.

O projeto começará como um **Modular Monolith**.

---

# 9. Modular Monolith

A primeira versão terá uma única aplicação Spring Boot:

```text
Giro
│
└── Spring Boot
    │
    ├── Auth
    ├── Companies
    ├── Customers
    ├── Products
    ├── Stock
    ├── Orders
    ├── Payments
    └── Notifications
```

Os módulos serão separados logicamente, mas executados dentro da mesma aplicação.

---

# 10. Estrutura interna da aplicação

A aplicação poderá seguir uma estrutura baseada em Clean Architecture:

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

A separação dos módulos de negócio poderá evoluir conforme o sistema crescer.

---

# 11. Quando o RabbitMQ entra?

Depois que o fluxo principal da aplicação estiver funcionando.

Inicialmente:

```text
Frontend
   ↓
Giro API
   ↓
PostgreSQL
```

Depois introduzimos:

```text
Frontend
   ↓
Giro API
   ↓
PostgreSQL
   │
   └──► RabbitMQ
```

A partir disso, eventos poderão ser publicados para processamento assíncrono.

---

# 12. Consumers dentro da própria API

Uma aplicação Spring Boot pode funcionar como API HTTP e também consumir mensagens do RabbitMQ.

Por exemplo:

```java
@RabbitListener(queues = "stock.queue")
public void process(OrderCreatedEvent event) {
    // Reserva estoque
}
```

Nesse cenário ainda temos apenas **uma aplicação Spring Boot**.

Ela possui:

```text
Controllers
Services
Repositories
Producers
Consumers
```

Ou seja:

```text
                Giro Spring Boot
                       │
          ┌────────────┼────────────┐
          │            │            │
          ▼            ▼            ▼
       HTTP API    RabbitMQ      PostgreSQL
                    Consumer
```

---

# 13. Quando separar um Worker?

Um Worker poderá ser separado quando existir uma justificativa arquitetural.

Por exemplo, imagine que o processamento de estoque fique pesado.

Podemos evoluir para:

```text
                  ┌──────────────┐
                  │   Frontend   │
                  └──────┬───────┘
                         │
                         ▼
                  ┌──────────────┐
                  │   Giro API   │
                  └──────┬───────┘
                         │
                         ▼
                    RabbitMQ
                         │
                         ▼
                  ┌──────────────┐
                  │ Stock Worker │
                  └──────────────┘
```

Agora o `Stock Worker` poderia ser uma aplicação Spring Boot independente.

---

# 14. Evolução para múltiplos serviços

Caso exista uma necessidade real, a arquitetura poderá evoluir para:

```text
Giro
│
├── giro-api
│
├── stock-worker
│
├── payment-worker
│
└── notification-worker
```

Cada aplicação poderá ter:

* Seu próprio processo
* Seu próprio ciclo de deploy
* Sua própria escala
* Seus próprios consumers
* Responsabilidades específicas

O RabbitMQ funcionará como mecanismo de comunicação assíncrona entre os processos.

---

# 15. Evolução arquitetural do projeto

A evolução planejada é:

## Fase 1 — Modular Monolith

```text
                 React
                   │
                   ▼
            ┌─────────────┐
            │ Giro API    │
            │ Spring Boot │
            └──────┬──────┘
                   │
          ┌────────┼────────┐
          ▼        ▼        ▼
      PostgreSQL  Redis   RabbitMQ
```

Tudo começa em uma única aplicação Java.

---

## Fase 2 — Processamento assíncrono

Introdução do RabbitMQ:

```text
Giro API
   │
   │ OrderCreated
   ▼
RabbitMQ
   │
   ├──► Stock Consumer
   ├──► Payment Consumer
   └──► Notification Consumer
```

Os consumers ainda podem estar dentro do mesmo Spring Boot.

---

## Fase 3 — Workers independentes

Somente caso exista uma justificativa:

```text
                    React
                      │
                      ▼
                ┌──────────┐
                │ Giro API  │
                └────┬─────┘
                     │
                     ▼
                  RabbitMQ
                /     |      \
               /      |       \
              ▼       ▼        ▼
          Stock     Payment  Notification
          Worker     Worker     Worker
```

Nesse estágio, os Workers poderão ser aplicações independentes.

---

# 16. Por que não começar com microsserviços?

O projeto não terá microsserviços desde o primeiro dia simplesmente para aumentar a complexidade tecnológica.

Começar diretamente com vários serviços criaria responsabilidades adicionais:

* Comunicação entre serviços
* Service discovery
* Configuração distribuída
* Logs distribuídos
* Deploy independente
* Observabilidade distribuída
* Gerenciamento de falhas
* Versionamento de contratos
* Mais containers
* Mais infraestrutura

Antes de existir uma necessidade real, essa complexidade não traz necessariamente benefícios proporcionais.

---

# 17. Decisão arquitetural

A decisão inicial é:

> **Começar com um Modular Monolith e evoluir a arquitetura conforme surgirem necessidades reais.**

Isso permite:

```text
Menor complexidade inicial
          ↓
Desenvolvimento do domínio
          ↓
Identificação de problemas reais
          ↓
Introdução de mensageria
          ↓
Processamento assíncrono
          ↓
Separação de Workers quando necessário
```

---

# 18. O que queremos aprender com essa evolução?

Mais importante que simplesmente implementar as tecnologias é entender **por que cada decisão foi tomada**.

Para cada evolução, devemos conseguir responder:

### Qual problema existia?

Exemplo:

> O processamento do pedido estava realizando tarefas que não precisavam bloquear a resposta HTTP.

### Qual solução foi escolhida?

> Introdução de processamento assíncrono utilizando RabbitMQ.

### Por que essa solução?

> Para desacoplar determinadas tarefas do fluxo principal da requisição.

### Quais eram as alternativas?

Por exemplo:

* Processamento síncrono
* Job agendado
* Event bus
* RabbitMQ
* Outro mecanismo de mensageria

### Quais são os trade-offs?

A solução escolhida também adiciona complexidade.

---

# 19. Princípio principal

> **Não criar complexidade arquitetural antes de existir um problema que justifique essa complexidade.**

O Giro será desenvolvido de forma incremental.

A arquitetura deverá evoluir junto com as necessidades do sistema.

```text
Problema
   ↓
Análise
   ↓
Decisão arquitetural
   ↓
Implementação
   ↓
Medição
   ↓
Evolução
```

Esse processo será parte importante do aprendizado do projeto.
