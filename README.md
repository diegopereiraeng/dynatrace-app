# Aplicação Spring Boot para Demonstração de Observabilidade com Dynatrace

Esta é uma aplicação Spring Boot simples desenvolvida para demonstrar a integração e monitoramento com o Dynatrace.
A aplicação expõe alguns endpoints REST que simulam diferentes cenários de resposta, incluindo sucessos, falhas, latência e comportamento instável.

## Pré-requisitos

- Java 17 ou superior
- Maven 3.6.x ou superior
- Conta Dynatrace e um ambiente com o Dynatrace OneAgent instalado (para a máquina/host onde esta aplicação será executada).

## Endpoints da API

A aplicação expõe os seguintes endpoints sob o caminho base `/api`:

- **`GET /api/payments`**: Simula o processamento de um pagamento.
  - Comportamento: Geralmente bem-sucedido com baixa latência.
  - Resposta Esperada (Sucesso): `HTTP 200 OK` - "Payment processed successfully."

- **`GET /api/cashout`**: Simula uma solicitação de saque.
  - Comportamento: Flaky (instável). Tem aproximadamente 20% de chance de retornar um erro `HTTP 503 Service Unavailable`.
  - Resposta Esperada (Sucesso): `HTTP 200 OK` - "Cashout request successful."
  - Resposta Esperada (Falha): `HTTP 503 Service Unavailable` - "Cashout service temporarily unavailable."

- **`GET /api/credit-analysis`**: Simula uma análise de crédito.
  - Comportamento: Alta latência. O tempo de resposta é intencionalmente configurado para ser entre 1.5 e 3 segundos.
  - Resposta Esperada (Sucesso): `HTTP 200 OK` - "Credit analysis completed."

- **`GET /api/loan-request`**: Simula o envio de uma solicitação de empréstimo.
  - Comportamento: Sempre resulta em um erro interno no servidor.
  - Resposta Esperada (Falha): `HTTP 500 Internal Server Error` - "Failed to process loan request due to an internal error."

## Compilando e Executando a Aplicação

1.  **Clone o repositório (se aplicável) ou certifique-se de ter os arquivos do projeto.**
2.  **Navegue até o diretório raiz do projeto** (onde o arquivo `pom.xml` está localizado).
3.  **Compile o projeto usando Maven:**
    ```bash
    mvn clean package
    ```
4.  **Execute a aplicação:**
    ```bash
    java -jar target/dynatrace-app-0.0.1-SNAPSHOT.jar
    ```
    A aplicação será iniciada por padrão na porta `8080`.

### Configurando Comportamento via Variáveis de Ambiente

A aplicação agora suporta a configuração de latência e taxas de erro para seus endpoints através de variáveis de ambiente (ou propriedades do Spring Boot). Isso é útil para simular diferentes cenários de performance e confiabilidade.

**Variáveis de Ambiente Disponíveis:**

| Variável de Ambiente                | Propriedade Spring Boot             | Default | Descrição                                                                 | Endpoint Afetado     |
| ----------------------------------- | ----------------------------------- | ------- | ------------------------------------------------------------------------- | -------------------- |
| `PAYMENTS_LATENCY_MIN_MS`           | `payments.latency.min.ms`           | 50      | Latência mínima em milissegundos para o processamento de pagamentos.      | `/api/payments`      |
| `PAYMENTS_LATENCY_MAX_MS`           | `payments.latency.max.ms`           | 200     | Latência máxima em milissegundos para o processamento de pagamentos.      | `/api/payments`      |
| `CASHOUT_FAILURE_PERCENTAGE`        | `cashout.failure.percentage`        | 20      | Porcentagem (0-100) de chance de falha para solicitações de saque.       | `/api/cashout`       |
| `CASHOUT_LATENCY_MIN_MS`            | `cashout.latency.min.ms`            | 100     | Latência mínima em milissegundos para solicitações de saque.              | `/api/cashout`       |
| `CASHOUT_LATENCY_MAX_MS`            | `cashout.latency.max.ms`            | 300     | Latência máxima em milissegundos para solicitações de saque.              | `/api/cashout`       |
| `CASHOUT_ERROR_5XX_CODE`            | `cashout.error.5xx.code`            | 503     | Código de erro HTTP 5xx a ser retornado em caso de falha 5xx.             | `/api/cashout`       |
| `CASHOUT_ERROR_4XX_CODE`            | `cashout.error.4xx.code`            | 400     | Código de erro HTTP 4xx a ser retornado em caso de falha 4xx.             | `/api/cashout`       |
| `CASHOUT_PERCENTAGE_OF_FAILURES_AS_5XX` | `cashout.percentage.of.failures.as.5xx` | 70 | Do total de falhas, % que serão erros 5xx (o restante será 4xx).        | `/api/cashout`       |
| `CREDIT_ANALYSIS_LATENCY_MIN_MS`    | `credit.analysis.latency.min.ms`    | 1500    | Latência mínima em milissegundos para análise de crédito.                 | `/api/credit-analysis` |
| `CREDIT_ANALYSIS_LATENCY_MAX_MS`    | `credit.analysis.latency.max.ms`    | 3000    | Latência máxima em milissegundos para análise de crédito.                 | `/api/credit-analysis` |
| `LOAN_REQUEST_FAILURE_PERCENTAGE`   | `loan.request.failure.percentage`   | 100     | Porcentagem (0-100) de chance de falha para solicitações de empréstimo.  | `/api/loan-request`  |
| `LOAN_REQUEST_LATENCY_MIN_MS`       | `loan.request.latency.min.ms`       | 50      | Latência mínima em milissegundos para solicitações de empréstimo.         | `/api/loan-request`  |
| `LOAN_REQUEST_LATENCY_MAX_MS`       | `loan.request.latency.max.ms`       | 150     | Latência máxima em milissegundos para solicitações de empréstimo.         | `/api/loan-request`  |
| `LOAN_REQUEST_ERROR_5XX_CODE`       | `loan.request.error.5xx.code`       | 500     | Código de erro HTTP 5xx a ser retornado/simulado.                       | `/api/loan-request`  |
| `LOAN_REQUEST_ERROR_4XX_CODE`       | `loan.request.error.4xx.code`       | 400     | Código de erro HTTP 4xx a ser retornado.                                | `/api/loan-request`  |
| `LOAN_REQUEST_PERCENTAGE_OF_FAILURES_AS_5XX` | `loan.request.percentage.of.failures.as.5xx` | 100 | Do total de falhas, % que serão erros 5xx (o restante será 4xx).    | `/api/loan-request`  |

**Exemplo de Execução com Variáveis de Ambiente:**

Para simular um cenário onde a análise de crédito é muito lenta e os saques falham com mais frequência:

```bash
export CREDIT_ANALYSIS_LATENCY_MIN_MS=5000
export CREDIT_ANALYSIS_LATENCY_MAX_MS=10000
export CASHOUT_FAILURE_PERCENTAGE=75
java -jar target/dynatrace-app-0.0.1-SNAPSHOT.jar
```

Ou passando como propriedades do sistema Java:

```bash
java -Dcredit.analysis.latency.min.ms=5000 \
     -Dcredit.analysis.latency.max.ms=10000 \
     -Dcashout.failure.percentage=75 \
     -jar target/dynatrace-app-0.0.1-SNAPSHOT.jar
```

Isto permite criar diferentes "perfis" de execução da aplicação para testar como o Dynatrace detecta e reporta essas mudanças de comportamento.

## Monitorando com Dynatrace

Para monitorar esta aplicação com o Dynatrace, siga os passos abaixo:

1.  **Certifique-se de que o Dynatrace OneAgent está instalado e ativo** no host onde você executará esta aplicação Java. O OneAgent detectará automaticamente o processo Java e começará a coletar métricas.
    - Se o OneAgent não estiver instalado, siga as instruções de instalação fornecidas na sua interface do Dynatrace.

2.  **Execute a aplicação Java** conforme as instruções na seção "Compilando e Executando a Aplicação".

3.  **Acesse sua interface do Dynatrace.**

4.  **Identificando o Serviço:**
    - Após alguns minutos da aplicação em execução e recebendo algumas requisições, o Dynatrace deverá detectar automaticamente o serviço.
    - Navegue até a seção "Services" no Dynatrace.
    - Procure por um serviço que corresponda à sua aplicação. O nome do serviço geralmente é derivado do nome da aplicação ou do processo Java (ex: `dynatrace-app` ou similar, dependendo da sua configuração do Dynatrace).
    - Os endpoints (`/api/payments`, `/api/cashout`, etc.) serão listados como "Request attributes" ou "Endpoints" dentro da visão detalhada do serviço.

5.  **Analisando o Processo:**
    - Você também pode encontrar o processo Java correspondente na seção "Hosts" -> [Seu Host] -> "Processes".
    - O Dynatrace fornecerá métricas de CPU, memória, threads, e outras métricas específicas do processo.

## Principais Métricas para Monitorar Durante um Deployment

Durante um deployment (ou em operação normal), é crucial monitorar as seguintes métricas no Dynatrace para garantir a saúde e disponibilidade da sua aplicação:

### 1. Degradação de Disponibilidade de Serviço

Estas métricas ajudam a entender se o seu serviço está respondendo corretamente e com performance adequada.

-   **Taxa de Falha (Failure Rate):**
    -   **O que é:** A porcentagem de requisições que resultaram em erro (ex: HTTP 5xx, exceções não tratadas).
    -   **Por que monitorar:** Um aumento súbito na taxa de falha é um indicador claro de problemas introduzidos por um novo deployment ou problemas no ambiente.
    -   **No Dynatrace:** Encontrado na página de visão geral do serviço.

-   **Tempo de Resposta (Response Time):**
    -   **O que é:** O tempo total que o serviço leva para processar uma requisição. Geralmente medido em percentis (P50, P90, P95, P99).
    -   **Por que monitorar:** Aumentos no tempo de resposta indicam degradação da performance, o que pode levar a uma má experiência do usuário e timeouts. Monitore especialmente os percentis mais altos para capturar outliers.
    -   **No Dynatrace:** Encontrado na página de visão geral do serviço. Analise os diferentes percentis.

-   **Taxa de Transferência (Throughput / Request Count):**
    -   **O que é:** O número de requisições que o serviço está processando por unidade de tempo (ex: requisições por minuto).
    -   **Por que monitorar:** Quedas inesperadas no throughput podem indicar que os usuários não conseguem acessar o serviço ou que há um gargalo impedindo o processamento das requisições. Durante um deployment, você espera que o throughput se mantenha estável ou aumente conforme o tráfego é direcionado para as novas instâncias.
    -   **No Dynatrace:** Encontrado na página de visão geral do serviço.

### 2. Erros

Métricas de erro fornecem insights diretos sobre problemas que afetam a funcionalidade da aplicação.

-   **Contagem de Erros (Error Count):**
    -   **O que é:** O número absoluto de erros ocorrendo.
    -   **Por que monitorar:** Um aumento na contagem de erros é um sinal direto de problemas. É útil para entender a magnitude do impacto.
    -   **No Dynatrace:** Visível na página do serviço, e detalhes podem ser encontrados na aba "Failures" ou "Errors".

-   **Tipos de Erros (Error Types / Exceptions):**
    -   **O que é:** A distribuição e frequência de diferentes tipos de erros ou exceções (ex: `NullPointerException`, `SQLException`, erros HTTP específicos).
    -   **Por que monitorar:** Ajuda a diagnosticar a causa raiz dos problemas. Um novo tipo de erro surgindo após um deployment é um forte indicador de um bug introduzido.
    -   **No Dynatrace:** A aba "Failures" ou "Errors" no serviço detalha as exceções e códigos de erro HTTP. O Dynatrace também pode agrupar erros semelhantes.

-   **Taxa de Erros (Error Rate - específico para certos tipos de erro):**
    -   **O que é:** Similar à taxa de falha geral, mas pode ser filtrada para tipos específicos de erros ou endpoints.
    -   **Por que monitorar:** Permite focar em problemas específicos que podem não ser aparentes na taxa de falha agregada, especialmente se um endpoint crítico começar a falhar enquanto outros permanecem estáveis.
    -   **No Dynatrace:** Pode ser configurado em dashboards ou ao analisar endpoints específicos dentro de um serviço.

### Dicas Adicionais com Dynatrace:

-   **Baselines Automáticas:** O Dynatrace automaticamente estabelece baselines de performance para suas métricas. Alertas são gerados quando há desvios significativos dessas baselines, o que é especialmente útil após deployments.
-   **Análise de Causa Raiz (Root Cause Analysis):** Se problemas forem detectados, o Dynatrace Davis® AI pode automaticamente identificar a causa raiz, correlacionando eventos e métricas de diferentes partes do seu stack.
-   **Comparação de Deployments:** Utilize as funcionalidades de marcação de eventos de deployment no Dynatrace. Isso permite comparar o comportamento da aplicação antes e depois de um deployment.

Ao monitorar essas métricas de perto, você pode detectar rapidamente problemas introduzidos por um novo deployment, minimizar o impacto nos usuários e garantir a estabilidade do seu serviço.
