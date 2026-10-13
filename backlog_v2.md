# Backlog v2 — Monaly

Plano de entrega para um ERP simples de estoque e vendas para pequenos comércios.

> **Objetivo do MVP:** permitir que uma empresa cadastre seus produtos e clientes, registre entradas e saídas de estoque e faça vendas sem perder o histórico do que aconteceu.
>
> **Premissa deste plano:** o primeiro MVP será uma API funcional. O frontend, pagamentos online e integrações externas ficam para depois. As sprints são sugestões; ajuste a duração ao tamanho do time e à disponibilidade.

---

## 1. Leitura rápida do repositório

### Já existe, ao menos parcialmente

- Projeto Java 24 com Spring Boot, Maven, PostgreSQL, JPA, Flyway, validação e Spring Security.
- Docker Compose para a aplicação e serviços locais PostgreSQL, Redis e RabbitMQ.
- Entidade, migration, repositório, casos de uso e endpoints para criar e listar empresas.
- Migration e classes iniciais de usuário/função.
- Endpoint de health check.
- Um teste de inicialização da aplicação.
- Documentos de arquitetura, produto e backlog anteriores.

### Ainda não existe no código

- Autenticação e autorização funcionais; os endpoints de empresa estão liberados sem autenticação.
- Associação de usuários à empresa e isolamento de dados por empresa.
- Catálogo de produtos, clientes, estoque, movimentações ou vendas.
- Frontend e testes das regras de negócio.

### Atenções para o começo do trabalho

- Revisar a migration de usuários antes de usá-la: os limites atuais de e-mail e senha são pequenos para os valores esperados; o cadastro também precisa armazenar hash de senha, não senha em texto puro.
- No MVP, cada usuário pertence a uma única empresa e cada e-mail identifica uma única conta no sistema todo; suporte a usuários associados a múltiplas empresas fica para um card futuro, se necessário.
- Manter o PostgreSQL como fonte da verdade para saldo e vendas. Redis, RabbitMQ, microsserviços e Kubernetes não são necessários para entregar o primeiro MVP.
- Substituir endpoints de demonstração e respostas diretas de entidades por contratos de API estáveis, conforme os módulos forem implementados.

---

## 2. Como usar este backlog

- **Prioridade:** `P0` bloqueia o MVP; `P1` importante para operar; `P2` evolução.
- **Estimativa:** pontos relativos, não horas. Reestime com o time antes de assumir uma sprint.
- Cada card tem descrição e critérios de aceite para poder ser copiado como história/tarefa no Jira ou Trello.
- Mantenha um card em andamento por pessoa sempre que possível; conclua e teste antes de puxar mais trabalho.
- Não comece a sprint seguinte se os critérios de aceite da sprint atual não estiverem atendidos.

### Definição de pronto (aplicar a todos os cards)

- Regras e validações cobertas por testes adequados.
- API documentada com exemplos de requisição e resposta, quando houver endpoint.
- Erros retornam status HTTP e mensagem consistentes, sem expor dados sensíveis.
- Mudanças de banco feitas por nova migration Flyway; migrations já aplicadas não são editadas.
- Dados de uma empresa não podem ser lidos nem alterados por outra.
- Build e testes relevantes passam.

---

## 3. Sprints sugeridas

### Sprint 0 — Base confiável

**Objetivo:** qualquer pessoa do time consegue subir o projeto localmente e validar as mudanças.

#### MON-001 — Documentar como rodar a aplicação localmente
- **Prioridade:** P0 | **Estimativa:** 2 pontos
- **Descrição:** atualizar as instruções do projeto para instalar dependências, configurar variáveis, subir a aplicação e executar testes.
- **Aceite:**
  - README contém comandos reproduzíveis para ambiente local e Docker Compose.
  - Credenciais de desenvolvimento ficam em configuração local/documentada como exemplo, não em arquivos de produção.
  - Há instrução para aplicar migrations e conferir o health check.

#### MON-002 — Padronizar configuração local e migrations
- **Prioridade:** P0 | **Estimativa:** 3 pontos
- **Descrição:** revisar configuração PostgreSQL/Flyway/JPA e garantir que uma instalação limpa sobe sem ajustes manuais no banco.
- **Aceite:**
  - Aplicação inicia contra um banco vazio usando Flyway.
  - Hibernate valida o schema sem criar tabelas automaticamente.
  - Configuração sensível e específica de ambiente é externa ao código.
  - Docker Compose informa claramente quando o banco está saudável e a aplicação pronta.

#### MON-003 — Criar base de testes unitários e de integração
- **Prioridade:** P0 | **Estimativa:** 3 pontos
- **Descrição:** definir padrão simples para testes de casos de uso, APIs e persistência, começando pelo que já existe.
- **Aceite:**
  - Testes unitários não precisam de banco nem de serviços externos.
  - Existe pelo menos um teste de integração que valida uma migration e uma operação persistida.
  - O comando de teste do Maven está documentado e é executado no build.

#### MON-004 — Padronizar respostas e erros da API
- **Prioridade:** P1 | **Estimativa:** 3 pontos
- **Descrição:** definir formato consistente para erros de validação, recurso não encontrado e conflito.
- **Aceite:**
  - Erros de validação retornam `400` com campos inválidos identificados.
  - Conflitos de unicidade retornam `409`.
  - Respostas não expõem stack trace, SQL ou dados internos.
  - O tratamento atual de empresa duplicada segue o padrão definido.

---

### Sprint 1 — Empresa, usuário e acesso seguro

**Objetivo:** cada pessoa entra em uma empresa autenticada e não consegue acessar dados de outra.

#### MON-005 — Definir o vínculo entre usuários e empresas
- **Prioridade:** P0 | **Estimativa:** 3 pontos
- **Descrição:** decidir e implementar o modelo inicial de associação. Para o MVP, assumir que cada usuário pertence a uma empresa.
- **Aceite:**
  - Usuário tem vínculo obrigatório com empresa.
  - Schema tem chave estrangeira e índices/unicidade necessários.
  - Migration nova corrige limites de campos e não armazena senha em texto puro.
  - Usuários associados a múltiplas empresas ficam fora do MVP; esse suporte poderá ser avaliado em um card futuro.

#### MON-006 — Finalizar cadastro de empresa
- **Prioridade:** P0 | **Estimativa:** 3 pontos
- **Descrição:** validar e persistir o cadastro de empresa com contrato de API estável.
- **Aceite:**
  - Nome é obrigatório e tem tamanho máximo definido.
  - CNPJ é validado e normalizado antes da gravação.
  - CNPJ duplicado retorna `409`, inclusive em tentativas simultâneas.
  - API não expõe diretamente a entidade JPA.
  - Há testes para dados válidos, inválidos e duplicados.

#### MON-007 — Cadastrar usuário administrador da empresa
- **Prioridade:** P0 | **Estimativa:** 5 pontos
- **Descrição:** permitir criar o primeiro usuário administrador associado à empresa.
- **Aceite:**
  - E-mail tem formato válido e unicidade definida (global no MVP).
  - Senha é armazenada com algoritmo de hash seguro.
  - Papel inicial é definido pelo servidor; o cliente não consegue se promover a administrador.
  - Resposta nunca inclui senha ou hash.
  - Tentativa de cadastrar e-mail já usado retorna conflito.

#### MON-008 — Implementar login e autenticação
- **Prioridade:** P0 | **Estimativa:** 5 pontos
- **Descrição:** implementar login para usuários cadastrados e proteger as rotas privadas.
- **Aceite:**
  - Credenciais válidas autenticam e credenciais inválidas retornam `401`.
  - Token/sessão tem validade e mecanismo de assinatura/configuração segura.
  - Senhas não são retornadas nem registradas em logs.
  - Rotas de saúde e login são públicas; demais rotas privadas exigem autenticação.
  - Testes cobrem login e acesso autenticado/não autenticado.

#### MON-009 — Aplicar papéis e autorização básica
- **Prioridade:** P0 | **Estimativa:** 3 pontos
- **Descrição:** definir papéis mínimos para o MVP e restringir operações administrativas.
- **Aceite:**
  - Apenas administrador pode cadastrar usuários e alterar configurações da empresa.
  - Usuário autenticado sem permissão recebe `403`.
  - O papel vem da identidade autenticada, não de parâmetro enviado na requisição.
  - Matriz de permissões é documentada e testada.

#### MON-010 — Aplicar isolamento por empresa nas APIs
- **Prioridade:** P0 | **Estimativa:** 5 pontos
- **Descrição:** extrair a empresa da identidade autenticada e aplicar o escopo em toda consulta e gravação de dados de negócio.
- **Aceite:**
  - Cliente não consegue escolher livremente `companyId` para ler ou alterar dados.
  - Toda consulta de negócio filtra pela empresa autenticada.
  - IDs de outra empresa não revelam nem alteram os dados (retorno consistente, por exemplo `404`).
  - Testes automatizados demonstram isolamento entre duas empresas.

---

### Sprint 2 — Catálogo de produtos

**Objetivo:** uma empresa consegue manter um catálogo utilizável para controlar e vender seus itens.

#### MON-011 — Criar modelo e migration de produto
- **Prioridade:** P0 | **Estimativa:** 3 pontos
- **Descrição:** persistir produto vinculado a uma empresa, com nome, SKU opcional, preço e estado ativo.
- **Aceite:**
  - Produto tem identificador, empresa, nome, preço, estado e datas de criação/atualização.
  - Preço usa tipo decimal adequado, nunca ponto flutuante binário.
  - SKU, quando informado, é único dentro da empresa.
  - Migration e relacionamentos têm restrições e índices necessários.

#### MON-012 — Cadastrar produto
- **Prioridade:** P0 | **Estimativa:** 3 pontos
- **Descrição:** criar produto validando os campos necessários para uso no estoque e na venda.
- **Aceite:**
  - Nome obrigatório e com limites definidos.
  - Preço não pode ser negativo.
  - SKU duplicado na mesma empresa retorna `409`; outra empresa pode usar o mesmo SKU.
  - Produto é sempre associado à empresa autenticada.
  - Testes cobrem validações e duplicidade.

#### MON-013 — Consultar e pesquisar produtos
- **Prioridade:** P0 | **Estimativa:** 5 pontos
- **Descrição:** listar produtos da empresa e buscar por identificador, nome e SKU, com paginação.
- **Aceite:**
  - Listagem é paginada e ordenação padrão é estável.
  - Busca não retorna produtos de outras empresas.
  - Produto inexistente ou pertencente a outra empresa não é exposto.
  - Filtros podem incluir ativos/inativos.

#### MON-014 — Editar e inativar produto
- **Prioridade:** P1 | **Estimativa:** 3 pontos
- **Descrição:** permitir corrigir dados do catálogo sem apagar o histórico de movimentações e vendas.
- **Aceite:**
  - Campos permitidos para edição são validados.
  - Inativar preserva registros históricos.
  - Produto inativo não pode ser incluído em nova venda.
  - Reativação, se permitida, exige operação explícita.

---

### Sprint 3 — Controle de estoque

**Objetivo:** registrar cada alteração de saldo com motivo e impedir saldo negativo.

#### MON-015 — Definir saldo e histórico de movimentações
- **Prioridade:** P0 | **Estimativa:** 5 pontos
- **Descrição:** modelar o saldo atual e o livro de movimentações, incluindo entrada, saída e ajuste.
- **Aceite:**
  - Cada movimentação pertence à empresa e ao produto.
  - Movimentação registra tipo, quantidade, data, usuário e motivo/referência.
  - Saldo inicial é zero; quantidades são positivas e o tipo define o sentido.
  - Registros históricos não são editados ou apagados para corrigir saldo; correções geram nova movimentação.
  - Operações de saldo e histórico são transacionais.

#### MON-016 — Registrar entrada de estoque
- **Prioridade:** P0 | **Estimativa:** 3 pontos
- **Descrição:** permitir entrada manual de mercadoria, incluindo estoque inicial.
- **Aceite:**
  - Usuário autorizado informa produto e quantidade maior que zero.
  - Saldo aumenta e uma movimentação correspondente é gravada atomicamente.
  - É possível incluir observação/motivo.
  - Operação respeita o escopo da empresa e tem testes.

#### MON-017 — Registrar saída e ajuste de estoque
- **Prioridade:** P0 | **Estimativa:** 5 pontos
- **Descrição:** permitir saída manual por perda/uso e ajuste de inventário com motivo obrigatório.
- **Aceite:**
  - Saída não deixa o saldo negativo.
  - Ajuste registra o saldo contado e gera movimentação auditável.
  - Tipo e motivo ficam disponíveis no histórico.
  - Duas saídas concorrentes não conseguem consumir mais que o saldo disponível.

#### MON-018 — Consultar saldo e histórico por produto
- **Prioridade:** P0 | **Estimativa:** 3 pontos
- **Descrição:** apresentar saldo atual e movimentações paginadas para conferência.
- **Aceite:**
  - Consulta retorna somente dados da empresa autenticada.
  - Histórico mostra tipo, quantidade, data, motivo/referência e saldo resultante ou informação suficiente para reconciliá-lo.
  - Filtros por período e tipo podem ser aplicados.
  - Há teste de consistência entre movimentações e saldo.

#### MON-019 — Definir baixo estoque e alerta visual por consulta
- **Prioridade:** P1 | **Estimativa:** 3 pontos
- **Descrição:** permitir cadastrar limite mínimo e identificar itens que precisam de reposição, sem ainda adicionar mensageria.
- **Aceite:**
  - Produto pode ter limite mínimo opcional, não negativo.
  - Consulta de estoque diferencia abaixo do mínimo, no limite e acima dele.
  - Produtos sem limite definido não são sinalizados como erro.

---

### Sprint 4 — Clientes e vendas

**Objetivo:** registrar uma venda completa e atualizar o estoque sem perder informação.

#### MON-020 — Cadastrar e consultar clientes
- **Prioridade:** P1 | **Estimativa:** 5 pontos
- **Descrição:** guardar os dados mínimos para identificar clientes, sem tornar o cadastro obrigatório para venda.
- **Aceite:**
  - Cliente é vinculado à empresa e tem nome obrigatório.
  - E-mail/telefone/documento são opcionais conforme regra definida; dados preenchidos são validados.
  - Listagem é paginada e permite busca por nome/documento.
  - Endpoints respeitam isolamento entre empresas.

#### MON-021 — Criar venda e itens
- **Prioridade:** P0 | **Estimativa:** 8 pontos
- **Descrição:** registrar uma venda com um ou mais produtos, quantidades e preços unitários congelados no momento da operação.
- **Aceite:**
  - Venda aceita cliente opcional e um ou mais itens ativos da empresa.
  - Quantidades devem ser positivas e saldo suficiente deve ser confirmado.
  - Preço e total são calculados no servidor; não se confia no total enviado pelo cliente.
  - Dados da venda preservam preço, nome/SKU necessários para consulta histórica mesmo que o produto mude depois.
  - Criar venda, itens, baixa do estoque e movimentações ocorre numa única transação.
  - Falha em qualquer item não deixa venda parcial nem estoque parcialmente baixado.

#### MON-022 — Consultar vendas
- **Prioridade:** P0 | **Estimativa:** 5 pontos
- **Descrição:** listar vendas e consultar detalhes para conferência operacional.
- **Aceite:**
  - Lista é paginada e permite filtrar por período e situação.
  - Detalhe inclui itens, quantidades, preços e total.
  - Vendas de outra empresa não são acessíveis.
  - Valores e timestamps têm formato documentado.

#### MON-023 — Cancelar venda e devolver estoque
- **Prioridade:** P1 | **Estimativa:** 5 pontos
- **Descrição:** cancelar uma venda seguindo regras explícitas e registrar devolução de estoque sem apagar a venda original.
- **Aceite:**
  - Só vendas em situação cancelável podem ser canceladas.
  - Cancelamento é idempotente: repetir a solicitação não duplica a devolução.
  - Devolução de itens e mudança de estado são atômicas e auditáveis.
  - Usuário, data e motivo do cancelamento ficam registrados.

---

### Sprint 5 — Fechamento do MVP

**Objetivo:** tornar o fluxo verificável e operável por uma pessoa do comércio.

#### MON-024 — Relatório operacional de vendas e estoque
- **Prioridade:** P1 | **Estimativa:** 5 pontos
- **Descrição:** disponibilizar consultas simples para acompanhamento diário sem montar um data warehouse.
- **Aceite:**
  - É possível consultar total de vendas por período.
  - É possível listar produtos abaixo do estoque mínimo.
  - Relatórios são limitados à empresa autenticada e têm limites de período/paginação quando aplicável.
  - Totais consideram somente vendas válidas conforme a regra documentada.

#### MON-025 — Documentar contratos da API e fluxo de operação
- **Prioridade:** P1 | **Estimativa:** 3 pontos
- **Descrição:** documentar como cadastrar empresa/usuário, produtos, estoque, clientes e registrar venda.
- **Aceite:**
  - Documentação contém exemplos para sucesso e erros comuns.
  - Uma pessoa consegue reproduzir o fluxo principal sem ler o código.
  - Permissões necessárias e configuração local estão descritas.

#### MON-026 — Automatizar teste ponta a ponta do fluxo principal
- **Prioridade:** P0 | **Estimativa:** 5 pontos
- **Descrição:** validar cadastro e venda com PostgreSQL real de teste e verificar saldo e histórico final.
- **Aceite:**
  - Cenário cobre empresa, usuário autenticado, produto, entrada, venda e consulta do saldo.
  - Cenário de saldo insuficiente não cria venda nem movimentação parcial.
  - Cenário de isolamento entre empresas é validado.
  - Teste pode rodar em CI sem depender de serviços externos compartilhados.

#### MON-027 — Preparar fluxo básico de interface web
- **Prioridade:** P1 | **Estimativa:** 8 pontos
- **Descrição:** criar uma interface mínima para o dono/operador usar o MVP (cadastro/login, produtos, estoque e venda). Este card depende da decisão de incluir um frontend neste repositório/projeto.
- **Aceite:**
  - Operador consegue executar o fluxo principal sem chamadas manuais à API.
  - Erros de validação e de saldo insuficiente são apresentados claramente.
  - Tela não permite selecionar ou alterar a empresa de outro usuário.

---

## 4. Backlog posterior ao MVP

Ordenado por valor provável para pequenos comércios. Reavaliar com usuários antes de iniciar.

### P1 — Operação comercial

#### MON-028 — Registrar fornecedores e compras para reposição
- **Estimativa:** 8 pontos
- Criar fornecedor, registrar compra/recebimento e gerar entrada de estoque vinculada à compra.
- **Aceite principal:** recebimento parcial ou completo gera movimentações rastreáveis; uma compra não pode ser recebida duas vezes acima da quantidade pedida.

#### MON-029 — Registrar formas de pagamento e situação de recebimento
- **Estimativa:** 5 pontos
- Registrar pagamento em dinheiro, cartão, Pix ou outro método escolhido, sem integrar inicialmente com adquirentes.
- **Aceite principal:** valor pago e forma ficam na venda; diferença entre total e recebido segue regra de negócio explícita.

#### MON-030 — Exportar produtos, estoque e vendas
- **Estimativa:** 5 pontos
- Exportar dados filtrados em CSV para conferência e migração.
- **Aceite principal:** exportação respeita empresa, filtros e limites; campos sensíveis não são incluídos.

#### MON-031 — Auditoria de operações importantes
- **Estimativa:** 5 pontos
- Registrar usuário, data e alterações relevantes para produto, estoque, venda e permissões.
- **Aceite principal:** trilha é consultável por usuário autorizado e não expõe credenciais.

### P2 — Crescimento e integrações

#### MON-032 — Devolução parcial e troca
- **Estimativa:** 8 pontos
- Registrar devolução de parte dos itens com atualização de estoque e histórico, separada do cancelamento integral.

#### MON-033 — Integração com gateway de pagamento
- **Estimativa:** 8 pontos
- Só iniciar após definir provedor, fluxo de confirmação, estorno e requisitos legais/operacionais.

#### MON-034 — Notificações de baixo estoque
- **Estimativa:** 5 pontos
- Notificar por canal escolhido quando produto cruzar o mínimo, com controle para não repetir alertas continuamente.

#### MON-035 — Cache e otimização medidos
- **Estimativa:** 5 pontos
- Medir gargalos antes de introduzir Redis/cache; invalidar corretamente dados mutáveis se houver necessidade comprovada.

#### MON-036 — Mensageria para processos assíncronos
- **Estimativa:** 8 pontos
- Introduzir RabbitMQ, outbox/idempotência, retry e DLQ apenas quando houver um processo que justifique execução assíncrona.

#### MON-037 — Implantação em nuvem e escala
- **Estimativa:** 8+ pontos
- Pipeline, monitoramento, backups e infraestrutura gerenciada. Kubernetes e microsserviços são decisões posteriores, não requisitos do MVP.

---

## 5. Fora do MVP

Para manter o primeiro produto simples, não bloquear a entrega com:

- Redis, locks distribuídos ou cache antes de comprovar necessidade.
- RabbitMQ, workers independentes ou microsserviços.
- Integração real de pagamentos, emissão fiscal ou entregas.
- WebSocket, notificações multicanal ou aplicativo mobile.
- Kubernetes, Terraform e implantação multi-cloud.
- Estoque multi-depósito, lotes, validade e código de barras — avaliar com usuários antes de incluir.

---

## 6. Critérios para considerar o MVP entregue

- Um administrador consegue criar empresa e usuário e autenticar.
- Usuários só consultam e alteram dados da própria empresa.
- A empresa cadastra e pesquisa produtos.
- Entradas, saídas, ajustes e vendas deixam histórico verificável.
- Uma venda válida baixa estoque de forma atômica; saldo insuficiente não gera venda parcial nem saldo negativo.
- É possível consultar vendas e itens com preço histórico.
- Testes automatizados cobrem regras críticas, persistência, autenticação e isolamento entre empresas.
- Outra pessoa consegue subir o projeto e repetir o fluxo usando a documentação.
