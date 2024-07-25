# Paper Summarizer

Ferramenta para resumir papers científicos em PDF.
A aplicação utiliza o GPT3.5 da OpenAI para resumir os papers descrevendo os seguintes tópicos:
- Metodologia
- Softwares
- Algoritmos utilizados

***ATENÇÃO***: Esse projeto é um rascunho para ajudar minha esposa. Não pretendo manter ou evoluir, mas fiquem á vontade para fazer um fork e editar do jeito que quiser :).

## Requisitos

Para executar esse projeto você precisará:
- JDK 21
- Conta no OpenAI

## Getting Started

Para executar esse projeto, siga os passos a seguir:
1. Crie uma cópia do arquivo `.env.example` e renomeie para `.env`
2. Preencha as variáveis ambientes no `.env`.
```properties
OPENAI_API_KEY=<Crie sua chave do OpenAI e adicione aqui>
DOCUMENT_DIR=<Defina o diretório onde estão os seus papers PDF>
```
3. No Windows, abra um  terminal PowerShell e execute o script `run.ps1` 
