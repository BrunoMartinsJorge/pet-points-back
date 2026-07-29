# Pet Points — Back-end (API)

API REST do **Pet Points**, um sistema de gestão para clínicas veterinárias desenvolvido como Trabalho de Conclusão de Curso (TCC). O back-end é construído com **Spring Boot** e expõe os serviços consumidos pelo front-end Angular: autenticação, cadastro de pets, agendamento e acompanhamento de consultas, pagamentos, controle de estoque, atendimento ao cliente, geração de relatórios em PDF e comunicação em tempo real via WebSocket.

## Sobre o projeto

A aplicação centraliza as operações de uma clínica veterinária e organiza o acesso por perfil de usuário. Cada tipo de usuário (cliente, atendente, veterinário, estoquista e gerente) possui seus próprios endpoints, protegidos por autorização baseada em papéis. A autenticação é feita por **JWT** (stateless), e recursos como chat e notificações trafegam por **WebSocket (STOMP/SockJS)**.

## Tecnologias

- **Java** + **Spring Boot**
- **Spring Web** (REST)
- **Spring Security** — autenticação stateless com JWT e autorização por papéis (`@EnableMethodSecurity`)
- **Spring Data JPA** / **Hibernate** — persistência
- **auth0 java-jwt** (`com.auth0.jwt`) — emissão e validação de tokens
- **Spring WebSocket** + **STOMP** — mensageria em tempo real
- **Spring Mail** (SMTP Gmail) — envio de e-mails (ex.: código de recuperação de senha)
- **Thymeleaf** — templates de e-mail e de relatórios
- **openhtmltopdf (PDFBox)** — geração de PDFs (relatórios e carteirinha do pet) a partir de HTML
- **Mercado Pago** (via `RestClient`) — cobranças PIX
- **Lombok** — redução de boilerplate
- **H2** (banco em memória, ambiente de desenvolvimento) / **PostgreSQL** (produção)

## Perfis de usuário

Os papéis são definidos no enum `TipoUsuario` e mapeados para autoridades do Spring Security (`RULE_REST_*`):

| Código | Papel | Descrição |
| --- | --- | --- |
| `C` | **CLIENTE** | Tutor dos pets — gerencia seus pets, consultas e pagamentos |
| `A` | **ATENDENTE** | Atende clientes, gerencia consultas e atendimentos |
| `V` | **VETERINARIO** | Realiza e acompanha consultas |
| `E` | **ESTOQUISTA** | Gerencia estoque e movimentações |
| `G` | **GERENTE** | Visão administrativa: funcionários, financeiro, relatórios, estoque, clientes, pets e logs |

## Estrutura do projeto

O código segue uma organização por domínio e por *feature*, mantendo controller, DTOs, forms e services próximos entre si:

```
src/main/java/br/com/api/petpoints/
├── PetPointsApplication.java      # Classe principal (Spring Boot)
├── core/                          # Infraestrutura da aplicação
│   ├── api/                       # Propriedades de integrações externas
│   ├── initializer/               # Carga inicial de usuários padrão
│   ├── security/                  # Config de segurança, filtro JWT, CORS, WebSocket auth
│   ├── token/                     # Serviço de token, TipoUsuario, modelos
│   └── web/                       # Configuração do WebSocket (STOMP)
├── domain/
│   ├── auth/                      # Login, registro e recuperação de senha
│   └── users/                     # Endpoints agrupados por perfil
│       ├── cliente/features/      # dashboard, meus-pets, minhas-consultas,
│       │                          #   meus-pagamentos, meus-atendimentos, meu-perfil
│       ├── atendente/features/    # consultas
│       ├── veterinario/features/  # dashboard, minhas-consultas
│       ├── estoquista/features/   # dashboard, estoque, movimentacoes
│       └── gerente/features/      # dashboard, financeiro, funcionarios, consultas,
│                                  #   estoque, movimentacoes, pets, logs,
│                                  #   desempenho de veterinarios
└── shared/                        # Recursos reutilizáveis entre perfis
    ├── features/
    │   ├── arquivos/              # Upload/entrega de arquivos e imagens
    │   ├── chatatendimento/       # Chat cliente ↔ atendente
    │   ├── chatinterno/           # Chat entre funcionários
    │   ├── notificacoes/          # Notificações em tempo real
    │   ├── payment/               # Pagamentos (Mercado Pago / PIX)
    │   ├── clientes/, pets/,      # Consultas compartilhadas de clientes e pets
    │   └── perfil/                # Perfil do usuário
    ├── annotations/, enums/,      # Utilitários transversais
    └── exception/, models/, repository/, utils/

src/main/resources/
├── application.properties
└── templates/
    ├── email/                     # E-mail de alteração de senha
    ├── relatorios/                # Relatórios em HTML (financeiro, produtos, logs,
    │                              #   desempenho de veterinários, genérico)
    └── carteirinha.html           # Carteirinha do pet (gerada em PDF)
```

## Segurança e autenticação

- Sessões **stateless**; toda requisição autenticada envia o token no cabeçalho `Authorization: Bearer <token>`.
- O `SecurityFilter` valida o JWT a cada requisição e popula o contexto de segurança.
- Senhas são armazenadas com **BCrypt**.
- Autorização por papel via `requestMatchers`, por exemplo:
  - `/autenticacao/**`, `/arquivos/**` e os endpoints WebSocket (`/ws/**`) são públicos;
  - `/cliente/**` exige `RULE_REST_CLIENTE`;
  - `/gerente/**` e `/relatorios/**` exigem `RULE_REST_GERENTE`;
  - `/gerente-atendente/**` aceita atendente ou gerente; e assim por diante.

### Endpoints de autenticação (`/autenticacao`)

| Método | Rota | Descrição |
| --- | --- | --- |
| `POST` | `/login` | Autentica e retorna o JWT |
| `POST` | `/register` | Cadastro de novo usuário (aceita foto) |
| `GET` | `/imagem/{id}` | Imagem de perfil do usuário |
| `GET` | `/enviar-codigo-alterar-senha` | Envia código de recuperação por e-mail |
| `PUT` | `/validar-codigo-alterar-senha` | Valida o código recebido |
| `PUT` | `/redefinir-senha` | Redefine a senha |

## Recursos em tempo real (WebSocket / STOMP)

- Broker simples com destinos `/topic` e `/queue`; prefixo de aplicação `/app`.
- Endpoints STOMP (com SockJS): `/ws/notificacoes`, `/ws/chat-interno`, `/ws/chat-atendimento`.
- Um interceptor (`WebSocketAuthInterceptor`) autentica as conexões no canal de entrada.

## Pagamentos

Integração com o **Mercado Pago** para cobranças via **PIX**, feita por chamadas HTTP (`RestClient`) à API do Mercado Pago. O pagamento é criado, persistido localmente e os dados do PIX são retornados; há também rota de **webhook** pública (`/pagamentos/webhook/**`) para atualização de status.

## Relatórios e documentos em PDF

Relatórios e a carteirinha do pet são renderizados a partir de templates **Thymeleaf** (`resources/templates/`) e convertidos em PDF com **openhtmltopdf**. Estão disponíveis relatórios financeiro, de produtos, de logs, de desempenho de veterinários e um modelo genérico.

## Pré-requisitos

- **JDK** compatível com a versão do Spring Boot do projeto
- **Maven** ou **Gradle** (conforme o gerenciador de build utilizado)
- Uma instância de **PostgreSQL** para produção (o ambiente de desenvolvimento usa **H2** em memória, sem configuração adicional)

> Observação: o arquivo de build (`pom.xml`/`build.gradle`) não acompanha este pacote — apenas o diretório `src/`. Utilize o arquivo de build do repositório original para compilar e executar.

## Configuração

As configurações ficam em `src/main/resources/application.properties`. Os principais parâmetros são parametrizáveis por variáveis de ambiente:

| Variável | Descrição | Padrão (dev) |
| --- | --- | --- |
| `JWT_SECRET` | Segredo de assinatura do JWT | *(valor de desenvolvimento)* |
| `api.security.token.expiration` | Expiração do token (ms) | `86400000` (24h) |
| `CORS_ALLOWED_ORIGINS` | Origens permitidas (CORS) | `http://localhost:4200` |
| `DATABASE_URL` / `DATABASE_USER` / `DATABASE_PASSWORD` | Conexão PostgreSQL (produção) | — |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | Credenciais SMTP para envio de e-mail | — |
| `PORT` | Porta do servidor | `8080` |

Em desenvolvimento, a aplicação sobe com um banco **H2 em memória**, e o console fica acessível em `/h2-console`.

## Como executar

Na raiz do projeto original (onde está o arquivo de build):

```bash
# Maven
./mvnw spring-boot:run

# ou Gradle
./gradlew bootRun
```

A API sobe por padrão em `http://localhost:8080`.

Para gerar o artefato empacotado:

```bash
# Maven
./mvnw clean package

# ou Gradle
./gradlew build
```

## Front-end

Este repositório contém apenas a API. O front-end (Angular) é um projeto separado que consome estes endpoints e, por padrão em desenvolvimento, aponta para `http://localhost:8080`.

---

Projeto desenvolvido como Trabalho de Conclusão de Curso (TCC).
