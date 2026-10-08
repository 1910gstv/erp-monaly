# Discovery do produto — Monaly

Documento de descoberta do produto para o ERP Monaly, voltado a pequenos comércios.

Este documento descreve **o que o produto deve fazer** e os requisitos identificados até agora. Ele complementa o backlog: requisitos descrevem capacidades e regras do produto; o backlog transforma essas capacidades em entregas e tarefas.

> **Status:** versão inicial, baseada no código e na documentação do repositório. Os requisitos são uma proposta para validação com comerciantes e usuários reais. Onde houver uma decisão ainda não confirmada, ela está marcada como **Pendente de decisão**.

---

## 1. Visão do produto

O Monaly deve ajudar um pequeno comércio a saber:

- Quais produtos possui e quanto cobrar por eles.
- Quanto há disponível em estoque e como esse saldo mudou.
- O que foi comprado, vendido, devolvido ou ajustado.
- Quanto vendeu em determinado período.
- Quais itens precisam de reposição.
- Quais pessoas podem acessar e operar os dados da empresa.

O sistema deve reduzir controles paralelos em papel e planilhas, sem exigir que o comerciante tenha equipe técnica ou conhecimento contábil.

### Objetivo principal

Permitir a operação diária de um comércio com cadastro de produtos, controle confiável de estoque, registro de vendas e consultas simples para tomada de decisão.

### Público inicial

- Proprietário ou administrador de pequeno comércio.
- Gerente responsável pela operação.
- Funcionário que cadastra produtos, registra movimentações ou realiza vendas.

### Princípios de produto

1. **Simples para a rotina:** tarefas frequentes devem exigir poucos passos e linguagem clara.
2. **Saldo explicável:** toda mudança de estoque deve ter origem e histórico.
3. **Dados isolados:** uma empresa jamais pode visualizar ou alterar dados de outra.
4. **Operação segura:** erros não devem gerar vendas ou saldos parcialmente gravados.
5. **Evolução gradual:** entregar primeiro um sistema útil; adicionar integrações e infraestrutura complexa apenas quando houver necessidade.

---

## 2. Escopo do produto

### Escopo inicial recomendado (MVP)

- Cadastro da empresa e de seus usuários.
- Login, papéis e permissões básicas.
- Catálogo de produtos.
- Saldo e histórico de estoque.
- Cadastro simples de clientes, sem torná-lo obrigatório para toda venda.
- Registro e consulta de vendas.
- Baixa automática do estoque ao concluir uma venda.
- Cancelamento de venda com reversão rastreável de estoque, segundo regras explícitas.
- Consultas básicas de vendas e produtos abaixo do estoque mínimo.
- API funcional; interface visual é uma decisão de escopo separada.

### Escopo de evolução

- Fornecedores, compras e recebimento de mercadorias.
- Registro detalhado de formas e recebimentos de pagamento.
- Devoluções parciais, trocas e reembolsos.
- Exportações e importações de dados.
- Auditoria ampliada, notificações e relatórios avançados.
- Interface web de operação, se não fizer parte do MVP.
- Integrações de pagamento, emissão fiscal, entrega e outros serviços externos.

### Fora do escopo inicial

- Contabilidade completa, folha de pagamento ou gestão fiscal abrangente.
- Emissão de nota fiscal sem definição dos requisitos legais e integração necessários.
- Marketplace, e-commerce ou gestão de múltiplos canais de venda.
- Estoque por lote, número de série, validade ou múltiplos depósitos.
- Aplicativo mobile nativo.
- Microsserviços, Kubernetes, cache distribuído e mensageria como requisitos do primeiro MVP.

Esses itens podem ser reconsiderados após validação de necessidade com usuários.

---

## 3. Atores e permissões

Os papéis abaixo são uma proposta inicial. Permissões detalhadas devem ser validadas antes de implementar a interface.

| Ator | Necessidade principal | Permissões propostas |
|---|---|---|
| Administrador da empresa | Configurar a conta e controlar acessos | Gerenciar empresa e usuários; acessar todos os módulos da empresa |
| Gerente | Acompanhar e operar o negócio | Gerenciar catálogo, estoque, clientes e vendas; consultar relatórios |
| Funcionário | Executar tarefas do dia a dia | Consultar produtos; registrar vendas e operações autorizadas |
| Administrador da plataforma | Dar suporte operacional ao SaaS | Acesso administrativo estritamente controlado e auditado; não faz parte do MVP operacional |

### Regras de acesso propostas

- Cada requisição autenticada atua em nome de um usuário e de uma empresa.
- No MVP, assume-se que cada usuário pertence a uma única empresa.
- A empresa da requisição deve ser derivada da identidade autenticada, não aceita livremente do cliente.
- Papéis e permissões são verificados no servidor.
- A remoção ou bloqueio de um usuário deve impedir novas autenticações sem apagar o histórico das operações já realizadas.

**Pendente de decisão:** permitir que um mesmo usuário trabalhe para mais de uma empresa? O modelo inicial do backlog assume que não.

---

## 4. Fluxos principais do usuário

### 4.1 Preparar a conta

1. Criar ou registrar uma empresa.
2. Criar o usuário administrador inicial.
3. Autenticar-se.
4. Configurar os dados básicos da empresa e convidar/cadastrar outros usuários, se habilitado.

### 4.2 Preparar o catálogo e o estoque

1. Cadastrar produto, preço e identificador interno (SKU opcional).
2. Informar o limite mínimo de estoque, se aplicável.
3. Registrar a quantidade inicial como entrada de estoque.
4. Consultar saldo e histórico.

### 4.3 Registrar uma venda

1. Localizar um ou mais produtos.
2. Informar quantidades e, opcionalmente, selecionar um cliente.
3. O sistema valida produtos ativos, preço e saldo disponível.
4. O sistema calcula os valores no servidor e apresenta o resumo.
5. Ao confirmar, registra venda, itens, valores históricos e saídas de estoque atomicamente.
6. O usuário consulta a venda e o saldo atualizado.

**Pendente de decisão:** o que significa “concluir venda” no MVP: registrar a venda e a forma de pagamento informada, ou controlar também pagamento pendente/parcial?

### 4.4 Corrigir operação ou cancelar venda

1. Usuário autorizado localiza a venda.
2. Informa motivo e solicita cancelamento ou devolução, conforme regra permitida.
3. O sistema valida o estado atual.
4. Registra a operação compensatória e o responsável, sem apagar a venda nem o histórico.

### 4.5 Repor mercadoria (evolução)

1. Identificar produtos abaixo do mínimo.
2. Registrar fornecedor e compra/reposição.
3. Receber total ou parcialmente os produtos.
4. Atualizar estoque por movimentações vinculadas ao recebimento.

---

## 5. Requisitos funcionais

Prioridades:

- **P0 — Essencial:** necessário para o MVP ou para proteger seus dados e operações.
- **P1 — Importante:** melhora a operação e deve ser planejado para a primeira evolução.
- **P2 — Futuro:** útil após validação de demanda ou necessidade comprovada.

### 5.1 Conta, empresa e usuários

#### RF-001 — Cadastrar empresa
- **Prioridade:** P0
- O sistema deve permitir registrar uma empresa com nome e identificador fiscal, quando aplicável.
- Deve validar campos obrigatórios e impedir duplicidade conforme a regra definida para o identificador.
- **Aceite:** uma empresa válida é persistida; dados inválidos são rejeitados; duplicidade retorna conflito claro.

#### RF-002 — Cadastrar o primeiro administrador
- **Prioridade:** P0
- O sistema deve permitir criar um usuário administrador ligado à empresa.
- O papel inicial deve ser determinado pelo fluxo do servidor, sem aceitar autoelevação enviada pelo cliente.
- **Aceite:** senha é armazenada apenas como hash seguro e nunca é incluída em respostas ou logs.

#### RF-003 — Autenticar usuário
- **Prioridade:** P0
- O sistema deve autenticar usuários com credenciais válidas e rejeitar credenciais inválidas.
- Deve permitir encerrar ou invalidar acesso conforme o mecanismo escolhido.
- **Aceite:** rotas privadas exigem identidade autenticada; falha de autenticação não revela se a conta ou a senha estava incorreta, conforme política definida.

#### RF-004 — Gerenciar usuários da empresa
- **Prioridade:** P0
- Administradores devem cadastrar, consultar, alterar papel e desativar usuários da própria empresa.
- **Aceite:** usuário desativado não inicia novas sessões; seu histórico de operações permanece disponível.

#### RF-005 — Aplicar autorização por papel
- **Prioridade:** P0
- O sistema deve autorizar ou negar operações de acordo com o papel do usuário.
- **Aceite:** operações não permitidas retornam acesso negado e têm testes cobrindo cada papel e recurso crítico.

#### RF-006 — Isolar dados por empresa
- **Prioridade:** P0
- Toda operação com dados de negócio deve ficar restrita à empresa associada ao usuário autenticado.
- **Aceite:** tentativa de consultar ou alterar recurso de outra empresa não revela dados nem produz alteração.

#### RF-007 — Gerenciar dados básicos da empresa
- **Prioridade:** P1
- Administrador autorizado deve consultar e atualizar dados básicos da empresa.
- **Aceite:** campos editáveis e identificadores imutáveis são definidos; mudanças ficam registradas quando forem relevantes.

### 5.2 Produtos e catálogo

#### RF-008 — Cadastrar produto
- **Prioridade:** P0
- O sistema deve cadastrar produto com nome, preço, estado ativo/inativo e identificador da empresa; descrição e SKU podem ser opcionais.
- **Aceite:** preço não pode ser negativo; nome tem limites definidos; SKU, se usado, segue unicidade por empresa.

#### RF-009 — Consultar, listar e pesquisar produtos
- **Prioridade:** P0
- O usuário deve pesquisar produtos por nome e, quando informado, SKU; deve poder consultar detalhes e listar resultados paginados.
- **Aceite:** todas as consultas são limitadas à empresa; filtros e ordenação são previsíveis.

#### RF-010 — Editar produto
- **Prioridade:** P0
- Usuário autorizado deve atualizar atributos permitidos do produto.
- **Aceite:** alteração de preço não modifica o preço histórico gravado em vendas anteriores.

#### RF-011 — Inativar e reativar produto
- **Prioridade:** P1
- Usuário autorizado deve poder inativar um produto sem apagar seu histórico.
- **Aceite:** produto inativo não pode ser incluído em novas vendas; vendas e movimentações antigas continuam consultáveis.

#### RF-012 — Configurar alerta de estoque mínimo
- **Prioridade:** P1
- Usuário deve poder definir uma quantidade mínima opcional por produto.
- **Aceite:** o sistema identifica produtos abaixo do limite; limite ausente não deve ser interpretado como estoque crítico.

#### RF-013 — Categorizar produtos
- **Prioridade:** P2
- O sistema poderá agrupar produtos por categoria para facilitar pesquisa e relatórios.
- **Aceite:** categoria pertence à empresa e não mistura dados de empresas distintas.

### 5.3 Estoque

#### RF-014 — Manter saldo de estoque
- **Prioridade:** P0
- O sistema deve manter o saldo atual por produto e empresa.
- **Aceite:** saldo inicial é zero ou decorre de uma entrada registrada; saldo negativo é proibido no MVP.

#### RF-015 — Registrar entrada de estoque
- **Prioridade:** P0
- Usuário autorizado deve registrar entrada com produto, quantidade, data e motivo/referência.
- **Aceite:** quantidade deve ser maior que zero; saldo e histórico são atualizados juntos.

#### RF-016 — Registrar saída manual
- **Prioridade:** P0
- Usuário autorizado deve registrar saída por motivo operacional, como perda ou uso interno.
- **Aceite:** saída exige quantidade positiva e motivo; não pode consumir mais do que o saldo disponível.

#### RF-017 — Ajustar estoque após contagem
- **Prioridade:** P1
- Usuário autorizado deve registrar contagem e ajuste para reconciliar o saldo físico com o saldo do sistema.
- **Aceite:** ajuste exige motivo, identifica o usuário e gera movimento auditável; não apaga operações anteriores.

#### RF-018 — Consultar saldo e movimentações
- **Prioridade:** P0
- O usuário deve consultar saldo atual e histórico de entradas, saídas, ajustes e operações relacionadas a vendas.
- **Aceite:** histórico é paginado/filtrável e permite explicar a composição do saldo.

#### RF-019 — Controlar concorrência no saldo
- **Prioridade:** P0
- O sistema deve impedir que operações simultâneas vendam ou retirem mais unidades do que estão disponíveis.
- **Aceite:** em concorrência, somente operações compatíveis com o saldo são confirmadas; rejeições não deixam alterações parciais.

#### RF-020 — Reservar estoque
- **Prioridade:** P2
- O sistema poderá reservar unidades temporariamente para pedidos não concluídos.
- **Aceite:** duração, expiração, liberação e relação com a venda são definidas antes da implementação.

### 5.4 Clientes

#### RF-021 — Cadastrar cliente
- **Prioridade:** P1
- O sistema deve permitir cadastro simples de cliente ligado à empresa, com nome e meios de contato opcionais.
- **Aceite:** dados opcionais são validados quando preenchidos; cadastro não é obrigatório para toda venda, salvo decisão posterior.

#### RF-022 — Consultar e pesquisar clientes
- **Prioridade:** P1
- O usuário deve listar, localizar e consultar clientes da empresa.
- **Aceite:** resultados são paginados e não expõem clientes de outras empresas.

#### RF-023 — Atualizar e inativar cliente
- **Prioridade:** P1
- O usuário autorizado deve corrigir dados e inativar cliente sem apagar as vendas associadas.
- **Aceite:** registros históricos preservam a identificação necessária da venda.

### 5.5 Vendas e pedidos

#### RF-024 — Registrar venda
- **Prioridade:** P0
- O usuário deve registrar uma venda com um ou mais produtos e quantidades; cliente pode ser opcional no MVP.
- **Aceite:** produtos precisam estar ativos; quantidade deve ser positiva; disponibilidade é verificada no momento da confirmação.

#### RF-025 — Calcular valores da venda
- **Prioridade:** P0
- O servidor deve calcular preço aplicado, subtotal e total usando valores decimais e regras definidas.
- **Aceite:** o servidor não confia em totais enviados pelo cliente; arredondamento e moeda são consistentes.

#### RF-026 — Preservar detalhes históricos da venda
- **Prioridade:** P0
- A venda deve guardar os itens, quantidades e preços aplicados no momento da confirmação.
- **Aceite:** edição posterior do produto não altera os valores exibidos em venda antiga.

#### RF-027 — Atualizar estoque ao confirmar venda
- **Prioridade:** P0
- Confirmar venda deve criar a venda, seus itens, baixas e movimentações correspondentes em uma operação atômica.
- **Aceite:** falha em qualquer parte não deixa venda parcial nem altera parcialmente o estoque.

#### RF-028 — Consultar vendas
- **Prioridade:** P0
- O usuário deve listar vendas e consultar seus detalhes.
- **Aceite:** listagem é paginada, filtrável por período e estado, e restrita à empresa.

#### RF-029 — Cancelar venda
- **Prioridade:** P1
- Usuário autorizado deve cancelar venda elegível com motivo e registrar a compensação de estoque aplicável.
- **Aceite:** repetição do cancelamento não duplica devoluções; venda original não é apagada.

#### RF-030 — Registrar desconto
- **Prioridade:** P1
- O sistema poderá permitir desconto por item ou venda, com limite e permissão definidos.
- **Aceite:** valor final, desconto, responsável e justificativa ficam registrados; não se aceita valor final negativo.

#### RF-031 — Registrar pagamentos da venda
- **Prioridade:** P1
- O sistema deve registrar método e valor recebido, inicialmente de forma manual, caso o fluxo de caixa faça parte do escopo.
- **Aceite:** métodos permitidos, pagamentos parciais, pendência e diferença de troco são definidos antes da implementação.

#### RF-032 — Devolver itens ou fazer troca
- **Prioridade:** P2
- O sistema poderá registrar devoluções totais/parciais e trocas ligadas à venda original.
- **Aceite:** quantidades devolvidas não excedem as vendidas; estoque e eventual reembolso são rastreáveis.

### 5.6 Fornecedores e compras

#### RF-033 — Cadastrar fornecedores
- **Prioridade:** P1
- O sistema deve permitir manter dados básicos de fornecedores da empresa.
- **Aceite:** nome é obrigatório; contatos e identificador fiscal são validados quando preenchidos.

#### RF-034 — Registrar compra/reposição
- **Prioridade:** P1
- O usuário deve registrar itens comprados, quantidades, custos e fornecedor, se conhecido.
- **Aceite:** criar compra não aumenta estoque automaticamente; somente o recebimento confirmado o faz.

#### RF-035 — Receber mercadorias
- **Prioridade:** P1
- O usuário deve confirmar recebimento total ou parcial de uma compra.
- **Aceite:** recebimento cria entradas vinculadas à compra; unidades não podem ser recebidas acima do saldo ainda não recebido.

#### RF-036 — Consultar compras e recebimentos
- **Prioridade:** P1
- O usuário deve consultar compras por fornecedor, período e situação.
- **Aceite:** recebimentos e saldos pendentes são visíveis e limitados à empresa.

### 5.7 Relatórios e exportação

#### RF-037 — Consultar resumo de vendas
- **Prioridade:** P1
- O sistema deve mostrar total de vendas em um intervalo e permitir detalhamento por data.
- **Aceite:** cancelamentos e devoluções são tratados conforme regra publicada; período é obrigatório ou tem padrão explícito.

#### RF-038 — Consultar posição de estoque
- **Prioridade:** P0
- O usuário deve visualizar produtos, saldos e itens abaixo do mínimo.
- **Aceite:** resultado pode ser filtrado; cálculo usa saldo confirmado e escopo da empresa.

#### RF-039 — Consultar produtos mais vendidos
- **Prioridade:** P2
- O sistema poderá ordenar produtos por unidades ou valor vendido em um período.
- **Aceite:** vendas canceladas e devoluções são tratadas de forma consistente.

#### RF-040 — Exportar dados
- **Prioridade:** P2
- O sistema poderá exportar produtos, saldos, clientes, compras e vendas para formatos acordados, como CSV.
- **Aceite:** exportação respeita permissões, filtros e limites; não inclui credenciais ou dados de outras empresas.

### 5.8 Configuração, auditoria e integrações

#### RF-041 — Manter histórico de operações críticas
- **Prioridade:** P0
- O sistema deve identificar usuário, horário e referência de operações de estoque, vendas, cancelamentos e alterações de permissão.
- **Aceite:** eventos de auditoria não podem ser alterados pela operação normal do usuário.

#### RF-042 — Consultar histórico de atividade
- **Prioridade:** P1
- Usuários autorizados devem consultar atividades relevantes da empresa.
- **Aceite:** consulta é filtrável e nunca revela dados de outro tenant ou segredos.

#### RF-043 — Importar dados iniciais
- **Prioridade:** P2
- O sistema poderá importar produtos e saldos por arquivo para facilitar adoção.
- **Aceite:** validar arquivo antes de aplicar; reportar linhas com erro; não criar dados parcialmente sem informar o resultado.

#### RF-044 — Notificar estoque baixo
- **Prioridade:** P2
- O sistema poderá avisar usuários quando o saldo cruzar o limite mínimo.
- **Aceite:** canal, destinatários, frequência e prevenção de notificações repetidas são configuráveis ou documentados.

#### RF-045 — Integrar pagamento ou entrega
- **Prioridade:** P2
- O sistema poderá integrar provedores externos após escolha de parceiro e definição de fluxos de falha, confirmação e estorno.
- **Aceite:** chamadas externas não duplicam vendas ou pagamentos em repetição; segredos ficam fora do código.

#### RF-046 — Registrar entrega
- **Prioridade:** P2
- Se venda com entrega fizer parte do público-alvo, o sistema poderá guardar endereço, instruções e estado da entrega.
- **Aceite:** ciclo de estados, acesso a dados pessoais e integração com transportadora são definidos antes do desenvolvimento.

---

## 6. Requisitos não funcionais

Os números abaixo são **metas iniciais propostas**, não compromissos já validados. Devem ser revistos com dados de uso, custo e criticidade do negócio.

### RNF-001 — Segurança de autenticação

- Senhas devem ser armazenadas com algoritmo de hash apropriado e salt gerenciado pela biblioteca.
- Segredos de assinatura e credenciais não devem ficar versionados no repositório.
- A aplicação deve limitar tentativas abusivas de autenticação antes de abrir acesso público.
- Tokens/sessões devem ter prazo e mecanismo de invalidação definidos.
- Senhas, tokens, hashes e credenciais não podem aparecer em logs ou respostas.

### RNF-002 — Isolamento e autorização

- Toda leitura ou escrita de dado de negócio deve verificar empresa e permissão no servidor.
- Não confiar em `companyId`, papel ou preço enviado pelo cliente como prova de autorização.
- Testes devem cobrir acesso cruzado entre empresas em endpoints e casos de uso.
- Acesso administrativo da plataforma, se existir, deve ser separado, limitado e auditável.

### RNF-003 — Proteção de dados pessoais

- Coletar apenas dados pessoais necessários à operação.
- Restringir acesso e exposição de contato/documento a usuários autorizados.
- Definir política de retenção, exportação e exclusão/anonimização conforme obrigações aplicáveis e decisões jurídicas.
- **Pendente:** validar obrigações de LGPD, prazos e processo de atendimento de solicitações antes de produção.

### RNF-004 — Integridade transacional

- Criar venda, itens e baixa de estoque deve ser atômico.
- Entradas, saídas, ajustes e recebimentos devem persistir saldo e histórico de forma consistente.
- Operações concorrentes não podem produzir saldo negativo.
- Repetir uma ação crítica não deve duplicar efeito quando for tecnicamente possível identificar repetição.

### RNF-005 — Consistência monetária

- Valores monetários devem usar representação decimal de precisão definida, moeda explícita e regra de arredondamento consistente.
- O cálculo final é feito no servidor.
- Valores históricos de uma operação confirmada não devem mudar quando o cadastro do produto for editado.

### RNF-006 — Disponibilidade e recuperação

- Meta inicial proposta: disponibilidade mensal de **99,5%**, excluindo manutenção previamente comunicada.
- Deve existir backup automatizado do banco e procedimento documentado de restauração.
- Meta inicial proposta: perda máxima de dados (`RPO`) de até **24 horas** e recuperação (`RTO`) de até **8 horas**; validar se é aceitável para a operação.
- Recuperações devem ser testadas periodicamente, não apenas presumidas a partir da existência do backup.

### RNF-007 — Desempenho

- Meta inicial proposta sob carga normal: operações de consulta comuns em até **2 segundos no percentil 95**.
- Registro de venda deve responder em até **3 segundos no percentil 95**, excluindo dependências externas futuras.
- Listagens devem ser paginadas; filtros não devem exigir carregar todo o catálogo ou histórico.
- Rever metas após definir volume de empresas, usuários, produtos e transações.

### RNF-008 — Escalabilidade

- O sistema deve atender múltiplas empresas sem compartilhamento acidental de dados.
- Modelo e consultas devem permitir crescimento gradual sem exigir microserviços no início.
- Capacidade inicial proposta para validação: centenas de empresas pequenas, dezenas de usuários concorrentes por empresa e dezenas de milhares de produtos por empresa.
- O dimensionamento real depende de testes de carga e infraestrutura escolhida.

### RNF-009 — Usabilidade

- A interface, quando definida, deve usar termos conhecidos do comércio e mensagens de erro acionáveis.
- Fluxos frequentes (pesquisar produto, registrar venda, consultar saldo) devem ser curtos e consistentes.
- Operações irreversíveis ou de impacto financeiro devem pedir confirmação adequada e explicar a consequência.
- Datas, moeda, números e fuso horário devem ser apresentados de forma localizável.

### RNF-010 — Acessibilidade

- Interface web deverá buscar conformidade WCAG 2.1 nível AA nas telas principais.
- Ações devem ser utilizáveis por teclado, ter foco visível e rótulos acessíveis.
- Cor não deve ser o único meio para comunicar estado ou erro.
- Validar com ferramentas automáticas e revisão manual; automação isolada não comprova conformidade.

### RNF-011 — Compatibilidade e experiência de API

- API deve usar contratos versionados ou estratégia explícita de compatibilidade antes de mudanças incompatíveis.
- Datas e erros devem seguir formatos consistentes e documentados.
- Requisições inválidas devem receber resposta compreensível, sem detalhes internos.
- Interface web, se construída, deve funcionar nas versões atuais dos principais navegadores desktop e mobile definidos pelo produto.

### RNF-012 — Auditabilidade

- Operações de estoque, vendas, cancelamentos, permissões e configurações relevantes devem permitir identificar ator, data, tipo e referência.
- Logs técnicos devem permitir investigar falhas sem guardar segredos ou dados pessoais desnecessários.
- Logs e trilhas de negócio têm propósitos distintos e políticas de retenção distintas.

### RNF-013 — Manutenibilidade

- Código deve manter responsabilidades separadas entre regras de negócio, API e persistência.
- Regras importantes devem ser testáveis sem infraestrutura externa.
- Mudanças de schema devem ser versionadas por migrations; migrations aplicadas não devem ser reescritas.
- Bibliotecas e versões devem ser atualizadas com política conhecida e testes de regressão.

### RNF-014 — Testabilidade e qualidade

- Regras críticas de saldo, venda, autorização e isolamento devem ter testes automatizados.
- O pipeline deve executar build e testes antes de aceitar uma entrega.
- Testes de integração devem usar dependências isoladas (por exemplo, bancos efêmeros), não dados de produção.
- Critérios de cobertura numérica só devem ser definidos após existir baseline; cobertura não substitui qualidade de cenário.

### RNF-015 — Observabilidade e operação

- A aplicação deve expor health checks que diferenciem estado da aplicação e dependências essenciais.
- Logs devem incluir identificador de requisição/correlação para diagnóstico.
- Métricas mínimas devem acompanhar erros, latência, disponibilidade e falhas de banco.
- Alertas devem ser adicionados quando houver ambiente de produção e responsáveis definidos.

### RNF-016 — Portabilidade de implantação

- Ambientes local, teste e produção devem ser configuráveis sem alterar código.
- Segredos de produção devem vir de mecanismo de gestão de segredos apropriado ao ambiente.
- A aplicação deve ter procedimento documentado de build, execução, migration, backup e atualização.
- Docker deve facilitar desenvolvimento/implantação, mas não deve ser tratado como requisito para o usuário final.

### RNF-017 — Localização e moeda

- O idioma inicial proposto é português do Brasil.
- O sistema deve armazenar valores com moeda definida e apresentar números, moeda e datas de forma consistente.
- **Pendente:** confirmar se haverá suporte inicial a múltiplas moedas, idiomas, fusos horários ou formatos fiscais.

---

## 7. Entidades e conceitos de negócio identificados

Conceitos iniciais a validar e modelar:

- **Empresa:** limite de isolamento dos dados.
- **Usuário:** identidade, estado de acesso e vínculo com empresa.
- **Papel/permissão:** capacidades autorizadas ao usuário.
- **Produto:** item comercializável e sua configuração.
- **Saldo de estoque:** quantidade disponível do produto.
- **Movimentação:** evento imutável que explica mudança no saldo.
- **Cliente:** pessoa ou organização atendida pela empresa.
- **Venda:** operação comercial, estado e totais.
- **Item da venda:** produto, quantidade e valores históricos aplicados.
- **Fornecedor:** origem de mercadorias compradas.
- **Compra/recebimento:** solicitação e confirmação de mercadorias, em evolução.
- **Pagamento:** valor, forma e estado de recebimento, caso incluído no escopo.
- **Auditoria:** registro de operações relevantes e de seus responsáveis.

### Regras transversais de modelagem

- Entidades de negócio devem estar associadas à empresa, direta ou indiretamente.
- Não apagar operações financeiras ou de estoque para “corrigir” o passado; criar cancelamento, ajuste ou movimento compensatório.
- Separar preço atual do produto de preço registrado em uma venda.
- Usar estados de operação explícitos, com transições válidas.
- Definir política de exclusão/inativação para cada tipo de cadastro.

---

## 8. Regras de negócio iniciais

1. Uma empresa não pode consultar nem operar os dados de outra.
2. Apenas usuários autenticados e autorizados podem executar operações privadas.
3. Um produto inativo não participa de nova venda.
4. Quantidade de entrada, saída ou venda deve ser maior que zero.
5. Venda não pode ser confirmada se o estoque disponível for insuficiente.
6. A confirmação da venda e a baixa do estoque devem ocorrer juntas.
7. O servidor calcula subtotal e total usando o preço aplicado e as regras vigentes.
8. Venda confirmada conserva valores e itens históricos.
9. Ajustes, saídas, cancelamentos e devoluções precisam de responsável e registro apropriado.
10. Não se apaga histórico de estoque ou venda para corrigir erro; registra-se uma compensação rastreável.
11. Operação repetida de cancelamento não deve duplicar a devolução.
12. Identificadores como SKU devem ter escopo de unicidade definido (proposta: por empresa).

Regras sobre descontos, impostos, pagamento parcial, troco, devolução parcial, reserva e venda offline ainda precisam de decisão.

---

## 9. Indicadores de sucesso do produto

Métricas propostas para validar se o ERP resolve o problema:

- Tempo para cadastrar os primeiros produtos e dar entrada no estoque.
- Tempo para concluir uma venda simples.
- Percentual de vendas registradas sem correção posterior.
- Diferença entre saldo do sistema e contagem física.
- Quantidade de operações rejeitadas por falta de estoque.
- Uso semanal por empresa e por usuário.
- Número de empresas que concluem o fluxo de configuração e registram a primeira venda.
- Incidentes de acesso cruzado entre empresas: meta absoluta de zero.

Não definir metas comerciais numéricas até obter baseline com usuários piloto.

---

## 10. Decisões pendentes de discovery

Validar estas perguntas com comerciantes e/ou responsáveis pelo produto antes de fechar o escopo:

1. O primeiro MVP precisa de interface web ou uma API é suficiente para a fase atual?
2. A venda deve aceitar cliente não cadastrado? O cliente pode ser totalmente opcional?
3. Como a venda é considerada paga: confirmação simples, pagamentos múltiplos, pagamento parcial ou pendente?
4. O MVP precisa controlar caixa/abertura, fechamento e sangria?
5. A venda pode ter desconto? Quem pode concedê-lo e qual é o limite?
6. O sistema precisa calcular impostos ou emitir documento fiscal desde a primeira versão?
7. CNPJ é obrigatório para cadastrar empresa? Há comerciantes que operam como pessoa física?
8. Um usuário pode pertencer a mais de uma empresa ou filial?
9. O comércio trabalha com unidades fracionadas (peso/metro) ou apenas quantidades inteiras?
10. São necessários código de barras, variações (tamanho/cor), kits ou produtos compostos?
11. Deve haver mais de um local/depósito de estoque?
12. Lotes, validade, número de série ou rastreabilidade regulatória são necessários?
13. Devoluções devem voltar automaticamente ao estoque ou depender de inspeção?
14. A operação precisa funcionar sem internet? Em quais dispositivos e por quanto tempo?
15. Qual volume esperado de produtos, usuários, vendas e histórico por empresa?
16. Quais relatórios são indispensáveis para a rotina diária?
17. Qual é a política de retenção, exportação e exclusão dos dados da empresa?
18. Quais formas de suporte, notificações e canais de contato serão necessários?

---

## 11. Critério de aprovação deste discovery

Este documento pode ser considerado aprovado quando:

- Pelo menos alguns comerciantes do público-alvo validarem os fluxos principais.
- Requisitos P0 tiverem prioridade e limites claros.
- As perguntas pendentes que impactam o MVP tiverem resposta ou decisão explícita de adiamento.
- O time concordar com os critérios de integridade de estoque, venda e isolamento por empresa.
- O backlog for atualizado para refletir decisões e remover itens que não agreguem valor ao público inicial.

